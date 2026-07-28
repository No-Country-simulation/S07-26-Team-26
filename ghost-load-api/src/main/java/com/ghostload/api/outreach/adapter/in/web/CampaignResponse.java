package com.ghostload.api.outreach.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

public record CampaignResponse(
        UUID id,
        String name,
        String status,
        String subject,
        int recipientCount,
        Instant scheduledAt,
        Instant sentAt,
        Instant createdAt) {
}
