package com.ghostload.api.reporting.application.service;

import com.ghostload.api.reporting.application.port.in.GenerateReportPdfUseCase;
import com.ghostload.api.reporting.application.port.out.GeneratedPdfPersistencePort;
import com.ghostload.api.reporting.application.port.out.LoadReportDataPort;
import com.ghostload.api.reporting.application.port.out.RenderReportPdfPort;
import com.ghostload.api.reporting.application.port.out.ReportPdfStoragePort;
import com.ghostload.api.reporting.application.port.out.SendReportEmailPort;
import com.ghostload.api.reporting.configuration.PdfProperties;
import com.ghostload.api.reporting.domain.exception.PdfNotFoundException;
import com.ghostload.api.reporting.domain.model.GeneratedPdf;
import com.ghostload.api.reporting.domain.model.ReportData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

// Caso de uso de generación del PDF. Corre dos veces: (1) al completar el
// benchmark encola la generación (queue) y (2) el worker procesa la cola
// (processBatch), con reintentos y backoff ante errores de generación o envío.
public final class GenerateReportPdfService implements GenerateReportPdfUseCase {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GenerateReportPdfService.class);

    private static final int MAXIMUM_ATTEMPTS = 3;
    private static final Duration CLAIM_TIMEOUT = Duration.ofMinutes(10);

    private final GeneratedPdfPersistencePort persistence;
    private final LoadReportDataPort reportData;
    private final RenderReportPdfPort renderer;
    private final ReportPdfStoragePort storage;
    private final SendReportEmailPort emailSender;
    private final Clock clock;
    private final PdfProperties properties;

    public GenerateReportPdfService(GeneratedPdfPersistencePort persistence,
                                    LoadReportDataPort reportData,
                                    RenderReportPdfPort renderer,
                                    ReportPdfStoragePort storage,
                                    SendReportEmailPort emailSender,
                                    Clock clock,
                                    PdfProperties properties) {
        this.persistence = persistence;
        this.reportData = reportData;
        this.renderer = renderer;
        this.storage = storage;
        this.emailSender = emailSender;
        this.clock = clock;
        this.properties = properties;
    }

    @Override
    public void queue(UUID evaluationId) {
        if (persistence.findByEvaluationId(evaluationId).isPresent()) {
            return; // idempotente: el reporte ya está pedido
        }
        persistence.save(GeneratedPdf.schedule(evaluationId, clock.instant()));
    }

    @Override
    public int processBatch(int maximumItems) {
        if (maximumItems <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad máxima de reportes debe ser mayor que cero.");
        }
        int processed = 0;
        while (processed < maximumItems) {
            Instant now = clock.instant();
            GeneratedPdfPersistencePort.PendingPdf pending =
                    persistence.claim(now, now.minus(CLAIM_TIMEOUT)).orElse(null);
            if (pending == null) {
                break;
            }
            generate(pending);
            processed++;
        }
        return processed;
    }

    private void generate(GeneratedPdfPersistencePort.PendingPdf pending) {
        Instant now = clock.instant();
        GeneratedPdf pdf = persistence.findByEvaluationId(pending.evaluationId())
                .orElseThrow(() -> new PdfNotFoundException(
                        "No existe el reporte de la evaluación " + pending.evaluationId() + "."));
        try {
            ReportData data = reportData.load(pending.evaluationId());
            byte[] content = renderer.render(data);
            String storageKey = storage.store(pending.evaluationId(), content);
            pdf.markGenerated(
                    fileNameFor(data),
                    storageKey,
                    downloadUrlFor(pending.evaluationId()),
                    clock.instant());
            persistence.save(pdf);
            emailSender.send(new SendReportEmailPort.ReportEmail(
                    data.operator().email(),
                    data.operator().fullName(),
                    "Tu Reporte de Eficiencia de Data Center - Ghost Load",
                    "Completamos el análisis de capacidad de " + data.operator().companyName()
                            + ". Adjuntamos tu reporte institucional con el resultado del benchmark.",
                    content,
                    pdf.fileName()));
            LOGGER.info("Reporte generado para la evaluación {}.", pending.evaluationId());
        } catch (RuntimeException exception) {
            handleFailure(pdf, exception, now);
        }
    }

    private void handleFailure(GeneratedPdf pdf, RuntimeException exception, Instant now) {
        String error = normalizeError(exception);
        if (pdf.attemptCount() < MAXIMUM_ATTEMPTS) {
            pdf.reschedule(error,
                    now.plus(Duration.ofMinutes(pdf.attemptCount())), now);
            LOGGER.warn("La generación del reporte {} falló y se reprogramará. intentos={}, error={}",
                    pdf.evaluationId(), pdf.attemptCount(), error);
        } else {
            pdf.markFailed(error, now);
            LOGGER.error("La generación del reporte {} agotó los reintentos. error={}",
                    pdf.evaluationId(), error);
        }
        persistence.save(pdf);
    }

    private String fileNameFor(ReportData data) {
        String company = data.operator().companyName() == null || data.operator().companyName().isBlank()
                ? "Empresa"
                : data.operator().companyName();
        return company.replaceAll("[^a-zA-Z0-9_-]+", "_") + "_Maturity_Report.pdf";
    }

    private String downloadUrlFor(UUID evaluationId) {
        String base = properties.downloadBaseUrl();
        if (base == null || base.isBlank()) {
            return null;
        }
        return base.replaceAll("/+$", "")
                + "/api/v1/evaluations/" + evaluationId + "/report/download";
    }

    private String normalizeError(RuntimeException exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            message = exception.getClass().getSimpleName();
        }
        return message.length() <= 1_000 ? message : message.substring(0, 1_000);
    }
}
