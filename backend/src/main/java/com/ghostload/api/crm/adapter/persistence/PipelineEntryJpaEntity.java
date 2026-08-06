package com.ghostload.api.crm.adapter.persistence;

import com.ghostload.api.crm.domain.model.PipelineStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "crm_pipeline_entries")
public class PipelineEntryJpaEntity {

    @Id
    private UUID id;

    @Column(name = "company_name", nullable = false, length = 160)
    private String companyName;

    @Column(name = "contact_name", length = 160)
    private String contactName;

    @Column(length = 254)
    private String email;

    @Column(length = 120)
    private String region;

    @Column(name = "benchmark_score")
    private Double benchmarkScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PipelineStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected PipelineEntryJpaEntity() {
    }

    public PipelineEntryJpaEntity(
            UUID id,
            String companyName,
            String contactName,
            String email,
            String region,
            Double benchmarkScore,
            PipelineStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.companyName = companyName;
        this.contactName = contactName;
        this.email = email;
        this.region = region;
        this.benchmarkScore = benchmarkScore;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public void update(
            String companyName,
            String contactName,
            String email,
            String region,
            Double benchmarkScore,
            PipelineStatus status,
            Instant updatedAt) {
        this.companyName = companyName;
        this.contactName = contactName;
        this.email = email;
        this.region = region;
        this.benchmarkScore = benchmarkScore;
        this.status = status;
        this.updatedAt = updatedAt;
    }
}