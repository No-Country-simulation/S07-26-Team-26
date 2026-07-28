package com.ghostload.api.outreach.adapter.in.scheduling;

import com.ghostload.api.outreach.application.port.in.ProcessPendingEmailsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "outreach.email.worker-enabled",
        havingValue = "true")
public class EmailOutboxWorker {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(EmailOutboxWorker.class);
    private static final int BATCH_SIZE = 20;

    private final ProcessPendingEmailsUseCase processPendingEmailsUseCase;

    public EmailOutboxWorker(ProcessPendingEmailsUseCase processPendingEmailsUseCase) {
        this.processPendingEmailsUseCase = processPendingEmailsUseCase;
    }

    @Scheduled(fixedDelayString = "${outreach.email.worker-delay-ms:5000}")
    public void processPendingEmails() {
        try {
            int processed = processPendingEmailsUseCase.processBatch(BATCH_SIZE);
            if (processed > 0) {
                LOGGER.info(
                        "El worker atendió {} trabajo(s) de correo. "
                                + "Los fallos aparecen como WARN y en email_outbox.last_error.",
                        processed);
            }
        } catch (RuntimeException exception) {
            LOGGER.error("Falló el worker de correos de outreach.", exception);
        }
    }
}
