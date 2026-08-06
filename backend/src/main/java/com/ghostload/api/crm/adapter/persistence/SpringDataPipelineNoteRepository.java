package com.ghostload.api.crm.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataPipelineNoteRepository
        extends JpaRepository<PipelineNoteJpaEntity, UUID> {

    void deleteByEntryId(UUID entryId);
}