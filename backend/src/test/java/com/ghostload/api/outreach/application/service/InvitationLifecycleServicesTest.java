package com.ghostload.api.outreach.application.service;

import com.ghostload.api.outreach.application.port.in.StartInvitationUseCase;
import com.ghostload.api.outreach.application.port.out.LoadInvitationTrackingPort;
import com.ghostload.api.outreach.application.port.out.UpdateInvitationTrackingPort;
import com.ghostload.api.outreach.domain.exception.InvalidInvitationException;
import com.ghostload.api.outreach.domain.exception.InvitationUnavailableException;
import com.ghostload.api.outreach.domain.model.InvitationStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvitationLifecycleServicesTest {

    private static final Instant NOW = Instant.parse("2026-07-30T18:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final UUID INVITATION_ID =
            UUID.fromString("e7ee8d12-aa53-42a1-ad1f-5a6b58d236c4");
    private static final UUID TOKEN =
            UUID.fromString("650c17ec-8f4f-4ddf-a50d-f0e6e165b1ca");

    @Test
    void shouldResolveSentInvitationAndMarkItVisited() {
        RecordingUpdater updater = new RecordingUpdater();
        ResolveInvitationService service = new ResolveInvitationService(
                loader(tracking(InvitationStatus.SENT, NOW.plusSeconds(3600), null)),
                updater,
                CLOCK);

        var result = service.resolve(TOKEN);

        assertThat(result.valid()).isTrue();
        assertThat(result.status()).isEqualTo(InvitationStatus.VISITED);
        assertThat(result.email()).isEqualTo("operator@example.com");
        assertThat(result.campaignName()).isEqualTo("Benchmark julio");
        assertThat(updater.visitedInvitation.get()).isEqualTo(INVITATION_ID);
        assertThat(updater.visitedAt.get()).isEqualTo(NOW);
    }

    @Test
    void shouldResolveVisitedInvitationWithoutUpdatingItAgain() {
        RecordingUpdater updater = new RecordingUpdater();
        ResolveInvitationService service = new ResolveInvitationService(
                loader(tracking(InvitationStatus.VISITED, null, null)),
                updater,
                CLOCK);

        var result = service.resolve(TOKEN);

        assertThat(result.status()).isEqualTo(InvitationStatus.VISITED);
        assertThat(updater.visitCalls.get()).isZero();
    }

    @Test
    void shouldRejectExpiredInvitation() {
        ResolveInvitationService service = new ResolveInvitationService(
                loader(tracking(InvitationStatus.SENT, NOW, null)),
                new RecordingUpdater(),
                CLOCK);

        assertThatThrownBy(() -> service.resolve(TOKEN))
                .isInstanceOf(InvitationUnavailableException.class)
                .hasMessageContaining("expirado");
    }

    @Test
    void shouldStartVisitedInvitationForMatchingEmail() {
        UUID operatorId = UUID.randomUUID();
        UUID evaluationId = UUID.randomUUID();
        RecordingUpdater updater = new RecordingUpdater();
        StartInvitationService service = new StartInvitationService(
                loader(tracking(InvitationStatus.VISITED, null, null)),
                updater);

        service.start(new StartInvitationUseCase.StartInvitationCommand(
                TOKEN,
                " Operator@Example.com ",
                operatorId,
                evaluationId,
                NOW));

        assertThat(updater.startedInvitation.get()).isEqualTo(INVITATION_ID);
        assertThat(updater.operatorId.get()).isEqualTo(operatorId);
        assertThat(updater.evaluationId.get()).isEqualTo(evaluationId);
        assertThat(updater.startedAt.get()).isEqualTo(NOW);
    }

    @Test
    void shouldRejectInvitationWhenEmailDoesNotMatch() {
        StartInvitationService service = new StartInvitationService(
                loader(tracking(InvitationStatus.VISITED, null, null)),
                new RecordingUpdater());

        assertThatThrownBy(() -> service.start(
                new StartInvitationUseCase.StartInvitationCommand(
                        TOKEN,
                        "other@example.com",
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        NOW)))
                .isInstanceOf(InvalidInvitationException.class)
                .hasMessageContaining("email");
    }

    @Test
    void shouldCompleteStartedInvitation() {
        UUID evaluationId = UUID.randomUUID();
        RecordingUpdater updater = new RecordingUpdater();
        CompleteInvitationService service = new CompleteInvitationService(
                loader(tracking(InvitationStatus.STARTED, null, evaluationId)),
                updater);

        service.complete(evaluationId, NOW);

        assertThat(updater.completedInvitation.get()).isEqualTo(INVITATION_ID);
        assertThat(updater.completedAt.get()).isEqualTo(NOW);
    }

    @Test
    void shouldIgnoreCompletionForDirectEvaluation() {
        RecordingUpdater updater = new RecordingUpdater();
        CompleteInvitationService service = new CompleteInvitationService(
                loader(null),
                updater);

        service.complete(UUID.randomUUID(), NOW);

        assertThat(updater.completedInvitation.get()).isNull();
    }

    private LoadInvitationTrackingPort loader(
            LoadInvitationTrackingPort.InvitationTracking tracking) {
        return new LoadInvitationTrackingPort() {
            @Override
            public Optional<InvitationTracking> loadByToken(UUID invitationToken) {
                return Optional.ofNullable(tracking);
            }

            @Override
            public Optional<InvitationTracking> loadByEvaluationId(UUID evaluationId) {
                if (tracking == null
                        || !evaluationId.equals(tracking.evaluationId())) {
                    return Optional.empty();
                }
                return Optional.of(tracking);
            }
        };
    }

    private LoadInvitationTrackingPort.InvitationTracking tracking(
            InvitationStatus status,
            Instant expiresAt,
            UUID evaluationId) {
        return new LoadInvitationTrackingPort.InvitationTracking(
                INVITATION_ID,
                TOKEN,
                status,
                expiresAt,
                "operator@example.com",
                "Ana",
                "Torres",
                "Northstar",
                "CTO",
                "Benchmark julio",
                null,
                evaluationId);
    }

    private static final class RecordingUpdater
            implements UpdateInvitationTrackingPort {

        private final AtomicInteger visitCalls = new AtomicInteger();
        private final AtomicReference<UUID> visitedInvitation = new AtomicReference<>();
        private final AtomicReference<Instant> visitedAt = new AtomicReference<>();
        private final AtomicReference<UUID> startedInvitation = new AtomicReference<>();
        private final AtomicReference<UUID> operatorId = new AtomicReference<>();
        private final AtomicReference<UUID> evaluationId = new AtomicReference<>();
        private final AtomicReference<Instant> startedAt = new AtomicReference<>();
        private final AtomicReference<UUID> completedInvitation = new AtomicReference<>();
        private final AtomicReference<Instant> completedAt = new AtomicReference<>();

        @Override
        public boolean markVisited(UUID invitationId, Instant visitedAt) {
            visitCalls.incrementAndGet();
            visitedInvitation.set(invitationId);
            this.visitedAt.set(visitedAt);
            return true;
        }

        @Override
        public boolean markStarted(
                UUID invitationId,
                UUID operatorId,
                UUID evaluationId,
                Instant startedAt) {
            startedInvitation.set(invitationId);
            this.operatorId.set(operatorId);
            this.evaluationId.set(evaluationId);
            this.startedAt.set(startedAt);
            return true;
        }

        @Override
        public boolean markCompleted(UUID invitationId, Instant completedAt) {
            completedInvitation.set(invitationId);
            this.completedAt.set(completedAt);
            return true;
        }
    }
}
