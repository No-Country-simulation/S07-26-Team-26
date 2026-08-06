package com.ghostload.api.assessment.application.port.out;

// Puerto de salida para notificar al operador por email con su token de
// acceso a la evaluación. El armado de la URL y el envío concreto (SMTP)
// los decide el adaptador.
public interface SendEvaluationInvitationPort {

    EmailSendResult send(InvitationEmail email);

    record InvitationEmail(
            String recipientEmail,
            String recipientName,
            String subject,
            String message,
            String callToActionText,
            String evaluationToken) {
    }

    record EmailSendResult(boolean sent) {
    }

    class EmailSendingException extends RuntimeException {
        public EmailSendingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}