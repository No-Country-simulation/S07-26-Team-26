package com.ghostload.api.outreach.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ContactImport(
        UUID id,
        String name,
        ContactImportStatus status,
        int totalRows,
        int validContacts,
        int duplicates,
        int invalidRows,
        Instant createdAt) {

    public ContactImport {
        if (id == null || status == null || createdAt == null) {
            throw new IllegalArgumentException("Los datos de la importación son obligatorios.");
        }
        if (name == null || name.trim().length() < 3 || name.trim().length() > 120) {
            throw new IllegalArgumentException("El nombre debe tener entre 3 y 120 caracteres.");
        }
        name = name.trim();
        if (totalRows < 0 || validContacts < 0 || duplicates < 0 || invalidRows < 0) {
            throw new IllegalArgumentException("Los contadores no pueden ser negativos.");
        }
        if (validContacts + duplicates + invalidRows != totalRows) {
            throw new IllegalArgumentException("Los contadores no coinciden con las filas procesadas.");
        }
    }

    public static ContactImport completed(
            UUID id,
            String name,
            int totalRows,
            int validContacts,
            int duplicates,
            int invalidRows,
            Instant createdAt) {
        return new ContactImport(
                id,
                name,
                ContactImportStatus.COMPLETED,
                totalRows,
                validContacts,
                duplicates,
                invalidRows,
                createdAt);
    }
}
