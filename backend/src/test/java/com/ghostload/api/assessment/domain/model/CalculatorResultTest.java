package com.ghostload.api.assessment.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculatorResultTest {

    @Test
    void computesAllFormulasForValidValues() {
        CalculatorResult result = CalculatorResult.compute(100.0, 70.0, 2.5, "USD");

        assertThat(result.totalCapacityMw()).isEqualTo(100.0);
        assertThat(result.productiveCapacityMw()).isEqualTo(70.0);
        assertThat(result.nonProductiveCapacityMw()).isEqualTo(30.0);
        assertThat(result.utilizationPercentage()).isCloseTo(70.0, within(0.0001));
        assertThat(result.nonProductivePercentage()).isCloseTo(30.0, within(0.0001));
        // 30 MW x 1000 kW/MW x $2.5/kW x 12 meses = $900.000
        assertThat(result.estimatedAnnualCost()).isCloseTo(900_000.0, within(0.0001));
        assertThat(result.currency()).isEqualTo("USD");
        assertThat(result.calculatedAt()).isNotNull();
    }

    @Test
    void zeroProductiveCapacityYieldsZeroCost() {
        CalculatorResult result = CalculatorResult.compute(100.0, 0.0, 5.0, "USD");

        assertThat(result.nonProductiveCapacityMw()).isEqualTo(100.0);
        assertThat(result.utilizationPercentage()).isCloseTo(0.0, within(0.0001));
        assertThat(result.estimatedAnnualCost()).isCloseTo(100.0 * 1000 * 5.0 * 12, within(0.0001));
    }

    @Test
    void fullProductiveCapacityYieldsZeroNonProductive() {
        CalculatorResult result = CalculatorResult.compute(100.0, 100.0, 5.0, "USD");

        assertThat(result.nonProductiveCapacityMw()).isEqualTo(0.0);
        assertThat(result.utilizationPercentage()).isCloseTo(100.0, within(0.0001));
        assertThat(result.estimatedAnnualCost()).isCloseTo(0.0, within(0.0001));
    }

    @Test
    void rejectsTotalCapacityZero() {
        assertThatThrownBy(() -> CalculatorResult.compute(0.0, 0.0, 1.0, "USD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("capacidad total debe ser mayor a 0");
    }

    @Test
    void rejectsProductiveGreaterThanTotal() {
        assertThatThrownBy(() -> CalculatorResult.compute(50.0, 80.0, 1.0, "USD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("productiva no puede ser mayor");
    }

    @Test
    void rejectsNegativeValues() {
        assertThatThrownBy(() -> CalculatorResult.compute(100.0, -5.0, 1.0, "USD"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> CalculatorResult.compute(100.0, 50.0, -1.0, "USD"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}