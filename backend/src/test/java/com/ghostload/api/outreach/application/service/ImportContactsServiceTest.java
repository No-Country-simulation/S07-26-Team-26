package com.ghostload.api.outreach.application.service;

import com.ghostload.api.outreach.application.port.in.ImportContactsCommand;
import com.ghostload.api.outreach.application.port.out.ContactFileRow;
import com.ghostload.api.outreach.application.port.out.LoadExistingContactsPort;
import com.ghostload.api.outreach.application.port.out.ParseContactFilePort;
import com.ghostload.api.outreach.application.port.out.SaveContactImportBatchPort;
import com.ghostload.api.outreach.domain.exception.ContactFileTooLargeException;
import com.ghostload.api.outreach.domain.exception.InvalidContactFileException;
import com.ghostload.api.outreach.domain.model.Contact;
import com.ghostload.api.outreach.domain.model.ContactEmail;
import com.ghostload.api.outreach.domain.model.ContactImport;
import com.ghostload.api.outreach.domain.model.ImportIssueCode;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImportContactsServiceTest {

    private static final Instant NOW = Instant.parse("2026-07-26T12:00:00Z");
    private static final Clock FIXED_CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Test
    void shouldImportNewContactsAndNormalizeEmails() {
        ParseContactFilePort parser = content -> List.of(
                new ContactFileRow(
                        2, " Ana ", "Torres", " ANA@EMPRESA.COM ",
                        "Empresa SAC", "Gerente TI"),
                new ContactFileRow(
                        3, "Luis", "Perez", "luis@empresa.com",
                        "Data Center SA", ""));
        AtomicReference<ContactImport> savedImport = new AtomicReference<>();
        AtomicReference<List<Contact>> savedContacts = new AtomicReference<>();
        AtomicReference<List<UUID>> savedAudience = new AtomicReference<>();
        SaveContactImportBatchPort saver = (contactImport, contacts, audience) -> {
            savedImport.set(contactImport);
            savedContacts.set(List.copyOf(contacts));
            savedAudience.set(List.copyOf(audience));
        };

        ImportContactsService service = service(parser, emails -> Map.of(), saver);
        var result = service.importContacts(command("contacts.csv"));

        assertThat(result.totalRows()).isEqualTo(2);
        assertThat(result.validContacts()).isEqualTo(2);
        assertThat(result.newContacts()).isEqualTo(2);
        assertThat(result.existingContacts()).isZero();
        assertThat(result.duplicates()).isZero();
        assertThat(result.invalidRows()).isZero();
        assertThat(result.issues()).isEmpty();
        assertThat(savedImport.get().id()).isEqualTo(result.importId());
        assertThat(savedContacts.get())
                .extracting(contact -> contact.email().value())
                .containsExactly("ana@empresa.com", "luis@empresa.com");
        assertThat(savedAudience.get())
                .containsExactlyElementsOf(savedContacts.get().stream().map(Contact::id).toList());
        assertThat(savedContacts.get().get(1).position()).isNull();
    }

    @Test
    void shouldReuseExistingContactAndOnlyRejectDuplicateInsideFile() {
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
        Contact existingContact = new Contact(
                UUID.randomUUID(),
                "Existe",
                "Contacto",
                new ContactEmail("existente@empresa.com"),
                "Empresa SAC",
                "Gerente",
                NOW.minusSeconds(3_600));
        AtomicReference<List<Contact>> savedNewContacts = new AtomicReference<>();
        AtomicReference<List<UUID>> savedAudience = new AtomicReference<>();
        SaveContactImportBatchPort saver = (contactImport, contacts, audience) -> {
            savedNewContacts.set(List.copyOf(contacts));
            savedAudience.set(List.copyOf(audience));
        };
        LoadExistingContactsPort existing =
                emails -> Map.of(existingContact.email().value(), existingContact);

        ImportContactsService service = service(parser, existing, saver);
        var result = service.importContacts(command("contacts.csv"));

        assertThat(result.totalRows()).isEqualTo(4);
        assertThat(result.validContacts()).isEqualTo(2);
        assertThat(result.newContacts()).isEqualTo(1);
        assertThat(result.existingContacts()).isEqualTo(1);
        assertThat(result.duplicates()).isEqualTo(1);
        assertThat(result.invalidRows()).isEqualTo(1);
        assertThat(result.issues())
                .extracting(issue -> issue.code())
                .containsExactly(
                        ImportIssueCode.DUPLICATE_IN_FILE,
                        ImportIssueCode.INVALID_EMAIL);
        assertThat(savedNewContacts.get())
                .extracting(contact -> contact.email().value())
                .containsExactly("ana@empresa.com");
        assertThat(savedAudience.get()).contains(existingContact.id());
    }

    @Test
    void shouldRejectFilesWithoutCsvExtension() {
        ImportContactsService service = service(
                content -> List.of(),
                emails -> Map.of(),
                (contactImport, contacts, audience) -> {
                });

        assertThatThrownBy(() -> service.importContacts(command("contacts.xlsx")))
                .isInstanceOf(InvalidContactFileException.class)
                .hasMessageContaining(".csv");
    }

    @Test
    void shouldRejectFilesLargerThanFiveMegabytes() {
        ImportContactsService service = service(
                content -> List.of(),
                emails -> Map.of(),
                (contactImport, contacts, audience) -> {
                });
        byte[] content = new byte[ImportContactsService.MAX_FILE_SIZE_BYTES + 1];

        assertThatThrownBy(() -> service.importContacts(
                new ImportContactsCommand("Prospectos", "contacts.csv", content)))
                .isInstanceOf(ContactFileTooLargeException.class);
    }

    private ImportContactsService service(
            ParseContactFilePort parser,
            LoadExistingContactsPort existing,
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
