package com.ghostload.api.outreach.application.port.out;

import com.ghostload.api.outreach.domain.model.Contact;

import java.util.Map;
import java.util.Set;

public interface LoadExistingContactsPort {

    Map<String, Contact> loadExistingContacts(Set<String> normalizedEmails);
}
