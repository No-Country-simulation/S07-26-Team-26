package com.ghostload.api.outreach.application.port.in;

public interface SendCampaignUseCase {

    SendCampaignResult send(SendCampaignCommand command);
}
