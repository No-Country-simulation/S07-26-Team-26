package com.ghostload.api.assessment.adapter.in.web;

import jakarta.validation.constraints.*;

public record CalculatorRequest(
        @NotNull @Positive @Max(100000)
        Double totalCapacityMw,

        @NotNull @PositiveOrZero @Max(100000)
        Double productiveCapacityMw,

        @NotNull @PositiveOrZero @Max(1000000)
        Double monthlyCostPerKw,

        @NotBlank @Pattern(regexp = "^[A-Z]{3}$")
        String currency
) {}
