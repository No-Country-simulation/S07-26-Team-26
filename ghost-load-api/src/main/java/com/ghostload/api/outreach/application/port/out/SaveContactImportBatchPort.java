package com.ghostload.api.outreach.application.port.out;

import com.ghostload.api.outreach.domain.model.Contact;
import com.ghostload.api.outreach.domain.model.ContactImport;

import java.util.List;

public interface SaveContactImportBatchPort {

    void save(ContactImport contactImport, List<Contact> contacts);
}
