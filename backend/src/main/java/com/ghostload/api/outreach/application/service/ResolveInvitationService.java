package com.ghostload.api.outreach.application.service;

import com.ghostload.api.outreach.application.port.in.ResolveInvitationUseCase;
import com.ghostload.api.outreach.application.port.out.LoadInvitationTrackingPort;
import com.ghostload.api.outreach.application.port.out.UpdateInvitationTrackingPort;
import com.ghostload.api.outreach.domain.exception.InvitationNotFoundException;
import com.ghostload.api.outreach.domain.exception.InvitationUnavailableException;
import com.ghostload.api.outreach.domain.model.InvitationStatus;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class ResolveInvitationService implements ResolveInvitationUseCase {

    private final LoadInvitationTrackingPort loadInvitationTrackingPort;
    private final UpdateInvitationTrackingPort updateInvitationTrackingPort;
    private final Clock clock;

    public ResolveInvitationService(
            LoadInvitationTrackingPort loadInvitationTrackingPort,
            UpdateInvitationTrackingPort updateInvitationTrackingPort,
            Clock clock) {
        this.loadInvitationTrackingPort =
                Objects.requireNonNull(loadInvitationTrackingPort);
        this.updateInvitationTrackingPort =
                Objects.requireNonNull(updateInvitationTrackingPort);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public ResolveInvitationResult resolve(UUID invitationToken) {
        if (invitationToken == null) {
            throw new InvitationNotFoundException();
        }

        var invitation = loadInvitationTrackingPort.loadByToken(invitationToken)
                .orElseThrow(InvitationNotFoundException::new);
        Instant now = clock.instant();
        ensureResolvable(invitation, now);

        InvitationStatus responseStatus = invitation.status();
        if (invitation.status() == InvitationStatus.SENT) {
            boolean updated = updateInvitationTrackingPort.markVisited(
                    invitation.invitationId(),
                    now);
            if (!updated) {
                throw new InvitationUnavailableException(
                        "La invitación cambió de estado y ya no puede abrirse.");
            }
            responseStatus = InvitationStatus.VISITED;
        }

        return new ResolveInvitationResult(
                true,
                responseStatus,
                invitation.email(),
                invitation.firstName(),
                invitation.lastName(),
                invitation.companyName(),
                invitation.position(),
                invitation.campaignName(),
                invitation.expiresAt());
    }

    private void ensureResolvable(
            LoadInvitationTrackingPort.InvitationTracking invitation,
            Instant now) {
        if (invitation.expiresAt() != null
                && !invitation.expiresAt().isAfter(now)) {
            throw new InvitationUnavailableException(
                    "La invitación ha expirado.");
        }
        if (invitation.status() != InvitationStatus.SENT
                && invitation.status() != InvitationStatus.VISITED) {
            throw new InvitationUnavailableException(
                    "La invitación ya no está disponible.");
        }
    }
}
