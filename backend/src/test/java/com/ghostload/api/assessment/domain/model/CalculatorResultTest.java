package com.ghostload.api.assessment.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculatorResultTest {

    @Test
    void shouldCalculateCapacityAndAnnualCost() {
        CalculatorResult result = CalculatorResult.compute(
                10,
                7.5,
                20,
                "USD");

        assertThat(result.nonProductiveCapacityMw()).isEqualTo(2.5);
        assertThat(result.utilizationPercentage()).isEqualTo(75);
        assertThat(result.nonProductivePercentage()).isEqualTo(25);
        assertThat(result.estimatedAnnualCost()).isEqualTo(600_000);
    }

    @Test
    void shouldRejectProductiveCapacityAboveTotalCapacity() {
        assertThatThrownBy(() -> CalculatorResult.compute(
                5,
                6,
                20,
                "USD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("mayor");
    }
}
