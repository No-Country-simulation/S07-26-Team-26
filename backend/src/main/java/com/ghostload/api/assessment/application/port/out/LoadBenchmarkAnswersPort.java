package com.ghostload.api.assessment.application.port.out;

import com.ghostload.api.assessment.domain.model.BenchmarkAnswer;

import java.util.List;
import java.util.UUID;

public interface LoadBenchmarkAnswersPort {

    List<BenchmarkAnswer> findAnswers(UUID evaluationId);
}