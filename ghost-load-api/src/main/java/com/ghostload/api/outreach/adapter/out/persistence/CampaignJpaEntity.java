package com.ghostload.api.outreach.adapter.out.persistence;

import com.ghostload.api.outreach.domain.model.CampaignStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "campaigns")
public class CampaignJpaEntity {

    @Id
    private UUID id;

    @Column(name = "contact_import_id", nullable = false)
    private UUID contactImportId;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 180)
    private String subject;

    @Column(nullable = false, length = 5_000)
    private String message;

    @Column(name = "call_to_action_text", nullable = false, length = 80)
    private String callToActionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CampaignStatus status;

    @Column(name = "recipient_count", nullable = false)
    private int recipientCount;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Column(length = 255)
    private String timezone;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CampaignJpaEntity() {
    }

    CampaignJpaEntity(
            UUID id,
            UUID contactImportId,
            String name,
            String description,
            String subject,
            String message,
            String callToActionText,
            CampaignStatus status,
            int recipientCount,
            Instant scheduledAt,
            String timezone,
            Instant sentAt,
            Instant createdAt) {
        this.id = id;
        this.contactImportId = contactImportId;
        this.name = name;
        this.description = description;
        this.subject = subject;
        this.message = message;
        this.callToActionText = callToActionText;
        this.status = status;
        this.recipientCount = recipientCount;
        this.scheduledAt = scheduledAt;
        this.timezone = timezone;
        this.sentAt = sentAt;
        this.createdAt = createdAt;
    }
}
