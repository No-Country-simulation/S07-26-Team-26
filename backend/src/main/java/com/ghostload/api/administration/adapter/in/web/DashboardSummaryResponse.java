package com.ghostload.api.administration.adapter.in.web;

import com.ghostload.api.assessment.domain.model.BenchmarkModule;
import com.ghostload.api.assessment.domain.model.MaturityLevel;

import java.util.List;

public record DashboardSummaryResponse(
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
        List<MaturityDistributionItemResponse> maturityDistribution,
        List<CategoryAverageItemResponse> categoryAverages) {

    public record MaturityDistributionItemResponse(MaturityLevel level, long count) {
    }

    public record CategoryAverageItemResponse(BenchmarkModule module, double score) {
    }
}
