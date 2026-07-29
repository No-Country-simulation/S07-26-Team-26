package com.ghostload.api.assessment.domain.exception;

// El openapi.yaml pide un 409 cuando "la evaluación no permite esta transición"
// (ej: querer completar la calculadora dos veces). Una excepción propia hace
// que esa regla de negocio sea explícita y fácil de traducir a HTTP después.
public class InvalidEvaluationStateException extends RuntimeException {
    public InvalidEvaluationStateException(String message) {
        super(message);
    }
}
