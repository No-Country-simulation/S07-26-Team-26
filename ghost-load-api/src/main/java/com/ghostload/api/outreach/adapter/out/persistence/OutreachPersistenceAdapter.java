package com.ghostload.api.outreach.adapter.out.persistence;

import com.ghostload.api.outreach.application.port.out.LoadCampaignAudiencePort;
import com.ghostload.api.outreach.application.port.out.LoadExistingContactEmailsPort;
import com.ghostload.api.outreach.application.port.out.SaveCampaignPort;
import com.ghostload.api.outreach.application.port.out.SaveContactImportBatchPort;
import com.ghostload.api.outreach.domain.model.Campaign;
import com.ghostload.api.outreach.domain.model.Contact;
import com.ghostload.api.outreach.domain.model.ContactEmail;
import com.ghostload.api.outreach.domain.model.ContactImport;
import com.ghostload.api.outreach.domain.model.Invitation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
public class OutreachPersistenceAdapter
        implements LoadExistingContactEmailsPort,
        SaveContactImportBatchPort,
        LoadCampaignAudiencePort,
        SaveCampaignPort {

    private final SpringDataContactImportRepository contactImportRepository;
    private final SpringDataContactRepository contactRepository;
    private final SpringDataCampaignRepository campaignRepository;
    private final SpringDataInvitationRepository invitationRepository;

    public OutreachPersistenceAdapter(
            SpringDataContactImportRepository contactImportRepository,
            SpringDataContactRepository contactRepository,
            SpringDataCampaignRepository campaignRepository,
            SpringDataInvitationRepository invitationRepository) {
        this.contactImportRepository = contactImportRepository;
        this.contactRepository = contactRepository;
        this.campaignRepository = campaignRepository;
        this.invitationRepository = invitationRepository;
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

    @Override
    @Transactional(readOnly = true)
    public Optional<CampaignAudience> load(UUID contactImportId) {
        return contactImportRepository.findById(contactImportId)
                .map(contactImport -> new CampaignAudience(
                        toDomain(contactImport),
                        contactRepository
                                .findAllByContactImportIdOrderByCreatedAtAsc(contactImportId)
                                .stream()
                                .map(this::toDomain)
                                .toList()));
    }

    @Override
    @Transactional
    public void save(Campaign campaign, List<Invitation> invitations) {
        campaignRepository.save(toEntity(campaign));
        invitationRepository.saveAll(invitations.stream().map(this::toEntity).toList());
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

    private CampaignJpaEntity toEntity(Campaign campaign) {
        return new CampaignJpaEntity(
                campaign.id(),
                campaign.contactImportId(),
                campaign.name(),
                campaign.description(),
                campaign.subject(),
                campaign.message(),
                campaign.callToActionText(),
                campaign.status(),
                campaign.recipientCount(),
                campaign.scheduledAt(),
                campaign.timezone(),
                campaign.sentAt(),
                campaign.createdAt());
    }

    private InvitationJpaEntity toEntity(Invitation invitation) {
        return new InvitationJpaEntity(
                invitation.id(),
                invitation.campaignId(),
                invitation.contactId(),
                invitation.token(),
                invitation.status(),
                invitation.createdAt());
    }

    private ContactImport toDomain(ContactImportJpaEntity entity) {
        return new ContactImport(
                entity.id(),
                entity.name(),
                entity.status(),
                entity.totalRows(),
                entity.validContacts(),
                entity.duplicates(),
                entity.invalidRows(),
                entity.createdAt());
    }

    private Contact toDomain(ContactJpaEntity entity) {
        return new Contact(
                entity.id(),
                entity.contactImportId(),
                entity.firstName(),
                entity.lastName(),
                new ContactEmail(entity.email()),
                entity.companyName(),
                entity.position(),
                entity.createdAt());
    }
}
