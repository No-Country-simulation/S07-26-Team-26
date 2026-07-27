package com.ghostload.api.outreach.adapter.in.web;

import com.ghostload.api.outreach.application.port.in.CreateCampaignCommand;
import com.ghostload.api.outreach.application.port.in.CreateCampaignResult;
import com.ghostload.api.outreach.application.port.in.CreateCampaignUseCase;
import com.ghostload.api.outreach.domain.model.CampaignStatus;
import com.ghostload.api.shared.adapter.in.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
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
                new CampaignController(useCase, new CampaignWebMapper());
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
                new CampaignController(useCase, new CampaignWebMapper());
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
}
