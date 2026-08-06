package com.ghostload.api.assessment.application.port.out;

import com.ghostload.api.assessment.domain.model.Operator;
import com.ghostload.api.assessment.domain.model.OperatorId;

import java.util.Optional;

public interface LoadOperatorByIdPort {

    Optional<Operator> findById(OperatorId id);
}