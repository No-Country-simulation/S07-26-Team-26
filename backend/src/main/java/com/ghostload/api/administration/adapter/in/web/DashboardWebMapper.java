package com.ghostload.api.administration.adapter.in.web;

import com.ghostload.api.administration.application.port.in.DashboardSummary;
import com.ghostload.api.administration.application.port.in.GetDashboardSummaryCommand;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class DashboardWebMapper {

    GetDashboardSummaryCommand toCommand(LocalDate from, LocalDate to, UUID campaignId) {
        return new GetDashboardSummaryCommand(
                from == null ? null : from.atStartOfDay(ZoneOffset.UTC).toInstant(),
                to == null ? null : to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant(),
                campaignId);
    }

    DashboardSummaryResponse toResponse(DashboardSummary summary) {
        return new DashboardSummaryResponse(
                summary.contactsLoaded(),
                summary.invitationsSent(),
                summary.linksVisited(),
                summary.evaluationsStarted(),
                summary.evaluationsCompleted(),
                summary.completionRate(),
                summary.averageBenchmarkScore(),
                summary.averageUtilization(),
                summary.accumulatedNonProductiveCapacityMw(),
                summary.accumulatedEstimatedAnnualCost(),
                summary.generatedReports(),
                summary.maturityDistribution().stream()
                        .map(item -> new DashboardSummaryResponse.MaturityDistributionItemResponse(
                                item.level(), item.count()))
                        .toList(),
                summary.categoryAverages().stream()
                        .map(item -> new DashboardSummaryResponse.CategoryAverageItemResponse(
                                item.module(), item.score()))
                        .toList());
    }
}
