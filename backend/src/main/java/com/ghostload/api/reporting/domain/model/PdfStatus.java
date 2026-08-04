package com.ghostload.api.reporting.domain.model;

// Estados de generación del PDF institucional. El estado "no pedido" no
// existe en la base: si no hay fila en generated_pdfs, el reporte aún no
// fue solicitado (NOT_REQUESTED en la API).
public enum PdfStatus {
    PROCESSING,   // encolado, en cola de reintentos o siendo generado
    GENERATED,    // PDF generado, almacenado y (idealmente) enviado por email
    FAILED        // se agotaron los reintentos
}
