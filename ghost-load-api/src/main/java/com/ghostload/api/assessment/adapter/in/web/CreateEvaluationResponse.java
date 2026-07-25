package com.ghostload.api.assessment.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

public record CreateEvaluationResponse(
        UUID operatorId,
        UUID evaluationId,
        String evaluationToken,
        String state,
        Instant createdAt
) {}
