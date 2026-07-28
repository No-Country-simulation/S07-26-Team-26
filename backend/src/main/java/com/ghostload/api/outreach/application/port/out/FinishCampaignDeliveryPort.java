package com.ghostload.api.outreach.application.port.out;

import com.ghostload.api.outreach.domain.model.CampaignStatus;

import java.time.Instant;
import java.util.UUID;

public interface FinishCampaignDeliveryPort {

    void finish(UUID campaignId, CampaignStatus status, Instant finishedAt);
}
