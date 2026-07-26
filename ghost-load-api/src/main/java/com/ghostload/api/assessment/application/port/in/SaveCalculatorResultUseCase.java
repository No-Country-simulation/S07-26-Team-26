package com.ghostload.api.assessment.application.port.in;

import java.time.Instant;
import java.util.UUID;

public interface SaveCalculatorResultUseCase {

    SaveCalculatorResultResult save(SaveCalculatorResultCommand command);

    record SaveCalculatorResultCommand(
            UUID evaluationId,
            String evaluationToken, // viene del header X-Evaluation-Token
            double totalCapacityMw,
            double productiveCapacityMw,
            double monthlyCostPerKw,
            String currency
    ) {}

    record SaveCalculatorResultResult(
            double totalCapacityMw,
            double productiveCapacityMw,
            double nonProductiveCapacityMw,
            double utilizationPercentage,
            double nonProductivePercentage,
            double monthlyCostPerKw,
            double estimatedAnnualCost,
            String currency,
            Instant calculatedAt
    ) {}
}
