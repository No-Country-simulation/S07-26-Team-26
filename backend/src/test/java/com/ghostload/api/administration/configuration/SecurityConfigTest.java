package com.ghostload.api.administration.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    @Test
    void shouldAllowConfiguredNextJsOriginsAndEvaluationHeader() {
        SecurityConfig securityConfig = new SecurityConfig();
        var source = securityConfig.corsConfigurationSource(
                "http://localhost:3000,http://localhost:3001");
        MockHttpServletRequest request = new MockHttpServletRequest(
                "OPTIONS",
                "/api/v1/invitations/token");

        var configuration = source.getCorsConfiguration(request);

        assertThat(configuration).isNotNull();
        assertThat(configuration.getAllowedOrigins())
                .containsExactly(
                        "http://localhost:3000",
                        "http://localhost:3001");
        assertThat(configuration.getAllowedMethods())
                .contains("GET", "POST", "PUT", "OPTIONS");
        assertThat(configuration.getAllowedHeaders())
                .contains("Authorization", "Content-Type", "X-Evaluation-Token");
    }
}
