package com.ghostload.api.administration.adapter.in.web;

import com.ghostload.api.assessment.domain.model.BenchmarkModule;
import com.ghostload.api.assessment.domain.model.MaturityLevel;

import java.util.List;

/**
 * REST response DTO for GET /api/v1/admin/dashboard/summary.
 * Exposes the 4 core KPIs required by Requirement 2 plus extended metrics.
 */
public record DashboardSummaryResponse(
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
        List<MaturityDistributionItemResponse> maturityDistribution,
        List<CategoryAverageItemResponse> categoryAverages) {

    public record MaturityDistributionItemResponse(MaturityLevel level, long count) {
    }

    public record CategoryAverageItemResponse(BenchmarkModule module, double score) {
    }
}
