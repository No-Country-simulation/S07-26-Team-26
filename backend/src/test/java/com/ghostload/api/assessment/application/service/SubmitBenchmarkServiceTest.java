package com.ghostload.api.assessment.application.service;

import com.ghostload.api.assessment.application.port.in.SubmitBenchmarkUseCase;
import com.ghostload.api.assessment.application.port.out.LoadBenchmarkQuestionsPort;
import com.ghostload.api.assessment.application.port.out.LoadEvaluationPort;
import com.ghostload.api.assessment.application.port.out.SaveBenchmarkResultPort;
import com.ghostload.api.assessment.application.port.out.SaveEvaluationPort;
import com.ghostload.api.assessment.domain.exception.InvalidEvaluationStateException;
import com.ghostload.api.assessment.domain.exception.InvalidEvaluationTokenException;
import com.ghostload.api.assessment.domain.model.BenchmarkAnswer;
import com.ghostload.api.assessment.domain.model.BenchmarkQuestion;
import com.ghostload.api.assessment.domain.model.BenchmarkResult;
import com.ghostload.api.assessment.domain.model.BenchmarkModule;
import com.ghostload.api.assessment.domain.model.Evaluation;
import com.ghostload.api.assessment.domain.model.EvaluationId;
import com.ghostload.api.assessment.domain.model.EvaluationSource;
import com.ghostload.api.assessment.domain.model.EvaluationState;
import com.ghostload.api.assessment.domain.model.OperatorId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SubmitBenchmarkServiceTest {

    private static final String TOKEN = "evaluation-token-123";
    private static final EvaluationId EVALUATION_ID = EvaluationId.of(UUID.randomUUID());
    private static final OperatorId OPERATOR_ID = OperatorId.of(UUID.randomUUID());

    private static Evaluation evaluation(EvaluationState state) {
        return Evaluation.reconstruct(
                EVALUATION_ID, OPERATOR_ID, state, EvaluationSource.CALCULATOR,
                TOKEN, Instant.parse("2026-07-01T00:00:00Z"),
                Instant.parse("2026-07-01T00:00:00Z"));
    }

    private static List<BenchmarkQuestion> questions() {
        List<BenchmarkQuestion> list = new ArrayList<>();
        for (BenchmarkModule module : BenchmarkModule.values()) {
            for (int i = 0; i < 4; i++) {
                list.add(new BenchmarkQuestion(UUID.randomUUID(), "v1", module, list.size() + 1,
                        "Pregunta", true));
            }
        }
        return list;
    }

    private static List<BenchmarkAnswer> answers(List<BenchmarkQuestion> qs) {
        return qs.stream().map(q -> new BenchmarkAnswer(q.id(), 4)).toList();
    }

    private SubmitBenchmarkService service(LoadEvaluationPort evaluations,
                                           SaveEvaluationPort saveEvaluation,
                                           LoadBenchmarkQuestionsPort questions,
                                           SaveBenchmarkResultPort results) {
        return new SubmitBenchmarkService(evaluations, saveEvaluation, questions, results);
    }

    @Test
    void submitsAndCompletesBenchmarkForValidToken() {
        List<BenchmarkQuestion> qs = questions();
        var saveCollector = new AtomicInteger();

        SubmitBenchmarkService svc = service(
                id -> Optional.of(evaluation(EvaluationState.CALCULATOR_COMPLETED)),
                e -> saveCollector.incrementAndGet(),
                version -> qs,
                (id, version, ans, result) -> {});

        BenchmarkResult result = svc.submit(new SubmitBenchmarkUseCase.SubmitBenchmarkCommand(
                EVALUATION_ID.value(), TOKEN, "v1", answers(qs)));

        assertThat(result.totalScore()).isBetween(20.0, 100.0);
        assertThat(saveCollector.get()).isEqualTo(1);
    }

    @Test
    void rejectsInvalidTokenForAnotherCompany() {
        List<BenchmarkQuestion> qs = questions();
        SubmitBenchmarkService svc = service(
                id -> Optional.of(evaluation(EvaluationState.CALCULATOR_COMPLETED)),
                e -> {},
                version -> qs,
                (id, version, ans, result) -> {});

        assertThatThrownBy(() -> svc.submit(new SubmitBenchmarkUseCase.SubmitBenchmarkCommand(
                EVALUATION_ID.value(), "wrong-token", "v1", answers(qs))))
                .isInstanceOf(InvalidEvaluationTokenException.class);
    }

    @Test
    void rejectsEvaluationNotFound() {
        SubmitBenchmarkService svc = service(
                id -> Optional.empty(), e -> {}, ThisVersion -> List.of(), (a, b, c, d) -> {});

        assertThatThrownBy(() -> svc.submit(new SubmitBenchmarkUseCase.SubmitBenchmarkCommand(
                EVALUATION_ID.value(), TOKEN, "v1", List.of())))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsSubmissionWhenQuestionnaireHasMissingQuestions() {
        List<BenchmarkQuestion> onlyOneModule = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            onlyOneModule.add(new BenchmarkQuestion(UUID.randomUUID(), "v1",
                    BenchmarkModule.AUTOMATION, i + 1, "P", true));
        }
        SubmitBenchmarkService svc = service(
                id -> Optional.of(evaluation(EvaluationState.CALCULATOR_COMPLETED)),
                e -> {},
                version -> onlyOneModule,
                (id, version, ans, result) -> {});

        assertThatThrownBy(() -> svc.submit(new SubmitBenchmarkUseCase.SubmitBenchmarkCommand(
                EVALUATION_ID.value(), TOKEN, "v1",
                onlyOneModule.stream().map(q -> new BenchmarkAnswer(q.id(), 3)).toList())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("20 preguntas");
    }

    @Test
    void doesNotDuplicateWhenAlreadyCompleted() {
        // Estado ya BENCHMARK_COMPLETED → el segundo submit lanza 409, no duplica.
        List<BenchmarkQuestion> qs = questions();
        var saves = new AtomicInteger();
        SubmitBenchmarkService svc = service(
                id -> Optional.of(evaluation(EvaluationState.BENCHMARK_COMPLETED)),
                e -> saves.incrementAndGet(),
                version -> qs,
                (id, version, ans, result) -> {});

        assertThatThrownBy(() -> svc.submit(new SubmitBenchmarkUseCase.SubmitBenchmarkCommand(
                EVALUATION_ID.value(), TOKEN, "v1", answers(qs))))
                .isInstanceOf(InvalidEvaluationStateException.class);

        assertThat(saves.get()).isZero();
    }
}
