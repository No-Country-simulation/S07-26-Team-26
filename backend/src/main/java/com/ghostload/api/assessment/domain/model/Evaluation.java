package com.ghostload.api.assessment.domain.model;

import com.ghostload.api.assessment.domain.exception.InvalidEvaluationStateException;

import java.time.Instant;


// Cambios: se agregó el campo evaluationToken (para poder guardarlo y
// validarlo después) y el método markCalculatorCompleted(), que es la
// única forma permitida de avanzar el estado -- nadie desde afuera puede
// "forzar" un estado inválido.
public final class Evaluation {

    private final EvaluationId id;
    private final OperatorId operatorId;
    private EvaluationState state;
    private final EvaluationSource source;
    private final String evaluationToken;
    private final Instant createdAt;
    private Instant updatedAt;

    private Evaluation(EvaluationId id, OperatorId operatorId, EvaluationState state,
                        EvaluationSource source, String evaluationToken,
                        Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.operatorId = operatorId;
        this.state = state;
        this.source = source;
        this.evaluationToken = evaluationToken;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Evaluation start(OperatorId operatorId, EvaluationSource source, String evaluationToken) {
        Instant now = Instant.now();
        return new Evaluation(EvaluationId.newId(), operatorId, EvaluationState.STARTED,
                source, evaluationToken, now, now);
    }

    public static Evaluation reconstruct(EvaluationId id, OperatorId operatorId, EvaluationState state,
                                          EvaluationSource source, String evaluationToken,
                                          Instant createdAt, Instant updatedAt) {
        return new Evaluation(id, operatorId, state, source, evaluationToken, createdAt, updatedAt);
    }

    // Regla de negocio: solo se puede completar la calculadora si la
    // evaluación está en STARTED. Si ya se completó antes, o si está en
    // cualquier otro estado, se rechaza con un 409 (vía la excepción).
    public void markCalculatorCompleted() {
        if (this.state != EvaluationState.STARTED) {
            throw new InvalidEvaluationStateException(
                    "No se puede completar la calculadora: la evaluación está en estado " + this.state);
        }
        this.state = EvaluationState.CALCULATOR_COMPLETED;
        this.updatedAt = Instant.now();
    }

    public void markBenchmarkCompleted() {
        if (this.state != EvaluationState.CALCULATOR_COMPLETED) {
            throw new InvalidEvaluationStateException(
                    "No se puede completar el benchmark: la evaluación está en estado " + this.state);
        }
        this.state = EvaluationState.BENCHMARK_COMPLETED;
        this.updatedAt = Instant.now();
    }

    public EvaluationId id() { return id; }
    public OperatorId operatorId() { return operatorId; }
    public EvaluationState state() { return state; }
    public EvaluationSource source() { return source; }
    public String evaluationToken() { return evaluationToken; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
