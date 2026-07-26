package com.ghostload.api.outreach.application.port.in;

import com.ghostload.api.outreach.domain.model.ContactImportStatus;
import com.ghostload.api.outreach.domain.model.ImportIssue;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ImportContactsResult(
        UUID importId,
        String name,
        ContactImportStatus status,
        int totalRows,
        int validContacts,
        int duplicates,
        int invalidRows,
        List<ImportIssue> issues,
        Instant createdAt) {

    public ImportContactsResult {
        issues = List.copyOf(issues);
    }
}
