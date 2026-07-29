package com.ghostload.api.outreach.application.port.out;

import java.time.Instant;
import java.util.UUID;

public interface RecordEmailDeliveryPort {

    DeliveryProgress recordSent(
            UUID outboxId,
            UUID invitationId,
            UUID campaignId,
            String providerMessageId,
            Instant sentAt);

    void reschedule(
            UUID outboxId,
            String error,
            Instant availableAt);

    DeliveryProgress recordFailed(
            UUID outboxId,
            UUID invitationId,
            UUID campaignId,
            String error,
            Instant failedAt);

    record DeliveryProgress(
            long unfinished,
            long sent,
            long failed) {

        public boolean finished() {
            return unfinished == 0;
        }
    }
}
