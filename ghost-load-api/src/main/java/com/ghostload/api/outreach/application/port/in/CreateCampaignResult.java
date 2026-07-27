package com.ghostload.api.outreach.application.port.in;

import com.ghostload.api.outreach.domain.model.CampaignStatus;

import java.time.Instant;
import java.util.UUID;

public record CreateCampaignResult(
        UUID id,
        String name,
        CampaignStatus status,
        String subject,
        int recipientCount,
        Instant scheduledAt,
        Instant sentAt,
        Instant createdAt) {
}
