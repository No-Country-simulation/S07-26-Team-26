package com.ghostload.api.administration.application.port.in;

import java.util.UUID;

public record AuthenticateAdminResult(
        String accessToken,
        long expiresInSeconds,
        UUID adminId,
        String adminName,
        String adminEmail) {
}
