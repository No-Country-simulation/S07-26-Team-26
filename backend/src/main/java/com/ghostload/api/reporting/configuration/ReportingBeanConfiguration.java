package com.ghostload.api.reporting.configuration;

import com.ghostload.api.assessment.application.port.out.LoadEvaluationPort;
import com.ghostload.api.reporting.application.port.in.DownloadReportPdfUseCase;
import com.ghostload.api.reporting.application.port.in.GenerateReportPdfUseCase;
import com.ghostload.api.reporting.application.port.out.GeneratedPdfPersistencePort;
import com.ghostload.api.reporting.application.port.out.LoadReportDataPort;
import com.ghostload.api.reporting.application.port.out.RenderReportPdfPort;
import com.ghostload.api.reporting.application.port.out.ReportPdfStoragePort;
import com.ghostload.api.reporting.application.port.out.SendReportEmailPort;
import com.ghostload.api.reporting.application.service.DownloadReportPdfService;
import com.ghostload.api.reporting.application.service.GenerateReportPdfService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(PdfProperties.class)
public class ReportingBeanConfiguration {

    @Bean
    GenerateReportPdfUseCase generateReportPdfUseCase(
            GeneratedPdfPersistencePort persistence,
            LoadReportDataPort reportData,
            RenderReportPdfPort renderer,
            ReportPdfStoragePort storage,
            SendReportEmailPort emailSender,
            Clock clock,
            PdfProperties properties) {
        return new GenerateReportPdfService(
                persistence, reportData, renderer, storage, emailSender, clock, properties);
    }

    @Bean
    DownloadReportPdfUseCase downloadReportPdfUseCase(
            LoadEvaluationPort evaluations,
            GeneratedPdfPersistencePort persistence,
            ReportPdfStoragePort storage,
            Clock clock) {
        return new DownloadReportPdfService(evaluations, persistence, storage, clock);
    }
}
