package com.ghostload.api.outreach.adapter.out.email;

import jakarta.mail.AuthenticationFailedException;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLHandshakeException;
import java.net.ConnectException;

import static org.assertj.core.api.Assertions.assertThat;

class EmailDeliveryExceptionTest {

    @Test
    void shouldClassifyAuthenticationFailure() {
        var result = EmailDeliveryException.classify(
                new AuthenticationFailedException(
                        "535 5.7.8 Authentication failed"));

        assertThat(result.code())
                .isEqualTo(EmailDeliveryException.AUTHENTICATION_FAILED);
        assertThat(result.getMessage()).contains("usuario o la contraseña");
    }

    @Test
    void shouldClassifyTlsFailure() {
        var result = EmailDeliveryException.classify(
                new SSLHandshakeException("certificate_unknown"));

        assertThat(result.code()).isEqualTo(EmailDeliveryException.TLS_FAILED);
    }

    @Test
    void shouldClassifyConnectionFailure() {
        var result = EmailDeliveryException.classify(
                new ConnectException("Connection refused"));

        assertThat(result.code())
                .isEqualTo(EmailDeliveryException.CONNECTION_FAILED);
    }
}
