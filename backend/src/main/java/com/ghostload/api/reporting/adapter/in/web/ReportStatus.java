package com.ghostload.api.reporting.adapter.in.web;

// Estado del reporte tal como lo expone la API (openapi.yaml -> ReportStatus).
public enum ReportStatus {
    NOT_REQUESTED,
    REPORT_GENERATING,
    REPORT_COMPLETED,
    REPORT_FAILED
}
