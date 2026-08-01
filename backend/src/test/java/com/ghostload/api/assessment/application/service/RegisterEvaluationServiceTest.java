package com.ghostload.api.assessment.application.service;

import com.ghostload.api.assessment.application.port.in.RegisterEvaluationUseCase;
import com.ghostload.api.assessment.domain.model.Evaluation;
import com.ghostload.api.assessment.domain.model.EvaluationSource;
import com.ghostload.api.assessment.domain.model.Operator;
import com.ghostload.api.outreach.application.port.in.StartInvitationUseCase;
import com.ghostload.api.outreach.domain.exception.InvalidInvitationException;
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

class RegisterEvaluationServiceTest {

    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-07-30T18:00:00Z"),
            ZoneOffset.UTC);
    private static final String EVALUATION_TOKEN =
            "0123456789abcdef0123456789abcdef0123456789abcdef";

    @Test
    void shouldLinkOutreachEvaluationWithInvitation() {
        UUID invitationToken =
                UUID.fromString("650c17ec-8f4f-4ddf-a50d-f0e6e165b1ca");
        AtomicReference<Operator> savedOperator = new AtomicReference<>();
        AtomicReference<Evaluation> savedEvaluation = new AtomicReference<>();
        AtomicReference<StartInvitationUseCase.StartInvitationCommand> startCommand =
                new AtomicReference<>();
        RegisterEvaluationService service = new RegisterEvaluationService(
                ignored -> Optional.empty(),
                savedOperator::set,
                savedEvaluation::set,
                () -> EVALUATION_TOKEN,
                startCommand::set,
                CLOCK);

        var result = service.register(command(
                " Operator@Example.com ",
                EvaluationSource.OUTREACH,
                invitationToken));

        assertThat(savedOperator.get().email().value())
                .isEqualTo("operator@example.com");
        assertThat(result.evaluationId())
                .isEqualTo(savedEvaluation.get().id().value());
        assertThat(startCommand.get().invitationToken())
                .isEqualTo(invitationToken);
        assertThat(startCommand.get().operatorEmail())
                .isEqualTo("operator@example.com");
        assertThat(startCommand.get().operatorId())
                .isEqualTo(result.operatorId());
        assertThat(startCommand.get().evaluationId())
                .isEqualTo(result.evaluationId());
        assertThat(startCommand.get().startedAt()).isEqualTo(CLOCK.instant());
    }

    @Test
    void shouldKeepDirectEvaluationIndependentFromInvitations() {
        AtomicInteger invitationCalls = new AtomicInteger();
        RegisterEvaluationService service = new RegisterEvaluationService(
                ignored -> Optional.empty(),
                ignored -> {
                },
                ignored -> {
                },
                () -> EVALUATION_TOKEN,
                ignored -> invitationCalls.incrementAndGet(),
                CLOCK);

        service.register(command(
                "operator@example.com",
                EvaluationSource.CALCULATOR,
                null));

        assertThat(invitationCalls.get()).isZero();
    }

    @Test
    void shouldRequireInvitationForOutreachSource() {
        RegisterEvaluationService service = new RegisterEvaluationService(
                ignored -> Optional.empty(),
                ignored -> {
                },
                ignored -> {
                },
                () -> EVALUATION_TOKEN,
                ignored -> {
                },
                CLOCK);

        assertThatThrownBy(() -> service.register(command(
                "operator@example.com",
                EvaluationSource.OUTREACH,
                null)))
                .isInstanceOf(InvalidInvitationException.class)
                .hasMessageContaining("requieren");
    }

    private RegisterEvaluationUseCase.RegisterEvaluationCommand command(
            String email,
            EvaluationSource source,
            UUID invitationToken) {
        return new RegisterEvaluationUseCase.RegisterEvaluationCommand(
                "Ana",
                "Torres",
                email,
                "Northstar",
                "CTO",
                "Peru",
                true,
                true,
                source,
                invitationToken);
    }
}
