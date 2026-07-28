package com.ghostload.api.administration.adapter.out.security;

import com.ghostload.api.administration.configuration.JwtProperties;
import com.ghostload.api.administration.domain.model.AdminRole;
import com.ghostload.api.administration.domain.model.AdminUser;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    @Test
    void shouldGenerateAndReadSignedAdminToken() {
        JwtProvider provider = new JwtProvider(
                new JwtProperties("test-secret-with-at-least-32-characters", 86400),
                Clock.fixed(Instant.parse("2026-07-24T20:00:00Z"), ZoneOffset.UTC));
        AdminUser admin = new AdminUser(
                UUID.randomUUID(),
                "Admin",
                "admin@ghostload.local",
                "hash",
                AdminRole.ADMIN,
                true);

        var token = provider.generate(admin);

        assertThat(provider.validateToken(token.value())).isTrue();
        assertThat(provider.getUsernameFromToken(token.value())).isEqualTo(admin.email());
        assertThat(provider.getRoleFromToken(token.value())).isEqualTo("ADMIN");
        assertThat(token.expiresInSeconds()).isEqualTo(86400);
    }

    @Test
    void shouldRejectManipulatedToken() {
        JwtProvider provider = new JwtProvider(
                new JwtProperties("test-secret-with-at-least-32-characters", 86400),
                Clock.systemUTC());

        assertThat(provider.validateToken("not-a-valid-token")).isFalse();
    }
}
