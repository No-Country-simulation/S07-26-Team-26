package com.ghostload.api.outreach.domain.exception;

import com.ghostload.api.outreach.domain.model.CampaignStatus;

public class InvalidCampaignStateException extends RuntimeException {

    public InvalidCampaignStateException(CampaignStatus status) {
        super("La campaña no se puede enviar desde el estado " + status + ".");
    }

    public InvalidCampaignStateException(String message) {
        super(message);
    }
}
