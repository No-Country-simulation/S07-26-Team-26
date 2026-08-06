package com.ghostload.api.assessment.application.port.in;

import com.ghostload.api.assessment.domain.model.BenchmarkAnswer;

import java.util.List;
import java.util.UUID;

public interface SaveBenchmarkProgressUseCase {

    ProgressResult saveProgress(SaveBenchmarkProgressCommand command);

    record SaveBenchmarkProgressCommand(
            UUID evaluationId,
            String evaluationToken,
            String questionnaireVersion,
            List<BenchmarkAnswer> answers) {
    }

    record ProgressResult(int answeredCount, double completionPercentage) {
    }
}