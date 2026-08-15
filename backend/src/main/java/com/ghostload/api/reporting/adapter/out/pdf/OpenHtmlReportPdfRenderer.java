package com.ghostload.api.reporting.adapter.out.pdf;

import com.ghostload.api.reporting.application.port.out.RenderReportPdfPort;
import com.ghostload.api.reporting.configuration.PdfProperties;
import com.ghostload.api.reporting.domain.model.ReportData;
import com.ghostload.api.reporting.domain.exception.PdfStorageException;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

// Renderer del PDF institucional usando openhtmltopdf (HTML/CSS -> PDF).
// Registra fuentes del sistema (Arial en Windows, DejaVu en Linux/Docker)
// para que el texto se dibuje correctamente en cualquier entorno.
@Component
public class OpenHtmlReportPdfRenderer implements RenderReportPdfPort {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(OpenHtmlReportPdfRenderer.class);

    private static final List<File> FONT_CANDIDATES = List.of(
            new File("C:/Windows/Fonts/arial.ttf"),
            new File("C:/Windows/Fonts/arialbd.ttf"),
            new File("C:/Windows/Fonts/ariali.ttf"),
            new File("C:/Windows/Fonts/arialbi.ttf"),
            new File("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"),
            new File("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"),
            new File("/usr/share/fonts/truetype/dejavu/DejaVuSans-Oblique.ttf"),
            new File("/usr/share/fonts/truetype/dejavu/DejaVuSans-BoldOblique.ttf"));

    private final PdfProperties properties;

    public OpenHtmlReportPdfRenderer(PdfProperties properties) {
        this.properties = properties;
    }

    @Override
    public byte[] render(ReportData data) {
        String html = new ReportHtmlTemplate(properties).render(data);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            registerFonts(builder);
            builder.withHtmlContent(html, null);
            builder.toStream(output);
            builder.run();
            byte[] content = output.toByteArray();
            if (content.length == 0) {
                throw new PdfStorageException("El renderer produjo un PDF vacío.", null);
            }
            LOGGER.info("PDF renderizado para la evaluación {} ({} bytes).",
                    data.evaluationId(), content.length);
            return content;
        } catch (IOException | RuntimeException exception) {
            throw new PdfStorageException(
                    "No se pudo renderizar el PDF del reporte.", exception);
        }
    }

    private void registerFonts(PdfRendererBuilder builder) {
        int registered = 0;
        for (File font : FONT_CANDIDATES) {
            if (!font.isFile()) {
                continue;
            }
            builder.useFont(font, familyFor(font.getPath()));
            registered++;
        }
        if (registered == 0) {
            LOGGER.warn("No se encontraron fuentes del sistema; el texto del PDF "
                    + "puede no renderizarse. Instalá Arial o DejaVu en el contenedor.");
        }
    }

    private String familyFor(String path) {
        return path.toLowerCase(java.util.Locale.ROOT).contains("dejavu")
                ? "DejaVu Sans"
                : "Arial";
    }
}
