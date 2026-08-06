package com.ghostload.api.assessment.application.service;

import com.ghostload.api.assessment.application.port.in.RegisterEvaluationUseCase;
import com.ghostload.api.assessment.application.port.out.GenerateEvaluationTokenPort;
import com.ghostload.api.assessment.application.port.out.LoadOperatorPort;
import com.ghostload.api.assessment.application.port.out.SaveEvaluationPort;
import com.ghostload.api.assessment.application.port.out.SaveOperatorPort;
import com.ghostload.api.assessment.application.port.out.SendEvaluationInvitationPort;
import com.ghostload.api.assessment.domain.model.Email;
import com.ghostload.api.assessment.domain.model.EvaluationSource;
import com.ghostload.api.assessment.domain.model.Operator;
import com.ghostload.api.assessment.domain.model.OperatorId;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterEvaluationServiceTest {

    private static final String TOKEN = "abc123-token-seguro-para-evaluacion";

    private RegisterEvaluationUseCase.RegisterEvaluationCommand command() {
        return new RegisterEvaluationUseCase.RegisterEvaluationCommand(
                "Juan", "Pérez", "juan@ghostload.local", "Acme SA",
                "Analista", "Argentina", true, false,
                EvaluationSource.CALCULATOR, null);
    }

    @Test
    void createsNewOperatorAndEvaluationWithToken() {
        AtomicInteger operatorSaves = new AtomicInteger();
        AtomicInteger evaluationSaves = new AtomicInteger();

        RegisterEvaluationService svc = new RegisterEvaluationService(
                email -> Optional.empty(),
                op -> operatorSaves.incrementAndGet(),
                eval -> evaluationSaves.incrementAndGet(),
                () -> TOKEN,
                email -> new SendEvaluationInvitationPort.EmailSendResult(true));

        var result = svc.register(command());

        assertThat(result.evaluationToken()).isEqualTo(TOKEN);
        assertThat(result.state().name()).isEqualTo("STARTED");
        assertThat(result.operatorId()).isNotNull();
        assertThat(result.evaluationId()).isNotNull();
        assertThat(result.createdAt()).isNotNull();
        assertThat(operatorSaves.get()).isEqualTo(1);
        assertThat(evaluationSaves.get()).isEqualTo(1);
    }

    @Test
    void reusesExistingOperatorByEmail() {
        var existing = Operator.reconstruct(
                OperatorId.of(UUID.randomUUID()), "Juan", "Pérez",
                new Email("juan@ghostload.local"), "Acme SA", "Analista", "Argentina");
        AtomicInteger operatorSaves = new AtomicInteger();

        RegisterEvaluationService svc = new RegisterEvaluationService(
                email -> Optional.of(existing),
                op -> operatorSaves.incrementAndGet(),
                eval -> {},
                () -> TOKEN,
                email -> new SendEvaluationInvitationPort.EmailSendResult(true));

        var result = svc.register(command());

        assertThat(result.operatorId()).isEqualTo(existing.id().value());
        // El operador existente se persiste como upsert (idemponente, sin duplicar).
        assertThat(operatorSaves.get()).isEqualTo(1);
    }
}