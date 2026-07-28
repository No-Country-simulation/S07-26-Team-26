package com.ghostload.api.outreach.application.service;

import com.ghostload.api.outreach.application.port.in.CreateCampaignCommand;
import com.ghostload.api.outreach.application.port.out.GenerateInvitationTokenPort;
import com.ghostload.api.outreach.application.port.out.LoadCampaignAudiencePort;
import com.ghostload.api.outreach.application.port.out.SaveCampaignPort;
import com.ghostload.api.outreach.domain.exception.InvalidCampaignException;
import com.ghostload.api.outreach.domain.model.Campaign;
import com.ghostload.api.outreach.domain.model.CampaignStatus;
import com.ghostload.api.outreach.domain.model.Contact;
import com.ghostload.api.outreach.domain.model.ContactEmail;
import com.ghostload.api.outreach.domain.model.ContactImport;
import com.ghostload.api.outreach.domain.model.ContactImportStatus;
import com.ghostload.api.outreach.domain.model.Invitation;
import com.ghostload.api.outreach.domain.model.InvitationStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateCampaignServiceTest {

    private static final Instant NOW = Instant.parse("2026-07-26T18:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final UUID IMPORT_ID =
            UUID.fromString("1dc43a6c-753c-4532-8146-7902479382d1");

    @Test
    void shouldCreateReadyCampaignAndOneInvitationPerContact() {
        List<Contact> contacts = List.of(
                contact("ana@empresa.com"),
                contact("luis@empresa.com"));
        LoadCampaignAudiencePort loader =
                ignored -> Optional.of(audience(ContactImportStatus.COMPLETED, contacts));
        Queue<UUID> tokens = new ArrayDeque<>(List.of(
                UUID.fromString("cc4a69ee-53e1-4eb8-ae9d-14431e229e3b"),
                UUID.fromString("0bbf869a-0d40-4d4a-9603-d02a5b387587")));
        GenerateInvitationTokenPort tokenGenerator = tokens::remove;
        AtomicReference<Campaign> savedCampaign = new AtomicReference<>();
        AtomicReference<List<Invitation>> savedInvitations = new AtomicReference<>();
        SaveCampaignPort saver = (campaign, invitations) -> {
            savedCampaign.set(campaign);
            savedInvitations.set(invitations);
        };
        CreateCampaignService service =
                new CreateCampaignService(loader, tokenGenerator, saver, CLOCK);

        var result = service.create(validCommand());

        assertThat(result.status()).isEqualTo(CampaignStatus.READY);
        assertThat(result.recipientCount()).isEqualTo(2);
        assertThat(result.createdAt()).isEqualTo(NOW);
        assertThat(savedCampaign.get().contactImportId()).isEqualTo(IMPORT_ID);
        assertThat(savedInvitations.get()).hasSize(2);
        assertThat(savedInvitations.get())
                .allMatch(invitation -> invitation.status() == InvitationStatus.UPLOADED);
        assertThat(savedInvitations.get())
                .extracting(Invitation::token)
                .doesNotHaveDuplicates();
    }

    @Test
    void shouldRejectUnknownContactImport() {
        CreateCampaignService service = service(
                ignored -> Optional.empty(),
                UUID::randomUUID,
                (campaign, invitations) -> {
                });

        assertThatThrownBy(() -> service.create(validCommand()))
                .isInstanceOf(InvalidCampaignException.class)
                .hasMessage("La importación de contactos no existe.");
    }

    @Test
    void shouldRejectContactImportThatIsNotCompleted() {
        CreateCampaignService service = service(
                ignored -> Optional.of(audience(
                        ContactImportStatus.PROCESSING,
                        List.of(contact("ana@empresa.com")))),
                UUID::randomUUID,
                (campaign, invitations) -> {
                });

        assertThatThrownBy(() -> service.create(validCommand()))
                .isInstanceOf(InvalidCampaignException.class)
                .hasMessageContaining("debe estar completada");
    }

    @Test
    void shouldRejectImportWithoutValidContacts() {
        CreateCampaignService service = service(
                ignored -> Optional.of(audience(ContactImportStatus.COMPLETED, List.of())),
                UUID::randomUUID,
                (campaign, invitations) -> {
                });

        assertThatThrownBy(() -> service.create(validCommand()))
                .isInstanceOf(InvalidCampaignException.class)
                .hasMessage("La importación no contiene contactos válidos.");
    }

    @Test
    void shouldRejectRepeatedInvitationTokens() {
        UUID repeatedToken =
                UUID.fromString("cc4a69ee-53e1-4eb8-ae9d-14431e229e3b");
        CreateCampaignService service = service(
                ignored -> Optional.of(audience(
                        ContactImportStatus.COMPLETED,
                        List.of(contact("ana@empresa.com"), contact("luis@empresa.com")))),
                () -> repeatedToken,
                (campaign, invitations) -> {
                });

        assertThatThrownBy(() -> service.create(validCommand()))
                .isInstanceOf(InvalidCampaignException.class)
                .hasMessageContaining("token único");
    }

    private CreateCampaignService service(
            LoadCampaignAudiencePort loader,
            GenerateInvitationTokenPort tokenGenerator,
            SaveCampaignPort saver) {
        return new CreateCampaignService(loader, tokenGenerator, saver, CLOCK);
    }

    private CreateCampaignCommand validCommand() {
        return new CreateCampaignCommand(
                "Benchmark julio",
                "Invitación para operadores",
                "Conoce la madurez de tu data center",
                "Completa el benchmark y recibe tu reporte personalizado.",
                "Comenzar evaluación",
                IMPORT_ID,
                null,
                "America/Lima");
    }

    private LoadCampaignAudiencePort.CampaignAudience audience(
            ContactImportStatus status,
            List<Contact> contacts) {
        return new LoadCampaignAudiencePort.CampaignAudience(
                new ContactImport(
                        IMPORT_ID,
                        "Prospectos Q3",
                        status,
                        contacts.size(),
                        contacts.size(),
                        0,
                        0,
                        NOW),
                contacts);
    }

    private Contact contact(String email) {
        return Contact.create(
                "Nombre",
                "Apellido",
                new ContactEmail(email),
                "Empresa SAC",
                "Gerente",
                NOW);
    }
}
