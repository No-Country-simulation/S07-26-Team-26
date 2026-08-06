package com.ghostload.api.outreach.domain.model;

import com.ghostload.api.outreach.domain.exception.InvalidCampaignStateException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CampaignStateTransitionTest {

    private static final Instant NOW = Instant.parse("2026-07-27T14:00:00Z");
    private static final UUID IMPORT_ID = UUID.randomUUID();

    @Test
    void shouldAllowStartSendingOnlyFromReady() {
        Campaign ready = campaign(CampaignStatus.READY);
        ready.startSending();
        assertThat(ready.status()).isEqualTo(CampaignStatus.SENDING);
    }

    @Test
    void shouldRejectStartSendingFromAnyOtherState() {
        for (CampaignStatus status : CampaignStatus.values()) {
            if (status == CampaignStatus.READY) {
                continue;
            }
            Campaign campaign = campaign(status);
            assertThatThrownBy(campaign::startSending)
                    .describedAs("startSending desde %s", status)
                    .isInstanceOf(InvalidCampaignStateException.class)
                    .hasMessageContaining("no se puede enviar desde el estado");
            assertThat(campaign.status()).isEqualTo(status);
        }
    }

    private Campaign campaign(CampaignStatus status) {
        return Campaign.reconstruct(
                UUID.randomUUID(),
                IMPORT_ID,
                "Benchmark julio",
                "Invitación para operadores",
                "Conoce la madurez de tu data center",
                "Completa el benchmark y recibe tu reporte personalizado.",
                "Comenzar evaluación",
                status,
                1,
                null,
                null,
                status == CampaignStatus.ACTIVE || status == CampaignStatus.COMPLETED
                        || status == CampaignStatus.FAILED ? NOW : null,
                NOW);
    }
}
