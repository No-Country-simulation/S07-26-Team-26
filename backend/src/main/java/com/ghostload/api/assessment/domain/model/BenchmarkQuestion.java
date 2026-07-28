package com.ghostload.api.assessment.domain.model;

import java.util.UUID;

public record BenchmarkQuestion(UUID id, String version, BenchmarkModule module, int questionOrder,
                                String text, boolean active) {
}
