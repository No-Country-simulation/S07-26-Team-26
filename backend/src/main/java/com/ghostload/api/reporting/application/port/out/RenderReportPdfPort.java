package com.ghostload.api.reporting.application.port.out;

import com.ghostload.api.reporting.domain.model.ReportData;

// Puerto de salida: convierte los datos del reporte en bytes de PDF.
// La implementación concreta (openhtmltopdf) vive en el adaptador.
public interface RenderReportPdfPort {

    byte[] render(ReportData data);
}
