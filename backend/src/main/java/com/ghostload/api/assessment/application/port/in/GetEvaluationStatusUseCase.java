package com.ghostload.api.assessment.application.port.in;

import com.ghostload.api.assessment.domain.model.BenchmarkAnswer;
import com.ghostload.api.assessment.domain.model.BenchmarkResult;
import com.ghostload.api.assessment.domain.model.CalculatorResult;

import java.util.List;
import java.util.UUID;

public interface GetEvaluationStatusUseCase {

    EvaluationStatus getStatus(GetEvaluationStatusCommand command);

    record GetEvaluationStatusCommand(UUID evaluationId, String evaluationToken) {
    }

    record EvaluationStatus(
            UUID evaluationId,
            String operatorId,
            String firstName,
            String lastName,
            String email,
            String companyName,
            String position,
            String state,
            CalculatorResult calculatorResult,
            List<BenchmarkAnswer> answers,
            int answeredCount,
            double completionPercentage,
            BenchmarkResult benchmarkResult) {
    }
}