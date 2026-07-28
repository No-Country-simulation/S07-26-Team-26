package com.ghostload.api.outreach.application.service;

import com.ghostload.api.outreach.application.port.in.CreateCampaignCommand;
import com.ghostload.api.outreach.application.port.in.CreateCampaignResult;
import com.ghostload.api.outreach.application.port.in.CreateCampaignUseCase;
import com.ghostload.api.outreach.application.port.out.GenerateInvitationTokenPort;
import com.ghostload.api.outreach.application.port.out.LoadCampaignAudiencePort;
import com.ghostload.api.outreach.application.port.out.SaveCampaignPort;
import com.ghostload.api.outreach.domain.exception.InvalidCampaignException;
import com.ghostload.api.outreach.domain.model.Campaign;
import com.ghostload.api.outreach.domain.model.Contact;
import com.ghostload.api.outreach.domain.model.ContactImportStatus;
import com.ghostload.api.outreach.domain.model.Invitation;

import java.time.Clock;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class CreateCampaignService implements CreateCampaignUseCase {

    private final LoadCampaignAudiencePort loadCampaignAudiencePort;
    private final GenerateInvitationTokenPort generateInvitationTokenPort;
    private final SaveCampaignPort saveCampaignPort;
    private final Clock clock;

    public CreateCampaignService(
            LoadCampaignAudiencePort loadCampaignAudiencePort,
            GenerateInvitationTokenPort generateInvitationTokenPort,
            SaveCampaignPort saveCampaignPort,
            Clock clock) {
        this.loadCampaignAudiencePort = loadCampaignAudiencePort;
        this.generateInvitationTokenPort = generateInvitationTokenPort;
        this.saveCampaignPort = saveCampaignPort;
        this.clock = clock;
    }

    @Override
    public CreateCampaignResult create(CreateCampaignCommand command) {
        if (command == null || command.contactImportId() == null) {
            throw new InvalidCampaignException(
                    "La importación de contactos es obligatoria.");
        }

        LoadCampaignAudiencePort.CampaignAudience audience =
                loadCampaignAudiencePort.load(command.contactImportId())
                        .orElseThrow(() -> new InvalidCampaignException(
                                "La importación de contactos no existe."));

        if (audience.contactImport().status() != ContactImportStatus.COMPLETED) {
            throw new InvalidCampaignException(
                    "La importación debe estar completada antes de crear una campaña.");
        }

        List<Contact> contacts = audience.contacts();
        if (contacts.isEmpty()) {
            throw new InvalidCampaignException(
                    "La importación no contiene contactos válidos.");
        }
        ensureUniqueEmails(contacts);

        Instant createdAt = clock.instant();
        Campaign campaign = Campaign.ready(
                command.contactImportId(),
                command.name(),
                command.description(),
                command.subject(),
                command.message(),
                command.callToActionText(),
                contacts.size(),
                command.scheduledAt(),
                command.timezone(),
                createdAt);

        Set<UUID> generatedTokens = new HashSet<>();
        List<Invitation> invitations = contacts.stream()
                .map(contact -> Invitation.uploaded(
                        campaign.id(),
                        contact.id(),
                        nextUniqueToken(generatedTokens),
                        createdAt))
                .toList();

        saveCampaignPort.save(campaign, invitations);

        return new CreateCampaignResult(
                campaign.id(),
                campaign.name(),
                campaign.status(),
                campaign.subject(),
                campaign.recipientCount(),
                campaign.scheduledAt(),
                campaign.sentAt(),
                campaign.createdAt());
    }

    private void ensureUniqueEmails(List<Contact> contacts) {
        Set<String> emails = new HashSet<>();
        boolean containsDuplicate = contacts.stream()
                .map(contact -> contact.email().value())
                .anyMatch(email -> !emails.add(email));
        if (containsDuplicate) {
            throw new InvalidCampaignException(
                    "La importación contiene contactos duplicados.");
        }
    }

    private UUID nextUniqueToken(Set<UUID> generatedTokens) {
        UUID token = generateInvitationTokenPort.generate();
        if (token == null || !generatedTokens.add(token)) {
            throw new InvalidCampaignException(
                    "No se pudo generar un token único para cada invitación.");
        }
        return token;
    }
}
