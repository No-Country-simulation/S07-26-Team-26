package com.ghostload.api.outreach.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataEmailOutboxRepository
        extends JpaRepository<EmailOutboxJpaEntity, UUID> {

    @Query(value = """
            select *
              from email_outbox
             where (status = 'PENDING' and available_at <= :now)
                or (status = 'PROCESSING' and claimed_at <= :staleBefore)
             order by available_at, created_at
             for update skip locked
             limit 1
            """, nativeQuery = true)
    Optional<EmailOutboxJpaEntity> findNextForUpdate(
            @Param("now") Instant now,
            @Param("staleBefore") Instant staleBefore);

    long countByCampaignIdAndStatusIn(
            UUID campaignId,
            Collection<EmailOutboxStatusJpa> statuses);

    long countByCampaignIdAndStatus(
            UUID campaignId,
            EmailOutboxStatusJpa status);

    default long countUnfinished(UUID campaignId) {
        return countByCampaignIdAndStatusIn(
                campaignId,
                List.of(EmailOutboxStatusJpa.PENDING, EmailOutboxStatusJpa.PROCESSING));
    }
}
