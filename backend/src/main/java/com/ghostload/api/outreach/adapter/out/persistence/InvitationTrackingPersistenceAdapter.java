package com.ghostload.api.outreach.adapter.out.persistence;

import com.ghostload.api.outreach.application.port.out.LoadInvitationTrackingPort;
import com.ghostload.api.outreach.application.port.out.UpdateInvitationTrackingPort;
import com.ghostload.api.outreach.domain.model.InvitationStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class InvitationTrackingPersistenceAdapter
        implements LoadInvitationTrackingPort, UpdateInvitationTrackingPort {

    private final SpringDataInvitationRepository invitationRepository;

    public InvitationTrackingPersistenceAdapter(
            SpringDataInvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InvitationTracking> loadByToken(UUID invitationToken) {
        return invitationRepository.findTrackingByToken(invitationToken)
                .map(this::toTracking);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InvitationTracking> loadByEvaluationId(UUID evaluationId) {
        return invitationRepository.findTrackingByEvaluationId(evaluationId)
                .map(this::toTracking);
    }

    @Override
    @Transactional
    public boolean markVisited(UUID invitationId, Instant visitedAt) {
        return invitationRepository.markVisited(
                invitationId,
                InvitationStatus.SENT,
                InvitationStatus.VISITED,
                visitedAt) == 1;
    }

    @Override
    @Transactional
    public boolean markStarted(
            UUID invitationId,
            UUID operatorId,
            UUID evaluationId,
            Instant startedAt) {
        return invitationRepository.markStarted(
                invitationId,
                InvitationStatus.VISITED,
                InvitationStatus.STARTED,
                operatorId,
                evaluationId,
                startedAt) == 1;
    }

    @Override
    @Transactional
    public boolean markCompleted(UUID invitationId, Instant completedAt) {
        return invitationRepository.markCompleted(
                invitationId,
                InvitationStatus.STARTED,
                InvitationStatus.COMPLETED,
                completedAt) == 1;
    }

    private InvitationTracking toTracking(
            SpringDataInvitationRepository.InvitationTrackingView view) {
        return new InvitationTracking(
                view.getInvitationId(),
                view.getInvitationToken(),
                view.getInvitationStatus(),
                view.getExpiresAt(),
                view.getEmail(),
                view.getFirstName(),
                view.getLastName(),
                view.getCompanyName(),
                view.getPosition(),
                view.getCampaignName(),
                view.getOperatorId(),
                view.getEvaluationId());
    }
}
