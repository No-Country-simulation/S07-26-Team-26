package com.ghostload.api.administration.adapter.out.persistence;

import com.ghostload.api.administration.application.port.in.ListOperatorsQuery;
import com.ghostload.api.administration.application.port.out.LoadOperatorListPort;
import com.ghostload.api.assessment.adapter.out.persistence.OperatorJpaRepository;
import com.ghostload.api.assessment.domain.model.EvaluationState;
import com.ghostload.api.assessment.domain.model.MaturityLevel;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class OperatorListPersistenceAdapter implements LoadOperatorListPort {

    private final OperatorJpaRepository operatorJpaRepository;

    public OperatorListPersistenceAdapter(OperatorJpaRepository operatorJpaRepository) {
        this.operatorJpaRepository = operatorJpaRepository;
    }

    @Override
    public ListOperatorsQuery.OperatorPage load(int page, int size, String state, String search) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var result = operatorJpaRepository.findPage(
                normalizeState(state),
                normalizeSearch(search),
                pageable);

        var items = result.getContent().stream()
                .map(this::toItem)
                .toList();

        return new ListOperatorsQuery.OperatorPage(
                items,
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize());
    }

    @Override
    public Optional<ListOperatorsQuery.OperatorListItem> loadDetail(UUID operatorId) {
        return operatorJpaRepository.findDetail(operatorId).map(this::toItem);
    }

    private ListOperatorsQuery.OperatorListItem toItem(OperatorJpaRepository.OperatorListView view) {
        return new ListOperatorsQuery.OperatorListItem(
                view.getOperatorId(),
                view.getFirstName(),
                view.getLastName(),
                view.getEmail(),
                view.getCompanyName(),
                view.getPosition(),
                view.getEvaluationId(),
                view.getState() == null ? null : EvaluationState.valueOf(view.getState()),
                view.getCreatedAt(),
                view.getTotalScore(),
                view.getMaturityLevel() == null ? null : MaturityLevel.valueOf(view.getMaturityLevel()),
                view.getCompletedAt());
    }

    private String normalizeState(String state) {
        if (state == null || state.isBlank()) {
            return null;
        }
        try {
            return EvaluationState.valueOf(state).name();
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Estado inválido: " + state);
        }
    }

    private String normalizeSearch(String search) {
        return search == null || search.isBlank() ? null : search.trim();
    }
}
