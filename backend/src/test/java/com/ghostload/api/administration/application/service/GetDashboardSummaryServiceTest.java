package com.ghostload.api.administration.application.service;

import com.ghostload.api.administration.application.port.in.DashboardSummary;
import com.ghostload.api.administration.application.port.in.GetDashboardSummaryCommand;
import com.ghostload.api.administration.application.port.out.LoadDashboardMetricsPort;
import com.ghostload.api.assessment.domain.model.BenchmarkModule;
import com.ghostload.api.assessment.domain.model.MaturityLevel;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GetDashboardSummaryServiceTest {

    private static final UUID CAMPAIGN_ID =
            UUID.fromString("0f04b111-7131-49f4-9fb7-9a04705e2309");

    @Test
    void shouldComputeCompletionRateAndMapAllMetrics() {
        var command = new GetDashboardSummaryCommand(
                Instant.parse("2026-07-01T00:00:00Z"),
                Instant.parse("2026-08-01T00:00:00Z"),
                CAMPAIGN_ID);
        // MetricsSnapshot: totalOperators, evaluationsCompleted, averageBenchmarkScore, generatedReports,
        //                  contactsLoaded, invitationsSent, linksVisited, evaluationsStarted,
        //                  averageUtilization, accumulatedNonProductiveCapacityMw,
        //                  accumulatedEstimatedAnnualCost, maturityDistribution, categoryAverages
        LoadDashboardMetricsPort port = c -> new LoadDashboardMetricsPort.MetricsSnapshot(
                10,    // totalOperators
                40,    // evaluationsCompleted
                72.5,  // averageBenchmarkScore
                40,    // generatedReports (same as evaluationsCompleted)
                120,   // contactsLoaded
                100,   // invitationsSent
                60,    // linksVisited
                50,    // evaluationsStarted
                65.0,  // averageUtilization
                3.2,   // accumulatedNonProductiveCapacityMw
                12_500.0, // accumulatedEstimatedAnnualCost
                List.of(new DashboardSummary.MaturityDistributionItem(MaturityLevel.INITIAL, 20),
                        new DashboardSummary.MaturityDistributionItem(MaturityLevel.OPTIMIZED, 20)),
                List.of(new DashboardSummary.CategoryAverageItem(BenchmarkModule.AUTOMATION, 70.0)));
        GetDashboardSummaryService service = new GetDashboardSummaryService(port);

        var result = service.summarize(command);

        // Core KPIs (Requirement 2, AC-1)
        assertThat(result.totalOperators()).isEqualTo(10);
        assertThat(result.evaluationsCompleted()).isEqualTo(40);
        assertThat(result.averageBenchmarkScore()).isEqualTo(72.5);
        assertThat(result.generatedReports()).isEqualTo(40);
        // Extended metrics
        assertThat(result.contactsLoaded()).isEqualTo(120);
        assertThat(result.invitationsSent()).isEqualTo(100);
        assertThat(result.linksVisited()).isEqualTo(60);
        assertThat(result.evaluationsStarted()).isEqualTo(50);
        assertThat(result.completionRate()).isEqualTo(80.0); // 40/50 * 100
        assertThat(result.averageUtilization()).isEqualTo(65.0);
        assertThat(result.accumulatedNonProductiveCapacityMw()).isEqualTo(3.2);
        assertThat(result.accumulatedEstimatedAnnualCost()).isEqualTo(12_500.0);
        assertThat(result.maturityDistribution()).hasSize(2);
        assertThat(result.categoryAverages()).hasSize(1);
    }

    @Test
    void shouldReturnZeroCompletionRateWhenNoEvaluationsStarted() {
        var command = new GetDashboardSummaryCommand(null, null, null);
        // All zeros — MetricsSnapshot positional args must match new record order
        LoadDashboardMetricsPort port = c -> new LoadDashboardMetricsPort.MetricsSnapshot(
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, List.of(), List.of());
        GetDashboardSummaryService service = new GetDashboardSummaryService(port);

        var result = service.summarize(command);

        assertThat(result.completionRate()).isZero();
        assertThat(result.maturityDistribution()).isEmpty();
        assertThat(result.categoryAverages()).isEmpty();
    }
}
