package com.ghostload.api.outreach.application.port.out;

import com.ghostload.api.outreach.domain.model.InvitationStatus;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface LoadInvitationTrackingPort {

    Optional<InvitationTracking> loadByToken(UUID invitationToken);

    Optional<InvitationTracking> loadByEvaluationId(UUID evaluationId);

    record InvitationTracking(
            UUID invitationId,
            UUID invitationToken,
            InvitationStatus status,
            Instant expiresAt,
            String email,
            String firstName,
            String lastName,
            String companyName,
            String position,
            String campaignName,
            UUID operatorId,
            UUID evaluationId) {
    }
}
