package com.ghostload.api.assessment.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CalculatorResultJpaRepository extends JpaRepository<CalculatorResultJpaEntity, UUID> {

    Optional<CalculatorResultJpaEntity> findById(UUID evaluationId);

    @Query("""
            select avg(c.utilizationPercentage) as averageUtilization,
                   sum(c.nonProductiveCapacityMw) as nonProductiveCapacityMw,
                   sum(c.estimatedAnnualCost) as estimatedAnnualCost
              from CalculatorResultJpaEntity c
            """)
    CalculatorAggregateProjection aggregate();

    interface CalculatorAggregateProjection {
        Double getAverageUtilization();
        Double getNonProductiveCapacityMw();
        Double getEstimatedAnnualCost();
    }
}
