package com.ghostload.api.outreach.adapter.in.web;

import com.ghostload.api.outreach.application.port.in.CreateCampaignCommand;
import com.ghostload.api.outreach.application.port.in.CreateCampaignResult;
import com.ghostload.api.outreach.application.port.in.SendCampaignResult;
import org.springframework.stereotype.Component;

@Component
public class CampaignWebMapper {

    CreateCampaignCommand toCommand(CreateCampaignRequest request) {
        return new CreateCampaignCommand(
                request.name(),
                request.description(),
                request.subject(),
                request.message(),
                request.callToActionText(),
                request.contactImportId(),
                request.scheduledAt(),
                request.timezone());
    }

    CampaignResponse toResponse(CreateCampaignResult result) {
        return new CampaignResponse(
                result.id(),
                result.name(),
                result.status().name(),
                result.subject(),
                result.recipientCount(),
                result.scheduledAt(),
                result.sentAt(),
                result.createdAt());
    }

    CampaignResponse toResponse(SendCampaignResult result) {
        return new CampaignResponse(
                result.id(),
                result.name(),
                result.status().name(),
                result.subject(),
                result.recipientCount(),
                result.scheduledAt(),
                result.sentAt(),
                result.createdAt());
    }
}
