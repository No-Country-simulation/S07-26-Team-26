package com.ghostload.api.reporting.adapter.in.web;

import com.ghostload.api.reporting.application.port.in.DownloadReportPdfUseCase;
import com.ghostload.api.reporting.domain.model.PdfStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

// Driving adapter del módulo de reportes. Expone el estado, la descarga
// segura (valida X-Evaluation-Token en el service) y el reintento manual.
@RestController
@RequestMapping("/api/v1/evaluations/{evaluationId}/report")
public class PdfController {

    private final DownloadReportPdfUseCase downloadReportPdf;

    public PdfController(DownloadReportPdfUseCase downloadReportPdf) {
        this.downloadReportPdf = downloadReportPdf;
    }

    @GetMapping
    public ReportResponse status(
            @PathVariable UUID evaluationId,
            @RequestHeader("X-Evaluation-Token") String evaluationToken) {
        return toResponse(downloadReportPdf.status(evaluationId, evaluationToken));
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(
            @PathVariable UUID evaluationId,
            @RequestHeader("X-Evaluation-Token") String evaluationToken) {
        DownloadReportPdfUseCase.ReportPdfFile file =
                downloadReportPdf.download(evaluationId, evaluationToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.fileName() + "\"")
                .body(file.content());
    }

    @PostMapping("/retry")
    public ResponseEntity<ReportResponse> retry(
            @PathVariable UUID evaluationId,
            @RequestHeader("X-Evaluation-Token") String evaluationToken) {
        downloadReportPdf.retry(evaluationId, evaluationToken);
        ReportResponse response =
                toResponse(downloadReportPdf.status(evaluationId, evaluationToken));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    private ReportResponse toResponse(DownloadReportPdfUseCase.ReportStatus status) {
        return new ReportResponse(
                status.reportId(),
                toApiStatus(status.status()),
                status.fileName(),
                status.downloadUrl(),
                status.expiresAt(),
                status.generatedAt(),
                status.failureReason());
    }

    private ReportStatus toApiStatus(PdfStatus status) {
        if (status == null) {
            return ReportStatus.NOT_REQUESTED;
        }
        return switch (status) {
            case PROCESSING -> ReportStatus.REPORT_GENERATING;
            case GENERATED -> ReportStatus.REPORT_COMPLETED;
            case FAILED -> ReportStatus.REPORT_FAILED;
        };
    }
}
