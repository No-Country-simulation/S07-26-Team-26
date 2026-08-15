package com.ghostload.api.reporting.adapter.out.storage;

import com.ghostload.api.reporting.application.port.out.ReportPdfStoragePort;
import com.ghostload.api.reporting.configuration.PdfProperties;
import com.ghostload.api.reporting.domain.exception.PdfNotFoundException;
import com.ghostload.api.reporting.domain.exception.PdfStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

// Almacenamiento local en filesystem. Guarda cada PDF como
// {storageDir}/{evaluationId}.pdf. Se puede reemplazar por Amazon S3
// implementando el mismo puerto (ReportPdfStoragePort) sin tocar el dominio.
@Component
public class LocalFilePdfStorageAdapter implements ReportPdfStoragePort {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(LocalFilePdfStorageAdapter.class);

    private final Path storageDirectory;

    public LocalFilePdfStorageAdapter(PdfProperties properties) {
        this.storageDirectory = Path.of(properties.storageDir()).toAbsolutePath().normalize();
    }

    @Override
    public String store(UUID evaluationId, byte[] content) {
        try {
            Files.createDirectories(storageDirectory);
            String key = evaluationId + ".pdf";
            Files.write(storageDirectory.resolve(key), content);
            LOGGER.info("PDF almacenado en {} ({}) bytes.",
                    storageDirectory.resolve(key), content.length);
            return key;
        } catch (IOException exception) {
            throw new PdfStorageException(
                    "No se pudo guardar el PDF en el almacenamiento local.", exception);
        }
    }

    @Override
    public byte[] load(String storageKey) {
        Path file = storageDirectory.resolve(storageKey).normalize();
        if (!file.startsWith(storageDirectory)) {
            throw new PdfNotFoundException("La clave de almacenamiento es inválida.");
        }
        if (!Files.exists(file)) {
            throw new PdfNotFoundException(
                    "No existe el archivo del reporte en el almacenamiento.");
        }
        try {
            return Files.readAllBytes(file);
        } catch (IOException exception) {
            throw new PdfStorageException("No se pudo leer el PDF almacenado.", exception);
        }
    }
}
