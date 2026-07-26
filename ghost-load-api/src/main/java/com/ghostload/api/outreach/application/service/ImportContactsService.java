package com.ghostload.api.outreach.application.service;

import com.ghostload.api.outreach.application.port.in.ImportContactsCommand;
import com.ghostload.api.outreach.application.port.in.ImportContactsResult;
import com.ghostload.api.outreach.application.port.in.ImportContactsUseCase;
import com.ghostload.api.outreach.application.port.out.ContactFileRow;
import com.ghostload.api.outreach.application.port.out.LoadExistingContactEmailsPort;
import com.ghostload.api.outreach.application.port.out.ParseContactFilePort;
import com.ghostload.api.outreach.application.port.out.SaveContactImportBatchPort;
import com.ghostload.api.outreach.domain.exception.ContactFileTooLargeException;
import com.ghostload.api.outreach.domain.exception.InvalidContactFileException;
import com.ghostload.api.outreach.domain.model.Contact;
import com.ghostload.api.outreach.domain.model.ContactEmail;
import com.ghostload.api.outreach.domain.model.ContactImport;
import com.ghostload.api.outreach.domain.model.ImportIssue;
import com.ghostload.api.outreach.domain.model.ImportIssueCode;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class ImportContactsService implements ImportContactsUseCase {

    static final int MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024;
    static final int MAX_ROWS = 5_000;

    private final ParseContactFilePort parseContactFilePort;
    private final LoadExistingContactEmailsPort loadExistingContactEmailsPort;
    private final SaveContactImportBatchPort saveContactImportBatchPort;
    private final Clock clock;

    public ImportContactsService(
            ParseContactFilePort parseContactFilePort,
            LoadExistingContactEmailsPort loadExistingContactEmailsPort,
            SaveContactImportBatchPort saveContactImportBatchPort,
            Clock clock) {
        this.parseContactFilePort = parseContactFilePort;
        this.loadExistingContactEmailsPort = loadExistingContactEmailsPort;
        this.saveContactImportBatchPort = saveContactImportBatchPort;
        this.clock = clock;
    }

    @Override
    public ImportContactsResult importContacts(ImportContactsCommand command) {
        validateCommand(command);

        List<ContactFileRow> rows = parseContactFilePort.parse(command.content());
        if (rows.size() > MAX_ROWS) {
            throw new InvalidContactFileException(
                    "El archivo supera el máximo permitido de " + MAX_ROWS + " filas.");
        }

        List<ImportIssue> issues = new ArrayList<>();
        List<ValidatedRow> validRows = new ArrayList<>();
        Set<String> emailsSeenInFile = new HashSet<>();

        for (ContactFileRow row : rows) {
            ValidatedRow validatedRow = validateRow(row, emailsSeenInFile, issues);
            if (validatedRow != null) {
                validRows.add(validatedRow);
            }
        }

        Set<String> candidateEmails = validRows.stream()
                .map(row -> row.email().value())
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
        Set<String> existingEmails =
                loadExistingContactEmailsPort.loadExistingEmails(candidateEmails);

        UUID importId = UUID.randomUUID();
        Instant createdAt = clock.instant();
        List<Contact> contacts = new ArrayList<>();

        for (ValidatedRow row : validRows) {
            if (existingEmails.contains(row.email().value())) {
                issues.add(new ImportIssue(
                        row.rowNumber(),
                        row.email().value(),
                        ImportIssueCode.EXISTING_CONTACT,
                        "El contacto ya existe."));
                continue;
            }
            contacts.add(Contact.create(
                    importId,
                    row.firstName(),
                    row.lastName(),
                    row.email(),
                    row.companyName(),
                    row.position(),
                    createdAt));
        }

        issues.sort(java.util.Comparator.comparingLong(ImportIssue::row));
        int duplicates = (int) issues.stream()
                .filter(this::isDuplicate)
                .count();
        int invalidRows = issues.size() - duplicates;

        ContactImport contactImport = ContactImport.completed(
                importId,
                command.name(),
                rows.size(),
                contacts.size(),
                duplicates,
                invalidRows,
                createdAt);

        saveContactImportBatchPort.save(contactImport, contacts);

        return new ImportContactsResult(
                contactImport.id(),
                contactImport.name(),
                contactImport.status(),
                contactImport.totalRows(),
                contactImport.validContacts(),
                contactImport.duplicates(),
                contactImport.invalidRows(),
                issues,
                contactImport.createdAt());
    }

    private void validateCommand(ImportContactsCommand command) {
        if (command == null) {
            throw new InvalidContactFileException("La solicitud de importación es obligatoria.");
        }
        if (command.name() == null
                || command.name().trim().length() < 3
                || command.name().trim().length() > 120) {
            throw new InvalidContactFileException(
                    "El nombre de la importación debe tener entre 3 y 120 caracteres.");
        }
        if (command.originalFilename() == null
                || !command.originalFilename().toLowerCase(Locale.ROOT).endsWith(".csv")) {
            throw new InvalidContactFileException("El archivo debe tener extensión .csv.");
        }
        byte[] content = command.content();
        if (content == null || content.length == 0) {
            throw new InvalidContactFileException("El archivo CSV está vacío.");
        }
        if (content.length > MAX_FILE_SIZE_BYTES) {
            throw new ContactFileTooLargeException(
                    "El archivo supera el máximo permitido de 5 MB.");
        }
    }

    private ValidatedRow validateRow(
            ContactFileRow row,
            Set<String> emailsSeenInFile,
            List<ImportIssue> issues) {
        String rawEmail = normalizeNullable(row.email());
        ContactEmail email;
        try {
            email = new ContactEmail(rawEmail);
        } catch (IllegalArgumentException exception) {
            issues.add(new ImportIssue(
                    row.rowNumber(),
                    rawEmail,
                    ImportIssueCode.INVALID_EMAIL,
                    exception.getMessage()));
            return null;
        }

        String invalidMessage = validateTextFields(row);
        if (invalidMessage != null) {
            issues.add(new ImportIssue(
                    row.rowNumber(),
                    email.value(),
                    ImportIssueCode.INVALID_VALUE,
                    invalidMessage));
            return null;
        }

        if (!emailsSeenInFile.add(email.value())) {
            issues.add(new ImportIssue(
                    row.rowNumber(),
                    email.value(),
                    ImportIssueCode.DUPLICATE_IN_FILE,
                    "El email está duplicado dentro del archivo."));
            return null;
        }

        return new ValidatedRow(
                row.rowNumber(),
                row.firstName().trim(),
                row.lastName().trim(),
                email,
                row.companyName().trim(),
                normalizeNullable(row.position()));
    }

    private String validateTextFields(ContactFileRow row) {
        if (isBlank(row.firstName()) || row.firstName().trim().length() > 80) {
            return "El nombre es obligatorio y debe tener máximo 80 caracteres.";
        }
        if (isBlank(row.lastName()) || row.lastName().trim().length() > 80) {
            return "El apellido es obligatorio y debe tener máximo 80 caracteres.";
        }
        if (isBlank(row.companyName())
                || row.companyName().trim().length() < 2
                || row.companyName().trim().length() > 160) {
            return "La empresa debe tener entre 2 y 160 caracteres.";
        }
        if (row.position() != null && row.position().trim().length() > 120) {
            return "El cargo debe tener máximo 120 caracteres.";
        }
        return null;
    }

    private boolean isDuplicate(ImportIssue issue) {
        return issue.code() == ImportIssueCode.DUPLICATE_IN_FILE
                || issue.code() == ImportIssueCode.EXISTING_CONTACT;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isBlank();
    }

    private static String normalizeNullable(String value) {
        if (value == null || value.trim().isBlank()) {
            return null;
        }
        return value.trim();
    }

    private record ValidatedRow(
            long rowNumber,
            String firstName,
            String lastName,
            ContactEmail email,
            String companyName,
            String position) {
    }
}
