package com.ghostload.api.assessment.domain.model;

import java.time.Instant;

// La otra entidad central de tu módulo. Controla su propia máquina de estados:
// nadie de afuera puede poner un Evaluation en cualquier estado porque sí,
// solo a través de los métodos que ella misma expone (ver advanceTo... más abajo,
// que agregaremos cuando trabajemos calculadora y benchmark).
public final class Evaluation {

    private final EvaluationId id;
    private final OperatorId operatorId;
    private EvaluationState state;
    private final EvaluationSource source;
    private final Instant createdAt;
    private Instant updatedAt;

    private Evaluation(EvaluationId id, OperatorId operatorId, EvaluationState state,
                        EvaluationSource source, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.operatorId = operatorId;
        this.state = state;
        this.source = source;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Toda evaluación nueva arranca en STARTED, según el contrato del MVP.
    public static Evaluation start(OperatorId operatorId, EvaluationSource source) {
        Instant now = Instant.now();
        return new Evaluation(EvaluationId.newId(), operatorId, EvaluationState.STARTED, source, now, now);
    }

    public static Evaluation reconstruct(EvaluationId id, OperatorId operatorId, EvaluationState state,
                                          EvaluationSource source, Instant createdAt, Instant updatedAt) {
        return new Evaluation(id, operatorId, state, source, createdAt, updatedAt);
    }

    public EvaluationId id() { return id; }
    public OperatorId operatorId() { return operatorId; }
    public EvaluationState state() { return state; }
    public EvaluationSource source() { return source; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
