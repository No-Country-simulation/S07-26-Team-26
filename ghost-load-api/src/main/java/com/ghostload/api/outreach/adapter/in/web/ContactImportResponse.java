package com.ghostload.api.outreach.adapter.in.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ContactImportResponse(
        UUID importId,
        String name,
        String status,
        int totalRows,
        int validContacts,
        int duplicates,
        int invalidRows,
        List<ImportIssueResponse> issues,
        Instant createdAt) {

    public ContactImportResponse {
        issues = List.copyOf(issues);
    }
}
