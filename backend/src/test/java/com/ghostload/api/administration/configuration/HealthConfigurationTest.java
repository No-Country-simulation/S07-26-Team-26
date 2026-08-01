package com.ghostload.api.administration.configuration;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class HealthConfigurationTest {

    @Test
    void shouldExposeOnlyHealthWithoutInternalDetails() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            assertThat(input).isNotNull();
            properties.load(input);
        }

        assertThat(properties.getProperty("management.endpoints.web.exposure.include"))
                .isEqualTo("health");
        assertThat(properties.getProperty("management.endpoint.health.show-details"))
                .isEqualTo("never");
        assertThat(properties.getProperty("management.endpoint.health.show-components"))
                .isEqualTo("never");
        assertThat(properties.getProperty("management.endpoint.health.group.readiness.include"))
                .isEqualTo("readinessState,db");
        assertThat(properties.getProperty("management.health.mail.enabled"))
                .isEqualTo("false");
    }
}
