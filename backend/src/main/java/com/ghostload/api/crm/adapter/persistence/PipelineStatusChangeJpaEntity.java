package com.ghostload.api.crm.adapter.persistence;

import com.ghostload.api.crm.domain.model.PipelineStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "crm_pipeline_status_changes")
public class PipelineStatusChangeJpaEntity {

    @Id
    private UUID id;

    @Column(name = "entry_id", nullable = false)
    private UUID entryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", nullable = false, length = 30)
    private PipelineStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 30)
    private PipelineStatus toStatus;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    protected PipelineStatusChangeJpaEntity() {
    }

    PipelineStatusChangeJpaEntity(
            UUID id,
            UUID entryId,
            PipelineStatus fromStatus,
            PipelineStatus toStatus,
            Instant changedAt) {
        this.id = id;
        this.entryId = entryId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedAt = changedAt;
    }

    UUID id() {
        return id;
    }

    UUID entryId() {
        return entryId;
    }

    PipelineStatus fromStatus() {
        return fromStatus;
    }

    PipelineStatus toStatus() {
        return toStatus;
    }

    Instant changedAt() {
        return changedAt;
    }
}