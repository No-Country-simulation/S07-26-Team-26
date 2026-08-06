package com.ghostload.api.crm.adapter.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "crm_pipeline_notes")
public class PipelineNoteJpaEntity {

    @Id
    private UUID id;

    @Column(name = "entry_id", nullable = false)
    private UUID entryId;

    @Column(nullable = false, length = 2_000)
    private String note;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected PipelineNoteJpaEntity() {
    }

    PipelineNoteJpaEntity(UUID id, UUID entryId, String note, Instant createdAt) {
        this.id = id;
        this.entryId = entryId;
        this.note = note;
        this.createdAt = createdAt;
    }

    UUID id() {
        return id;
    }

    UUID entryId() {
        return entryId;
    }

    String note() {
        return note;
    }

    Instant createdAt() {
        return createdAt;
    }
}