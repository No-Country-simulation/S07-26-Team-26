package com.ghostload.api.outreach.adapter.out.persistence;

import com.ghostload.api.outreach.domain.model.ContactImportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface SpringDataContactImportRepository
        extends JpaRepository<ContactImportJpaEntity, UUID> {

    List<ContactImportJpaEntity>
    findAllByStatusAndValidContactsGreaterThanOrderByCreatedAtDesc(
            ContactImportStatus status,
            int minimumValidContacts);
}
