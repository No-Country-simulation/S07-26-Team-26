package com.ghostload.api.outreach.adapter.out.persistence;

import com.ghostload.api.outreach.application.port.out.LoadExistingContactEmailsPort;
import com.ghostload.api.outreach.application.port.out.SaveContactImportBatchPort;
import com.ghostload.api.outreach.domain.model.Contact;
import com.ghostload.api.outreach.domain.model.ContactImport;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Component
public class OutreachPersistenceAdapter
        implements LoadExistingContactEmailsPort, SaveContactImportBatchPort {

    private final SpringDataContactImportRepository contactImportRepository;
    private final SpringDataContactRepository contactRepository;

    public OutreachPersistenceAdapter(
            SpringDataContactImportRepository contactImportRepository,
            SpringDataContactRepository contactRepository) {
        this.contactImportRepository = contactImportRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    public Set<String> loadExistingEmails(Set<String> normalizedEmails) {
        if (normalizedEmails.isEmpty()) {
            return Set.of();
        }
        return Set.copyOf(contactRepository.findExistingNormalizedEmails(normalizedEmails));
    }

    @Override
    @Transactional
    public void save(ContactImport contactImport, List<Contact> contacts) {
        contactImportRepository.save(toEntity(contactImport));
        contactRepository.saveAll(contacts.stream().map(this::toEntity).toList());
    }

    private ContactImportJpaEntity toEntity(ContactImport contactImport) {
        return new ContactImportJpaEntity(
                contactImport.id(),
                contactImport.name(),
                contactImport.status(),
                contactImport.totalRows(),
                contactImport.validContacts(),
                contactImport.duplicates(),
                contactImport.invalidRows(),
                contactImport.createdAt());
    }

    private ContactJpaEntity toEntity(Contact contact) {
        return new ContactJpaEntity(
                contact.id(),
                contact.contactImportId(),
                contact.firstName(),
                contact.lastName(),
                contact.email().value(),
                contact.companyName(),
                contact.position(),
                contact.createdAt());
    }
}
