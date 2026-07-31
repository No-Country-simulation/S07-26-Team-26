package com.ghostload.api.outreach.adapter.in.web;

import com.ghostload.api.outreach.domain.model.InvitationStatus;

import java.time.Instant;

public record InvitationResolutionResponse(
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
