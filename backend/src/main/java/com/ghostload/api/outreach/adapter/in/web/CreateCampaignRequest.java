package com.ghostload.api.outreach.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record CreateCampaignRequest(
        @NotBlank
        @Size(min = 3, max = 160)
        String name,

        @Size(max = 500)
        String description,

        @NotBlank
        @Size(min = 3, max = 180)
        String subject,

        @NotBlank
        @Size(min = 10, max = 5_000)
        String message,

        @NotBlank
        @Size(min = 2, max = 80)
        String callToActionText,

        @NotNull
        UUID contactImportId,

        Instant scheduledAt,

        String timezone) {
}
