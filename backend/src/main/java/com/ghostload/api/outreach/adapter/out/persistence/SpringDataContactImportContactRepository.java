package com.ghostload.api.outreach.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

interface SpringDataContactImportContactRepository extends JpaRepository<
        ContactImportContactJpaEntity,
        ContactImportContactJpaEntity.ContactImportContactId> {

    @Query("""
            select contact
              from ContactImportContactJpaEntity membership
              join ContactJpaEntity contact on contact.id = membership.contactId
             where membership.contactImportId = :contactImportId
             order by membership.createdAt, contact.email
            """)
    List<ContactJpaEntity> findContactsByImportId(
            @Param("contactImportId") UUID contactImportId);
}
