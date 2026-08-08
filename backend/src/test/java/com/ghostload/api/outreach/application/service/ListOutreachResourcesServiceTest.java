package com.ghostload.api.outreach.application.service;

import com.ghostload.api.outreach.application.port.in.ListCampaignsUseCase.ListCampaignsQuery;
import com.ghostload.api.outreach.application.port.out.LoadCampaignsPort;
import com.ghostload.api.outreach.domain.model.Campaign;
import com.ghostload.api.outreach.domain.model.CampaignStatus;
import com.ghostload.api.outreach.domain.model.ContactImport;
import com.ghostload.api.outreach.domain.model.ContactImportStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class ListOutreachResourcesServiceTest {

    private static final Instant NOW = Instant.parse("2026-08-07T16:00:00Z");

    @Test
    void shouldReturnEligibleContactImportSummaries() {
        UUID importId = UUID.fromString("257db69e-e08a-4a62-bcdb-0c6465164bdd");
        ContactImport contactImport = new ContactImport(
                importId,
                "Contactos agosto",
                ContactImportStatus.COMPLETED,
                45,
                45,
                0,
                0,
                NOW);
        ListContactImportsService service =
                new ListContactImportsService(() -> List.of(contactImport));

        var result = service.listEligible();

        assertThat(result).singleElement().satisfies(summary -> {
            assertThat(summary.importId()).isEqualTo(importId);
            assertThat(summary.name()).isEqualTo("Contactos agosto");
            assertThat(summary.validContacts()).isEqualTo(45);
            assertThat(summary.createdAt()).isEqualTo(NOW);
        });
    }

    @Test
    void shouldForwardStatusAndReturnCampaignSummaries() {
        UUID campaignId = UUID.fromString("0f04b111-7131-49f4-9fb7-9a04705e2309");
        Campaign campaign = Campaign.reconstruct(
                campaignId,
                UUID.fromString("1dc43a6c-753c-4532-8146-7902479382d1"),
                "Campaña agosto",
                null,
                "Completa el benchmark",
                "Completa el benchmark y recibe tu reporte.",
                "Comenzar evaluación",
                CampaignStatus.READY,
                45,
                null,
                "America/Lima",
                null,
                NOW);
        AtomicReference<CampaignStatus> receivedStatus = new AtomicReference<>();
        LoadCampaignsPort loader = status -> {
            receivedStatus.set(status);
            return List.of(campaign);
        };
        ListCampaignsService service = new ListCampaignsService(loader);

        var result = service.list(new ListCampaignsQuery(CampaignStatus.READY));

        assertThat(receivedStatus.get()).isEqualTo(CampaignStatus.READY);
        assertThat(result).singleElement().satisfies(summary -> {
            assertThat(summary.id()).isEqualTo(campaignId);
            assertThat(summary.status()).isEqualTo(CampaignStatus.READY);
            assertThat(summary.recipientCount()).isEqualTo(45);
            assertThat(summary.createdAt()).isEqualTo(NOW);
        });
    }
}
