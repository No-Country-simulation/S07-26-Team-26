package com.ghostload.api.reporting.domain.model;

import java.time.Instant;
import java.util.UUID;

// Entidad de DOMINIO del módulo de reportes. NO conoce JPA ni Spring:
// modela el ciclo de vida de la generación del PDF (encolar -> generar ->
// almacenar -> enviar), con reintentos y trazabilidad del error.
public final class GeneratedPdf {

    private final UUID id;
    private final UUID evaluationId;
    private PdfStatus status;
    private String storageKey;
    private String downloadUrl;
    private String fileName;
    private int attemptCount;
    private String lastError;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant availableAt;
    private Instant claimedAt;
    private Instant generatedAt;

    private GeneratedPdf(UUID id, UUID evaluationId, PdfStatus status,
                         String storageKey, String downloadUrl, String fileName, int attemptCount,
                         String lastError, Instant createdAt, Instant updatedAt,
                         Instant availableAt, Instant claimedAt, Instant generatedAt) {
        this.id = id;
        this.evaluationId = evaluationId;
        this.status = status;
        this.storageKey = storageKey;
        this.downloadUrl = downloadUrl;
        this.fileName = fileName;
        this.attemptCount = attemptCount;
        this.lastError = lastError;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.availableAt = availableAt;
        this.claimedAt = claimedAt;
        this.generatedAt = generatedAt;
    }

    // Se pide la generación apenas se completa el benchmark.
    public static GeneratedPdf schedule(UUID evaluationId, Instant now) {
        return new GeneratedPdf(UUID.randomUUID(), evaluationId, PdfStatus.PROCESSING,
                null, null, null, 0, null, now, now, now, null, null);
    }

    // Para reconstruir un registro que ya existía en la base.
    public static GeneratedPdf reconstruct(UUID id, UUID evaluationId, PdfStatus status,
                                           String storageKey, String downloadUrl, String fileName, int attemptCount,
                                           String lastError, Instant createdAt, Instant updatedAt,
                                           Instant availableAt, Instant claimedAt, Instant generatedAt) {
        return new GeneratedPdf(id, evaluationId, status, storageKey, downloadUrl, fileName,
                attemptCount, lastError, createdAt, updatedAt, availableAt, claimedAt, generatedAt);
    }

    // El worker reclama el trabajo: cuenta un intento y marca el claim para
    // detectar procesos caídos (si nunca termina, otra ejecución lo recupera).
    public void claim(Instant now) {
        this.status = PdfStatus.PROCESSING;
        this.attemptCount++;
        this.claimedAt = now;
        this.updatedAt = now;
    }

    // Falló este intento pero aún quedan reintentos: se reprograma con backoff.
    public void reschedule(String error, Instant nextAvailableAt, Instant now) {
        this.status = PdfStatus.PROCESSING;
        this.lastError = error;
        this.availableAt = nextAvailableAt;
        this.claimedAt = null;
        this.updatedAt = now;
    }

    public void markGenerated(String fileName, String storageKey, String downloadUrl, Instant now) {
        this.status = PdfStatus.GENERATED;
        this.fileName = fileName;
        this.storageKey = storageKey;
        this.downloadUrl = downloadUrl;
        this.lastError = null;
        this.generatedAt = now;
        this.availableAt = now;
        this.claimedAt = null;
        this.updatedAt = now;
    }

    // Se agotaron los reintentos: el reporte queda en FAILED hasta reintento manual.
    public void markFailed(String error, Instant now) {
        this.status = PdfStatus.FAILED;
        this.lastError = error;
        this.claimedAt = null;
        this.updatedAt = now;
    }

    // Reintento manual desde la API (solo sobre un reporte FAILED).
    public void resetForRetry(Instant now) {
        this.status = PdfStatus.PROCESSING;
        this.storageKey = null;
        this.downloadUrl = null;
        this.fileName = null;
        this.lastError = null;
        this.generatedAt = null;
        this.attemptCount = 0;
        this.availableAt = now;
        this.claimedAt = null;
        this.updatedAt = now;
    }

    public UUID id() { return id; }
    public UUID evaluationId() { return evaluationId; }
    public PdfStatus status() { return status; }
    public String storageKey() { return storageKey; }
    public String downloadUrl() { return downloadUrl; }
    public String fileName() { return fileName; }
    public int attemptCount() { return attemptCount; }
    public String lastError() { return lastError; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public Instant availableAt() { return availableAt; }
    public Instant claimedAt() { return claimedAt; }
    public Instant generatedAt() { return generatedAt; }
}
