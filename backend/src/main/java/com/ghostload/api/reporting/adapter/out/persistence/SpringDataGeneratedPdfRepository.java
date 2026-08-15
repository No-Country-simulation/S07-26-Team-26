package com.ghostload.api.reporting.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

interface SpringDataGeneratedPdfRepository
        extends JpaRepository<GeneratedPdfJpaEntity, UUID> {

    Optional<GeneratedPdfJpaEntity> findByEvaluationId(UUID evaluationId);

    @Query(value = """
            select *
              from generated_pdfs
             where (status = 'PROCESSING' and available_at <= :now)
                or (status = 'PROCESSING' and claimed_at <= :staleBefore)
             order by available_at, created_at
             for update skip locked
             limit 1
            """, nativeQuery = true)
    Optional<GeneratedPdfJpaEntity> findNextForUpdate(
            @Param("now") Instant now,
            @Param("staleBefore") Instant staleBefore);
}
