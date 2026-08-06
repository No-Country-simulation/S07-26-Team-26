package com.ghostload.api.administration.adapter.in.web;

import com.ghostload.api.administration.application.port.in.DashboardSummary;
import com.ghostload.api.administration.application.port.in.GetDashboardSummaryCommand;
import com.ghostload.api.administration.application.port.in.GetRecentResponsesQuery;
import org.springframework.stereotype.Component;

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

    GetRecentResponsesQuery.RecentResponsesCommand toRecentResponsesCommand(
            LocalDate from, LocalDate to, int page, int size) {
        return new GetRecentResponsesQuery.RecentResponsesCommand(
                page,
                size,
                from == null ? null : from.atStartOfDay(ZoneOffset.UTC).toInstant(),
                to == null ? null : to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant());
    }

    DashboardSummaryResponse toResponse(DashboardSummary summary) {
        return new DashboardSummaryResponse(
                summary.totalOperators(),
                summary.evaluationsCompleted(),
                summary.averageBenchmarkScore(),
                summary.generatedReports(),
                summary.contactsLoaded(),
                summary.invitationsSent(),
                summary.linksVisited(),
                summary.evaluationsStarted(),
                summary.completionRate(),
                summary.averageUtilization(),
                summary.accumulatedNonProductiveCapacityMw(),
                summary.accumulatedEstimatedAnnualCost(),
                summary.maturityDistribution().stream()
                        .map(item -> new DashboardSummaryResponse.MaturityDistributionItemResponse(
                                item.level(), item.count()))
                        .toList(),
                summary.categoryAverages().stream()
                        .map(item -> new DashboardSummaryResponse.CategoryAverageItemResponse(
                                item.module(), item.score()))
                        .toList());
    }

    RecentResponsesPageResponse toRecentResponsesResponse(
            GetRecentResponsesQuery.RecentResponsesPage page) {
        return new RecentResponsesPageResponse(
                page.items().stream()
                        .map(item -> new RecentResponseItemResponse(
                                item.operatorId(),
                                item.fullName(),
                                item.email(),
                                item.companyName(),
                                item.evaluationId(),
                                item.score(),
                                item.percentile(),
                                item.maturityLevel(),
                                item.completedAt()))
                        .toList(),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages());
    }
}
