package com.ghostload.api.assessment.application.service;

import com.ghostload.api.assessment.application.port.in.SubmitBenchmarkUseCase;
import com.ghostload.api.assessment.domain.model.BenchmarkAnswer;
import com.ghostload.api.assessment.domain.model.BenchmarkModule;
import com.ghostload.api.assessment.domain.model.BenchmarkQuestion;
import com.ghostload.api.assessment.domain.model.Evaluation;
import com.ghostload.api.assessment.domain.model.EvaluationSource;
import com.ghostload.api.assessment.domain.model.OperatorId;
import com.ghostload.api.outreach.application.port.in.CompleteInvitationUseCase;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class SubmitBenchmarkServiceTest {

    private static final Instant NOW = Instant.parse("2026-07-30T18:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Test
    void shouldCompleteInvitationAfterSavingBenchmark() {
        Evaluation evaluation = Evaluation.start(
                OperatorId.newId(),
                EvaluationSource.OUTREACH,
                "evaluation-secret");
        evaluation.markCalculatorCompleted();
        List<BenchmarkQuestion> questions = questions();
        List<BenchmarkAnswer> answers = questions.stream()
                .map(question -> new BenchmarkAnswer(question.id(), 5))
                .toList();
        AtomicReference<Evaluation> savedEvaluation = new AtomicReference<>();
        AtomicReference<java.util.UUID> completedEvaluation = new AtomicReference<>();
        AtomicReference<Instant> completedAt = new AtomicReference<>();
        CompleteInvitationUseCase completeInvitation = (evaluationId, instant) -> {
            completedEvaluation.set(evaluationId);
            completedAt.set(instant);
        };
        SubmitBenchmarkService service = new SubmitBenchmarkService(
                ignored -> Optional.of(evaluation),
                savedEvaluation::set,
                ignored -> questions,
                (evaluationId, version, savedAnswers, result) -> {
                },
                completeInvitation,
                CLOCK);

        var result = service.submit(new SubmitBenchmarkUseCase.SubmitBenchmarkCommand(
                evaluation.id().value(),
                "evaluation-secret",
                "v1",
                answers));

        assertThat(result.totalScore()).isEqualTo(100);
        assertThat(savedEvaluation.get().state().name())
                .isEqualTo("BENCHMARK_COMPLETED");
        assertThat(completedEvaluation.get()).isEqualTo(evaluation.id().value());
        assertThat(completedAt.get()).isEqualTo(NOW);
    }

    private List<BenchmarkQuestion> questions() {
        List<BenchmarkQuestion> questions = new ArrayList<>();
        int order = 1;
        for (BenchmarkModule module : BenchmarkModule.values()) {
            for (int index = 0; index < 4; index++) {
                questions.add(new BenchmarkQuestion(
                        java.util.UUID.randomUUID(),
                        "v1",
                        module,
                        order++,
                        "Pregunta",
                        true));
            }
        }
        return questions;
    }
}
