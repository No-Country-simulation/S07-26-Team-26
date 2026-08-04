package com.ghostload.api.reporting.application.port.in;

import com.ghostload.api.reporting.domain.model.PdfStatus;

import java.time.Instant;
import java.util.UUID;

// Puerto de entrada del lado del operador: consultar el estado del reporte,
// descargar el PDF y reintentar una generación fallida. Todos los métodos
// validan que el token X-Evaluation-Token corresponda a la evaluación.
public interface DownloadReportPdfUseCase {

    ReportStatus status(UUID evaluationId, String evaluationToken);

    ReportPdfFile download(UUID evaluationId, String evaluationToken);

    void retry(UUID evaluationId, String evaluationToken);

    record ReportStatus(
            UUID reportId,
            PdfStatus status,
            String fileName,
            String downloadUrl,
            Instant expiresAt,
            Instant generatedAt,
            String failureReason) {
    }

    record ReportPdfFile(String fileName, byte[] content) {
    }
}
