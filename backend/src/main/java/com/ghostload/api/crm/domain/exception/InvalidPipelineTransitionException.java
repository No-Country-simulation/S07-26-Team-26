package com.ghostload.api.crm.domain.exception;

public class InvalidPipelineTransitionException extends RuntimeException {

    public InvalidPipelineTransitionException(String message) {
        super(message);
    }
}