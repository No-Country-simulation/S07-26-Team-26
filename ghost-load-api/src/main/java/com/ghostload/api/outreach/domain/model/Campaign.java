package com.ghostload.api.outreach.domain.model;

import com.ghostload.api.outreach.domain.exception.InvalidCampaignException;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

public final class Campaign {

    private final UUID id;
    private final UUID contactImportId;
    private final String name;
    private final String description;
    private final String subject;
    private final String message;
    private final String callToActionText;
    private final CampaignStatus status;
    private final int recipientCount;
    private final Instant scheduledAt;
    private final String timezone;
    private final Instant sentAt;
    private final Instant createdAt;

    private Campaign(
            UUID id,
            UUID contactImportId,
            String name,
            String description,
            String subject,
            String message,
            String callToActionText,
            CampaignStatus status,
            int recipientCount,
            Instant scheduledAt,
            String timezone,
            Instant sentAt,
            Instant createdAt) {
        if (id == null || contactImportId == null || status == null || createdAt == null) {
            throw new InvalidCampaignException(
                    "Los identificadores, estado y fecha de la campaña son obligatorios.");
        }
        if (recipientCount <= 0) {
            throw new InvalidCampaignException(
                    "La campaña debe contener al menos un destinatario.");
        }
        this.id = id;
        this.contactImportId = contactImportId;
        this.name = requireText(name, "El nombre", 3, 160);
        this.description = normalizeOptional(description, "La descripción", 500);
        this.subject = requireText(subject, "El asunto", 3, 180);
        this.message = requireText(message, "El mensaje", 10, 5_000);
        this.callToActionText =
                requireText(callToActionText, "El texto del botón", 2, 80);
        this.status = status;
        this.recipientCount = recipientCount;
        this.scheduledAt = scheduledAt;
        this.timezone = normalizeTimezone(timezone);
        this.sentAt = sentAt;
        this.createdAt = createdAt;
    }

    public static Campaign ready(
            UUID contactImportId,
            String name,
            String description,
            String subject,
            String message,
            String callToActionText,
            int recipientCount,
            Instant scheduledAt,
            String timezone,
            Instant createdAt) {
        return new Campaign(
                UUID.randomUUID(),
                contactImportId,
                name,
                description,
                subject,
                message,
                callToActionText,
                CampaignStatus.READY,
                recipientCount,
                scheduledAt,
                timezone,
                null,
                createdAt);
    }

    private static String requireText(
            String value,
            String fieldName,
            int minimumLength,
            int maximumLength) {
        if (value == null) {
            throw new InvalidCampaignException(fieldName + " es obligatorio.");
        }
        String normalized = value.trim();
        if (normalized.length() < minimumLength || normalized.length() > maximumLength) {
            throw new InvalidCampaignException(
                    fieldName + " debe tener entre " + minimumLength
                            + " y " + maximumLength + " caracteres.");
        }
        return normalized;
    }

    private static String normalizeOptional(
            String value,
            String fieldName,
            int maximumLength) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.length() > maximumLength) {
            throw new InvalidCampaignException(
                    fieldName + " debe tener máximo " + maximumLength + " caracteres.");
        }
        return normalized;
    }

    private static String normalizeTimezone(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim();
        try {
            ZoneId.of(normalized);
        } catch (DateTimeException exception) {
            throw new InvalidCampaignException("La zona horaria no es válida.");
        }
        return normalized;
    }

    public UUID id() {
        return id;
    }

    public UUID contactImportId() {
        return contactImportId;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public String subject() {
        return subject;
    }

    public String message() {
        return message;
    }

    public String callToActionText() {
        return callToActionText;
    }

    public CampaignStatus status() {
        return status;
    }

    public int recipientCount() {
        return recipientCount;
    }

    public Instant scheduledAt() {
        return scheduledAt;
    }

    public String timezone() {
        return timezone;
    }

    public Instant sentAt() {
        return sentAt;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
