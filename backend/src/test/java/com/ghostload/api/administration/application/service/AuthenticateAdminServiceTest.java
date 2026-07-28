package com.ghostload.api.administration.application.service;

import com.ghostload.api.administration.application.port.in.AuthenticateAdminCommand;
import com.ghostload.api.administration.application.port.out.GeneratedAdminToken;
import com.ghostload.api.administration.domain.exception.InvalidAdminCredentialsException;
import com.ghostload.api.administration.domain.model.AdminRole;
import com.ghostload.api.administration.domain.model.AdminUser;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthenticateAdminServiceTest {

    private static final AdminUser ACTIVE_ADMIN = new AdminUser(
            UUID.fromString("8f744cf4-df09-4dc1-985a-a1bb27f7b25f"),
            "Ghost Load Admin",
            "admin@ghostload.local",
            "encoded-password",
            AdminRole.ADMIN,
            true);

    @Test
    void shouldAuthenticateActiveAdminWithValidPassword() {
        AuthenticateAdminService service = new AuthenticateAdminService(
                email -> Optional.of(ACTIVE_ADMIN),
                (raw, encoded) -> raw.equals("GhostLoad2026!") && encoded.equals("encoded-password"),
                admin -> new GeneratedAdminToken("signed-jwt", 86400));

        var result = service.authenticate(
                new AuthenticateAdminCommand(" ADMIN@GHOSTLOAD.LOCAL ", "GhostLoad2026!"));

        assertThat(result.accessToken()).isEqualTo("signed-jwt");
        assertThat(result.adminEmail()).isEqualTo("admin@ghostload.local");
        assertThat(result.expiresInSeconds()).isEqualTo(86400);
    }

    @Test
    void shouldRejectInvalidPassword() {
        AuthenticateAdminService service = new AuthenticateAdminService(
                email -> Optional.of(ACTIVE_ADMIN),
                (raw, encoded) -> false,
                admin -> new GeneratedAdminToken("unused", 86400));

        assertThatThrownBy(() -> service.authenticate(
                new AuthenticateAdminCommand("admin@ghostload.local", "incorrect-password")))
                .isInstanceOf(InvalidAdminCredentialsException.class);
    }

    @Test
    void shouldRejectUnknownAdmin() {
        AuthenticateAdminService service = new AuthenticateAdminService(
                email -> Optional.empty(),
                (raw, encoded) -> true,
                admin -> new GeneratedAdminToken("unused", 86400));

        assertThatThrownBy(() -> service.authenticate(
                new AuthenticateAdminCommand("missing@ghostload.local", "GhostLoad2026!")))
                .isInstanceOf(InvalidAdminCredentialsException.class);
    }
}
