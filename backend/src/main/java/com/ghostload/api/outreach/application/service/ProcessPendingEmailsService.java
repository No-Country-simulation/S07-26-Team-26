package com.ghostload.api.outreach.application.service;

import com.ghostload.api.outreach.application.port.in.ProcessPendingEmailsUseCase;
import com.ghostload.api.outreach.application.port.out.ClaimQueuedEmailPort;
import com.ghostload.api.outreach.application.port.out.FinishCampaignDeliveryPort;
import com.ghostload.api.outreach.application.port.out.RecordEmailDeliveryPort;
import com.ghostload.api.outreach.application.port.out.SendEmailPort;
import com.ghostload.api.outreach.domain.model.CampaignStatus;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public final class ProcessPendingEmailsService implements ProcessPendingEmailsUseCase {

    private static final int MAXIMUM_ATTEMPTS = 3;
    private static final Duration CLAIM_TIMEOUT = Duration.ofMinutes(5);

    private final ClaimQueuedEmailPort claimQueuedEmailPort;
    private final SendEmailPort sendEmailPort;
    private final RecordEmailDeliveryPort recordEmailDeliveryPort;
    private final FinishCampaignDeliveryPort finishCampaignDeliveryPort;
    private final Clock clock;

    public ProcessPendingEmailsService(
            ClaimQueuedEmailPort claimQueuedEmailPort,
            SendEmailPort sendEmailPort,
            RecordEmailDeliveryPort recordEmailDeliveryPort,
            FinishCampaignDeliveryPort finishCampaignDeliveryPort,
            Clock clock) {
        this.claimQueuedEmailPort = Objects.requireNonNull(claimQueuedEmailPort);
        this.sendEmailPort = Objects.requireNonNull(sendEmailPort);
        this.recordEmailDeliveryPort = Objects.requireNonNull(recordEmailDeliveryPort);
        this.finishCampaignDeliveryPort =
                Objects.requireNonNull(finishCampaignDeliveryPort);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public int processBatch(int maximumItems) {
        if (maximumItems <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad máxima de correos debe ser mayor que cero.");
        }

        int processed = 0;
        while (processed < maximumItems) {
            Instant now = clock.instant();
            var queuedEmail = claimQueuedEmailPort
                    .claim(now, now.minus(CLAIM_TIMEOUT))
                    .orElse(null);
            if (queuedEmail == null) {
                break;
            }
            deliver(queuedEmail);
            processed++;
        }
        return processed;
    }

    private void deliver(ClaimQueuedEmailPort.QueuedEmailDelivery queuedEmail) {
        Instant now = clock.instant();
        try {
            SendEmailPort.EmailSendResult result = sendEmailPort.send(
                    new SendEmailPort.EmailMessage(
                            queuedEmail.recipientEmail(),
                            queuedEmail.recipientName(),
                            queuedEmail.subject(),
                            queuedEmail.message(),
                            queuedEmail.callToActionText(),
                            queuedEmail.invitationToken()));
            var progress = recordEmailDeliveryPort.recordSent(
                    queuedEmail.id(),
                    queuedEmail.invitationId(),
                    queuedEmail.campaignId(),
                    result.providerMessageId(),
                    now);
            finishCampaignIfNecessary(queuedEmail, progress, now);
        } catch (RuntimeException exception) {
            String error = normalizeError(exception);
            if (queuedEmail.attemptCount() < MAXIMUM_ATTEMPTS) {
                recordEmailDeliveryPort.reschedule(
                        queuedEmail.id(),
                        error,
                        now.plus(Duration.ofMinutes(queuedEmail.attemptCount())));
                return;
            }
            var progress = recordEmailDeliveryPort.recordFailed(
                    queuedEmail.id(),
                    queuedEmail.invitationId(),
                    queuedEmail.campaignId(),
                    error,
                    now);
            finishCampaignIfNecessary(queuedEmail, progress, now);
        }
    }

    private void finishCampaignIfNecessary(
            ClaimQueuedEmailPort.QueuedEmailDelivery queuedEmail,
            RecordEmailDeliveryPort.DeliveryProgress progress,
            Instant now) {
        if (!progress.finished()) {
            return;
        }
        CampaignStatus finalStatus =
                progress.sent() > 0 ? CampaignStatus.ACTIVE : CampaignStatus.FAILED;
        finishCampaignDeliveryPort.finish(queuedEmail.campaignId(), finalStatus, now);
    }

    private String normalizeError(RuntimeException exception) {
        String message = exception.getMessage();
        if (exception instanceof SendEmailPort.EmailSendingException emailException) {
            message = emailException.code() + ": " + message;
        }
        if (message == null || message.isBlank()) {
            message = exception.getClass().getSimpleName();
        }
        return message.length() <= 1_000 ? message : message.substring(0, 1_000);
    }
}
