package com.ghostload.api.administration.domain.model;

import java.util.Objects;
import java.util.UUID;

public record AdminUser(
        UUID id,
        String name,
        String email,
        String passwordHash,
        AdminRole role,
        boolean active) {

    public AdminUser {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(name, "name is required");
        Objects.requireNonNull(email, "email is required");
        Objects.requireNonNull(passwordHash, "passwordHash is required");
        Objects.requireNonNull(role, "role is required");
    }
}
