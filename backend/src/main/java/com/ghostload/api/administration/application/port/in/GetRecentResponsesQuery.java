package com.ghostload.api.administration.application.port.in;

import com.ghostload.api.assessment.domain.model.MaturityLevel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface GetRecentResponsesQuery {

    RecentResponsesPage list(RecentResponsesCommand command);

    record RecentResponsesCommand(int page, int size, Instant from, Instant to) {
    }

    record RecentResponsesPage(List<RecentResponsesItem> items, long totalElements, int totalPages,
                               int page, int size) {
    }

    record RecentResponsesItem(
            UUID operatorId,
            String fullName,
            String email,
            String companyName,
            UUID evaluationId,
            double score,
            double percentile,
            MaturityLevel maturityLevel,
            Instant completedAt) {
    }
}
