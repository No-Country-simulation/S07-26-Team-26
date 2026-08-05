package com.ghostload.api.administration.application.port.in;

import com.ghostload.api.assessment.domain.model.EvaluationState;
import com.ghostload.api.assessment.domain.model.MaturityLevel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ListOperatorsQuery {

    OperatorPage list(ListOperatorsCommand command);

    record ListOperatorsCommand(int page, int size, String state, String search) {
    }

    record OperatorPage(List<OperatorListItem> items, long totalElements, int totalPages,
                        int page, int size) {
    }

    record OperatorListItem(
            UUID operatorId,
            String firstName,
            String lastName,
            String email,
            String companyName,
            String position,
            UUID evaluationId,
            EvaluationState state,
            Instant createdAt,
            Double totalScore,
            MaturityLevel maturityLevel,
            Instant completedAt) {
    }
}
