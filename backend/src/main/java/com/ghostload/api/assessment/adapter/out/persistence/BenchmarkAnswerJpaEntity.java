package com.ghostload.api.assessment.adapter.out.persistence;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "benchmark_answers", uniqueConstraints = @UniqueConstraint(columnNames = {"evaluation_id", "question_id"}))
public class BenchmarkAnswerJpaEntity {
    @Id private UUID id;
    @Column(name = "evaluation_id", nullable = false) private UUID evaluationId;
    @Column(name = "question_id", nullable = false) private UUID questionId;
    @Column(nullable = false) private int value;
    protected BenchmarkAnswerJpaEntity() {}
    public BenchmarkAnswerJpaEntity(UUID evaluationId, UUID questionId, int value) {
        this.id = UUID.randomUUID(); this.evaluationId = evaluationId; this.questionId = questionId; this.value = value;
    }
}
