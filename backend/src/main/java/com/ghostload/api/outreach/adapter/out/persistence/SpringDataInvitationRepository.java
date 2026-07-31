package com.ghostload.api.outreach.adapter.out.persistence;

import com.ghostload.api.outreach.domain.model.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataInvitationRepository extends JpaRepository<InvitationJpaEntity, UUID> {

    List<InvitationJpaEntity> findAllByCampaignIdOrderByCreatedAtAsc(UUID campaignId);

    @Query("""
            select invitation.id as invitationId,
                   invitation.token as invitationToken,
                   invitation.status as invitationStatus,
                   invitation.expiresAt as expiresAt,
                   contact.email as email,
                   contact.firstName as firstName,
                   contact.lastName as lastName,
                   contact.companyName as companyName,
                   contact.position as position,
                   campaign.name as campaignName,
                   invitation.operatorId as operatorId,
                   invitation.evaluationId as evaluationId
              from InvitationJpaEntity invitation
              join ContactJpaEntity contact on contact.id = invitation.contactId
              join CampaignJpaEntity campaign on campaign.id = invitation.campaignId
             where invitation.token = :invitationToken
            """)
    Optional<InvitationTrackingView> findTrackingByToken(
            @Param("invitationToken") UUID invitationToken);

    @Query("""
            select invitation.id as invitationId,
                   invitation.token as invitationToken,
                   invitation.status as invitationStatus,
                   invitation.expiresAt as expiresAt,
                   contact.email as email,
                   contact.firstName as firstName,
                   contact.lastName as lastName,
                   contact.companyName as companyName,
                   contact.position as position,
                   campaign.name as campaignName,
                   invitation.operatorId as operatorId,
                   invitation.evaluationId as evaluationId
              from InvitationJpaEntity invitation
              join ContactJpaEntity contact on contact.id = invitation.contactId
              join CampaignJpaEntity campaign on campaign.id = invitation.campaignId
             where invitation.evaluationId = :evaluationId
            """)
    Optional<InvitationTrackingView> findTrackingByEvaluationId(
            @Param("evaluationId") UUID evaluationId);

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

    @Modifying
    @Query("""
            update InvitationJpaEntity invitation
               set invitation.status = :newStatus,
                   invitation.visitedAt = :visitedAt
             where invitation.id = :invitationId
               and invitation.status = :expectedStatus
            """)
    int markVisited(
            @Param("invitationId") UUID invitationId,
            @Param("expectedStatus") InvitationStatus expectedStatus,
            @Param("newStatus") InvitationStatus newStatus,
            @Param("visitedAt") Instant visitedAt);

    @Modifying
    @Query("""
            update InvitationJpaEntity invitation
               set invitation.status = :newStatus,
                   invitation.operatorId = :operatorId,
                   invitation.evaluationId = :evaluationId,
                   invitation.startedAt = :startedAt
             where invitation.id = :invitationId
               and invitation.status = :expectedStatus
            """)
    int markStarted(
            @Param("invitationId") UUID invitationId,
            @Param("expectedStatus") InvitationStatus expectedStatus,
            @Param("newStatus") InvitationStatus newStatus,
            @Param("operatorId") UUID operatorId,
            @Param("evaluationId") UUID evaluationId,
            @Param("startedAt") Instant startedAt);

    @Modifying
    @Query("""
            update InvitationJpaEntity invitation
               set invitation.status = :newStatus,
                   invitation.completedAt = :completedAt
             where invitation.id = :invitationId
               and invitation.status = :expectedStatus
            """)
    int markCompleted(
            @Param("invitationId") UUID invitationId,
            @Param("expectedStatus") InvitationStatus expectedStatus,
            @Param("newStatus") InvitationStatus newStatus,
            @Param("completedAt") Instant completedAt);

    interface CampaignRecipientView {

        UUID getInvitationId();

        UUID getInvitationToken();

        InvitationStatus getInvitationStatus();

        String getFirstName();

        String getLastName();

        String getEmail();
    }

    interface InvitationTrackingView {

        UUID getInvitationId();

        UUID getInvitationToken();

        InvitationStatus getInvitationStatus();

        Instant getExpiresAt();

        String getEmail();

        String getFirstName();

        String getLastName();

        String getCompanyName();

        String getPosition();

        String getCampaignName();

        UUID getOperatorId();

        UUID getEvaluationId();
    }
}
