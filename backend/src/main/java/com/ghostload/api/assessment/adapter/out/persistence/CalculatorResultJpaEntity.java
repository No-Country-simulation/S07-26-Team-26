package com.ghostload.api.assessment.adapter.out.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "calculator_results")
public class CalculatorResultJpaEntity {

    @Id
    @Column(name = "evaluation_id")
    private UUID evaluationId;

    @Column(name = "total_capacity_mw", nullable = false)
    private double totalCapacityMw;

    @Column(name = "productive_capacity_mw", nullable = false)
    private double productiveCapacityMw;

    @Column(name = "non_productive_capacity_mw", nullable = false)
    private double nonProductiveCapacityMw;

    @Column(name = "utilization_percentage", nullable = false)
    private double utilizationPercentage;

    @Column(name = "non_productive_percentage", nullable = false)
    private double nonProductivePercentage;

    @Column(name = "monthly_cost_per_kw", nullable = false)
    private double monthlyCostPerKw;

    @Column(name = "estimated_annual_cost", nullable = false)
    private double estimatedAnnualCost;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "calculated_at", nullable = false)
    private Instant calculatedAt;

    protected CalculatorResultJpaEntity() {
    }

    public CalculatorResultJpaEntity(UUID evaluationId, double totalCapacityMw, double productiveCapacityMw,
                                      double nonProductiveCapacityMw, double utilizationPercentage,
                                      double nonProductivePercentage, double monthlyCostPerKw,
                                      double estimatedAnnualCost, String currency, Instant calculatedAt) {
        this.evaluationId = evaluationId;
        this.totalCapacityMw = totalCapacityMw;
        this.productiveCapacityMw = productiveCapacityMw;
        this.nonProductiveCapacityMw = nonProductiveCapacityMw;
        this.utilizationPercentage = utilizationPercentage;
        this.nonProductivePercentage = nonProductivePercentage;
        this.monthlyCostPerKw = monthlyCostPerKw;
        this.estimatedAnnualCost = estimatedAnnualCost;
        this.currency = currency;
        this.calculatedAt = calculatedAt;
    }

    public UUID getEvaluationId() { return evaluationId; }
    public double getTotalCapacityMw() { return totalCapacityMw; }
    public double getProductiveCapacityMw() { return productiveCapacityMw; }
    public double getNonProductiveCapacityMw() { return nonProductiveCapacityMw; }
    public double getUtilizationPercentage() { return utilizationPercentage; }
    public double getNonProductivePercentage() { return nonProductivePercentage; }
    public double getMonthlyCostPerKw() { return monthlyCostPerKw; }
    public double getEstimatedAnnualCost() { return estimatedAnnualCost; }
    public String getCurrency() { return currency; }
    public Instant getCalculatedAt() { return calculatedAt; }
}
