package com.ghostload.api.administration.application.service;

import com.ghostload.api.administration.application.port.in.AuthenticateAdminCommand;
import com.ghostload.api.administration.application.port.in.AuthenticateAdminResult;
import com.ghostload.api.administration.application.port.in.AuthenticateAdminUseCase;
import com.ghostload.api.administration.application.port.out.GenerateAdminTokenPort;
import com.ghostload.api.administration.application.port.out.GeneratedAdminToken;
import com.ghostload.api.administration.application.port.out.LoadAdminByEmailPort;
import com.ghostload.api.administration.application.port.out.VerifyPasswordPort;
import com.ghostload.api.administration.domain.exception.InvalidAdminCredentialsException;
import com.ghostload.api.administration.domain.model.AdminUser;

import java.util.Locale;
import java.util.Objects;

public final class AuthenticateAdminService implements AuthenticateAdminUseCase {

    private final LoadAdminByEmailPort loadAdminByEmailPort;
    private final VerifyPasswordPort verifyPasswordPort;
    private final GenerateAdminTokenPort generateAdminTokenPort;

    public AuthenticateAdminService(
            LoadAdminByEmailPort loadAdminByEmailPort,
            VerifyPasswordPort verifyPasswordPort,
            GenerateAdminTokenPort generateAdminTokenPort) {
        this.loadAdminByEmailPort = loadAdminByEmailPort;
        this.verifyPasswordPort = verifyPasswordPort;
        this.generateAdminTokenPort = generateAdminTokenPort;
    }

    @Override
    public AuthenticateAdminResult authenticate(AuthenticateAdminCommand command) {
        Objects.requireNonNull(command, "command is required");

        String normalizedEmail = command.email().trim().toLowerCase(Locale.ROOT);
        AdminUser adminUser = loadAdminByEmailPort.loadByEmail(normalizedEmail)
                .filter(AdminUser::active)
                .orElseThrow(InvalidAdminCredentialsException::new);

        if (!verifyPasswordPort.matches(command.password(), adminUser.passwordHash())) {
            throw new InvalidAdminCredentialsException();
        }

        GeneratedAdminToken token = generateAdminTokenPort.generate(adminUser);

        return new AuthenticateAdminResult(
                token.value(),
                token.expiresInSeconds(),
                adminUser.id(),
                adminUser.name(),
                adminUser.email());
    }
}
