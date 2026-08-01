package com.ghostload.api.assessment.adapter.out.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;


// Único cambio: se agregó el campo evaluationToken.
@Entity
@Table(name = "evaluations")
public class EvaluationJpaEntity {

    @Id
    private UUID id;

    @Column(name = "operator_id", nullable = false)
    private UUID operatorId;

    @Column(nullable = false, length = 30)
    private String state;

    @Column(nullable = false, length = 20)
    private String source;

    @Column(name = "evaluation_token", length = 100)
    private String evaluationToken;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected EvaluationJpaEntity() {
    }

    public EvaluationJpaEntity(UUID id, UUID operatorId, String state, String source,
                                String evaluationToken, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.operatorId = operatorId;
        this.state = state;
        this.source = source;
        this.evaluationToken = evaluationToken;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public UUID getOperatorId() { return operatorId; }
    public String getState() { return state; }
    public String getSource() { return source; }
    public String getEvaluationToken() { return evaluationToken; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
