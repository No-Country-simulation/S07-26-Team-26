package com.ghostload.api.outreach.application.port.out;

import com.ghostload.api.outreach.domain.model.Contact;
import com.ghostload.api.outreach.domain.model.ContactImport;

import java.util.List;
import java.util.UUID;

public interface SaveContactImportBatchPort {

    void save(
            ContactImport contactImport,
            List<Contact> newContacts,
            List<UUID> audienceContactIds);
}
