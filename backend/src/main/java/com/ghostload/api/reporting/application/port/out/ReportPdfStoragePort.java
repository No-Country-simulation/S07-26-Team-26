package com.ghostload.api.reporting.application.port.out;

import java.util.UUID;

// Puerto de salida: guarda el PDF generado y permite recuperarlo después.
// La implementación actual es filesystem local; se puede reemplazar por S3
// (u otro bucket) sin tocar el resto del módulo.
public interface ReportPdfStoragePort {

    String store(UUID evaluationId, byte[] content);

    byte[] load(String storageKey);
}
