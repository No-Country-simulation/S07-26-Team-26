package com.ghostload.api.outreach.application.port.in;

import com.ghostload.api.outreach.domain.model.ContactImport;

import java.util.List;

public interface ListContactImportsUseCase {

    List<ContactImport> listAll();
}