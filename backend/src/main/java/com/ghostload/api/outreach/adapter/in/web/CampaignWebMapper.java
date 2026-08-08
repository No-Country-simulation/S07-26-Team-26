package com.ghostload.api.outreach.adapter.in.web;

import com.ghostload.api.outreach.application.port.in.CreateCampaignCommand;
import com.ghostload.api.outreach.application.port.in.CreateCampaignResult;
import com.ghostload.api.outreach.application.port.in.ListCampaignsUseCase.CampaignSummary;
import com.ghostload.api.outreach.application.port.in.SendCampaignResult;
import com.ghostload.api.outreach.domain.model.CampaignStatus;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class CampaignWebMapper {

    CampaignStatus toStatus(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return CampaignStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "El estado de campaña no es válido: " + value + ".");
        }
    }

    CampaignSummaryResponse toResponse(CampaignSummary summary) {
        return new CampaignSummaryResponse(
                summary.id(),
                summary.name(),
                summary.status().name(),
                summary.recipientCount(),
                summary.createdAt());
    }

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
