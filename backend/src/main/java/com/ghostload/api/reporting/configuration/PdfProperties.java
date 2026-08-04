package com.ghostload.api.reporting.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Configuración del módulo de reportes: almacenamiento, contacto del
// fundador (bloque del PDF) y logo opcional. Todos los valores tienen
// default en application.properties y se pueden sobrescribir por env.
@ConfigurationProperties(prefix = "app.pdf")
public record PdfProperties(
        String storageDir,
        String downloadBaseUrl,
        String founderName,
        String founderRole,
        String founderPhone,
        String founderEmail,
        String founderLinkedin,
        String founderBookingUrl,
        String logoPath) {
}
