package com.ghostload.api.crm.domain.model;

import com.ghostload.api.crm.domain.exception.InvalidPipelineTransitionException;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class CrmPipeline {

    private static final Set<PipelineStatus> TERMINAL =
            EnumSet.of(PipelineStatus.CONVERTED, PipelineStatus.LOST);

    private final UUID id;
    private String companyName;
    private String contactName;
    private String email;
    private String region;
    private Double benchmarkScore;
    private PipelineStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private final List<PipelineNote> notes = new ArrayList<>();
    private final List<PipelineStatusChange> history = new ArrayList<>();

    private CrmPipeline(
            UUID id,
            String companyName,
            String contactName,
            String email,
            String region,
            Double benchmarkScore,
            PipelineStatus status,
            Instant createdAt,
            Instant updatedAt,
            List<PipelineNote> notes,
            List<PipelineStatusChange> history) {
        this.id = id;
        this.companyName = companyName;
        this.contactName = contactName;
        this.email = email;
        this.region = region;
        this.benchmarkScore = benchmarkScore;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.notes.addAll(notes);
        this.history.addAll(history);
    }

    public static CrmPipeline create(
            String companyName,
            String contactName,
            String email,
            String region,
            Double benchmarkScore,
            Instant createdAt) {
        return new CrmPipeline(
                UUID.randomUUID(),
                requireCompany(companyName),
                normalizeOptional(contactName, 160),
                normalizeOptional(email, 254),
                normalizeOptional(region, 120),
                benchmarkScore,
                PipelineStatus.OUTREACH_PENDING,
                createdAt,
                createdAt,
                List.of(),
                List.of());
    }

    public static CrmPipeline reconstruct(
            UUID id,
            String companyName,
            String contactName,
            String email,
            String region,
            Double benchmarkScore,
            PipelineStatus status,
            Instant createdAt,
            Instant updatedAt,
            List<PipelineNote> notes,
            List<PipelineStatusChange> history) {
        return new CrmPipeline(
                id,
                companyName,
                contactName,
                email,
                region,
                benchmarkScore,
                status,
                createdAt,
                updatedAt,
                notes,
                history);
    }

    public void transitionTo(PipelineStatus newStatus, Clock clock) {
        if (newStatus == status) {
            throw new InvalidPipelineTransitionException(
                    "La empresa ya se encuentra en el estado " + status + ".");
        }
        if (TERMINAL.contains(status)) {
            throw new InvalidPipelineTransitionException(
                    "No se puede cambiar el estado " + status + " (estado final).");
        }
        if (!allowed(status).contains(newStatus)) {
            throw new InvalidPipelineTransitionException(
                    "No se puede pasar de " + status + " a " + newStatus + ".");
        }
        history.add(new PipelineStatusChange(
                UUID.randomUUID(),
                status,
                newStatus,
                clock.instant()));
        status = newStatus;
        updatedAt = clock.instant();
    }

    public void updateProfile(
            String companyName,
            String contactName,
            String email,
            String region,
            Double benchmarkScore,
            Clock clock) {
        this.companyName = requireCompany(companyName);
        this.contactName = normalizeOptional(contactName, 160);
        this.email = normalizeOptional(email, 254);
        this.region = normalizeOptional(region, 120);
        this.benchmarkScore = benchmarkScore;
        this.updatedAt = clock.instant();
    }

    public void addNote(String note, Clock clock) {
        if (note == null || note.trim().isEmpty()) {
            throw new InvalidPipelineTransitionException("La nota no puede estar vacía.");
        }
        String normalized = note.trim();
        if (normalized.length() > 2_000) {
            throw new InvalidPipelineTransitionException(
                    "La nota debe tener máximo 2000 caracteres.");
        }
        notes.add(new PipelineNote(UUID.randomUUID(), normalized, clock.instant()));
        updatedAt = clock.instant();
    }

    private static Set<PipelineStatus> allowed(PipelineStatus from) {
        return switch (from) {
            case OUTREACH_PENDING -> Set.of(PipelineStatus.OUTREACH_SENT, PipelineStatus.LOST);
            case OUTREACH_SENT -> Set.of(
                    PipelineStatus.MEETING_SCHEDULED,
                    PipelineStatus.OUTREACH_PENDING,
                    PipelineStatus.LOST);
            case MEETING_SCHEDULED -> Set.of(
                    PipelineStatus.CONVERTED,
                    PipelineStatus.LOST,
                    PipelineStatus.OUTREACH_SENT);
            default -> Set.of();
        };
    }

    private static String requireCompany(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidPipelineTransitionException("El nombre de la empresa es obligatorio.");
        }
        String normalized = value.trim();
        if (normalized.length() > 160) {
            throw new InvalidPipelineTransitionException(
                    "El nombre de la empresa debe tener máximo 160 caracteres.");
        }
        return normalized;
    }

    private static String normalizeOptional(String value, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new InvalidPipelineTransitionException(
                    "El campo excede los " + maxLength + " caracteres permitidos.");
        }
        return normalized;
    }

    public UUID id() {
        return id;
    }

    public String companyName() {
        return companyName;
    }

    public String contactName() {
        return contactName;
    }

    public String email() {
        return email;
    }

    public String region() {
        return region;
    }

    public Double benchmarkScore() {
        return benchmarkScore;
    }

    public PipelineStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public List<PipelineNote> notes() {
        return Collections.unmodifiableList(notes);
    }

    public List<PipelineStatusChange> history() {
        return Collections.unmodifiableList(history);
    }
}