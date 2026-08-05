package com.ghostload.api.administration.adapter.in.web;

import com.ghostload.api.administration.application.port.in.ListOperatorsQuery;
import org.springframework.stereotype.Component;

@Component
public class OperatorWebMapper {

    OperatorPageResponse toResponse(ListOperatorsQuery.OperatorPage page) {
        return new OperatorPageResponse(
                page.items().stream()
                        .map(this::toSummary)
                        .toList(),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages());
    }

    OperatorSummaryResponse toSummary(ListOperatorsQuery.OperatorListItem item) {
        return new OperatorSummaryResponse(
                item.operatorId(),
                item.firstName() + " " + item.lastName(),
                item.email(),
                item.companyName(),
                item.evaluationId(),
                item.state(),
                item.totalScore(),
                item.maturityLevel(),
                item.completedAt(),
                item.createdAt());
    }
}
