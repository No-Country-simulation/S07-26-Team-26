package com.ghostload.api.outreach.application.port.in;

import com.ghostload.api.outreach.domain.model.Campaign;

import java.util.List;

public interface ListCampaignsUseCase {

    List<Campaign> listAll();
}