package com.ghostload.api.outreach.adapter.in.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CampaignTrackingResponse(
        UUID id,
        String name,
        String status,
        String description,
        String subject,
        String callToActionText,
        int recipientCount,
        Instant scheduledAt,
        Instant sentAt,
        Instant createdAt,
        List<InvitationTrackingResponse> invitations) {

    public CampaignTrackingResponse {
        invitations = List.copyOf(invitations);
    }
}