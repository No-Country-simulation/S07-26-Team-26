package com.ghostload.api.assessment.adapter.out.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface BenchmarkModuleScoreJpaRepository extends JpaRepository<BenchmarkModuleScoreJpaEntity, UUID> {
    void deleteByEvaluationId(UUID evaluationId);

    @Query("""
            select m.moduleCode as module, avg(m.score) as average
              from BenchmarkModuleScoreJpaEntity m
             group by m.moduleCode
            """)
    List<ModuleAverageProjection> averageByModule();

    interface ModuleAverageProjection {
        String getModule();
        double getAverage();
    }
}
