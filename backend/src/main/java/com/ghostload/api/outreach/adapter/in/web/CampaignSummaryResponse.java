package com.ghostload.api.outreach.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

public record CampaignSummaryResponse(
        UUID id,
        String name,
        String status,
        int recipientCount,
        Instant createdAt) {
}
