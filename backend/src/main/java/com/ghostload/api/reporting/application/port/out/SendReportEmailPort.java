package com.ghostload.api.reporting.application.port.out;

// Puerto de salida: envía el PDF por email al operador.
public interface SendReportEmailPort {

    void send(ReportEmail email);

    record ReportEmail(
            String recipientEmail,
            String recipientName,
            String subject,
            String message,
            byte[] pdfAttachment,
            String attachmentName) {
    }
}
