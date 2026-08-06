package com.ghostload.api.outreach.application.port.out;

import com.ghostload.api.outreach.domain.model.Campaign;
import com.ghostload.api.outreach.domain.model.InvitationStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadCampaignTrackingPort {

    Optional<CampaignTracking> loadTracking(UUID campaignId);

    record CampaignTracking(
            Campaign campaign,
            List<InvitationTracking> invitations) {

        public CampaignTracking {
            invitations = List.copyOf(invitations);
        }
    }

    record InvitationTracking(
            UUID invitationId,
            String firstName,
            String lastName,
            String email,
            InvitationStatus status,
            Instant sentAt,
            Instant visitedAt,
            Instant startedAt,
            Instant completedAt,
            Instant failedAt,
            String failureReason,
            Instant createdAt) {
    }
}