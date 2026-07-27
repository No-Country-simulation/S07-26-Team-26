package com.ghostload.api.outreach.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

interface SpringDataContactRepository extends JpaRepository<ContactJpaEntity, UUID> {

    @Query("""
            select lower(contact.email)
            from ContactJpaEntity contact
            where lower(contact.email) in :emails
            """)
    List<String> findExistingNormalizedEmails(@Param("emails") Set<String> emails);

    List<ContactJpaEntity> findAllByContactImportIdOrderByCreatedAtAsc(UUID contactImportId);
}
