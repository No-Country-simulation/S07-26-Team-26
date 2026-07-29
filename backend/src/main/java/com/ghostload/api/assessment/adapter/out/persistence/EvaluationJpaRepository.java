package com.ghostload.api.assessment.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EvaluationJpaRepository extends JpaRepository<EvaluationJpaEntity, UUID> {
}
