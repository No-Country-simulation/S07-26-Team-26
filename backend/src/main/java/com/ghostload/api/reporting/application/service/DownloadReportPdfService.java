package com.ghostload.api.reporting.application.service;

import com.ghostload.api.assessment.application.port.out.LoadEvaluationPort;
import com.ghostload.api.assessment.domain.exception.InvalidEvaluationTokenException;
import com.ghostload.api.assessment.domain.model.Evaluation;
import com.ghostload.api.assessment.domain.model.EvaluationId;
import com.ghostload.api.reporting.application.port.in.DownloadReportPdfUseCase;
import com.ghostload.api.reporting.application.port.out.GeneratedPdfPersistencePort;
import com.ghostload.api.reporting.application.port.out.ReportPdfStoragePort;
import com.ghostload.api.reporting.domain.exception.PdfNotFoundException;
import com.ghostload.api.reporting.domain.exception.PdfNotReadyException;
import com.ghostload.api.reporting.domain.model.GeneratedPdf;
import com.ghostload.api.reporting.domain.model.PdfStatus;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

// Caso de uso del lado del operador: estado, descarga y reintento del reporte.
// Valida el token de la evaluación en cada operación (misma regla que la
// calculadora y el benchmark).
public final class DownloadReportPdfService implements DownloadReportPdfUseCase {

    private final LoadEvaluationPort evaluations;
    private final GeneratedPdfPersistencePort persistence;
    private final ReportPdfStoragePort storage;
    private final Clock clock;

    public DownloadReportPdfService(LoadEvaluationPort evaluations,
                                    GeneratedPdfPersistencePort persistence,
                                    ReportPdfStoragePort storage,
                                    Clock clock) {
        this.evaluations = evaluations;
        this.persistence = persistence;
        this.storage = storage;
        this.clock = clock;
    }

    @Override
    public ReportStatus status(UUID evaluationId, String evaluationToken) {
        validateAccess(evaluationId, evaluationToken);
        Optional<GeneratedPdf> pdf = persistence.findByEvaluationId(evaluationId);
        if (pdf.isEmpty()) {
            return new ReportStatus(null, null, null, null, null, null, null);
        }
        GeneratedPdf record = pdf.get();
        return new ReportStatus(
                record.id(),
                record.status(),
                record.fileName(),
                record.downloadUrl(),
                null,
                record.generatedAt(),
                record.lastError());
    }

    @Override
    public ReportPdfFile download(UUID evaluationId, String evaluationToken) {
        validateAccess(evaluationId, evaluationToken);
        GeneratedPdf pdf = persistence.findByEvaluationId(evaluationId)
                .orElseThrow(() -> new PdfNotFoundException(
                        "El reporte de esta evaluación aún no fue generado."));
        if (pdf.status() == PdfStatus.PROCESSING) {
            throw new PdfNotReadyException(PdfStatus.PROCESSING,
                    "El reporte se está generando; inténtalo de nuevo en unos minutos.");
        }
        if (pdf.status() == PdfStatus.FAILED) {
            throw new PdfNotReadyException(PdfStatus.FAILED,
                    "El reporte no pudo generarse: " + pdf.lastError());
        }
        byte[] content = storage.load(pdf.storageKey());
        String fileName = pdf.fileName() == null ? "Maturity_Report.pdf" : pdf.fileName();
        return new ReportPdfFile(fileName, content);
    }

    @Override
    public void retry(UUID evaluationId, String evaluationToken) {
        validateAccess(evaluationId, evaluationToken);
        GeneratedPdf pdf = persistence.findByEvaluationId(evaluationId)
                .orElseThrow(() -> new PdfNotReadyException(null,
                        "El reporte no existe o no está en estado fallido."));
        if (pdf.status() != PdfStatus.FAILED) {
            throw new PdfNotReadyException(pdf.status(),
                    "El reporte no está en estado fallido; no es necesario reintentarlo.");
        }
        pdf.resetForRetry(clock.instant());
        persistence.save(pdf);
    }

    private void validateAccess(UUID evaluationId, String evaluationToken) {
        Evaluation evaluation = evaluations.findById(EvaluationId.of(evaluationId))
                .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada."));
        if (!evaluation.evaluationToken().equals(evaluationToken)) {
            throw new InvalidEvaluationTokenException("El token de evaluación no es válido.");
        }
    }
}
