package com.ghostload.api.outreach.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

public record ContactImportSummaryResponse(
        UUID importId,
        String name,
        int validContacts,
        Instant createdAt) {
}
