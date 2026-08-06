package com.ghostload.api.outreach.application.service;

import com.ghostload.api.outreach.application.port.in.SendCampaignCommand;
import com.ghostload.api.outreach.application.port.out.LoadCampaignDeliveryPort;
import com.ghostload.api.outreach.application.port.out.QueueCampaignEmailsPort;
import com.ghostload.api.outreach.domain.exception.CampaignNotFoundException;
import com.ghostload.api.outreach.domain.exception.InvalidCampaignStateException;
import com.ghostload.api.outreach.domain.model.Campaign;
import com.ghostload.api.outreach.domain.model.CampaignStatus;
import com.ghostload.api.outreach.domain.model.InvitationStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SendCampaignServiceTest {

    private static final Instant NOW = Instant.parse("2026-07-27T14:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final UUID IMPORT_ID = UUID.randomUUID();

    @Test
    void shouldQueueReadyCampaignAndReturnSendingStatus() {
        Campaign campaign = readyCampaign(null);
        var recipient = recipient(InvitationStatus.UPLOADED);
        AtomicReference<List<QueueCampaignEmailsPort.QueuedEmail>> queued =
                new AtomicReference<>();
        SendCampaignService service = new SendCampaignService(
                ignored -> Optional.of(new LoadCampaignDeliveryPort.CampaignDelivery(
                        campaign,
                        List.of(recipient))),
                (queuedCampaign, emails) -> queued.set(emails),
                CLOCK);

        var result = service.send(new SendCampaignCommand(campaign.id()));

        assertThat(result.status()).isEqualTo(CampaignStatus.SENDING);
        assertThat(queued.get()).hasSize(1);
        assertThat(queued.get().getFirst().recipientEmail())
                .isEqualTo("ana@empresa.com");
        assertThat(queued.get().getFirst().recipientName())
                .isEqualTo("Ana Torres");
        assertThat(queued.get().getFirst().availableAt()).isEqualTo(NOW);
    }

    @Test
    void shouldRespectFutureScheduleWhenQueueing() {
        Instant scheduledAt = NOW.plusSeconds(3_600);
        Campaign campaign = readyCampaign(scheduledAt);
        AtomicReference<List<QueueCampaignEmailsPort.QueuedEmail>> queued =
                new AtomicReference<>();
        SendCampaignService service = new SendCampaignService(
                ignored -> Optional.of(new LoadCampaignDeliveryPort.CampaignDelivery(
                        campaign,
                        List.of(recipient(InvitationStatus.UPLOADED)))),
                (queuedCampaign, emails) -> queued.set(emails),
                CLOCK);

        service.send(new SendCampaignCommand(campaign.id()));

        assertThat(queued.get().getFirst().availableAt()).isEqualTo(scheduledAt);
    }

    @Test
    void shouldRejectUnknownCampaign() {
        UUID campaignId = UUID.randomUUID();
        SendCampaignService service = new SendCampaignService(
                ignored -> Optional.empty(),
                (campaign, emails) -> {
                },
                CLOCK);

        assertThatThrownBy(() -> service.send(new SendCampaignCommand(campaignId)))
                .isInstanceOf(CampaignNotFoundException.class);
    }

    @Test
    void shouldRejectCampaignWithAlreadyProcessedInvitation() {
        Campaign campaign = readyCampaign(null);
        SendCampaignService service = new SendCampaignService(
                ignored -> Optional.of(new LoadCampaignDeliveryPort.CampaignDelivery(
                        campaign,
                        List.of(recipient(InvitationStatus.SENT)))),
                (queuedCampaign, emails) -> {
                },
                CLOCK);

        assertThatThrownBy(() -> service.send(new SendCampaignCommand(campaign.id())))
                .isInstanceOf(InvalidCampaignStateException.class);
        assertThat(campaign.status()).isEqualTo(CampaignStatus.READY);
    }

    @Test
    void shouldNotDuplicateEmailsOnRetryAfterQueueing() {
        Campaign campaign = readyCampaign(null);
        AtomicInteger queueCalls = new AtomicInteger();
        SendCampaignService service = new SendCampaignService(
                ignored -> Optional.of(new LoadCampaignDeliveryPort.CampaignDelivery(
                        campaign,
                        List.of(recipient(InvitationStatus.UPLOADED)))),
                (queuedCampaign, emails) -> queueCalls.incrementAndGet(),
                CLOCK);

        // Primer envío: encola los correos y deja la campaña en SENDING.
        service.send(new SendCampaignCommand(campaign.id()));
        assertThat(queueCalls.get()).isEqualTo(1);

        // Un retry de envío con las invitations YA procesadas (SENT) no debe
        // volver a encolar: se rechaza y no se duplican invitations/correos.
        SendCampaignService retryService = new SendCampaignService(
                ignored -> Optional.of(new LoadCampaignDeliveryPort.CampaignDelivery(
                        campaign,
                        List.of(recipient(InvitationStatus.SENT)))),
                (queuedCampaign, emails) -> queueCalls.incrementAndGet(),
                CLOCK);

        assertThatThrownBy(() -> retryService.send(new SendCampaignCommand(campaign.id())))
                .isInstanceOf(InvalidCampaignStateException.class);
        assertThat(queueCalls.get()).isEqualTo(1);
        assertThat(campaign.status()).isEqualTo(CampaignStatus.SENDING);
    }

    private Campaign readyCampaign(Instant scheduledAt) {
        return Campaign.ready(
                IMPORT_ID,
                "Benchmark julio",
                "Invitación para operadores",
                "Conoce la madurez de tu data center",
                "Completa el benchmark y recibe tu reporte personalizado.",
                "Comenzar evaluación",
                1,
                scheduledAt,
                "America/Lima",
                NOW);
    }

    private LoadCampaignDeliveryPort.CampaignRecipient recipient(
            InvitationStatus status) {
        return new LoadCampaignDeliveryPort.CampaignRecipient(
                UUID.randomUUID(),
                UUID.randomUUID(),
                status,
                "Ana",
                "Torres",
                "ana@empresa.com");
    }
}
