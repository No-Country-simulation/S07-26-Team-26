package com.ghostload.api.administration.application.service;

import com.ghostload.api.administration.application.port.in.DashboardSummary;
import com.ghostload.api.administration.application.port.in.GetDashboardSummaryCommand;
import com.ghostload.api.administration.application.port.in.GetDashboardSummaryQuery;
import com.ghostload.api.administration.application.port.out.LoadDashboardMetricsPort;

/**
 * Application service that aggregates global KPIs for the admin dashboard.
 * Satisfies Requirement 2 (AC-1: display KPIs, AC-2: current values, AC-3: date range filter).
 */
public final class GetDashboardSummaryService implements GetDashboardSummaryQuery {

    private final LoadDashboardMetricsPort loadDashboardMetricsPort;

    public GetDashboardSummaryService(LoadDashboardMetricsPort loadDashboardMetricsPort) {
        this.loadDashboardMetricsPort = loadDashboardMetricsPort;
    }

    @Override
    public DashboardSummary summarize(GetDashboardSummaryCommand command) {
        // command.from() and command.to() may be null — the port implementation
        // must handle null values as "no date filter" (all-time stats).
        LoadDashboardMetricsPort.MetricsSnapshot metrics = loadDashboardMetricsPort.load(command);

        double completionRate = metrics.evaluationsStarted() == 0
                ? 0d
                : metrics.evaluationsCompleted() * 100d / metrics.evaluationsStarted();

        return new DashboardSummary(
                metrics.totalOperators(),
                metrics.evaluationsCompleted(),
                metrics.averageBenchmarkScore(),
                metrics.generatedReports(),
                metrics.contactsLoaded(),
                metrics.invitationsSent(),
                metrics.linksVisited(),
                metrics.evaluationsStarted(),
                completionRate,
                metrics.averageUtilization(),
                metrics.accumulatedNonProductiveCapacityMw(),
                metrics.accumulatedEstimatedAnnualCost(),
                metrics.maturityDistribution(),
                metrics.categoryAverages());
    }
}
