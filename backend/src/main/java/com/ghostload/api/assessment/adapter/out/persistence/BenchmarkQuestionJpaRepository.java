package com.ghostload.api.assessment.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface BenchmarkQuestionJpaRepository extends JpaRepository<BenchmarkQuestionJpaEntity, UUID> {
    List<BenchmarkQuestionJpaEntity> findByVersionAndActiveTrueOrderByQuestionOrder(String version);
}
