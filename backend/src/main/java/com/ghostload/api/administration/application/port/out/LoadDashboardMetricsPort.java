package com.ghostload.api.administration.application.port.out;

import com.ghostload.api.administration.application.port.in.DashboardSummary;
import com.ghostload.api.administration.application.port.in.GetDashboardSummaryCommand;

import java.util.List;

/**
 * Output port that loads all raw metrics needed to build the dashboard summary.
 * Implementations query the appropriate persistence stores.
 */
public interface LoadDashboardMetricsPort {

    MetricsSnapshot load(GetDashboardSummaryCommand command);

    record MetricsSnapshot(
            // Core KPIs (Requirement 2, AC-1)
            long totalOperators,
            long evaluationsCompleted,
            double averageBenchmarkScore,
            long generatedReports,
            // Extended metrics
            long contactsLoaded,
            long invitationsSent,
            long linksVisited,
            long evaluationsStarted,
            double averageUtilization,
            double accumulatedNonProductiveCapacityMw,
            double accumulatedEstimatedAnnualCost,
            List<DashboardSummary.MaturityDistributionItem> maturityDistribution,
            List<DashboardSummary.CategoryAverageItem> categoryAverages) {
    }
}
