package com.ghostload.api.outreach.domain.model;

import java.util.Locale;
import java.util.regex.Pattern;

public record ContactEmail(String value) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public ContactEmail {
        if (value == null) {
            throw new IllegalArgumentException("El email es obligatorio.");
        }
        value = value.trim().toLowerCase(Locale.ROOT);
        if (value.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio.");
        }
        if (value.length() > 254 || !EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("El email no tiene un formato válido.");
        }
    }
}
