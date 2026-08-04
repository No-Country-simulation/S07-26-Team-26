package com.ghostload.api.reporting.adapter.out.email;

import com.ghostload.api.outreach.adapter.out.email.EmailDeliveryException;
import com.ghostload.api.outreach.configuration.OutreachEmailProperties;
import com.ghostload.api.reporting.application.port.out.SendReportEmailPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

// Envía el PDF institucional como adjunto al correo del operador, con la
// misma configuración SMTP que el módulo de outreach (Hostinger).
@Component
public class HostingerSmtpReportEmailAdapter implements SendReportEmailPort {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(HostingerSmtpReportEmailAdapter.class);

    private final JavaMailSenderImpl mailSender;
    private final OutreachEmailProperties properties;

    public HostingerSmtpReportEmailAdapter(
            JavaMailSenderImpl mailSender,
            OutreachEmailProperties properties) {
        this.mailSender = mailSender;
        this.properties = properties;
    }

    @Override
    public void send(ReportEmail email) {
        validateConfiguration();
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(properties.fromAddress(), properties.fromName());
            helper.setTo(email.recipientEmail());
            helper.setSubject(email.subject());
            helper.setText(plainText(email), html(email));
            helper.addAttachment(
                    email.attachmentName(),
                    new ByteArrayResource(email.pdfAttachment()),
                    "application/pdf");
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException | RuntimeException exception) {
            EmailDeliveryException classified = EmailDeliveryException.classify(exception);
            LOGGER.warn(
                    "Falló el envío del reporte por email. code={}, detail={}",
                    classified.code(),
                    classified.getMessage());
            throw classified;
        }
    }

    private String plainText(ReportEmail email) {
        return "Hola " + email.recipientName() + ",\n\n"
                + email.message() + "\n\n"
                + "Te adjuntamos el reporte institucional de Ghost Load.";
    }

    private String html(ReportEmail email) {
        String name = HtmlUtils.htmlEscape(email.recipientName());
        String message = HtmlUtils.htmlEscape(email.message())
                .replace("\r\n", "<br>")
                .replace("\n", "<br>");
        String fileName = HtmlUtils.htmlEscape(email.attachmentName());

        return """
                <!doctype html>
                <html lang="es">
                  <body style="margin:0;background:#f4f1e8;font-family:Arial,sans-serif;color:#153c32">
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0">
                      <tr>
                        <td align="center" style="padding:32px 16px">
                          <table role="presentation" width="100%%" cellspacing="0" cellpadding="0"
                                 style="max-width:600px;background:#ffffff;border:1px solid #ddd6c5">
                            <tr>
                              <td style="background:#153c32;padding:28px 36px">
                                <p style="margin:0;font-size:22px;font-weight:bold;color:#d8b75b;letter-spacing:1px">
                                  GHOST LOAD
                                </p>
                                <p style="margin:4px 0 0;font-size:12px;color:#cfded8">
                                  Intelligence for AI Data Centers
                                </p>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding:36px">
                                <p style="margin:0 0 18px;font-size:18px">Hola %s,</p>
                                <p style="margin:0 0 28px;line-height:1.6;color:#344b44">%s</p>
                                <p style="margin:0 0 8px;font-weight:bold;font-size:14px">
                                  Archivo adjunto: %s
                                </p>
                                <p style="margin:0 0 28px;line-height:1.6;color:#344b44">
                                  También podés descargarlo más tarde desde el sistema con tu
                                  enlace de evaluación.
                                </p>
                                <p style="margin:28px 0 0;font-size:12px;color:#66736e">
                                  Equipo Ghost Load - hablemos de tu capacidad desperdiciada.
                                </p>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </body>
                </html>
                """.formatted(name, message, fileName);
    }

    private void validateConfiguration() {
        if (properties.fromAddress() == null || properties.fromAddress().isBlank()) {
            throw new EmailDeliveryException(
                    EmailDeliveryException.CONFIGURATION_FAILED,
                    "MAIL_FROM_ADDRESS no está configurado.");
        }
        if (properties.fromName() == null || properties.fromName().isBlank()) {
            throw new EmailDeliveryException(
                    EmailDeliveryException.CONFIGURATION_FAILED,
                    "MAIL_FROM_NAME no está configurado.");
        }
    }
}
