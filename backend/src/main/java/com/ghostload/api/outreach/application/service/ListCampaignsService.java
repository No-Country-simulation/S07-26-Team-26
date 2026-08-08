package com.ghostload.api.outreach.application.service;

import com.ghostload.api.outreach.application.port.in.ListCampaignsUseCase;
import com.ghostload.api.outreach.application.port.out.LoadCampaignsPort;

import java.util.List;
import java.util.Objects;

public final class ListCampaignsService implements ListCampaignsUseCase {

    private final LoadCampaignsPort loadCampaignsPort;

    public ListCampaignsService(LoadCampaignsPort loadCampaignsPort) {
        this.loadCampaignsPort = Objects.requireNonNull(loadCampaignsPort);
    }

    @Override
    public List<CampaignSummary> list(ListCampaignsQuery query) {
        Objects.requireNonNull(query, "La consulta de campañas es obligatoria.");
        return loadCampaignsPort.load(query.status()).stream()
                .map(campaign -> new CampaignSummary(
                        campaign.id(),
                        campaign.name(),
                        campaign.status(),
                        campaign.recipientCount(),
                        campaign.createdAt()))
                .toList();
    }
}
