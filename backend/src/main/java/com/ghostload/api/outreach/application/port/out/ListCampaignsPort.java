package com.ghostload.api.outreach.application.port.out;

import com.ghostload.api.outreach.domain.model.Campaign;

import java.util.List;

public interface ListCampaignsPort {

    List<Campaign> findCampaignsOrderByCreatedAtDesc();
}