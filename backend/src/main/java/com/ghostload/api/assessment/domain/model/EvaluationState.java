package com.ghostload.api.assessment.domain.model;

// Estos son los 6 estados que puede tener una evaluación, tal cual los definió
// el contrato OpenAPI (components/schemas/EvaluationState). Un enum de dominio
// puro no depende de nada de Spring ni de JPA.
public enum EvaluationState {
    STARTED,
    CALCULATOR_COMPLETED,
    BENCHMARK_COMPLETED,
    REPORT_GENERATING,
    REPORT_COMPLETED,
    REPORT_FAILED
}
