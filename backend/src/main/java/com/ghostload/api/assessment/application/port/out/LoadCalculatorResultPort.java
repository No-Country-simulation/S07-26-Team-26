package com.ghostload.api.assessment.application.port.out;

import com.ghostload.api.assessment.domain.model.CalculatorResult;
import com.ghostload.api.assessment.domain.model.EvaluationId;

import java.util.Optional;

public interface LoadCalculatorResultPort {

    Optional<CalculatorResult> findByEvaluationId(EvaluationId evaluationId);
}