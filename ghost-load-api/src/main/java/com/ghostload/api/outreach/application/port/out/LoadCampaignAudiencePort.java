package com.ghostload.api.outreach.application.port.out;

import com.ghostload.api.outreach.domain.model.Contact;
import com.ghostload.api.outreach.domain.model.ContactImport;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadCampaignAudiencePort {

    Optional<CampaignAudience> load(UUID contactImportId);

    record CampaignAudience(ContactImport contactImport, List<Contact> contacts) {

        public CampaignAudience {
            contacts = List.copyOf(contacts);
        }
    }
}
