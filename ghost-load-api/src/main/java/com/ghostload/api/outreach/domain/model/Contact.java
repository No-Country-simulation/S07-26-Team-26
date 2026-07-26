package com.ghostload.api.outreach.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Contact(
        UUID id,
        UUID contactImportId,
        String firstName,
        String lastName,
        ContactEmail email,
        String companyName,
        String position,
        Instant createdAt) {

    public Contact {
        if (id == null || contactImportId == null || email == null || createdAt == null) {
            throw new IllegalArgumentException("Los identificadores, email y fecha son obligatorios.");
        }
        firstName = requireText(firstName, "El nombre es obligatorio.", 80);
        lastName = requireText(lastName, "El apellido es obligatorio.", 80);
        companyName = requireText(companyName, "La empresa es obligatoria.", 160);
        position = normalizeOptional(position, 120);
    }

    public static Contact create(
            UUID contactImportId,
            String firstName,
            String lastName,
            ContactEmail email,
            String companyName,
            String position,
            Instant createdAt) {
        return new Contact(
                UUID.randomUUID(),
                contactImportId,
                firstName,
                lastName,
                email,
                companyName,
                position,
                createdAt);
    }

    private static String requireText(String value, String message, int maxLength) {
        if (value == null || value.trim().isBlank()) {
            throw new IllegalArgumentException(message);
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private static String normalizeOptional(String value, int maxLength) {
        if (value == null || value.trim().isBlank()) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException("El cargo supera la longitud permitida.");
        }
        return normalized;
    }
}
