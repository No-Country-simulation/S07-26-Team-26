package com.ghostload.api.assessment.domain.model;

import java.util.UUID;

public record BenchmarkAnswer(UUID questionId, int value) {
    public BenchmarkAnswer {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("Cada respuesta del benchmark debe tener un valor entre 1 y 5.");
        }
    }
}
