package com.ghostload.api.outreach.application.port.out;

import java.util.Set;

public interface LoadExistingContactEmailsPort {

    Set<String> loadExistingEmails(Set<String> normalizedEmails);
}
