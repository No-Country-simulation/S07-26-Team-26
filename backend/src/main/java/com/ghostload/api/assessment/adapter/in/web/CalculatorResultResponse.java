package com.ghostload.api.assessment.adapter.in.web;

import java.time.Instant;

public record CalculatorResultResponse(
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
