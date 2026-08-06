package com.ghostload.api.administration.application.port.in;

import com.ghostload.api.assessment.domain.model.BenchmarkModule;
import com.ghostload.api.assessment.domain.model.MaturityLevel;

import java.util.List;

public record DashboardSummary(
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
        double completionRate,
        double averageUtilization,
        double accumulatedNonProductiveCapacityMw,
        double accumulatedEstimatedAnnualCost,
        List<MaturityDistributionItem> maturityDistribution,
        List<CategoryAverageItem> categoryAverages) {

    public record MaturityDistributionItem(MaturityLevel level, long count) {
    }

    public record CategoryAverageItem(BenchmarkModule module, double score) {
    }
}
