package com.ghostload.api.assessment.application.port.out;

import com.ghostload.api.assessment.domain.model.Operator;

public interface SaveOperatorPort {

    void save(Operator operator);
}
