package com.ghostload.api.reporting.adapter.out.persistence;

import com.ghostload.api.reporting.domain.model.PdfStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "generated_pdfs")
public class GeneratedPdfJpaEntity {

    @Id
    private UUID id;

    @Column(name = "evaluation_id", nullable = false, unique = true)
    private UUID evaluationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PdfStatus status;

    @Column(name = "storage_key", length = 500)
    private String storageKey;

    @Column(name = "download_url", length = 1000)
    private String downloadUrl;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "last_error", length = 1000)
    private String lastError;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "available_at", nullable = false)
    private Instant availableAt;

    @Column(name = "claimed_at")
    private Instant claimedAt;

    @Column(name = "generated_at")
    private Instant generatedAt;

    protected GeneratedPdfJpaEntity() {
    }

    GeneratedPdfJpaEntity(UUID id, UUID evaluationId, PdfStatus status,
                          String storageKey, String downloadUrl, String fileName,
                          int attemptCount, String lastError,
                          Instant createdAt, Instant updatedAt, Instant availableAt,
                          Instant claimedAt, Instant generatedAt) {
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

    // Marca el trabajo como reclamado por el worker (incrementa intentos).
    void claim(Instant now) {
        this.status = PdfStatus.PROCESSING;
        this.attemptCount++;
        this.claimedAt = now;
        this.updatedAt = now;
    }

    UUID id() {
        return id;
    }

    UUID evaluationId() {
        return evaluationId;
    }

    int attemptCount() {
        return attemptCount;
    }

    Instant createdAt() {
        return createdAt;
    }

    Instant updatedAt() {
        return updatedAt;
    }

    Instant availableAt() {
        return availableAt;
    }

    Instant claimedAt() {
        return claimedAt;
    }

    Instant generatedAt() {
        return generatedAt;
    }

    PdfStatus status() {
        return status;
    }

    String storageKey() {
        return storageKey;
    }

    String downloadUrl() {
        return downloadUrl;
    }

    String fileName() {
        return fileName;
    }

    String lastError() {
        return lastError;
    }
}
