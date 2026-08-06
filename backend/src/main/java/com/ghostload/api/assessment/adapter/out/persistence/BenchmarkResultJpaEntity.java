package com.ghostload.api.assessment.adapter.out.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "benchmark_results")
public class BenchmarkResultJpaEntity {
    @Id @Column(name = "evaluation_id") private UUID evaluationId;
    @Column(name = "questionnaire_version", nullable = false, length = 20) private String questionnaireVersion;
    @Column(name = "total_score", nullable = false) private double totalScore;
    @Column(name = "maturity_level", nullable = false, length = 20) private String maturityLevel;
    @Column(nullable = false) private double percentile;
    @Column(name = "completed_at", nullable = false) private Instant completedAt;
    protected BenchmarkResultJpaEntity() {}
    public BenchmarkResultJpaEntity(UUID evaluationId, String questionnaireVersion, double totalScore,
                                    String maturityLevel, double percentile, Instant completedAt) {
        this.evaluationId = evaluationId; this.questionnaireVersion = questionnaireVersion; this.totalScore = totalScore;
        this.maturityLevel = maturityLevel; this.percentile = percentile; this.completedAt = completedAt;
    }
    public double getTotalScore() { return totalScore; }
    public String getMaturityLevel() { return maturityLevel; }
    public double getPercentile() { return percentile; }
    public Instant getCompletedAt() { return completedAt; }
}
