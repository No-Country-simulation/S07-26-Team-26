package com.ghostload.api.reporting.application.port.out;

import com.ghostload.api.reporting.domain.model.GeneratedPdf;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

// Puerto de salida: persistencia del estado de generación del PDF.
public interface GeneratedPdfPersistencePort {

    void save(GeneratedPdf pdf);

    Optional<GeneratedPdf> findByEvaluationId(UUID evaluationId);

    // Reclama (con bloqueo) el siguiente trabajo pendiente, para que varios
    // workers no procesen el mismo reporte a la vez.
    Optional<PendingPdf> claim(Instant now, Instant staleBefore);

    record PendingPdf(UUID id, UUID evaluationId, int attemptCount) {
    }
}
