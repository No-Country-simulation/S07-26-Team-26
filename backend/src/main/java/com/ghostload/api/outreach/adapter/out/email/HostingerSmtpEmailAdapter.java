package com.ghostload.api.outreach.adapter.out.email;

import com.ghostload.api.outreach.application.port.out.SendEmailPort;
import com.ghostload.api.outreach.application.port.out.VerifyEmailConnectionPort;
import com.ghostload.api.outreach.configuration.OutreachEmailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Component
public class HostingerSmtpEmailAdapter
        implements SendEmailPort, VerifyEmailConnectionPort {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(HostingerSmtpEmailAdapter.class);

    private final JavaMailSenderImpl mailSender;
    private final OutreachEmailProperties properties;

    public HostingerSmtpEmailAdapter(
            JavaMailSenderImpl mailSender,
            OutreachEmailProperties properties) {
        this.mailSender = mailSender;
        this.properties = properties;
    }

    @Override
    public EmailSendResult send(EmailMessage email) {
        validateConfiguration();
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            helper.setFrom(properties.fromAddress(), properties.fromName());
            helper.setTo(email.recipientEmail());
            helper.setSubject(email.subject());
            helper.setText(plainText(email), html(email));
            mailSender.send(mimeMessage);
            return new EmailSendResult(mimeMessage.getMessageID());
        } catch (MessagingException | UnsupportedEncodingException | RuntimeException exception) {
            EmailDeliveryException classified = EmailDeliveryException.classify(exception);
            LOGGER.warn(
                    "Falló un envío SMTP. code={}, detail={}",
                    classified.code(),
                    classified.getMessage());
            throw classified;
        }
    }

    @Override
    public EmailConnectionDiagnostic verifyConnection() {
        try {
            mailSender.testConnection();
            return new EmailConnectionDiagnostic(
                    true,
                    "SMTP_AVAILABLE",
                    "Conexión y autenticación SMTP verificadas correctamente.");
        } catch (MessagingException | RuntimeException exception) {
            EmailDeliveryException classified = EmailDeliveryException.classify(exception);
            LOGGER.warn(
                    "Falló la prueba de conexión SMTP. code={}, detail={}",
                    classified.code(),
                    classified.getMessage());
            return new EmailConnectionDiagnostic(
                    false,
                    classified.code(),
                    classified.getMessage());
        }
    }

    private String plainText(EmailMessage email) {
        return "Hola " + email.recipientName() + ",\n\n"
                + email.message() + "\n\n"
                + email.callToActionText() + ": " + invitationUrl(email);
    }

    private String html(EmailMessage email) {
        String escapedName = HtmlUtils.htmlEscape(email.recipientName());
        String escapedMessage = HtmlUtils.htmlEscape(email.message())
                .replace("\r\n", "<br>")
                .replace("\n", "<br>");
        String escapedCallToAction = HtmlUtils.htmlEscape(email.callToActionText());
        String escapedUrl = HtmlUtils.htmlEscape(invitationUrl(email));

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
                              <td style="padding:36px">
                                <p style="margin:0 0 18px;font-size:18px">Hola %s,</p>
                                <p style="margin:0 0 28px;line-height:1.6;color:#344b44">%s</p>
                                <a href="%s"
                                   style="display:inline-block;padding:14px 22px;background:#153c32;color:#d8b75b;text-decoration:none;font-weight:bold">
                                  %s
                                </a>
                                <p style="margin:28px 0 0;font-size:12px;color:#66736e">
                                  Si el botón no abre, copia este enlace: %s
                                </p>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </body>
                </html>
                """.formatted(
                escapedName,
                escapedMessage,
                escapedUrl,
                escapedCallToAction,
                escapedUrl);
    }

    private String invitationUrl(EmailMessage email) {
        String baseUrl = properties.invitationBaseUrl().replaceAll("/+$", "");
        return baseUrl + "/" + email.invitationToken();
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
        if (properties.invitationBaseUrl() == null
                || properties.invitationBaseUrl().isBlank()) {
            throw new EmailDeliveryException(
                    EmailDeliveryException.CONFIGURATION_FAILED,
                    "FRONTEND_INVITATION_URL no está configurado.");
        }
    }
}
