package com.ghostload.api.assessment.application.service;

import com.ghostload.api.assessment.application.port.in.SaveCalculatorResultUseCase;
import com.ghostload.api.assessment.application.port.out.LoadEvaluationPort;
import com.ghostload.api.assessment.application.port.out.SaveCalculatorResultPort;
import com.ghostload.api.assessment.application.port.out.SaveEvaluationPort;
import com.ghostload.api.assessment.domain.exception.InvalidEvaluationStateException;
import com.ghostload.api.assessment.domain.exception.InvalidEvaluationTokenException;
import com.ghostload.api.assessment.domain.model.Evaluation;
import com.ghostload.api.assessment.domain.model.EvaluationId;
import com.ghostload.api.assessment.domain.model.EvaluationSource;
import com.ghostload.api.assessment.domain.model.EvaluationState;
import com.ghostload.api.assessment.domain.model.OperatorId;
import com.ghostload.api.assessment.domain.model.CalculatorResult;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SaveCalculatorResultServiceTest {

    private static final String TOKEN = "evaluation-token-123";
    private static final EvaluationId EVALUATION_ID = EvaluationId.of(UUID.randomUUID());
    private static final OperatorId OPERATOR_ID = OperatorId.of(UUID.randomUUID());

    private static Evaluation evaluation(EvaluationState state) {
        return Evaluation.reconstruct(
                EVALUATION_ID, OPERATOR_ID, state, EvaluationSource.CALCULATOR,
                TOKEN, Instant.parse("2026-07-01T00:00:00Z"),
                Instant.parse("2026-07-01T00:00:00Z"));
    }

    private SaveCalculatorResultService build(LoadEvaluationPort load,
                                              SaveEvaluationPort saveEval,
                                              SaveCalculatorResultPort saveResult) {
        return new SaveCalculatorResultService(load, saveEval, saveResult);
    }

    @Test
    void savesResultAndAdvancesStateForValidToken() {
        var saveCalls = new AtomicInteger();
        AtomicInteger calculatorSave = new AtomicInteger();
        SaveCalculatorResultService svc = build(
                id -> Optional.of(evaluation(EvaluationState.STARTED)),
                e -> saveCalls.incrementAndGet(),
                (id, result) -> calculatorSave.incrementAndGet());

        var result = svc.save(new SaveCalculatorResultUseCase.SaveCalculatorResultCommand(
                EVALUATION_ID.value(), TOKEN, 100.0, 70.0, 2.5, "USD"));

        assertThat(result.nonProductiveCapacityMw()).isCloseTo(30.0, within(0.0001));
        assertThat(result.utilizationPercentage()).isCloseTo(70.0, within(0.0001));
        assertThat(result.estimatedAnnualCost()).isCloseTo(900_000.0, within(0.0001));
        assertThat(saveCalls.get()).isEqualTo(1);
        assertThat(calculatorSave.get()).isEqualTo(1);
    }

    @Test
    void rejectsInvalidToken() {
        SaveCalculatorResultService svc = build(
                id -> Optional.of(evaluation(EvaluationState.STARTED)),
                e -> {}, (id, result) -> {});

        assertThatThrownBy(() -> svc.save(new SaveCalculatorResultUseCase.SaveCalculatorResultCommand(
                EVALUATION_ID.value(), "wrong-token", 100.0, 70.0, 2.5, "USD")))
                .isInstanceOf(InvalidEvaluationTokenException.class);
    }

    @Test
    void rejectsStateTransitionWhenAlreadyCompleted() {
        SaveCalculatorResultService svc = build(
                id -> Optional.of(evaluation(EvaluationState.CALCULATOR_COMPLETED)),
                e -> {}, (id, result) -> {});

        assertThatThrownBy(() -> svc.save(new SaveCalculatorResultUseCase.SaveCalculatorResultCommand(
                EVALUATION_ID.value(), TOKEN, 100.0, 70.0, 2.5, "USD")))
                .isInstanceOf(InvalidEvaluationStateException.class);
    }
}
