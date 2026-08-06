package com.ghostload.api.assessment.application.service;

import com.ghostload.api.assessment.application.port.in.GetEvaluationStatusUseCase;
import com.ghostload.api.assessment.application.port.out.LoadBenchmarkAnswersPort;
import com.ghostload.api.assessment.application.port.out.LoadBenchmarkQuestionsPort;
import com.ghostload.api.assessment.application.port.out.LoadBenchmarkResultPort;
import com.ghostload.api.assessment.application.port.out.LoadCalculatorResultPort;
import com.ghostload.api.assessment.application.port.out.LoadEvaluationPort;
import com.ghostload.api.assessment.application.port.out.LoadOperatorByIdPort;
import com.ghostload.api.assessment.domain.exception.InvalidEvaluationTokenException;
import com.ghostload.api.assessment.domain.model.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetEvaluationStatusServiceTest {

    private static final UUID EVAL_ID = UUID.randomUUID();
    private static final UUID OPERATOR_ID = UUID.randomUUID();
    private static final String TOKEN = "token-seguro";

    private Operator operator() {
        return Operator.reconstruct(
                OperatorId.of(OPERATOR_ID), "Juan", "Pérez",
                new Email("juan@ghostload.local"), "Acme SA", "Analista", "Argentina");
    }

    private Evaluation evaluation(EvaluationState state) {
        return Evaluation.reconstruct(
                EvaluationId.of(EVAL_ID), OperatorId.of(OPERATOR_ID),
                state, EvaluationSource.CALCULATOR, TOKEN, Instant.now(), Instant.now());
    }

    private GetEvaluationStatusService service(
            LoadEvaluationPort evaluations,
            LoadOperatorByIdPort operators,
            LoadCalculatorResultPort calculators,
            LoadBenchmarkAnswersPort answers,
            LoadBenchmarkResultPort results,
            LoadBenchmarkQuestionsPort questions) {
        return new GetEvaluationStatusService(evaluations, operators, calculators, answers, results, questions);
    }

    private GetEvaluationStatusService happyPathService() {
        List<BenchmarkAnswer> savedAnswers = List.of(new BenchmarkAnswer(UUID.randomUUID(), 4));
        return service(
                id -> Optional.of(evaluation(EvaluationState.CALCULATOR_COMPLETED)),
                operatorId -> Optional.of(operator()),
                evaluationId -> Optional.of(CalculatorResult.compute(100, 60, 5, "USD")),
                evaluationId -> savedAnswers,
                evaluationId -> Optional.empty(),
                version -> List.of(
                        new BenchmarkQuestion(UUID.randomUUID(), "v1", BenchmarkModule.CAPACITY_VISIBILITY, 1, "P1", true),
                        new BenchmarkQuestion(UUID.randomUUID(), "v1", BenchmarkModule.CAPACITY_VISIBILITY, 2, "P2", true),
                        new BenchmarkQuestion(UUID.randomUUID(), "v1", BenchmarkModule.CAPACITY_VISIBILITY, 3, "P3", true),
                        new BenchmarkQuestion(UUID.randomUUID(), "v1", BenchmarkModule.CAPACITY_VISIBILITY, 4, "P4", true)));
    }

    @Test
    void returnsStatusWithOperatorCalculatorAndProgress() {
        var status = happyPathService().getStatus(
                new GetEvaluationStatusUseCase.GetEvaluationStatusCommand(EVAL_ID, TOKEN));

        assertThat(status.evaluationId()).isEqualTo(EVAL_ID);
        assertThat(status.firstName()).isEqualTo("Juan");
        assertThat(status.email()).isEqualTo("juan@ghostload.local");
        assertThat(status.state()).isEqualTo("CALCULATOR_COMPLETED");
        assertThat(status.calculatorResult()).isNotNull();
        assertThat(status.calculatorResult().utilizationPercentage()).isEqualTo(60.0);
        assertThat(status.answers()).hasSize(1);
        assertThat(status.answeredCount()).isEqualTo(1);
        assertThat(status.completionPercentage()).isEqualTo(25.0);
        assertThat(status.benchmarkResult()).isNull();
    }

    @Test
    void rejectsInvalidToken() {
        var svc = happyPathService();

        assertThatThrownBy(() -> svc.getStatus(
                new GetEvaluationStatusUseCase.GetEvaluationStatusCommand(EVAL_ID, "token-incorrecto")))
                .isInstanceOf(InvalidEvaluationTokenException.class);
    }
}