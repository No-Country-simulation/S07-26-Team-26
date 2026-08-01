package com.ghostload.api.assessment.domain.model;

import java.util.Locale;
import java.util.regex.Pattern;

// Un "value object": en vez de pasar un String suelto por todos lados,
// envolvemos el email en su propia clase para que sea imposible crear
// un Email inválido. Es un record (Java 21), inmutable por definición.
public record Email(String value) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    // Este bloque se ejecuta SIEMPRE que se crea un Email nuevo (compact constructor).
    // Si el email no es válido, ni siquiera se puede construir el objeto.
    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        value = value.trim().toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("El email no tiene un formato válido: " + value);
        }
        if (value.length() > 254) {
            throw new IllegalArgumentException("El email es demasiado largo");
        }
    }
}
