package com.ghostload.api.assessment.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "assessment.email")
public record AssessmentEmailProperties(
        String fromAddress,
        String fromName,
        String invitationBaseUrl) {
}