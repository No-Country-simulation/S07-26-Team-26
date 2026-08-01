package com.ghostload.api.assessment.domain.model;

import java.time.Instant;

// Acá viven las 4 fórmulas oficiales del contrato, en un solo lugar.
// Es un value object: se calcula una vez y no cambia (no tiene setters).
public record CalculatorResult(
        double totalCapacityMw,
        double productiveCapacityMw,
        double nonProductiveCapacityMw,
        double utilizationPercentage,
        double nonProductivePercentage,
        double monthlyCostPerKw,
        double estimatedAnnualCost,
        String currency,
        Instant calculatedAt
) {

    // "Factory method" que aplica las 4 fórmulas del contrato:
    // 1. Capacidad no productiva = total - productiva
    // 2. % utilización = (productiva / total) x 100
    // 3. % no productivo = (no productiva / total) x 100
    // 4. Costo anual estimado = no_productiva_MW x 1000 x costo_kw x 12
    public static CalculatorResult compute(double totalCapacityMw, double productiveCapacityMw,
                                            double monthlyCostPerKw, String currency) {
        if (totalCapacityMw <= 0) {
            throw new IllegalArgumentException("La capacidad total debe ser mayor a 0");
        }
        if (productiveCapacityMw > totalCapacityMw) {
            throw new IllegalArgumentException(
                    "La capacidad productiva no puede ser mayor que la capacidad total");
        }
        if (productiveCapacityMw < 0 || monthlyCostPerKw < 0) {
            throw new IllegalArgumentException("Los valores no pueden ser negativos");
        }

        double nonProductiveCapacityMw = totalCapacityMw - productiveCapacityMw;
        double utilizationPercentage = (productiveCapacityMw / totalCapacityMw) * 100;
        double nonProductivePercentage = (nonProductiveCapacityMw / totalCapacityMw) * 100;
        double estimatedAnnualCost = nonProductiveCapacityMw * 1000 * monthlyCostPerKw * 12;

        return new CalculatorResult(
                totalCapacityMw,
                productiveCapacityMw,
                nonProductiveCapacityMw,
                utilizationPercentage,
                nonProductivePercentage,
                monthlyCostPerKw,
                estimatedAnnualCost,
                currency,
                Instant.now()
        );
    }
}
