package com.ghostload.api.assessment.adapter.out.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface BenchmarkModuleScoreJpaRepository extends JpaRepository<BenchmarkModuleScoreJpaEntity, UUID> {
    List<BenchmarkModuleScoreJpaEntity> findByEvaluationId(UUID evaluationId);
    void deleteByEvaluationId(UUID evaluationId);
}
