package com.ghostload.api.outreach.domain.exception;

public final class InvalidContactFileException extends RuntimeException {

    public InvalidContactFileException(String message) {
        super(message);
    }

    public InvalidContactFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
