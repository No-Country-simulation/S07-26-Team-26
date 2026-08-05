package com.ghostload.api.administration.application.service;

import com.ghostload.api.administration.application.port.in.DashboardSummary;
import com.ghostload.api.administration.application.port.in.GetDashboardSummaryCommand;
import com.ghostload.api.administration.application.port.in.GetDashboardSummaryQuery;
import com.ghostload.api.administration.application.port.out.LoadDashboardMetricsPort;

public final class GetDashboardSummaryService implements GetDashboardSummaryQuery {

    private final LoadDashboardMetricsPort loadDashboardMetricsPort;

    public GetDashboardSummaryService(LoadDashboardMetricsPort loadDashboardMetricsPort) {
        this.loadDashboardMetricsPort = loadDashboardMetricsPort;
    }

    @Override
    public DashboardSummary summarize(GetDashboardSummaryCommand command) {
        LoadDashboardMetricsPort.MetricsSnapshot metrics = loadDashboardMetricsPort.load(command);

        double completionRate = metrics.evaluationsStarted() == 0
                ? 0d
                : metrics.evaluationsCompleted() * 100d / metrics.evaluationsStarted();

        return new DashboardSummary(
                metrics.contactsLoaded(),
                metrics.invitationsSent(),
                metrics.linksVisited(),
                metrics.evaluationsStarted(),
                metrics.evaluationsCompleted(),
                completionRate,
                metrics.averageBenchmarkScore(),
                metrics.averageUtilization(),
                metrics.accumulatedNonProductiveCapacityMw(),
                metrics.accumulatedEstimatedAnnualCost(),
                0,
                metrics.maturityDistribution(),
                metrics.categoryAverages());
    }
}
