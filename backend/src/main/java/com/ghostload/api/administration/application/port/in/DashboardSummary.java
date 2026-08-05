package com.ghostload.api.administration.application.port.in;

import com.ghostload.api.assessment.domain.model.BenchmarkModule;
import com.ghostload.api.assessment.domain.model.MaturityLevel;

import java.util.List;

public record DashboardSummary(
        long contactsLoaded,
        long invitationsSent,
        long linksVisited,
        long evaluationsStarted,
        long evaluationsCompleted,
        double completionRate,
        double averageBenchmarkScore,
        double averageUtilization,
        double accumulatedNonProductiveCapacityMw,
        double accumulatedEstimatedAnnualCost,
        long generatedReports,
        List<MaturityDistributionItem> maturityDistribution,
        List<CategoryAverageItem> categoryAverages) {

    public record MaturityDistributionItem(MaturityLevel level, long count) {
    }

    public record CategoryAverageItem(BenchmarkModule module, double score) {
    }
}
