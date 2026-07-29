package com.ghostload.api.assessment.domain.exception;

public class InvalidEvaluationTokenException extends RuntimeException {
    public InvalidEvaluationTokenException(String message) {
        super(message);
    }
}
