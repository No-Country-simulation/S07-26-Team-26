package com.ghostload.api.assessment.application.port.in;

import com.ghostload.api.assessment.domain.model.BenchmarkAnswer;
import com.ghostload.api.assessment.domain.model.BenchmarkResult;
import java.util.List;
import java.util.UUID;

public interface SubmitBenchmarkUseCase {
    BenchmarkResult submit(SubmitBenchmarkCommand command);

    record SubmitBenchmarkCommand(UUID evaluationId, String evaluationToken, String questionnaireVersion,
                                  List<BenchmarkAnswer> answers) {}
}
