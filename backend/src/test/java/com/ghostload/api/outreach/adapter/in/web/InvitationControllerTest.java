package com.ghostload.api.outreach.adapter.in.web;

import com.ghostload.api.outreach.application.port.in.ResolveInvitationUseCase;
import com.ghostload.api.outreach.domain.model.InvitationStatus;
import com.ghostload.api.shared.adapter.in.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InvitationControllerTest {

    @Test
    void shouldResolveInvitationUsingPathToken() throws Exception {
        UUID token = UUID.fromString("650c17ec-8f4f-4ddf-a50d-f0e6e165b1ca");
        ResolveInvitationUseCase useCase = receivedToken -> {
            if (!token.equals(receivedToken)) {
                throw new AssertionError("El token del path no fue mapeado.");
            }
            return new ResolveInvitationUseCase.ResolveInvitationResult(
                    true,
                    InvitationStatus.VISITED,
                    "operator@example.com",
                    "Ana",
                    "Torres",
                    "Northstar",
                    "CTO",
                    "Benchmark julio",
                    Instant.parse("2026-08-30T18:00:00Z"));
        };
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new InvitationController(useCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/api/v1/invitations/{token}", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.status").value("VISITED"))
                .andExpect(jsonPath("$.email").value("operator@example.com"))
                .andExpect(jsonPath("$.companyName").value("Northstar"))
                .andExpect(jsonPath("$.campaignName").value("Benchmark julio"));
    }
}
