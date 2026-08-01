package com.ghostload.api.outreach.application.port.in;

public interface CreateCampaignUseCase {

    CreateCampaignResult create(CreateCampaignCommand command);
}
