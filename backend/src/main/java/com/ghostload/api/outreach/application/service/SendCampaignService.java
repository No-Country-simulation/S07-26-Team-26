package com.ghostload.api.outreach.application.service;

import com.ghostload.api.outreach.application.port.in.SendCampaignCommand;
import com.ghostload.api.outreach.application.port.in.SendCampaignResult;
import com.ghostload.api.outreach.application.port.in.SendCampaignUseCase;
import com.ghostload.api.outreach.application.port.out.LoadCampaignDeliveryPort;
import com.ghostload.api.outreach.application.port.out.QueueCampaignEmailsPort;
import com.ghostload.api.outreach.domain.exception.CampaignNotFoundException;
import com.ghostload.api.outreach.domain.exception.InvalidCampaignStateException;
import com.ghostload.api.outreach.domain.model.Campaign;
import com.ghostload.api.outreach.domain.model.InvitationStatus;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class SendCampaignService implements SendCampaignUseCase {

    private final LoadCampaignDeliveryPort loadCampaignDeliveryPort;
    private final QueueCampaignEmailsPort queueCampaignEmailsPort;
    private final Clock clock;

    public SendCampaignService(
            LoadCampaignDeliveryPort loadCampaignDeliveryPort,
            QueueCampaignEmailsPort queueCampaignEmailsPort,
            Clock clock) {
        this.loadCampaignDeliveryPort =
                Objects.requireNonNull(loadCampaignDeliveryPort);
        this.queueCampaignEmailsPort =
                Objects.requireNonNull(queueCampaignEmailsPort);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public SendCampaignResult send(SendCampaignCommand command) {
        if (command == null || command.campaignId() == null) {
            throw new IllegalArgumentException("El identificador de la campaña es obligatorio.");
        }

        LoadCampaignDeliveryPort.CampaignDelivery delivery =
                loadCampaignDeliveryPort.loadCampaignDelivery(command.campaignId())
                        .orElseThrow(() -> new CampaignNotFoundException(command.campaignId()));
        Campaign campaign = delivery.campaign();
        if (delivery.recipients().isEmpty()
                || delivery.recipients().stream()
                .anyMatch(recipient ->
                        recipient.invitationStatus() != InvitationStatus.UPLOADED)) {
            throw new InvalidCampaignStateException(campaign.status());
        }
        campaign.startSending();

        Instant now = clock.instant();
        Instant availableAt =
                campaign.scheduledAt() != null && campaign.scheduledAt().isAfter(now)
                        ? campaign.scheduledAt()
                        : now;
        List<QueueCampaignEmailsPort.QueuedEmail> queuedEmails =
                delivery.recipients().stream()
                        .map(recipient -> new QueueCampaignEmailsPort.QueuedEmail(
                                UUID.randomUUID(),
                                campaign.id(),
                                recipient.invitationId(),
                                recipient.email(),
                                fullName(recipient.firstName(), recipient.lastName()),
                                campaign.subject(),
                                campaign.message(),
                                campaign.callToActionText(),
                                recipient.invitationToken(),
                                availableAt,
                                now))
                        .toList();

        queueCampaignEmailsPort.queue(campaign, queuedEmails);
        return new SendCampaignResult(
                campaign.id(),
                campaign.name(),
                campaign.status(),
                campaign.subject(),
                campaign.recipientCount(),
                campaign.scheduledAt(),
                campaign.sentAt(),
                campaign.createdAt());
    }

    private String fullName(String firstName, String lastName) {
        return (firstName + " " + lastName).trim();
    }
}
