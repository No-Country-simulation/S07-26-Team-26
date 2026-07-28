package com.ghostload.api.outreach.application.port.out;

import com.ghostload.api.outreach.domain.model.Campaign;
import com.ghostload.api.outreach.domain.model.InvitationStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadCampaignDeliveryPort {

    Optional<CampaignDelivery> loadCampaignDelivery(UUID campaignId);

    record CampaignDelivery(
            Campaign campaign,
            List<CampaignRecipient> recipients) {
    }

    record CampaignRecipient(
            UUID invitationId,
            UUID invitationToken,
            InvitationStatus invitationStatus,
            String firstName,
            String lastName,
            String email) {
    }
}
