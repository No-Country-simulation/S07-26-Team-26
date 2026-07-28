package com.ghostload.api.outreach.adapter.out.email;

import com.ghostload.api.outreach.application.port.out.SendEmailPort;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;

import javax.net.ssl.SSLException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class EmailDeliveryException extends SendEmailPort.EmailSendingException {

    public static final String AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";
    public static final String TLS_FAILED = "TLS_FAILED";
    public static final String CONNECTION_FAILED = "CONNECTION_FAILED";
    public static final String SMTP_REJECTED = "SMTP_REJECTED";
    public static final String CONFIGURATION_FAILED = "CONFIGURATION_FAILED";
    public static final String UNKNOWN_SMTP_ERROR = "UNKNOWN_SMTP_ERROR";

    public EmailDeliveryException(String code, String message) {
        super(code, message);
    }

    public EmailDeliveryException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public static EmailDeliveryException classify(Throwable throwable) {
        List<Throwable> causes = causes(throwable);
        String combinedMessage = causes.stream()
                .map(Throwable::getMessage)
                .filter(message -> message != null && !message.isBlank())
                .map(message -> message.toLowerCase(Locale.ROOT))
                .reduce("", (left, right) -> left + " " + right);

        if (containsType(causes, AuthenticationFailedException.class)
                || containsClassName(causes, "MailAuthenticationException")
                || containsAny(combinedMessage,
                "authentication failed",
                "authenticate failed",
                "invalid credentials",
                "username and password not accepted",
                "535 5.7")) {
            return new EmailDeliveryException(
                    AUTHENTICATION_FAILED,
                    "Hostinger rechazó el usuario o la contraseña SMTP.",
                    throwable);
        }

        if (containsType(causes, SSLException.class)
                || containsAny(combinedMessage,
                "sslhandshake",
                "unable to find valid certification path",
                "certificate",
                "could not convert socket to tls",
                "starttls")) {
            return new EmailDeliveryException(
                    TLS_FAILED,
                    "No se pudo establecer una conexión SSL/TLS válida con Hostinger.",
                    throwable);
        }

        if (containsType(causes, ConnectException.class)
                || containsType(causes, SocketTimeoutException.class)
                || containsType(causes, UnknownHostException.class)
                || containsClassName(causes, "MailConnectException")
                || containsAny(combinedMessage,
                "connection timed out",
                "connection refused",
                "couldn't connect to host",
                "could not connect to smtp host",
                "unknown host")) {
            return new EmailDeliveryException(
                    CONNECTION_FAILED,
                    "No se pudo conectar al servidor SMTP configurado.",
                    throwable);
        }

        if (containsAny(combinedMessage,
                "sender address rejected",
                "recipient address rejected",
                "message rejected",
                "send failed",
                "relay access denied",
                "mailbox unavailable")) {
            return new EmailDeliveryException(
                    SMTP_REJECTED,
                    "El servidor SMTP rechazó el remitente, destinatario o mensaje.",
                    throwable);
        }

        return new EmailDeliveryException(
                UNKNOWN_SMTP_ERROR,
                technicalSummary(causes),
                throwable);
    }

    private static List<Throwable> causes(Throwable throwable) {
        List<Throwable> causes = new ArrayList<>();
        Set<Throwable> visited = new HashSet<>();
        Throwable current = throwable;
        while (current != null && visited.add(current) && causes.size() < 20) {
            causes.add(current);
            if (current instanceof MessagingException messagingException
                    && messagingException.getNextException() != null
                    && !visited.contains(messagingException.getNextException())) {
                current = messagingException.getNextException();
            } else {
                current = current.getCause();
            }
        }
        return causes;
    }

    private static boolean containsType(
            List<Throwable> causes,
            Class<? extends Throwable> type) {
        return causes.stream().anyMatch(type::isInstance);
    }

    private static boolean containsClassName(
            List<Throwable> causes,
            String simpleName) {
        return causes.stream()
                .anyMatch(cause -> cause.getClass().getSimpleName().equals(simpleName));
    }

    private static boolean containsAny(String text, String... fragments) {
        for (String fragment : fragments) {
            if (text.contains(fragment)) {
                return true;
            }
        }
        return false;
    }

    private static String technicalSummary(List<Throwable> causes) {
        Throwable deepest = causes.getLast();
        String message = deepest.getMessage();
        if (message == null || message.isBlank()) {
            message = "El proveedor SMTP devolvió un error sin detalle.";
        }
        String normalized = message.replace('\r', ' ').replace('\n', ' ').trim();
        String summary = deepest.getClass().getSimpleName() + ": " + normalized;
        return summary.length() <= 700 ? summary : summary.substring(0, 700);
    }
}
