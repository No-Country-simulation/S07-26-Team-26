package com.ghostload.api.assessment.application.port.out;

import com.ghostload.api.assessment.domain.model.Evaluation;
import com.ghostload.api.assessment.domain.model.EvaluationId;

import java.util.Optional;

public interface LoadEvaluationPort {
    Optional<Evaluation> findById(EvaluationId id);
}
