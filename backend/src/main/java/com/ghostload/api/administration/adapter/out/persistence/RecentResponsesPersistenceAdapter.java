package com.ghostload.api.administration.adapter.out.persistence;

import com.ghostload.api.administration.application.port.in.GetRecentResponsesQuery;
import com.ghostload.api.administration.application.port.out.LoadCompletedEvaluationsPort;
import com.ghostload.api.assessment.adapter.out.persistence.OperatorJpaRepository;
import com.ghostload.api.assessment.domain.model.MaturityLevel;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RecentResponsesPersistenceAdapter implements LoadCompletedEvaluationsPort {

    private final OperatorJpaRepository operatorJpaRepository;

    public RecentResponsesPersistenceAdapter(OperatorJpaRepository operatorJpaRepository) {
        this.operatorJpaRepository = operatorJpaRepository;
    }

    @Override
    public GetRecentResponsesQuery.RecentResponsesPage load(int page, int size, Instant from, Instant to) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "completedAt"));
        var result = operatorJpaRepository.findRecentResponses(from, to, pageable);

        var items = result.getContent().stream()
                .map(this::toItem)
                .toList();

        return new GetRecentResponsesQuery.RecentResponsesPage(
                items,
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize());
    }

    private GetRecentResponsesQuery.RecentResponsesItem toItem(OperatorJpaRepository.RecentResponseView view) {
        return new GetRecentResponsesQuery.RecentResponsesItem(
                view.getOperatorId(),
                view.getFirstName() + " " + view.getLastName(),
                view.getEmail(),
                view.getCompanyName(),
                view.getEvaluationId(),
                view.getTotalScore(),
                view.getPercentile(),
                view.getMaturityLevel() == null ? null : MaturityLevel.valueOf(view.getMaturityLevel()),
                view.getCompletedAt());
    }
}
