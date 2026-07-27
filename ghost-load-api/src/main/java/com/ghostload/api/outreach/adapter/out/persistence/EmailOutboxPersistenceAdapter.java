package com.ghostload.api.outreach.adapter.out.persistence;

import com.ghostload.api.outreach.application.port.out.ClaimQueuedEmailPort;
import com.ghostload.api.outreach.application.port.out.FinishCampaignDeliveryPort;
import com.ghostload.api.outreach.application.port.out.RecordEmailDeliveryPort;
import com.ghostload.api.outreach.domain.model.CampaignStatus;
import com.ghostload.api.outreach.domain.model.InvitationStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class EmailOutboxPersistenceAdapter
        implements ClaimQueuedEmailPort,
        RecordEmailDeliveryPort,
        FinishCampaignDeliveryPort {

    private final SpringDataEmailOutboxRepository emailOutboxRepository;
    private final SpringDataInvitationRepository invitationRepository;
    private final SpringDataCampaignRepository campaignRepository;

    public EmailOutboxPersistenceAdapter(
            SpringDataEmailOutboxRepository emailOutboxRepository,
            SpringDataInvitationRepository invitationRepository,
            SpringDataCampaignRepository campaignRepository) {
        this.emailOutboxRepository = emailOutboxRepository;
        this.invitationRepository = invitationRepository;
        this.campaignRepository = campaignRepository;
    }

    @Override
    @Transactional
    public Optional<QueuedEmailDelivery> claim(Instant now, Instant staleBefore) {
        return emailOutboxRepository.findNextForUpdate(now, staleBefore)
                .map(entity -> {
                    entity.claim(now);
                    return toDelivery(entity);
                });
    }

    @Override
    @Transactional
    public DeliveryProgress recordSent(
            UUID outboxId,
            UUID invitationId,
            UUID campaignId,
            String providerMessageId,
            Instant sentAt) {
        loadOutbox(outboxId).markSent(providerMessageId, sentAt);
        invitationRepository.markSent(
                invitationId,
                InvitationStatus.UPLOADED,
                InvitationStatus.SENT,
                sentAt);
        return progress(campaignId);
    }

    @Override
    @Transactional
    public void reschedule(UUID outboxId, String error, Instant availableAt) {
        loadOutbox(outboxId).reschedule(error, availableAt);
    }

    @Override
    @Transactional
    public DeliveryProgress recordFailed(
            UUID outboxId,
            UUID invitationId,
            UUID campaignId,
            String error,
            Instant failedAt) {
        loadOutbox(outboxId).markFailed(error, failedAt);
        invitationRepository.markFailed(
                invitationId,
                InvitationStatus.UPLOADED,
                InvitationStatus.FAILED,
                failedAt,
                error);
        return progress(campaignId);
    }

    @Override
    @Transactional
    public void finish(UUID campaignId, CampaignStatus status, Instant finishedAt) {
        campaignRepository.finishSending(
                campaignId,
                CampaignStatus.SENDING,
                status,
                status == CampaignStatus.ACTIVE ? finishedAt : null);
    }

    private EmailOutboxJpaEntity loadOutbox(UUID outboxId) {
        return emailOutboxRepository.findById(outboxId)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe el correo encolado " + outboxId + "."));
    }

    private DeliveryProgress progress(UUID campaignId) {
        return new DeliveryProgress(
                emailOutboxRepository.countUnfinished(campaignId),
                emailOutboxRepository.countByCampaignIdAndStatus(
                        campaignId,
                        EmailOutboxStatusJpa.SENT),
                emailOutboxRepository.countByCampaignIdAndStatus(
                        campaignId,
                        EmailOutboxStatusJpa.FAILED));
    }

    private QueuedEmailDelivery toDelivery(EmailOutboxJpaEntity entity) {
        return new QueuedEmailDelivery(
                entity.id(),
                entity.campaignId(),
                entity.invitationId(),
                entity.recipientEmail(),
                entity.recipientName(),
                entity.subject(),
                entity.message(),
                entity.callToActionText(),
                entity.invitationToken(),
                entity.attemptCount());
    }
}
