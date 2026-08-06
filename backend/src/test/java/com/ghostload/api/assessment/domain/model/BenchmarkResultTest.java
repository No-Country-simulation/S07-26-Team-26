package com.ghostload.api.assessment.domain.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BenchmarkResultTest {

    private static List<BenchmarkQuestion> questions(int valuePerModule) {
        List<BenchmarkQuestion> list = new ArrayList<>();
        for (BenchmarkModule module : BenchmarkModule.values()) {
            for (int i = 0; i < 4; i++) {
                list.add(new BenchmarkQuestion(UUID.randomUUID(), "v1", module, list.size() + 1,
                        "Pregunta", true));
            }
        }
        return list;
    }

    private static List<BenchmarkAnswer> answers(List<BenchmarkQuestion> questions, int value) {
        return questions.stream()
                .map(q -> new BenchmarkAnswer(q.id(), value))
                .toList();
    }

    @Test
    void computesScoreAndPercentileForAllFives() {
        // Todas las respuestas en 5 → score 100.
        List<BenchmarkQuestion> qs = questions(5);
        BenchmarkResult result = BenchmarkResult.calculate(qs, answers(qs, 5));

        assertThat(result.totalScore()).isCloseTo(100.0, within(0.0001));
        assertThat(result.maturityLevel()).isEqualTo(MaturityLevel.OPTIMIZED);
        assertThat(result.percentile()).isCloseTo(100.0, within(0.0001));
        assertThat(result.moduleScores()).hasSize(5);
        result.moduleScores().forEach(ms -> assertThat(ms.score()).isCloseTo(100.0, within(0.0001)));
    }

    @Test
    void computesScoreForAllOnes() {
        List<BenchmarkQuestion> qs = questions(1);
        BenchmarkResult result = BenchmarkResult.calculate(qs, answers(qs, 1));

        assertThat(result.totalScore()).isCloseTo(20.0, within(0.0001));
        assertThat(result.maturityLevel()).isEqualTo(MaturityLevel.INITIAL);
    }

    @Test
    void mapsModuleBoundariesForMaturityLevel() {
        assertThat(MaturityLevel.fromScore(35.9)).isEqualTo(MaturityLevel.INITIAL);
        assertThat(MaturityLevel.fromScore(36.0)).isEqualTo(MaturityLevel.DEVELOPING);
        assertThat(MaturityLevel.fromScore(51.9)).isEqualTo(MaturityLevel.DEVELOPING);
        assertThat(MaturityLevel.fromScore(52.0)).isEqualTo(MaturityLevel.MANAGED);
        assertThat(MaturityLevel.fromScore(67.9)).isEqualTo(MaturityLevel.MANAGED);
        assertThat(MaturityLevel.fromScore(68.0)).isEqualTo(MaturityLevel.ADVANCED);
        assertThat(MaturityLevel.fromScore(83.9)).isEqualTo(MaturityLevel.ADVANCED);
        assertThat(MaturityLevel.fromScore(84.0)).isEqualTo(MaturityLevel.OPTIMIZED);
        assertThat(MaturityLevel.fromScore(100.0)).isEqualTo(MaturityLevel.OPTIMIZED);
    }

    @Test
    void rejectsMissingAnswers() {
        List<BenchmarkQuestion> qs = questions(5);
        List<BenchmarkAnswer> partial = qs.subList(0, 19).stream()
                .map(q -> new BenchmarkAnswer(q.id(), 3))
                .toList();

        assertThatThrownBy(() -> BenchmarkResult.calculate(qs, partial))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Debes responder exactamente las 20 preguntas");
    }

    @Test
    void rejectsDuplicateAnswerAndIncompleteModules() {
        List<BenchmarkQuestion> qs = questions(5);
        List<BenchmarkQuestion> bad = qs.subList(0, 8).stream()
                .map(q -> new BenchmarkQuestion(q.id(), "v1", q.module(), q.questionOrder(), q.text(), true))
                .toList();
        List<BenchmarkAnswer> ans = bad.stream().map(q -> new BenchmarkAnswer(q.id(), 3)).toList();

        assertThatThrownBy(() -> BenchmarkResult.calculate(bad, ans))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("20 preguntas");
    }
}