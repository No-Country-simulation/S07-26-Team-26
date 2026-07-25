package com.ghostload.api.assessment.adapter.out.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "evaluations")
public class EvaluationJpaEntity {

    @Id
    private UUID id;

    @Column(name = "operator_id", nullable = false)
    private UUID operatorId;

    // Guardamos el estado como String simple (ya viene convertido con .name()
    // desde el adaptador). @Enumerated es solo para cuando el campo Java ES
    // un enum -- acá no lo es a propósito, por eso no lleva esa anotación.
    @Column(nullable = false, length = 30)
    private String state;

    @Column(nullable = false, length = 20)
    private String source;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected EvaluationJpaEntity() {
    }

    public EvaluationJpaEntity(UUID id, UUID operatorId, String state, String source,
                               Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.operatorId = operatorId;
        this.state = state;
        this.source = source;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public UUID getOperatorId() { return operatorId; }
    public String getState() { return state; }
    public String getSource() { return source; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}