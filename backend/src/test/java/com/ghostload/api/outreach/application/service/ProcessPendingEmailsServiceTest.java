package com.ghostload.api.outreach.application.service;

import com.ghostload.api.outreach.application.port.out.ClaimQueuedEmailPort;
import com.ghostload.api.outreach.application.port.out.FinishCampaignDeliveryPort;
import com.ghostload.api.outreach.application.port.out.RecordEmailDeliveryPort;
import com.ghostload.api.outreach.application.port.out.SendEmailPort;
import com.ghostload.api.outreach.domain.model.CampaignStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessPendingEmailsServiceTest {

    private static final Instant NOW = Instant.parse("2026-07-27T14:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Test
    void shouldSendEmailAndActivateCampaignWhenBatchFinishes() {
        var email = queuedEmail(1);
        Queue<ClaimQueuedEmailPort.QueuedEmailDelivery> queue =
                new ArrayDeque<>();
        queue.add(email);
        AtomicReference<SendEmailPort.EmailMessage> sent = new AtomicReference<>();
        AtomicReference<CampaignStatus> finalStatus = new AtomicReference<>();

        ProcessPendingEmailsService service = new ProcessPendingEmailsService(
                (now, staleBefore) -> Optional.ofNullable(queue.poll()),
                message -> {
                    sent.set(message);
                    return new SendEmailPort.EmailSendResult("smtp-message-id");
                },
                successfulRecorder(),
                (campaignId, status, finishedAt) -> finalStatus.set(status),
                CLOCK);

        int processed = service.processBatch(20);

        assertThat(processed).isEqualTo(1);
        assertThat(sent.get().recipientEmail()).isEqualTo("ana@empresa.com");
        assertThat(finalStatus.get()).isEqualTo(CampaignStatus.ACTIVE);
    }

    @Test
    void shouldRescheduleBeforeMaximumAttempt() {
        var email = queuedEmail(2);
        AtomicReference<Instant> nextAttempt = new AtomicReference<>();
        AtomicReference<String> recordedError = new AtomicReference<>();
        RecordEmailDeliveryPort recorder = new NoOpRecorder() {
            @Override
            public void reschedule(UUID outboxId, String error, Instant availableAt) {
                recordedError.set(error);
                nextAttempt.set(availableAt);
            }
        };
        ProcessPendingEmailsService service = serviceThatFails(email, recorder, null);

        service.processBatch(1);

        assertThat(recordedError.get()).contains("SMTP no disponible");
        assertThat(nextAttempt.get()).isEqualTo(NOW.plusSeconds(120));
    }

    @Test
    void shouldFailInvitationAndCampaignAfterThirdAttempt() {
        var email = queuedEmail(3);
        AtomicReference<CampaignStatus> finalStatus = new AtomicReference<>();
        AtomicReference<String> failure = new AtomicReference<>();
        RecordEmailDeliveryPort recorder = new NoOpRecorder() {
            @Override
            public DeliveryProgress recordFailed(
                    UUID outboxId,
                    UUID invitationId,
                    UUID campaignId,
                    String error,
                    Instant failedAt) {
                failure.set(error);
                return new DeliveryProgress(0, 0, 1);
            }
        };
        ProcessPendingEmailsService service = serviceThatFails(
                email,
                recorder,
                (campaignId, status, finishedAt) -> finalStatus.set(status));

        service.processBatch(1);

        assertThat(failure.get()).contains("SMTP no disponible");
        assertThat(finalStatus.get()).isEqualTo(CampaignStatus.FAILED);
    }

    private ProcessPendingEmailsService serviceThatFails(
            ClaimQueuedEmailPort.QueuedEmailDelivery email,
            RecordEmailDeliveryPort recorder,
            FinishCampaignDeliveryPort finisher) {
        Queue<ClaimQueuedEmailPort.QueuedEmailDelivery> queue = new ArrayDeque<>();
        queue.add(email);
        return new ProcessPendingEmailsService(
                (now, staleBefore) -> Optional.ofNullable(queue.poll()),
                message -> {
                    throw new IllegalStateException("SMTP no disponible");
                },
                recorder,
                finisher == null ? (campaignId, status, finishedAt) -> {
                } : finisher,
                CLOCK);
    }

    private RecordEmailDeliveryPort successfulRecorder() {
        return new NoOpRecorder() {
            @Override
            public DeliveryProgress recordSent(
                    UUID outboxId,
                    UUID invitationId,
                    UUID campaignId,
                    String providerMessageId,
                    Instant sentAt) {
                return new DeliveryProgress(0, 1, 0);
            }
        };
    }

    private ClaimQueuedEmailPort.QueuedEmailDelivery queuedEmail(int attemptCount) {
        return new ClaimQueuedEmailPort.QueuedEmailDelivery(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "ana@empresa.com",
                "Ana Torres",
                "Benchmark",
                "Completa el benchmark y recibe tu reporte.",
                "Comenzar",
                UUID.randomUUID(),
                attemptCount);
    }

    private static class NoOpRecorder implements RecordEmailDeliveryPort {

        @Override
        public DeliveryProgress recordSent(
                UUID outboxId,
                UUID invitationId,
                UUID campaignId,
                String providerMessageId,
                Instant sentAt) {
            return new DeliveryProgress(1, 0, 0);
        }

        @Override
        public void reschedule(UUID outboxId, String error, Instant availableAt) {
        }

        @Override
        public DeliveryProgress recordFailed(
                UUID outboxId,
                UUID invitationId,
                UUID campaignId,
                String error,
                Instant failedAt) {
            return new DeliveryProgress(1, 0, 0);
        }
    }
}
