package com.ghostload.api.reporting.application.service;

import com.ghostload.api.assessment.domain.model.BenchmarkModule;
import com.ghostload.api.assessment.domain.model.MaturityLevel;
import com.ghostload.api.assessment.domain.model.ModuleScore;
import com.ghostload.api.reporting.application.port.out.GeneratedPdfPersistencePort;
import com.ghostload.api.reporting.application.port.out.LoadReportDataPort;
import com.ghostload.api.reporting.application.port.out.RenderReportPdfPort;
import com.ghostload.api.reporting.application.port.out.ReportPdfStoragePort;
import com.ghostload.api.reporting.application.port.out.SendReportEmailPort;
import com.ghostload.api.reporting.configuration.PdfProperties;
import com.ghostload.api.reporting.domain.model.GeneratedPdf;
import com.ghostload.api.reporting.domain.model.PdfStatus;
import com.ghostload.api.reporting.domain.model.ReportData;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenerateReportPdfServiceTest {

    private static final Instant NOW = Instant.parse("2026-07-31T14:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final PdfProperties PROPERTIES = new PdfProperties(
            "./pdf-storage",
            "https://api.ghostload.com",
            "Ander",
            "Founder",
            "+54 11 0000 0000",
            "ander@ghostload.com",
            "https://www.linkedin.com/in/ander",
            "https://calendly.com/ander/ghost-load",
            null);

    @Test
    void shouldQueueReportWhenNotRequestedYet() {
        UUID evaluationId = UUID.randomUUID();
        AtomicReference<GeneratedPdf> saved = new AtomicReference<>();
        GenerateReportPdfService service = new GenerateReportPdfService(
                persistenceWithQueue(Optional.empty(), saved),
                data -> reportData(evaluationId),
                data -> new byte[]{1, 2, 3},
                storage(),
                email -> {
                },
                CLOCK,
                PROPERTIES);

        service.queue(evaluationId);

        assertThat(saved.get()).isNotNull();
        assertThat(saved.get().evaluationId()).isEqualTo(evaluationId);
        assertThat(saved.get().status()).isEqualTo(PdfStatus.PROCESSING);
    }

    @Test
    void shouldNotQueueTwice() {
        UUID evaluationId = UUID.randomUUID();
        AtomicReference<GeneratedPdf> saved = new AtomicReference<>();
        AtomicReference<Integer> saves = new AtomicReference<>(0);
        GenerateReportPdfService service = new GenerateReportPdfService(
                new InMemoryPersistence() {
                    @Override
                    public Optional<GeneratedPdf> findByEvaluationId(UUID id) {
                        return Optional.ofNullable(saved.get());
                    }

                    @Override
                    public void save(GeneratedPdf pdf) {
                        saved.set(pdf);
                        saves.set(saves.get() + 1);
                    }
                },
                data -> reportData(evaluationId),
                data -> new byte[]{1, 2, 3},
                storage(),
                email -> {
                },
                CLOCK,
                PROPERTIES);

        service.queue(evaluationId);
        service.queue(evaluationId);

        assertThat(saves.get()).isEqualTo(1);
        assertThat(saved.get().evaluationId()).isEqualTo(evaluationId);
    }

    @Test
    void shouldGenerateStoreAndSendPdf() {
        UUID evaluationId = UUID.randomUUID();
        AtomicReference<GeneratedPdf> saved = new AtomicReference<>();
        AtomicReference<SendReportEmailPort.ReportEmail> sent = new AtomicReference<>();
        GeneratedPdfPersistencePort persistence = new InMemoryPersistence() {
            @Override
            public Optional<PendingPdf> claim(Instant now, Instant staleBefore) {
                return Optional.of(new PendingPdf(UUID.randomUUID(), evaluationId, 1));
            }

            @Override
            public Optional<GeneratedPdf> findByEvaluationId(UUID id) {
                return Optional.of(claimedPdf(id, 1));
            }

            @Override
            public void save(GeneratedPdf pdf) {
                saved.set(pdf);
            }
        };
        GenerateReportPdfService service = new GenerateReportPdfService(
                persistence,
                data -> reportData(evaluationId),
                data -> new byte[]{10, 20, 30},
                storing("Northstar_Data_Centers_Maturity_Report.pdf"),
                sent::set,
                CLOCK,
                PROPERTIES);

        int processed = service.processBatch(1);

        assertThat(processed).isEqualTo(1);
        assertThat(saved.get().status()).isEqualTo(PdfStatus.GENERATED);
        assertThat(saved.get().fileName()).isEqualTo("Northstar_Data_Centers_Maturity_Report.pdf");
        assertThat(saved.get().downloadUrl())
                .isEqualTo("https://api.ghostload.com/api/v1/evaluations/" + evaluationId + "/report/download");
        assertThat(sent.get().recipientEmail()).isEqualTo("maria@northstar.com");
        assertThat(sent.get().attachmentName()).isEqualTo("Northstar_Data_Centers_Maturity_Report.pdf");
    }

    @Test
    void shouldRescheduleWithBackoffBeforeMaximumAttempt() {
        UUID evaluationId = UUID.randomUUID();
        AtomicReference<GeneratedPdf> saved = new AtomicReference<>();
        GeneratedPdfPersistencePort persistence = failingPersistence(evaluationId, saved, 1);
        GenerateReportPdfService service = new GenerateReportPdfService(
                persistence,
                data -> reportData(evaluationId),
                data -> {
                    throw new IllegalStateException("Falla de renderizado");
                },
                storage(),
                email -> {
                },
                CLOCK,
                PROPERTIES);

        service.processBatch(1);

        assertThat(saved.get().status()).isEqualTo(PdfStatus.PROCESSING);
        assertThat(saved.get().lastError()).contains("Falla de renderizado");
        assertThat(saved.get().availableAt()).isEqualTo(NOW.plusSeconds(60));
    }

    @Test
    void shouldMarkFailedAfterMaximumAttempt() {
        UUID evaluationId = UUID.randomUUID();
        AtomicReference<GeneratedPdf> saved = new AtomicReference<>();
        GeneratedPdfPersistencePort persistence = failingPersistence(evaluationId, saved, 3);
        GenerateReportPdfService service = new GenerateReportPdfService(
                persistence,
                data -> reportData(evaluationId),
                data -> {
                    throw new IllegalStateException("Falla de renderizado");
                },
                storage(),
                email -> {
                },
                CLOCK,
                PROPERTIES);

        service.processBatch(1);

        assertThat(saved.get().status()).isEqualTo(PdfStatus.FAILED);
        assertThat(saved.get().lastError()).contains("Falla de renderizado");
    }

    @Test
    void shouldRejectInvalidBatchSize() {
        GenerateReportPdfService service = new GenerateReportPdfService(
                new InMemoryPersistence(),
                data -> reportData(UUID.randomUUID()),
                data -> new byte[]{1},
                storage(),
                email -> {
                },
                CLOCK,
                PROPERTIES);

        assertThatThrownBy(() -> service.processBatch(0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private GeneratedPdfPersistencePort failingPersistence(
            UUID evaluationId, AtomicReference<GeneratedPdf> saved, int attemptCount) {
        return new InMemoryPersistence() {
            @Override
            public Optional<PendingPdf> claim(Instant now, Instant staleBefore) {
                return Optional.of(new PendingPdf(UUID.randomUUID(), evaluationId, attemptCount));
            }

            @Override
            public Optional<GeneratedPdf> findByEvaluationId(UUID id) {
                return Optional.of(claimedPdf(id, attemptCount));
            }

            @Override
            public void save(GeneratedPdf pdf) {
                saved.set(pdf);
            }
        };
    }

    private GeneratedPdf claimedPdf(UUID evaluationId, int attemptCount) {
        return GeneratedPdf.reconstruct(
                UUID.randomUUID(), evaluationId, PdfStatus.PROCESSING,
                null, null, null, attemptCount,
                null, NOW, NOW, NOW, NOW, null);
    }

    private GeneratedPdfPersistencePort persistenceWithQueue(
            Optional<GeneratedPdf> existing, AtomicReference<GeneratedPdf> saved) {
        return new InMemoryPersistence() {
            @Override
            public Optional<GeneratedPdf> findByEvaluationId(UUID id) {
                return existing;
            }

            @Override
            public void save(GeneratedPdf pdf) {
                saved.set(pdf);
            }
        };
    }

    private ReportPdfStoragePort storage() {
        return new ReportPdfStoragePort() {
            @Override
            public String store(UUID evaluationId, byte[] content) {
                return evaluationId + ".pdf";
            }

            @Override
            public byte[] load(String storageKey) {
                return new byte[0];
            }
        };
    }

    private ReportPdfStoragePort storing(String key) {
        return new ReportPdfStoragePort() {
            @Override
            public String store(UUID evaluationId, byte[] content) {
                return key;
            }

            @Override
            public byte[] load(String storageKey) {
                return new byte[0];
            }
        };
    }

    private ReportData reportData(UUID evaluationId) {
        return new ReportData(
                evaluationId,
                "v1",
                new ReportData.OperatorInfo(
                        "MarÃ­a", "GonzÃ¡lez", "maria@northstar.com",
                        "Northstar Data Centers", "CTO", "Chile"),
                new ReportData.CalculatorMetrics(
                        12.0, 9.6, 2.4, 80.0, 20.0,
                        180.0, 4_200_000.0, "USD"),
                new ReportData.BenchmarkSummary(
                        82.4, MaturityLevel.MATURE, 73.0,
                List.of(
                        new ModuleScore(BenchmarkModule.ENERGY, 4),
                        new ModuleScore(BenchmarkModule.GPU_UTILIZATION, 4),
                        new ModuleScore(BenchmarkModule.COOLING, 3),
                        new ModuleScore(BenchmarkModule.OPERATIONS, 5),
                        new ModuleScore(BenchmarkModule.CAPACITY, 4)),
                        NOW));
    }

    private static class InMemoryPersistence implements GeneratedPdfPersistencePort {

        private final Queue<PendingPdf> pending = new ArrayDeque<>();

        @Override
        public void save(GeneratedPdf pdf) {
        }

        @Override
        public Optional<GeneratedPdf> findByEvaluationId(UUID evaluationId) {
            return Optional.empty();
        }

        @Override
        public Optional<PendingPdf> claim(Instant now, Instant staleBefore) {
            return Optional.ofNullable(pending.poll());
        }
    }
}
