package com.ghostload.api.outreach.adapter.out.persistence;

import com.ghostload.api.outreach.domain.model.ContactImportStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "contact_imports")
public class ContactImportJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContactImportStatus status;

    @Column(name = "total_rows", nullable = false)
    private int totalRows;

    @Column(name = "valid_contacts", nullable = false)
    private int validContacts;

    @Column(nullable = false)
    private int duplicates;

    @Column(name = "invalid_rows", nullable = false)
    private int invalidRows;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ContactImportJpaEntity() {
    }

    ContactImportJpaEntity(
            UUID id,
            String name,
            ContactImportStatus status,
            int totalRows,
            int validContacts,
            int duplicates,
            int invalidRows,
            Instant createdAt) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.totalRows = totalRows;
        this.validContacts = validContacts;
        this.duplicates = duplicates;
        this.invalidRows = invalidRows;
        this.createdAt = createdAt;
    }
}
