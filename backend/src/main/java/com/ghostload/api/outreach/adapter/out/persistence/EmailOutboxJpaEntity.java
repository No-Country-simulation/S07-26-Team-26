package com.ghostload.api.outreach.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "email_outbox")
public class EmailOutboxJpaEntity {

    @Id
    private UUID id;

    @Column(name = "campaign_id", nullable = false)
    private UUID campaignId;

    @Column(name = "invitation_id", nullable = false, unique = true)
    private UUID invitationId;

    @Column(name = "recipient_email", nullable = false, length = 254)
    private String recipientEmail;

    @Column(name = "recipient_name", nullable = false, length = 161)
    private String recipientName;

    @Column(nullable = false, length = 180)
    private String subject;

    @Column(nullable = false, length = 5_000)
    private String message;

    @Column(name = "call_to_action_text", nullable = false, length = 80)
    private String callToActionText;

    @Column(name = "invitation_token", nullable = false)
    private UUID invitationToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmailOutboxStatusJpa status;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "available_at", nullable = false)
    private Instant availableAt;

    @Column(name = "claimed_at")
    private Instant claimedAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "failed_at")
    private Instant failedAt;

    @Column(name = "provider_message_id", length = 255)
    private String providerMessageId;

    @Column(name = "last_error", length = 1_000)
    private String lastError;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected EmailOutboxJpaEntity() {
    }

    EmailOutboxJpaEntity(
            UUID id,
            UUID campaignId,
            UUID invitationId,
            String recipientEmail,
            String recipientName,
            String subject,
            String message,
            String callToActionText,
            UUID invitationToken,
            Instant availableAt,
            Instant createdAt) {
        this.id = id;
        this.campaignId = campaignId;
        this.invitationId = invitationId;
        this.recipientEmail = recipientEmail;
        this.recipientName = recipientName;
        this.subject = subject;
        this.message = message;
        this.callToActionText = callToActionText;
        this.invitationToken = invitationToken;
        this.status = EmailOutboxStatusJpa.PENDING;
        this.attemptCount = 0;
        this.availableAt = availableAt;
        this.createdAt = createdAt;
    }

    void claim(Instant now) {
        status = EmailOutboxStatusJpa.PROCESSING;
        attemptCount++;
        claimedAt = now;
    }

    void markSent(String messageId, Instant now) {
        status = EmailOutboxStatusJpa.SENT;
        providerMessageId = messageId;
        sentAt = now;
        claimedAt = null;
        lastError = null;
    }

    void reschedule(String error, Instant nextAttemptAt) {
        status = EmailOutboxStatusJpa.PENDING;
        lastError = error;
        availableAt = nextAttemptAt;
        claimedAt = null;
    }

    void markFailed(String error, Instant now) {
        status = EmailOutboxStatusJpa.FAILED;
        lastError = error;
        failedAt = now;
        claimedAt = null;
    }

    UUID id() {
        return id;
    }

    UUID campaignId() {
        return campaignId;
    }

    UUID invitationId() {
        return invitationId;
    }

    String recipientEmail() {
        return recipientEmail;
    }

    String recipientName() {
        return recipientName;
    }

    String subject() {
        return subject;
    }

    String message() {
        return message;
    }

    String callToActionText() {
        return callToActionText;
    }

    UUID invitationToken() {
        return invitationToken;
    }

    int attemptCount() {
        return attemptCount;
    }
}
