package com.ghostload.api.outreach.application.port.out;

import java.time.Instant;
import java.util.UUID;

public interface UpdateInvitationTrackingPort {

    boolean markVisited(UUID invitationId, Instant visitedAt);

    boolean markStarted(
            UUID invitationId,
            UUID operatorId,
            UUID evaluationId,
            Instant startedAt);

    boolean markCompleted(UUID invitationId, Instant completedAt);
}
