package com.ghostload.api.outreach.adapter.out.persistence;

import com.ghostload.api.outreach.domain.model.InvitationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "invitations")
public class InvitationJpaEntity {

    @Id
    private UUID id;

    @Column(name = "campaign_id", nullable = false)
    private UUID campaignId;

    @Column(name = "contact_id", nullable = false)
    private UUID contactId;

    @Column(nullable = false, unique = true)
    private UUID token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvitationStatus status;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "visited_at")
    private Instant visitedAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "failed_at")
    private Instant failedAt;

    @Column(name = "failure_reason", length = 1_000)
    private String failureReason;

    @Column(name = "operator_id")
    private UUID operatorId;

    @Column(name = "evaluation_id")
    private UUID evaluationId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected InvitationJpaEntity() {
    }

    InvitationJpaEntity(
            UUID id,
            UUID campaignId,
            UUID contactId,
            UUID token,
            InvitationStatus status,
            Instant createdAt) {
        this.id = id;
        this.campaignId = campaignId;
        this.contactId = contactId;
        this.token = token;
        this.status = status;
        this.createdAt = createdAt;
    }

    UUID id() {
        return id;
    }

    UUID campaignId() {
        return campaignId;
    }

    UUID contactId() {
        return contactId;
    }

    UUID token() {
        return token;
    }

    InvitationStatus status() {
        return status;
    }
}
