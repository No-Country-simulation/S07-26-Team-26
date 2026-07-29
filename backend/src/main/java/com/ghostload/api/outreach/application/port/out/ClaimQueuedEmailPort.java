package com.ghostload.api.outreach.application.port.out;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ClaimQueuedEmailPort {

    Optional<QueuedEmailDelivery> claim(Instant now, Instant staleBefore);

    record QueuedEmailDelivery(
            UUID id,
            UUID campaignId,
            UUID invitationId,
            String recipientEmail,
            String recipientName,
            String subject,
            String message,
            String callToActionText,
            UUID invitationToken,
            int attemptCount) {
    }
}
