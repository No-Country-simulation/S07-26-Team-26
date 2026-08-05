package com.ghostload.api.administration.application.port.out;

import com.ghostload.api.administration.application.port.in.DashboardSummary;
import com.ghostload.api.administration.application.port.in.GetDashboardSummaryCommand;

import java.util.List;

public interface LoadDashboardMetricsPort {

    MetricsSnapshot load(GetDashboardSummaryCommand command);

    record MetricsSnapshot(
            long contactsLoaded,
            long invitationsSent,
            long linksVisited,
            long evaluationsStarted,
            long evaluationsCompleted,
            double averageBenchmarkScore,
            double averageUtilization,
            double accumulatedNonProductiveCapacityMw,
            double accumulatedEstimatedAnnualCost,
            List<DashboardSummary.MaturityDistributionItem> maturityDistribution,
            List<DashboardSummary.CategoryAverageItem> categoryAverages) {
    }
}
