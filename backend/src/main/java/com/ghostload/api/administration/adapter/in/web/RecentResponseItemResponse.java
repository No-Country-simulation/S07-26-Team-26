package com.ghostload.api.administration.adapter.in.web;

import com.ghostload.api.assessment.domain.model.MaturityLevel;

import java.time.Instant;
import java.util.UUID;

public record RecentResponseItemResponse(
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