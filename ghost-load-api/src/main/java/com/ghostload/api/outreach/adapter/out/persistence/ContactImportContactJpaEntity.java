package com.ghostload.api.outreach.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "contact_import_contacts")
@IdClass(ContactImportContactJpaEntity.ContactImportContactId.class)
public class ContactImportContactJpaEntity {

    @Id
    @Column(name = "contact_import_id", nullable = false)
    private UUID contactImportId;

    @Id
    @Column(name = "contact_id", nullable = false)
    private UUID contactId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ContactImportContactJpaEntity() {
    }

    ContactImportContactJpaEntity(
            UUID contactImportId,
            UUID contactId,
            Instant createdAt) {
        this.contactImportId = contactImportId;
        this.contactId = contactId;
        this.createdAt = createdAt;
    }

    public static class ContactImportContactId implements Serializable {

        private UUID contactImportId;
        private UUID contactId;

        public ContactImportContactId() {
        }

        public ContactImportContactId(UUID contactImportId, UUID contactId) {
            this.contactImportId = contactImportId;
            this.contactId = contactId;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ContactImportContactId that)) {
                return false;
            }
            return Objects.equals(contactImportId, that.contactImportId)
                    && Objects.equals(contactId, that.contactId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(contactImportId, contactId);
        }
    }
}
