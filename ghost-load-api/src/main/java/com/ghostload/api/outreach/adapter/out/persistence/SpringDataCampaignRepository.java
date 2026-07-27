package com.ghostload.api.outreach.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface SpringDataCampaignRepository extends JpaRepository<CampaignJpaEntity, UUID> {
}
