package com.ghostload.api.outreach.application.port.out;

import com.ghostload.api.outreach.domain.model.ContactImport;

import java.util.List;

public interface LoadContactImportsPort {

    List<ContactImport> loadEligible();
}
