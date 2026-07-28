package com.ghostload.api.outreach.application.port.out;

import java.util.UUID;

public interface SendEmailPort {

    EmailSendResult send(EmailMessage message);

    record EmailMessage(
            String recipientEmail,
            String recipientName,
            String subject,
            String message,
            String callToActionText,
            UUID invitationToken) {
    }

    record EmailSendResult(String providerMessageId) {
    }

    class EmailSendingException extends RuntimeException {

        private final String code;

        public EmailSendingException(String code, String message) {
            super(message);
            this.code = code;
        }

        public EmailSendingException(String code, String message, Throwable cause) {
            super(message, cause);
            this.code = code;
        }

        public String code() {
            return code;
        }
    }
}
