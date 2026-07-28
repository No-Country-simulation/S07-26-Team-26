package com.ghostload.api.outreach.application.port.in;

import java.time.Instant;
import java.util.UUID;

public record CreateCampaignCommand(
        String name,
        String description,
        String subject,
        String message,
        String callToActionText,
        UUID contactImportId,
        Instant scheduledAt,
        String timezone) {
}
