package com.ghostload.api.crm.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataPipelineStatusChangeRepository
        extends JpaRepository<PipelineStatusChangeJpaEntity, UUID> {

    void deleteByEntryId(UUID entryId);
}