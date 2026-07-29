package com.ghostload.api.assessment.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CalculatorResultJpaRepository extends JpaRepository<CalculatorResultJpaEntity, UUID> {
}
