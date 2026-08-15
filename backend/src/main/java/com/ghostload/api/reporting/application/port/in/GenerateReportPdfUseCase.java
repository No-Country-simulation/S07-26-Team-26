package com.ghostload.api.reporting.application.port.in;

import java.util.UUID;

// Puerto de entrada de la generación del PDF: encola la generación cuando se
// completa el benchmark y procesa la cola desde el worker.
public interface GenerateReportPdfUseCase {

    // Encola el reporte de una evaluación (idempotente: no duplica registros).
    void queue(UUID evaluationId);

    // Procesa hasta maximumItems reportes pendientes. Devuelve cuántos se atendieron.
    int processBatch(int maximumItems);
}
