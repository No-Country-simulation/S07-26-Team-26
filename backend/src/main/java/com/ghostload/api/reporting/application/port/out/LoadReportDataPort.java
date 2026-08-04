package com.ghostload.api.reporting.application.port.out;

import com.ghostload.api.reporting.domain.model.ReportData;

import java.util.UUID;

// Puerto de salida: el módulo de reportes necesita los datos del operador,
// de la calculadora y del benchmark para armar el PDF. La implementación
// concreta (JPA/PostgreSQL) vive en el adaptador.
public interface LoadReportDataPort {

    ReportData load(UUID evaluationId);
}
