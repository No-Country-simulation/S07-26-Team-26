package com.ghostload.api.outreach.adapter.out.persistence;

import com.ghostload.api.outreach.domain.model.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

interface SpringDataCampaignRepository extends JpaRepository<CampaignJpaEntity, UUID> {

    List<CampaignJpaEntity> findAllByOrderByCreatedAtDesc();

    @Modifying
    @Query("""
            update CampaignJpaEntity campaign
               set campaign.status = :newStatus
             where campaign.id = :campaignId
               and campaign.status = :expectedStatus
            """)
    int transitionStatus(
            @Param("campaignId") UUID campaignId,
            @Param("expectedStatus") CampaignStatus expectedStatus,
            @Param("newStatus") CampaignStatus newStatus);

    @Modifying
    @Query("""
            update CampaignJpaEntity campaign
               set campaign.status = :newStatus,
                   campaign.sentAt = :finishedAt
             where campaign.id = :campaignId
               and campaign.status = :expectedStatus
            """)
    int finishSending(
            @Param("campaignId") UUID campaignId,
            @Param("expectedStatus") CampaignStatus expectedStatus,
            @Param("newStatus") CampaignStatus newStatus,
            @Param("finishedAt") Instant finishedAt);
}
