package com.ghostload.api.reporting.adapter.out.persistence;

import com.ghostload.api.reporting.application.port.out.GeneratedPdfPersistencePort;
import com.ghostload.api.reporting.domain.model.GeneratedPdf;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class GeneratedPdfPersistenceAdapter implements GeneratedPdfPersistencePort {

    private final SpringDataGeneratedPdfRepository repository;

    public GeneratedPdfPersistenceAdapter(SpringDataGeneratedPdfRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void save(GeneratedPdf pdf) {
        repository.save(toEntity(pdf));
    }

    @Override
    public Optional<GeneratedPdf> findByEvaluationId(UUID evaluationId) {
        return repository.findByEvaluationId(evaluationId).map(this::toDomain);
    }

    @Override
    @Transactional
    public Optional<PendingPdf> claim(Instant now, Instant staleBefore) {
        return repository.findNextForUpdate(now, staleBefore)
                .map(entity -> {
                    entity.claim(now);
                    return new PendingPdf(entity.id(), entity.evaluationId(), entity.attemptCount());
                });
    }

    private GeneratedPdfJpaEntity toEntity(GeneratedPdf pdf) {
        return new GeneratedPdfJpaEntity(
                pdf.id(),
                pdf.evaluationId(),
                pdf.status(),
                pdf.storageKey(),
                pdf.downloadUrl(),
                pdf.fileName(),
                pdf.attemptCount(),
                pdf.lastError(),
                pdf.createdAt(),
                pdf.updatedAt(),
                pdf.availableAt(),
                pdf.claimedAt(),
                pdf.generatedAt());
    }

    private GeneratedPdf toDomain(GeneratedPdfJpaEntity entity) {
        return GeneratedPdf.reconstruct(
                entity.id(),
                entity.evaluationId(),
                entity.status(),
                entity.storageKey(),
                entity.downloadUrl(),
                entity.fileName(),
                entity.attemptCount(),
                entity.lastError(),
                entity.createdAt(),
                entity.updatedAt(),
                entity.availableAt(),
                entity.claimedAt(),
                entity.generatedAt());
    }
}
