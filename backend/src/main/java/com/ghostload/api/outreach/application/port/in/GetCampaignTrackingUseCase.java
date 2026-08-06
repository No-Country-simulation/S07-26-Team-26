package com.ghostload.api.outreach.application.port.in;

import com.ghostload.api.outreach.application.port.out.LoadCampaignTrackingPort;

import java.util.Optional;
import java.util.UUID;

public interface GetCampaignTrackingUseCase {

    Optional<LoadCampaignTrackingPort.CampaignTracking> getTracking(UUID campaignId);
}