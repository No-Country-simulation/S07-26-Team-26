package com.ghostload.api.outreach.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface SpringDataContactRepository extends JpaRepository<ContactJpaEntity, UUID> {

    @Query("""
            select contact
            from ContactJpaEntity contact
            where lower(contact.email) in :emails
            """)
    List<ContactJpaEntity> findExistingContacts(@Param("emails") Set<String> emails);
}
