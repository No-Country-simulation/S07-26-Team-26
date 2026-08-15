package com.ghostload.api.outreach.application.service;

import com.ghostload.api.outreach.application.port.in.ListContactImportsUseCase;
import com.ghostload.api.outreach.application.port.out.LoadContactImportsPort;

import java.util.List;
import java.util.Objects;

public final class ListContactImportsService implements ListContactImportsUseCase {

    private final LoadContactImportsPort loadContactImportsPort;

    public ListContactImportsService(LoadContactImportsPort loadContactImportsPort) {
        this.loadContactImportsPort = Objects.requireNonNull(loadContactImportsPort);
    }

    @Override
    public List<ContactImportSummary> listEligible() {
        return loadContactImportsPort.loadEligible().stream()
                .map(contactImport -> new ContactImportSummary(
                        contactImport.id(),
                        contactImport.name(),
                        contactImport.validContacts(),
                        contactImport.createdAt()))
                .toList();
    }
}
