package com.ghostload.api.outreach.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface SpringDataContactImportRepository
        extends JpaRepository<ContactImportJpaEntity, UUID> {

    List<ContactImportJpaEntity> findAllByOrderByCreatedAtDesc();
}
