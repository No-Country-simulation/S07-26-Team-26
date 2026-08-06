package com.ghostload.api.outreach.adapter.in.web;

import com.ghostload.api.outreach.application.port.in.CreateCampaignCommand;
import com.ghostload.api.outreach.application.port.in.CreateCampaignResult;
import com.ghostload.api.outreach.application.port.in.SendCampaignResult;
import com.ghostload.api.outreach.application.port.out.LoadCampaignTrackingPort;
import com.ghostload.api.outreach.domain.model.Campaign;
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

    CampaignResponse toResponse(Campaign campaign) {
        return new CampaignResponse(
                campaign.id(),
                campaign.name(),
                campaign.status().name(),
                campaign.subject(),
                campaign.recipientCount(),
                campaign.scheduledAt(),
                campaign.sentAt(),
                campaign.createdAt());
    }

    CampaignTrackingResponse toTrackingResponse(
            LoadCampaignTrackingPort.CampaignTracking tracking) {
        Campaign campaign = tracking.campaign();
        return new CampaignTrackingResponse(
                campaign.id(),
                campaign.name(),
                campaign.status().name(),
                campaign.description(),
                campaign.subject(),
                campaign.callToActionText(),
                campaign.recipientCount(),
                campaign.scheduledAt(),
                campaign.sentAt(),
                campaign.createdAt(),
                tracking.invitations().stream()
                        .map(invitation -> new InvitationTrackingResponse(
                                invitation.invitationId(),
                                invitation.firstName(),
                                invitation.lastName(),
                                invitation.email(),
                                invitation.status().name(),
                                invitation.sentAt(),
                                invitation.visitedAt(),
                                invitation.startedAt(),
                                invitation.completedAt(),
                                invitation.failedAt(),
                                invitation.failureReason(),
                                invitation.createdAt()))
                        .toList());
    }
}
