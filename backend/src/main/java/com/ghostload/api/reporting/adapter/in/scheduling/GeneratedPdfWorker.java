package com.ghostload.api.reporting.adapter.in.scheduling;

import com.ghostload.api.reporting.application.port.in.GenerateReportPdfUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "app.pdf.worker-enabled",
        havingValue = "true")
public class GeneratedPdfWorker {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GeneratedPdfWorker.class);
    private static final int BATCH_SIZE = 10;

    private final GenerateReportPdfUseCase generateReportPdf;

    public GeneratedPdfWorker(GenerateReportPdfUseCase generateReportPdf) {
        this.generateReportPdf = generateReportPdf;
    }

    @Scheduled(fixedDelayString = "${app.pdf.worker-delay-ms:5000}")
    public void processPendingReports() {
        try {
            int processed = generateReportPdf.processBatch(BATCH_SIZE);
            if (processed > 0) {
                LOGGER.info(
                        "El worker de reportes atendió {} trabajo(s) de PDF. "
                                + "Los fallos aparecen como WARN/ERROR y en generated_pdfs.last_error.",
                        processed);
            }
        } catch (RuntimeException exception) {
            LOGGER.error("Falló el worker de generación de PDFs.", exception);
        }
    }
}
