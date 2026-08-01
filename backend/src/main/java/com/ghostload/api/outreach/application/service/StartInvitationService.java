package com.ghostload.api.outreach.application.service;

import com.ghostload.api.outreach.application.port.in.StartInvitationUseCase;
import com.ghostload.api.outreach.application.port.out.LoadInvitationTrackingPort;
import com.ghostload.api.outreach.application.port.out.UpdateInvitationTrackingPort;
import com.ghostload.api.outreach.domain.exception.InvalidInvitationException;
import com.ghostload.api.outreach.domain.model.InvitationStatus;

import java.util.Locale;
import java.util.Objects;

public final class StartInvitationService implements StartInvitationUseCase {

    private final LoadInvitationTrackingPort loadInvitationTrackingPort;
    private final UpdateInvitationTrackingPort updateInvitationTrackingPort;

    public StartInvitationService(
            LoadInvitationTrackingPort loadInvitationTrackingPort,
            UpdateInvitationTrackingPort updateInvitationTrackingPort) {
        this.loadInvitationTrackingPort =
                Objects.requireNonNull(loadInvitationTrackingPort);
        this.updateInvitationTrackingPort =
                Objects.requireNonNull(updateInvitationTrackingPort);
    }

    @Override
    public void start(StartInvitationCommand command) {
        if (command == null
                || command.invitationToken() == null
                || command.operatorId() == null
                || command.evaluationId() == null
                || command.startedAt() == null) {
            throw new InvalidInvitationException(
                    "Los datos de la invitación son obligatorios.");
        }

        var invitation = loadInvitationTrackingPort
                .loadByToken(command.invitationToken())
                .orElseThrow(() -> new InvalidInvitationException(
                        "La invitación no existe."));

        if (invitation.expiresAt() != null
                && !invitation.expiresAt().isAfter(command.startedAt())) {
            throw new InvalidInvitationException(
                    "La invitación ha expirado.");
        }
        if (invitation.status() != InvitationStatus.VISITED) {
            throw new InvalidInvitationException(
                    "La invitación debe abrirse antes de comenzar la evaluación.");
        }
        if (!normalize(invitation.email()).equals(normalize(command.operatorEmail()))) {
            throw new InvalidInvitationException(
                    "El email no corresponde a la invitación.");
        }

        boolean updated = updateInvitationTrackingPort.markStarted(
                invitation.invitationId(),
                command.operatorId(),
                command.evaluationId(),
                command.startedAt());
        if (!updated) {
            throw new InvalidInvitationException(
                    "La invitación ya fue utilizada.");
        }
    }

    private String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
