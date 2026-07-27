package com.ghostload.api.outreach.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "contacts")
public class ContactJpaEntity {

    @Id
    private UUID id;

    @Column(name = "contact_import_id", nullable = false)
    private UUID contactImportId;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "company_name", nullable = false, length = 160)
    private String companyName;

    @Column(length = 120)
    private String position;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ContactJpaEntity() {
    }

    ContactJpaEntity(
            UUID id,
            UUID contactImportId,
            String firstName,
            String lastName,
            String email,
            String companyName,
            String position,
            Instant createdAt) {
        this.id = id;
        this.contactImportId = contactImportId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.companyName = companyName;
        this.position = position;
        this.createdAt = createdAt;
    }

    UUID id() {
        return id;
    }

    UUID contactImportId() {
        return contactImportId;
    }

    String firstName() {
        return firstName;
    }

    String lastName() {
        return lastName;
    }

    String email() {
        return email;
    }

    String companyName() {
        return companyName;
    }

    String position() {
        return position;
    }

    Instant createdAt() {
        return createdAt;
    }
}
