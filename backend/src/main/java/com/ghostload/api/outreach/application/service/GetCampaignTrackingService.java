package com.ghostload.api.outreach.application.service;

import com.ghostload.api.outreach.application.port.in.GetCampaignTrackingUseCase;
import com.ghostload.api.outreach.application.port.out.LoadCampaignTrackingPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class GetCampaignTrackingService implements GetCampaignTrackingUseCase {

    private final LoadCampaignTrackingPort trackingPort;

    public GetCampaignTrackingService(LoadCampaignTrackingPort trackingPort) {
        this.trackingPort = trackingPort;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LoadCampaignTrackingPort.CampaignTracking> getTracking(UUID campaignId) {
        return trackingPort.loadTracking(campaignId);
    }
}