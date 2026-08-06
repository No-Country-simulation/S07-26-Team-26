package com.ghostload.api.outreach.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

public record ContactImportSummaryResponse(
        UUID importId,
        String name,
        String status,
        int totalRows,
        int validContacts,
        int duplicates,
        int invalidRows,
        Instant createdAt) {
}