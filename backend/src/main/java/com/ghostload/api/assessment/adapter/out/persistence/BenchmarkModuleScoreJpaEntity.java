package com.ghostload.api.assessment.adapter.out.persistence;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "benchmark_module_scores", uniqueConstraints = @UniqueConstraint(columnNames = {"evaluation_id", "module_code"}))
public class BenchmarkModuleScoreJpaEntity {
    @Id private UUID id;
    @Column(name = "evaluation_id", nullable = false) private UUID evaluationId;
    @Column(name = "module_code", nullable = false, length = 40) private String moduleCode;
    @Column(nullable = false) private double score;
    protected BenchmarkModuleScoreJpaEntity() {}
    public BenchmarkModuleScoreJpaEntity(UUID evaluationId, String moduleCode, double score) {
        this.id = UUID.randomUUID(); this.evaluationId = evaluationId; this.moduleCode = moduleCode; this.score = score;
    }
    public String getModuleCode() { return moduleCode; }
    public double getScore() { return score; }
}
