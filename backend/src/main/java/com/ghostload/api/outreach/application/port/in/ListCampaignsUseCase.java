package com.ghostload.api.outreach.application.port.in;

import com.ghostload.api.outreach.domain.model.CampaignStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ListCampaignsUseCase {

    List<CampaignSummary> list(ListCampaignsQuery query);

    record ListCampaignsQuery(CampaignStatus status) {
    }

    record CampaignSummary(
            UUID id,
            String name,
            CampaignStatus status,
            int recipientCount,
            Instant createdAt) {
    }
}
