package com.ghostload.api.outreach.adapter.in.web;

import com.ghostload.api.outreach.application.port.in.ImportContactsResult;
import org.springframework.stereotype.Component;

@Component
public class ContactImportWebMapper {

    ContactImportResponse toResponse(ImportContactsResult result) {
        return new ContactImportResponse(
                result.importId(),
                result.name(),
                result.status().name(),
                result.totalRows(),
                result.validContacts(),
                result.newContacts(),
                result.existingContacts(),
                result.duplicates(),
                result.invalidRows(),
                result.issues().stream()
                        .map(issue -> new ImportIssueResponse(
                                issue.row(),
                                issue.email(),
                                issue.code().name(),
                                issue.message()))
                        .toList(),
                result.createdAt());
    }
}
