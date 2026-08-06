package com.ghostload.api.outreach.application.service;

import com.ghostload.api.outreach.application.port.in.ListContactImportsUseCase;
import com.ghostload.api.outreach.application.port.out.ListContactImportsPort;
import com.ghostload.api.outreach.domain.model.ContactImport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListContactImportsService implements ListContactImportsUseCase {

    private final ListContactImportsPort contactImportsPort;

    public ListContactImportsService(ListContactImportsPort contactImportsPort) {
        this.contactImportsPort = contactImportsPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactImport> listAll() {
        return contactImportsPort.findImportsOrderByCreatedAtDesc();
    }
}