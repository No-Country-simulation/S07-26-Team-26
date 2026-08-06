package com.ghostload.api.outreach.adapter.out.persistence;

import com.ghostload.api.outreach.application.port.out.LoadCampaignAudiencePort;
import com.ghostload.api.outreach.application.port.out.LoadCampaignDeliveryPort;
import com.ghostload.api.outreach.application.port.out.LoadCampaignTrackingPort;
import com.ghostload.api.outreach.application.port.out.ListCampaignsPort;
import com.ghostload.api.outreach.application.port.out.ListContactImportsPort;
import com.ghostload.api.outreach.application.port.out.LoadExistingContactsPort;
import com.ghostload.api.outreach.application.port.out.QueueCampaignEmailsPort;
import com.ghostload.api.outreach.application.port.out.SaveCampaignPort;
import com.ghostload.api.outreach.application.port.out.SaveContactImportBatchPort;
import com.ghostload.api.outreach.domain.exception.InvalidCampaignStateException;
import com.ghostload.api.outreach.domain.model.Campaign;
import com.ghostload.api.outreach.domain.model.CampaignStatus;
import com.ghostload.api.outreach.domain.model.Contact;
import com.ghostload.api.outreach.domain.model.ContactEmail;
import com.ghostload.api.outreach.domain.model.ContactImport;
import com.ghostload.api.outreach.domain.model.Invitation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OutreachPersistenceAdapter
        implements LoadExistingContactsPort,
        SaveContactImportBatchPort,
        LoadCampaignAudiencePort,
        SaveCampaignPort,
        LoadCampaignDeliveryPort,
        QueueCampaignEmailsPort,
        ListContactImportsPort,
        ListCampaignsPort,
        LoadCampaignTrackingPort {

    private final SpringDataContactImportRepository contactImportRepository;
    private final SpringDataContactRepository contactRepository;
    private final SpringDataContactImportContactRepository contactImportContactRepository;
    private final SpringDataCampaignRepository campaignRepository;
    private final SpringDataInvitationRepository invitationRepository;
    private final SpringDataEmailOutboxRepository emailOutboxRepository;

    public OutreachPersistenceAdapter(
            SpringDataContactImportRepository contactImportRepository,
            SpringDataContactRepository contactRepository,
            SpringDataContactImportContactRepository contactImportContactRepository,
            SpringDataCampaignRepository campaignRepository,
            SpringDataInvitationRepository invitationRepository,
            SpringDataEmailOutboxRepository emailOutboxRepository) {
        this.contactImportRepository = contactImportRepository;
        this.contactRepository = contactRepository;
        this.contactImportContactRepository = contactImportContactRepository;
        this.campaignRepository = campaignRepository;
        this.invitationRepository = invitationRepository;
        this.emailOutboxRepository = emailOutboxRepository;
    }

    @Override
    public Map<String, Contact> loadExistingContacts(Set<String> normalizedEmails) {
        if (normalizedEmails.isEmpty()) {
            return Map.of();
        }
        return contactRepository.findExistingContacts(normalizedEmails).stream()
                .map(this::toDomain)
                .collect(Collectors.toUnmodifiableMap(
                        contact -> contact.email().value(),
                        Function.identity()));
    }

    @Override
    @Transactional
    public void save(
            ContactImport contactImport,
            List<Contact> newContacts,
            List<UUID> audienceContactIds) {
        contactImportRepository.saveAndFlush(toEntity(contactImport));
        contactRepository.saveAllAndFlush(
                newContacts.stream().map(this::toEntity).toList());
        contactImportContactRepository.saveAll(
                audienceContactIds.stream()
                        .map(contactId -> new ContactImportContactJpaEntity(
                                contactImport.id(),
                                contactId,
                                contactImport.createdAt()))
                        .toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CampaignAudience> load(UUID contactImportId) {
        return contactImportRepository.findById(contactImportId)
                .map(contactImport -> new CampaignAudience(
                        toDomain(contactImport),
                        contactImportContactRepository
                                .findContactsByImportId(contactImportId)
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

    @Override
    @Transactional(readOnly = true)
    public Optional<CampaignDelivery> loadCampaignDelivery(UUID campaignId) {
        return campaignRepository.findById(campaignId)
                .map(campaign -> new CampaignDelivery(
                        toDomain(campaign),
                        invitationRepository
                                .findRecipientsByCampaignId(campaignId)
                                .stream()
                                .map(this::toRecipient)
                                .toList()));
    }

    @Override
    @Transactional
    public void queue(Campaign campaign, List<QueuedEmail> emails) {
        int updated = campaignRepository.transitionStatus(
                campaign.id(),
                CampaignStatus.READY,
                CampaignStatus.SENDING);
        if (updated != 1) {
            throw new InvalidCampaignStateException(
                    "La campaña ya fue enviada o está siendo procesada.");
        }
        emailOutboxRepository.saveAll(emails.stream().map(this::toEntity).toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactImport> findImportsOrderByCreatedAtDesc() {
        return contactImportRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Campaign> findCampaignsOrderByCreatedAtDesc() {
        return campaignRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LoadCampaignTrackingPort.CampaignTracking> loadTracking(UUID campaignId) {
        return campaignRepository.findById(campaignId)
                .map(campaign -> new LoadCampaignTrackingPort.CampaignTracking(
                        toDomain(campaign),
                        invitationRepository
                                .findTrackingByCampaignId(campaignId)
                                .stream()
                                .map(this::toTracking)
                                .toList()));
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

    private EmailOutboxJpaEntity toEntity(QueuedEmail email) {
        return new EmailOutboxJpaEntity(
                email.id(),
                email.campaignId(),
                email.invitationId(),
                email.recipientEmail(),
                email.recipientName(),
                email.subject(),
                email.message(),
                email.callToActionText(),
                email.invitationToken(),
                email.availableAt(),
                email.createdAt());
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
                entity.firstName(),
                entity.lastName(),
                new ContactEmail(entity.email()),
                entity.companyName(),
                entity.position(),
                entity.createdAt());
    }

    private Campaign toDomain(CampaignJpaEntity entity) {
        return Campaign.reconstruct(
                entity.id(),
                entity.contactImportId(),
                entity.name(),
                entity.description(),
                entity.subject(),
                entity.message(),
                entity.callToActionText(),
                entity.status(),
                entity.recipientCount(),
                entity.scheduledAt(),
                entity.timezone(),
                entity.sentAt(),
                entity.createdAt());
    }

    private CampaignRecipient toRecipient(
            SpringDataInvitationRepository.CampaignRecipientView recipient) {
        return new CampaignRecipient(
                recipient.getInvitationId(),
                recipient.getInvitationToken(),
                recipient.getInvitationStatus(),
                recipient.getFirstName(),
                recipient.getLastName(),
                recipient.getEmail());
    }

    private LoadCampaignTrackingPort.InvitationTracking toTracking(
            SpringDataInvitationRepository.InvitationTrackingView tracking) {
        return new LoadCampaignTrackingPort.InvitationTracking(
                tracking.getInvitationId(),
                tracking.getFirstName(),
                tracking.getLastName(),
                tracking.getEmail(),
                tracking.getInvitationStatus(),
                tracking.getSentAt(),
                tracking.getVisitedAt(),
                tracking.getStartedAt(),
                tracking.getCompletedAt(),
                tracking.getFailedAt(),
                tracking.getFailureReason(),
                tracking.getCreatedAt());
    }
}
