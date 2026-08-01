package com.ghostload.api.outreach.application.port.in;

import java.time.Instant;
import java.util.UUID;

public interface CompleteInvitationUseCase {

    void complete(UUID evaluationId, Instant completedAt);
}
