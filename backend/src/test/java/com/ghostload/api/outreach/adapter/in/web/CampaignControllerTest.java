package com.ghostload.api.outreach.adapter.in.web;

import com.ghostload.api.outreach.application.port.in.CreateCampaignCommand;
import com.ghostload.api.outreach.application.port.in.CreateCampaignResult;
import com.ghostload.api.outreach.application.port.in.CreateCampaignUseCase;
import com.ghostload.api.outreach.application.port.in.SendCampaignResult;
import com.ghostload.api.outreach.application.port.in.SendCampaignUseCase;
import com.ghostload.api.outreach.application.port.in.ListCampaignsUseCase;
import com.ghostload.api.outreach.domain.model.CampaignStatus;
import com.ghostload.api.shared.adapter.in.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampaignControllerTest {

    @Test
    void shouldMapRequestAndReturnCreatedCampaign() throws Exception {
        UUID campaignId =
                UUID.fromString("0f04b111-7131-49f4-9fb7-9a04705e2309");
        UUID importId =
                UUID.fromString("1dc43a6c-753c-4532-8146-7902479382d1");
        AtomicReference<CreateCampaignCommand> receivedCommand = new AtomicReference<>();
        CreateCampaignUseCase useCase = command -> {
            receivedCommand.set(command);
            return new CreateCampaignResult(
                    campaignId,
                    "Benchmark julio",
                    CampaignStatus.READY,
                    "Conoce la madurez de tu data center",
                    2,
                    null,
                    null,
                    Instant.parse("2026-07-26T18:00:00Z"));
        };
        CampaignController controller =
                new CampaignController(
                        useCase,
                        command -> {
                            throw new AssertionError("No debe enviarse la campaña.");
                        },
                        query -> List.of(),
                        new CampaignWebMapper());
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(post("/api/v1/admin/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Benchmark julio",
                                  "subject": "Conoce la madurez de tu data center",
                                  "message": "Completa el benchmark y recibe tu reporte.",
                                  "callToActionText": "Comenzar evaluación",
                                  "contactImportId": "%s",
                                  "timezone": "America/Lima"
                                }
                                """.formatted(importId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(campaignId.toString()))
                .andExpect(jsonPath("$.status").value("READY"))
                .andExpect(jsonPath("$.recipientCount").value(2))
                .andExpect(jsonPath("$.sentAt").doesNotExist());

        assertThat(receivedCommand.get().contactImportId()).isEqualTo(importId);
        assertThat(receivedCommand.get().callToActionText())
                .isEqualTo("Comenzar evaluación");
    }

    @Test
    void shouldRejectInvalidRequestBeforeCallingUseCase() throws Exception {
        CreateCampaignUseCase useCase = command -> {
            throw new AssertionError("El caso de uso no debe ejecutarse.");
        };
        CampaignController controller =
                new CampaignController(
                        useCase,
                        command -> {
                            throw new AssertionError("No debe enviarse la campaña.");
                        },
                        query -> List.of(),
                        new CampaignWebMapper());
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(post("/api/v1/admin/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "subject": "a",
                                  "message": "corto"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldAcceptCampaignForAsynchronousDelivery() throws Exception {
        UUID campaignId =
                UUID.fromString("0f04b111-7131-49f4-9fb7-9a04705e2309");
        SendCampaignUseCase sendUseCase = command -> new SendCampaignResult(
                command.campaignId(),
                "Benchmark julio",
                CampaignStatus.SENDING,
                "Conoce la madurez de tu data center",
                2,
                null,
                null,
                Instant.parse("2026-07-26T18:00:00Z"));
        CampaignController controller = new CampaignController(
                command -> {
                    throw new AssertionError("No debe crearse una campaña.");
                },
                sendUseCase,
                query -> List.of(),
                new CampaignWebMapper());
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(post(
                        "/api/v1/admin/campaigns/{campaignId}/send",
                        campaignId))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(campaignId.toString()))
                .andExpect(jsonPath("$.status").value("SENDING"))
                .andExpect(jsonPath("$.recipientCount").value(2));
    }

    @Test
    void shouldListCampaignsFilteredByStatus() throws Exception {
        UUID campaignId = UUID.fromString("0f04b111-7131-49f4-9fb7-9a04705e2309");
        Instant createdAt = Instant.parse("2026-08-07T16:00:00Z");
        AtomicReference<ListCampaignsUseCase.ListCampaignsQuery> receivedQuery =
                new AtomicReference<>();
        ListCampaignsUseCase listUseCase = query -> {
            receivedQuery.set(query);
            return List.of(new ListCampaignsUseCase.CampaignSummary(
                    campaignId,
                    "Campaña agosto",
                    CampaignStatus.READY,
                    45,
                    createdAt));
        };
        CampaignController controller = new CampaignController(
                command -> {
                    throw new AssertionError("No debe crearse una campaña.");
                },
                command -> {
                    throw new AssertionError("No debe enviarse una campaña.");
                },
                listUseCase,
                new CampaignWebMapper());
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/api/v1/admin/campaigns")
                        .queryParam("status", "ready"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(campaignId.toString()))
                .andExpect(jsonPath("$[0].name").value("Campaña agosto"))
                .andExpect(jsonPath("$[0].status").value("READY"))
                .andExpect(jsonPath("$[0].recipientCount").value(45));

        assertThat(receivedQuery.get().status()).isEqualTo(CampaignStatus.READY);
    }

    @Test
    void shouldRejectUnknownCampaignStatus() throws Exception {
        CampaignController controller = new CampaignController(
                command -> {
                    throw new AssertionError("No debe crearse una campaña.");
                },
                command -> {
                    throw new AssertionError("No debe enviarse una campaña.");
                },
                query -> {
                    throw new AssertionError("No debe listarse con un estado inválido.");
                },
                new CampaignWebMapper());
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/api/v1/admin/campaigns")
                        .queryParam("status", "UNKNOWN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void shouldReturnAllCampaignsWhenStatusIsMissing() throws Exception {
        AtomicReference<ListCampaignsUseCase.ListCampaignsQuery> receivedQuery =
                new AtomicReference<>();
        CampaignController controller = new CampaignController(
                command -> {
                    throw new AssertionError("No debe crearse una campaña.");
                },
                command -> {
                    throw new AssertionError("No debe enviarse una campaña.");
                },
                query -> {
                    receivedQuery.set(query);
                    return List.of();
                },
                new CampaignWebMapper());
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/api/v1/admin/campaigns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        assertThat(receivedQuery.get().status()).isNull();
    }
}
