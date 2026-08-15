package com.ghostload.api.outreach.application.port.in;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ListContactImportsUseCase {

    List<ContactImportSummary> listEligible();

    record ContactImportSummary(
            UUID importId,
            String name,
            int validContacts,
            Instant createdAt) {
    }
}
