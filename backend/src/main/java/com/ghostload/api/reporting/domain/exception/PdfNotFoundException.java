package com.ghostload.api.reporting.domain.exception;

public class PdfNotFoundException extends RuntimeException {

    public PdfNotFoundException(String message) {
        super(message);
    }
}
