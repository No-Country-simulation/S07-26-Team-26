package com.ghostload.api.reporting.application.service;

import com.ghostload.api.assessment.application.port.out.LoadEvaluationPort;
import com.ghostload.api.assessment.domain.exception.InvalidEvaluationTokenException;
import com.ghostload.api.assessment.domain.model.Evaluation;
import com.ghostload.api.assessment.domain.model.EvaluationSource;
import com.ghostload.api.assessment.domain.model.OperatorId;
import com.ghostload.api.reporting.application.port.out.GeneratedPdfPersistencePort;
import com.ghostload.api.reporting.application.port.out.ReportPdfStoragePort;
import com.ghostload.api.reporting.domain.exception.PdfNotFoundException;
import com.ghostload.api.reporting.domain.exception.PdfNotReadyException;
import com.ghostload.api.reporting.domain.model.GeneratedPdf;
import com.ghostload.api.reporting.domain.model.PdfStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DownloadReportPdfServiceTest {

    private static final Instant NOW = Instant.parse("2026-07-31T14:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final String TOKEN = "evaluation-secret";

    private final Evaluation evaluation = Evaluation.start(
            OperatorId.newId(), EvaluationSource.OUTREACH, TOKEN);

    @Test
    void shouldReturnEmptyStatusWhenNoReportExists() {
        DownloadReportPdfService service = new DownloadReportPdfService(
                id -> Optional.of(evaluation),
                persistence(Optional.empty()),
                storage(),
                CLOCK);

        var status = service.status(evaluation.id().value(), TOKEN);

        assertThat(status.reportId()).isNull();
        assertThat(status.status()).isNull();
        assertThat(status.fileName()).isNull();
    }

    @Test
    void shouldReturnStatusWhenReportExists() {
        UUID reportId = UUID.randomUUID();
        GeneratedPdf pdf = GeneratedPdf.reconstruct(
                reportId, evaluation.id().value(), PdfStatus.GENERATED,
                "reporte.pdf", "https://api.ghostload.com/api/v1/evaluations/x/report/download",
                "Northstar_Maturity_Report.pdf", 1, null,
                NOW, NOW, NOW, null, NOW);
        DownloadReportPdfService service = new DownloadReportPdfService(
                id -> Optional.of(evaluation),
                persistence(Optional.of(pdf)),
                storage(),
                CLOCK);

        var status = service.status(evaluation.id().value(), TOKEN);

        assertThat(status.reportId()).isEqualTo(reportId);
        assertThat(status.status()).isEqualTo(PdfStatus.GENERATED);
        assertThat(status.fileName()).isEqualTo("Northstar_Maturity_Report.pdf");
        assertThat(status.generatedAt()).isEqualTo(NOW);
    }

    @Test
    void shouldDownloadPdfWhenGenerated() {
        byte[] content = new byte[]{5, 6, 7, 8};
        GeneratedPdf pdf = generatedPdf();
        DownloadReportPdfService service = new DownloadReportPdfService(
                id -> Optional.of(evaluation),
                persistence(Optional.of(pdf)),
                storage(content),
                CLOCK);

        var file = service.download(evaluation.id().value(), TOKEN);

        assertThat(file.fileName()).isEqualTo("Northstar_Maturity_Report.pdf");
        assertThat(file.content()).containsExactly(content);
    }

    @Test
    void shouldRejectDownloadWhileProcessing() {
        GeneratedPdf pdf = GeneratedPdf.reconstruct(
                UUID.randomUUID(), evaluation.id().value(), PdfStatus.PROCESSING,
                null, null, null, 1, null,
                NOW, NOW, NOW, NOW, null);
        DownloadReportPdfService service = new DownloadReportPdfService(
                id -> Optional.of(evaluation),
                persistence(Optional.of(pdf)),
                storage(),
                CLOCK);

        assertThatThrownBy(() -> service.download(evaluation.id().value(), TOKEN))
                .isInstanceOf(PdfNotReadyException.class);
    }

    @Test
    void shouldRejectDownloadWhenFailed() {
        GeneratedPdf pdf = GeneratedPdf.reconstruct(
                UUID.randomUUID(), evaluation.id().value(), PdfStatus.FAILED,
                null, null, null, 3, "Falla de renderizado",
                NOW, NOW, NOW, null, null);
        DownloadReportPdfService service = new DownloadReportPdfService(
                id -> Optional.of(evaluation),
                persistence(Optional.of(pdf)),
                storage(),
                CLOCK);

        assertThatThrownBy(() -> service.download(evaluation.id().value(), TOKEN))
                .isInstanceOf(PdfNotReadyException.class);
    }

    @Test
    void shouldRejectDownloadWhenNoReportExists() {
        DownloadReportPdfService service = new DownloadReportPdfService(
                id -> Optional.of(evaluation),
                persistence(Optional.empty()),
                storage(),
                CLOCK);

        assertThatThrownBy(() -> service.download(evaluation.id().value(), TOKEN))
                .isInstanceOf(PdfNotFoundException.class);
    }

    @Test
    void shouldRejectInvalidToken() {
        DownloadReportPdfService service = new DownloadReportPdfService(
                id -> Optional.of(evaluation),
                persistence(Optional.of(generatedPdf())),
                storage(),
                CLOCK);

        assertThatThrownBy(() -> service.download(evaluation.id().value(), "otro-token"))
                .isInstanceOf(InvalidEvaluationTokenException.class);
    }

    @Test
    void shouldRetryFailedReport() {
        GeneratedPdf pdf = GeneratedPdf.reconstruct(
                UUID.randomUUID(), evaluation.id().value(), PdfStatus.FAILED,
                null, null, null, 3, "Falla de renderizado",
                NOW, NOW, NOW, null, null);
        AtomicReference<GeneratedPdf> saved = new AtomicReference<>();
        DownloadReportPdfService service = new DownloadReportPdfService(
                id -> Optional.of(evaluation),
                new GeneratedPdfPersistencePort() {
                    @Override
                    public void save(GeneratedPdf value) {
                        saved.set(value);
                    }

                    @Override
                    public Optional<GeneratedPdf> findByEvaluationId(UUID id) {
                        return Optional.of(pdf);
                    }

                    @Override
                    public Optional<PendingPdf> claim(Instant now, Instant staleBefore) {
                        return Optional.empty();
                    }
                },
                storage(),
                CLOCK);

        service.retry(evaluation.id().value(), TOKEN);

        assertThat(saved.get().status()).isEqualTo(PdfStatus.PROCESSING);
        assertThat(saved.get().attemptCount()).isZero();
        assertThat(saved.get().lastError()).isNull();
    }

    @Test
    void shouldRejectRetryWhenNotFailed() {
        GeneratedPdf pdf = generatedPdf();
        DownloadReportPdfService service = new DownloadReportPdfService(
                id -> Optional.of(evaluation),
                persistence(Optional.of(pdf)),
                storage(),
                CLOCK);

        assertThatThrownBy(() -> service.retry(evaluation.id().value(), TOKEN))
                .isInstanceOf(PdfNotReadyException.class);
    }

    private GeneratedPdf generatedPdf() {
        return GeneratedPdf.reconstruct(
                UUID.randomUUID(), evaluation.id().value(), PdfStatus.GENERATED,
                "reporte.pdf", "https://api.ghostload.com/api/v1/evaluations/x/report/download",
                "Northstar_Maturity_Report.pdf", 1, null,
                NOW, NOW, NOW, null, NOW);
    }

    private ReportPdfStoragePort storage() {
        return storage(new byte[]{1});
    }

    private ReportPdfStoragePort storage(byte[] content) {
        return new ReportPdfStoragePort() {
            @Override
            public String store(UUID evaluationId, byte[] value) {
                return evaluationId + ".pdf";
            }

            @Override
            public byte[] load(String storageKey) {
                return content;
            }
        };
    }

    private GeneratedPdfPersistencePort persistence(Optional<GeneratedPdf> existing) {
        return new GeneratedPdfPersistencePort() {
            @Override
            public void save(GeneratedPdf value) {
            }

            @Override
            public Optional<GeneratedPdf> findByEvaluationId(UUID id) {
                return existing;
            }

            @Override
            public Optional<PendingPdf> claim(Instant now, Instant staleBefore) {
                return Optional.empty();
            }
        };
    }
}


