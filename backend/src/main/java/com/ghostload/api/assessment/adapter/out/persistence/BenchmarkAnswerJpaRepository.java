package com.ghostload.api.assessment.adapter.out.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.UUID;
public interface BenchmarkAnswerJpaRepository extends JpaRepository<BenchmarkAnswerJpaEntity, UUID> {
    void deleteByEvaluationId(UUID evaluationId);
}
