package com.ghostload.api.outreach.adapter.out.persistence;

import com.ghostload.api.outreach.domain.model.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SpringDataInvitationRepository extends JpaRepository<InvitationJpaEntity, UUID> {

    List<InvitationJpaEntity> findAllByCampaignIdOrderByCreatedAtAsc(UUID campaignId);

    @Query("""
            select count(invitation)
              from InvitationJpaEntity invitation
             where invitation.sentAt is not null
               and (:campaignId is null or invitation.campaignId = :campaignId)
            """)
    long countSent(@Param("campaignId") UUID campaignId);

    @Query("""
            select count(invitation)
              from InvitationJpaEntity invitation
             where invitation.visitedAt is not null
               and (:campaignId is null or invitation.campaignId = :campaignId)
            """)
    long countVisited(@Param("campaignId") UUID campaignId);

    @Query("""
            select invitation.id as invitationId,
                   invitation.token as invitationToken,
                   invitation.status as invitationStatus,
                   contact.firstName as firstName,
                   contact.lastName as lastName,
                   contact.email as email
              from InvitationJpaEntity invitation
              join ContactJpaEntity contact on contact.id = invitation.contactId
             where invitation.campaignId = :campaignId
             order by invitation.createdAt
            """)
    List<CampaignRecipientView> findRecipientsByCampaignId(
            @Param("campaignId") UUID campaignId);

    @Modifying
    @Query("""
            update InvitationJpaEntity invitation
               set invitation.status = :newStatus,
                   invitation.sentAt = :sentAt
             where invitation.id = :invitationId
               and invitation.status = :expectedStatus
            """)
    int markSent(
            @Param("invitationId") UUID invitationId,
            @Param("expectedStatus") InvitationStatus expectedStatus,
            @Param("newStatus") InvitationStatus newStatus,
            @Param("sentAt") Instant sentAt);

    @Modifying
    @Query("""
            update InvitationJpaEntity invitation
               set invitation.status = :newStatus,
                   invitation.failedAt = :failedAt,
                   invitation.failureReason = :failureReason
             where invitation.id = :invitationId
               and invitation.status = :expectedStatus
            """)
    int markFailed(
            @Param("invitationId") UUID invitationId,
            @Param("expectedStatus") InvitationStatus expectedStatus,
            @Param("newStatus") InvitationStatus newStatus,
            @Param("failedAt") Instant failedAt,
            @Param("failureReason") String failureReason);

    interface CampaignRecipientView {

        UUID getInvitationId();

        UUID getInvitationToken();

        InvitationStatus getInvitationStatus();

        String getFirstName();

        String getLastName();

        String getEmail();
    }
}
