package com.ghostload.api.outreach.domain.exception;

import java.util.UUID;

public class CampaignNotFoundException extends RuntimeException {

    public CampaignNotFoundException(UUID campaignId) {
        super("No existe la campaña " + campaignId + ".");
    }
}
