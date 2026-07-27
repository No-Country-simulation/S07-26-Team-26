package com.ghostload.api.outreach.application.port.out;

import com.ghostload.api.outreach.domain.model.Campaign;
import com.ghostload.api.outreach.domain.model.Invitation;

import java.util.List;

public interface SaveCampaignPort {

    void save(Campaign campaign, List<Invitation> invitations);
}
