package com.ghostload.api.outreach.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "outreach.email")
public record OutreachEmailProperties(
        String fromAddress,
        String fromName,
        String invitationBaseUrl) {
}
