package com.ghostload.api.outreach.application.port.in;

import java.time.Instant;
import java.util.UUID;

public interface StartInvitationUseCase {

    void start(StartInvitationCommand command);

    record StartInvitationCommand(
            UUID invitationToken,
            String operatorEmail,
            UUID operatorId,
            UUID evaluationId,
            Instant startedAt) {
    }
}
