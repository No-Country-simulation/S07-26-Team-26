package com.ghostload.api.outreach.application.service;

import com.ghostload.api.outreach.application.port.in.ImportContactsCommand;
import com.ghostload.api.outreach.application.port.out.ContactFileRow;
import com.ghostload.api.outreach.application.port.out.LoadExistingContactEmailsPort;
import com.ghostload.api.outreach.application.port.out.ParseContactFilePort;
import com.ghostload.api.outreach.application.port.out.SaveContactImportBatchPort;
import com.ghostload.api.outreach.domain.exception.ContactFileTooLargeException;
import com.ghostload.api.outreach.domain.exception.InvalidContactFileException;
import com.ghostload.api.outreach.domain.model.Contact;
import com.ghostload.api.outreach.domain.model.ContactImport;
import com.ghostload.api.outreach.domain.model.ImportIssueCode;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImportContactsServiceTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-07-26T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void shouldImportValidContactsAndNormalizeEmails() {
        ParseContactFilePort parser = content -> List.of(
                new ContactFileRow(
                        2, " Ana ", "Torres", " ANA@EMPRESA.COM ",
                        "Empresa SAC", "Gerente TI"),
                new ContactFileRow(
                        3, "Luis", "Perez", "luis@empresa.com",
                        "Data Center SA", ""));
        AtomicReference<ContactImport> savedImport = new AtomicReference<>();
        AtomicReference<List<Contact>> savedContacts = new AtomicReference<>();
        SaveContactImportBatchPort saver = (contactImport, contacts) -> {
            savedImport.set(contactImport);
            savedContacts.set(List.copyOf(contacts));
        };

        ImportContactsService service = service(parser, emails -> Set.of(), saver);
        var result = service.importContacts(command("contacts.csv"));

        assertThat(result.totalRows()).isEqualTo(2);
        assertThat(result.validContacts()).isEqualTo(2);
        assertThat(result.duplicates()).isZero();
        assertThat(result.invalidRows()).isZero();
        assertThat(result.issues()).isEmpty();
        assertThat(savedImport.get().id()).isEqualTo(result.importId());
        assertThat(savedContacts.get())
                .extracting(contact -> contact.email().value())
                .containsExactly("ana@empresa.com", "luis@empresa.com");
        assertThat(savedContacts.get().get(1).position()).isNull();
    }

    @Test
    void shouldReportDuplicateInvalidAndExistingRowsWithoutSavingThem() {
        ParseContactFilePort parser = content -> List.of(
                new ContactFileRow(
                        2, "Ana", "Torres", "ana@empresa.com",
                        "Empresa SAC", "Gerente"),
                new ContactFileRow(
                        3, "Ana 2", "Torres", "ANA@EMPRESA.COM",
                        "Empresa SAC", "Gerente"),
                new ContactFileRow(
                        4, "Sin", "Email", "correo-invalido",
                        "Empresa SAC", "Gerente"),
                new ContactFileRow(
                        5, "Existe", "Contacto", "existente@empresa.com",
                        "Empresa SAC", "Gerente"));
        AtomicReference<List<Contact>> savedContacts = new AtomicReference<>();
        SaveContactImportBatchPort saver =
                (contactImport, contacts) -> savedContacts.set(List.copyOf(contacts));
        LoadExistingContactEmailsPort existing =
                emails -> Set.of("existente@empresa.com");

        ImportContactsService service = service(parser, existing, saver);
        var result = service.importContacts(command("contacts.csv"));

        assertThat(result.totalRows()).isEqualTo(4);
        assertThat(result.validContacts()).isEqualTo(1);
        assertThat(result.duplicates()).isEqualTo(2);
        assertThat(result.invalidRows()).isEqualTo(1);
        assertThat(result.issues())
                .extracting(issue -> issue.code())
                .containsExactly(
                        ImportIssueCode.DUPLICATE_IN_FILE,
                        ImportIssueCode.INVALID_EMAIL,
                        ImportIssueCode.EXISTING_CONTACT);
        assertThat(savedContacts.get()).hasSize(1);
    }

    @Test
    void shouldRejectFilesWithoutCsvExtension() {
        ImportContactsService service = service(
                content -> List.of(),
                emails -> Set.of(),
                (contactImport, contacts) -> {
                });

        assertThatThrownBy(() -> service.importContacts(command("contacts.xlsx")))
                .isInstanceOf(InvalidContactFileException.class)
                .hasMessageContaining(".csv");
    }

    @Test
    void shouldRejectFilesLargerThanFiveMegabytes() {
        ImportContactsService service = service(
                content -> List.of(),
                emails -> Set.of(),
                (contactImport, contacts) -> {
                });
        byte[] content = new byte[ImportContactsService.MAX_FILE_SIZE_BYTES + 1];

        assertThatThrownBy(() -> service.importContacts(
                new ImportContactsCommand("Prospectos", "contacts.csv", content)))
                .isInstanceOf(ContactFileTooLargeException.class);
    }

    private ImportContactsService service(
            ParseContactFilePort parser,
            LoadExistingContactEmailsPort existing,
            SaveContactImportBatchPort saver) {
        return new ImportContactsService(parser, existing, saver, FIXED_CLOCK);
    }

    private ImportContactsCommand command(String filename) {
        return new ImportContactsCommand(
                "Prospectos Q3",
                filename,
                "csv-content".getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
