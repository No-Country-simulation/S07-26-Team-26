package com.ghostload.api.outreach.adapter.out.email;

import com.ghostload.api.outreach.application.port.out.SendEmailPort;
import com.ghostload.api.outreach.configuration.OutreachEmailProperties;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HostingerSmtpEmailAdapterTest {

    @Test
    void shouldBuildMultipartEmailWithPlainTextAndHtml() throws Exception {
        JavaMailSenderImpl mailSender = mock(JavaMailSenderImpl.class);
        MimeMessage mimeMessage =
                new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));
        HostingerSmtpEmailAdapter adapter = new HostingerSmtpEmailAdapter(
                mailSender,
                new OutreachEmailProperties(
                        "contacto@trinitylabs.app",
                        "Trinity Labs",
                        "http://localhost:5173/invitations"));

        adapter.send(new SendEmailPort.EmailMessage(
                "operador@example.com",
                "Operador Test",
                "Completa nuestro benchmark",
                "Te invitamos a completar el benchmark.",
                "Comenzar evaluación",
                UUID.randomUUID()));

        mimeMessage.saveChanges();
        assertThat(mimeMessage.isMimeType("multipart/*")).isTrue();
    }
}
