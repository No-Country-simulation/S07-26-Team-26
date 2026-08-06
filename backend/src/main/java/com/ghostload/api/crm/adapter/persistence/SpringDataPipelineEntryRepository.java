package com.ghostload.api.crm.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataPipelineEntryRepository
        extends JpaRepository<PipelineEntryJpaEntity, UUID> {
}