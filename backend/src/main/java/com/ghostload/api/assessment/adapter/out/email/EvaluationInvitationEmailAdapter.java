package com.ghostload.api.assessment.adapter.out.email;

import com.ghostload.api.assessment.application.port.out.SendEvaluationInvitationPort;
import com.ghostload.api.assessment.configuration.AssessmentEmailProperties;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;

public class EvaluationInvitationEmailAdapter implements SendEvaluationInvitationPort {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(EvaluationInvitationEmailAdapter.class);

    private final JavaMailSenderImpl mailSender;
    private final AssessmentEmailProperties properties;

    public EvaluationInvitationEmailAdapter(
            JavaMailSenderImpl mailSender,
            AssessmentEmailProperties properties) {
        this.mailSender = mailSender;
        this.properties = properties;
    }

    @Override
    public EmailSendResult send(InvitationEmail email) {
        if (!isConfigured()) {
            LOGGER.warn(
                    "Envío de invitación a evaluación omitido: SMTP o URL base no configurados (assessment.email).");
            return new EmailSendResult(false);
        }
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage, true, StandardCharsets.UTF_8.name());
            helper.setFrom(properties.fromAddress(), properties.fromName());
            helper.setTo(email.recipientEmail());
            helper.setSubject(email.subject());
            helper.setText(plainText(email), html(email));
            mailSender.send(mimeMessage);
            return new EmailSendResult(true);
        } catch (Exception exception) {
            LOGGER.warn(
                    "Fallo el envío de invitación a evaluación para {}. causa={}",
                    email.recipientEmail(),
                    exception.getMessage());
            return new EmailSendResult(false);
        }
    }

    private boolean isConfigured() {
        return properties.fromAddress() != null && !properties.fromAddress().isBlank()
                && properties.fromName() != null && !properties.fromName().isBlank()
                && properties.invitationBaseUrl() != null && !properties.invitationBaseUrl().isBlank();
    }

    private String evaluationUrl(InvitationEmail email) {
        String baseUrl = properties.invitationBaseUrl().replaceAll("/+$", "");
        return baseUrl + "/" + email.evaluationToken();
    }

    private String plainText(InvitationEmail email) {
        return "Hola " + email.recipientName() + ",\n\n"
                + email.message() + "\n\n"
                + email.callToActionText() + ": " + evaluationUrl(email);
    }

    private String html(InvitationEmail email) {
        String escapedName = HtmlUtils.htmlEscape(email.recipientName());
        String escapedMessage = HtmlUtils.htmlEscape(email.message())
                .replace("\r\n", "<br>")
                .replace("\n", "<br>");
        String escapedCallToAction = HtmlUtils.htmlEscape(email.callToActionText());
        String escapedUrl = HtmlUtils.htmlEscape(evaluationUrl(email));

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
}