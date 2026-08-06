package com.ghostload.api.outreach.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

public record InvitationTrackingResponse(
        UUID invitationId,
        String firstName,
        String lastName,
        String email,
        String status,
        Instant sentAt,
        Instant visitedAt,
        Instant startedAt,
        Instant completedAt,
        Instant failedAt,
        String failureReason,
        Instant createdAt) {
}