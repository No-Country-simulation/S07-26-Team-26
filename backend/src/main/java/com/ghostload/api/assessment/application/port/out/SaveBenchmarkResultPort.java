package com.ghostload.api.assessment.application.port.out;

import com.ghostload.api.assessment.domain.model.BenchmarkAnswer;
import com.ghostload.api.assessment.domain.model.BenchmarkResult;
import java.util.List;
import java.util.UUID;

public interface SaveBenchmarkResultPort {
    void save(UUID evaluationId, String questionnaireVersion, List<BenchmarkAnswer> answers, BenchmarkResult result);
}
