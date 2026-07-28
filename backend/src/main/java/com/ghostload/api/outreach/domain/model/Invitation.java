package com.ghostload.api.outreach.domain.model;

import com.ghostload.api.outreach.domain.exception.InvalidCampaignException;

import java.time.Instant;
import java.util.UUID;

public record Invitation(
        UUID id,
        UUID campaignId,
        UUID contactId,
        UUID token,
        InvitationStatus status,
        Instant createdAt) {

    public Invitation {
        if (id == null
                || campaignId == null
                || contactId == null
                || token == null
                || status == null
                || createdAt == null) {
            throw new InvalidCampaignException(
                    "Los datos principales de la invitación son obligatorios.");
        }
    }

    public static Invitation uploaded(
            UUID campaignId,
            UUID contactId,
            UUID token,
            Instant createdAt) {
        return new Invitation(
                UUID.randomUUID(),
                campaignId,
                contactId,
                token,
                InvitationStatus.UPLOADED,
                createdAt);
    }
}
