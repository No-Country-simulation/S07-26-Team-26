package com.ghostload.api.outreach.application.port.in;

import com.ghostload.api.outreach.domain.model.InvitationStatus;

import java.time.Instant;
import java.util.UUID;

public interface ResolveInvitationUseCase {

    ResolveInvitationResult resolve(UUID invitationToken);

    record ResolveInvitationResult(
            boolean valid,
            InvitationStatus status,
            String email,
            String firstName,
            String lastName,
            String companyName,
            String position,
            String campaignName,
            Instant expiresAt) {
    }
}
