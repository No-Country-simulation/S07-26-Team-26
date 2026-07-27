package com.ghostload.api.outreach.application.port.out;

import com.ghostload.api.outreach.domain.model.Campaign;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface QueueCampaignEmailsPort {

    void queue(Campaign campaign, List<QueuedEmail> emails);

    record QueuedEmail(
            UUID id,
            UUID campaignId,
            UUID invitationId,
            String recipientEmail,
            String recipientName,
            String subject,
            String message,
            String callToActionText,
            UUID invitationToken,
            Instant availableAt,
            Instant createdAt) {
    }
}
