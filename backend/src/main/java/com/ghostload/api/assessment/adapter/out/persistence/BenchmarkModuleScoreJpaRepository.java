package com.ghostload.api.assessment.adapter.out.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface BenchmarkModuleScoreJpaRepository extends JpaRepository<BenchmarkModuleScoreJpaEntity, UUID> {
    void deleteByEvaluationId(UUID evaluationId);
}
