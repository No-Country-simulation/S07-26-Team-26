package com.ghostload.api.assessment.application.port.out;

import com.ghostload.api.assessment.domain.model.CalculatorResult;
import com.ghostload.api.assessment.domain.model.EvaluationId;

public interface SaveCalculatorResultPort {
    void save(EvaluationId evaluationId, CalculatorResult result);
}
