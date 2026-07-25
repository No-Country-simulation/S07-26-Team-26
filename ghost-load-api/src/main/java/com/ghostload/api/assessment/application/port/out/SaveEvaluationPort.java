package com.ghostload.api.assessment.application.port.out;

import com.ghostload.api.assessment.domain.model.Evaluation;

public interface SaveEvaluationPort {

    void save(Evaluation evaluation);
}
