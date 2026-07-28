package com.ghostload.api.assessment.domain.model;

import java.util.UUID;

public record EvaluationId(UUID value) {

    public static EvaluationId newId() {
        return new EvaluationId(UUID.randomUUID());
    }

    public static EvaluationId of(UUID value) {
        return new EvaluationId(value);
    }
}
