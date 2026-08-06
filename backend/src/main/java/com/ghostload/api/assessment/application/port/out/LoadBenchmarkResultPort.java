package com.ghostload.api.assessment.application.port.out;

import com.ghostload.api.assessment.domain.model.BenchmarkResult;

import java.util.Optional;
import java.util.UUID;

public interface LoadBenchmarkResultPort {

    Optional<BenchmarkResult> findByEvaluationId(UUID evaluationId);
}