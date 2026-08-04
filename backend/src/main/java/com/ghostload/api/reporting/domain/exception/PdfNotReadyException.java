package com.ghostload.api.reporting.domain.exception;

import com.ghostload.api.reporting.domain.model.PdfStatus;

// El reporte todavía no se puede descargar: está en proceso o falló.
// La API lo expone como 409 (REPORT_PROCESSING o REPORT_FAILED).
public class PdfNotReadyException extends RuntimeException {

    private final PdfStatus status;

    public PdfNotReadyException(PdfStatus status, String message) {
        super(message);
        this.status = status;
    }

    public PdfStatus status() {
        return status;
    }
}
