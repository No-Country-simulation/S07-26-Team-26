package com.ghostload.api.administration.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(String secret, long expirationSeconds) {

    public JwtProperties {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must contain at least 32 characters");
        }
        if (expirationSeconds < 60) {
            throw new IllegalArgumentException("JWT expiration must be at least 60 seconds");
        }
    }
}
