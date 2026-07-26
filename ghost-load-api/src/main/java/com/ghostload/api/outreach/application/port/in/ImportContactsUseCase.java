package com.ghostload.api.outreach.application.port.in;

public interface ImportContactsUseCase {

    ImportContactsResult importContacts(ImportContactsCommand command);
}
