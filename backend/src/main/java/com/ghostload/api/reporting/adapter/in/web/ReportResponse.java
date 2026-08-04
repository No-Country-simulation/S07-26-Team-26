package com.ghostload.api.reporting.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

// Respuesta del estado del reporte (openapi.yaml -> ReportResponse).
public record ReportResponse(
        UUID reportId,
        ReportStatus status,
        String fileName,
        String downloadUrl,
        Instant expiresAt,
        Instant generatedAt,
        String failureReason) {
}
