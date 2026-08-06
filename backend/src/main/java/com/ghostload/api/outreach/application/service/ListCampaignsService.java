package com.ghostload.api.outreach.application.service;

import com.ghostload.api.outreach.application.port.in.ListCampaignsUseCase;
import com.ghostload.api.outreach.application.port.out.ListCampaignsPort;
import com.ghostload.api.outreach.domain.model.Campaign;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListCampaignsService implements ListCampaignsUseCase {

    private final ListCampaignsPort listCampaignsPort;

    public ListCampaignsService(ListCampaignsPort listCampaignsPort) {
        this.listCampaignsPort = listCampaignsPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Campaign> listAll() {
        return listCampaignsPort.findCampaignsOrderByCreatedAtDesc();
    }
}