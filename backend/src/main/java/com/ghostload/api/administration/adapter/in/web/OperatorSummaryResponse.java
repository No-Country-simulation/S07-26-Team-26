package com.ghostload.api.administration.adapter.in.web;

import com.ghostload.api.assessment.domain.model.EvaluationState;
import com.ghostload.api.assessment.domain.model.MaturityLevel;

import java.time.Instant;
import java.util.UUID;

public record OperatorSummaryResponse(
        UUID operatorId,
        String fullName,
        String email,
        String companyName,
        UUID evaluationId,
        EvaluationState state,
        Double benchmarkScore,
        MaturityLevel maturityLevel,
        Instant completedAt,
        Instant createdAt) {
}
