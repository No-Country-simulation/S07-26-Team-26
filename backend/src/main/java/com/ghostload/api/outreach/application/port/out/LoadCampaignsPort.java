package com.ghostload.api.outreach.application.port.out;

import com.ghostload.api.outreach.domain.model.Campaign;
import com.ghostload.api.outreach.domain.model.CampaignStatus;

import java.util.List;

public interface LoadCampaignsPort {

    List<Campaign> load(CampaignStatus status);
}
