package com.ghostload.api.assessment.application.service;

import com.ghostload.api.assessment.application.port.in.SaveBenchmarkProgressUseCase;
import com.ghostload.api.assessment.application.port.out.LoadBenchmarkQuestionsPort;
import com.ghostload.api.assessment.application.port.out.LoadEvaluationPort;
import com.ghostload.api.assessment.application.port.out.SaveBenchmarkProgressPort;
import com.ghostload.api.assessment.domain.exception.InvalidEvaluationStateException;
import com.ghostload.api.assessment.domain.exception.InvalidEvaluationTokenException;
import com.ghostload.api.assessment.domain.model.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SaveBenchmarkProgressServiceTest {

    private static final UUID EVAL_ID = UUID.randomUUID();
    private static final String TOKEN = "token-seguro";

    private Evaluation startedEvaluation() {
        return Evaluation.reconstruct(
                EvaluationId.of(EVAL_ID), OperatorId.of(UUID.randomUUID()),
                EvaluationState.STARTED, EvaluationSource.CALCULATOR,
                TOKEN, Instant.now(), Instant.now());
    }

    private List<BenchmarkQuestion> fourQuestions() {
        return List.of(
                new BenchmarkQuestion(UUID.randomUUID(), "v1", BenchmarkModule.CAPACITY_VISIBILITY, 1, "P1", true),
                new BenchmarkQuestion(UUID.randomUUID(), "v1", BenchmarkModule.CAPACITY_VISIBILITY, 2, "P2", true),
                new BenchmarkQuestion(UUID.randomUUID(), "v1", BenchmarkModule.CAPACITY_VISIBILITY, 3, "P3", true),
                new BenchmarkQuestion(UUID.randomUUID(), "v1", BenchmarkModule.CAPACITY_VISIBILITY, 4, "P4", true));
    }

    private SaveBenchmarkProgressUseCase.SaveBenchmarkProgressCommand command(List<BenchmarkAnswer> answers) {
        return new SaveBenchmarkProgressUseCase.SaveBenchmarkProgressCommand(
                EVAL_ID, TOKEN, "v1", answers);
    }

    @Test
    void savesProgressAndComputesCompletion() {
        AtomicInteger saves = new AtomicInteger();
        SaveBenchmarkProgressService svc = new SaveBenchmarkProgressService(
                id -> Optional.of(startedEvaluation()),
                version -> fourQuestions(),
                (evalId, answers) -> saves.incrementAndGet());

        var result = svc.saveProgress(command(List.of(
                new BenchmarkAnswer(UUID.randomUUID(), 3),
                new BenchmarkAnswer(UUID.randomUUID(), 4))));

        assertThat(result.answeredCount()).isEqualTo(2);
        assertThat(result.completionPercentage()).isEqualTo(50.0);
        assertThat(saves.get()).isEqualTo(1);
    }

    @Test
    void rejectsInvalidToken() {
        SaveBenchmarkProgressService svc = new SaveBenchmarkProgressService(
                id -> Optional.of(startedEvaluation()),
                version -> fourQuestions(),
                (evalId, answers) -> {});

        SaveBenchmarkProgressUseCase.SaveBenchmarkProgressCommand bad =
                new SaveBenchmarkProgressUseCase.SaveBenchmarkProgressCommand(
                        EVAL_ID, "token-incorrecto", "v1", List.of());

        assertThatThrownBy(() -> svc.saveProgress(bad))
                .isInstanceOf(InvalidEvaluationTokenException.class);
    }

    @Test
    void rejectsProgressOnCompletedBenchmark() {
        Evaluation completed = Evaluation.reconstruct(
                EvaluationId.of(EVAL_ID), OperatorId.of(UUID.randomUUID()),
                EvaluationState.BENCHMARK_COMPLETED, EvaluationSource.CALCULATOR,
                TOKEN, Instant.now(), Instant.now());
        SaveBenchmarkProgressService svc = new SaveBenchmarkProgressService(
                id -> Optional.of(completed),
                version -> fourQuestions(),
                (evalId, answers) -> {});

        assertThatThrownBy(() -> svc.saveProgress(command(List.of())))
                .isInstanceOf(InvalidEvaluationStateException.class);
    }
}