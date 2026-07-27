package com.ghostload.api.outreach.adapter.in.web;

import com.ghostload.api.outreach.application.port.in.VerifyEmailConnectionUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmailDiagnosticsControllerTest {

    @Test
    void shouldReturnSmtpDiagnosticWithoutSendingEmail() throws Exception {
        VerifyEmailConnectionUseCase useCase =
                () -> new VerifyEmailConnectionUseCase.EmailConnectionTestResult(
                        false,
                        "AUTHENTICATION_FAILED",
                        "Hostinger rechazó el usuario o la contraseña SMTP.");
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new EmailDiagnosticsController(useCase))
                .build();

        mockMvc.perform(post("/api/v1/admin/email/test-connection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));
    }
}
