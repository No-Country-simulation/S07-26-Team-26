package com.ghostload.api.outreach.application.service;

import com.ghostload.api.outreach.application.port.in.CompleteInvitationUseCase;
import com.ghostload.api.outreach.application.port.out.LoadInvitationTrackingPort;
import com.ghostload.api.outreach.application.port.out.UpdateInvitationTrackingPort;
import com.ghostload.api.outreach.domain.exception.InvalidInvitationException;
import com.ghostload.api.outreach.domain.model.InvitationStatus;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class CompleteInvitationService implements CompleteInvitationUseCase {

    private final LoadInvitationTrackingPort loadInvitationTrackingPort;
    private final UpdateInvitationTrackingPort updateInvitationTrackingPort;

    public CompleteInvitationService(
            LoadInvitationTrackingPort loadInvitationTrackingPort,
            UpdateInvitationTrackingPort updateInvitationTrackingPort) {
        this.loadInvitationTrackingPort =
                Objects.requireNonNull(loadInvitationTrackingPort);
        this.updateInvitationTrackingPort =
                Objects.requireNonNull(updateInvitationTrackingPort);
    }

    @Override
    public void complete(UUID evaluationId, Instant completedAt) {
        if (evaluationId == null || completedAt == null) {
            throw new IllegalArgumentException(
                    "La evaluación y la fecha de finalización son obligatorias.");
        }

        var invitation = loadInvitationTrackingPort
                .loadByEvaluationId(evaluationId)
                .orElse(null);
        if (invitation == null || invitation.status() == InvitationStatus.COMPLETED) {
            return;
        }
        if (invitation.status() != InvitationStatus.STARTED) {
            throw new InvalidInvitationException(
                    "La invitación no está asociada a una evaluación iniciada.");
        }
        if (!updateInvitationTrackingPort.markCompleted(
                invitation.invitationId(),
                completedAt)) {
            throw new InvalidInvitationException(
                    "No se pudo completar el tracking de la invitación.");
        }
    }
}
