package com.ghostload.api.assessment.domain.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BenchmarkResultTest {
    @Test
    void shouldCalculateFiveModuleScoresAndOverallMaturity() {
        List<BenchmarkQuestion> questions = questions();
        List<BenchmarkAnswer> answers = questions.stream().map(question -> new BenchmarkAnswer(question.id(), 4)).toList();

        BenchmarkResult result = BenchmarkResult.calculate(questions, answers);

        assertThat(result.totalScore()).isEqualTo(80d);
        assertThat(result.percentile()).isEqualTo(80d);
        assertThat(result.maturityLevel()).isEqualTo(MaturityLevel.OPTIMIZED);
        assertThat(result.moduleScores()).hasSize(5).allSatisfy(score -> assertThat(score.score()).isEqualTo(80d));
    }

    @Test
    void shouldRejectIncompleteBenchmark() {
        List<BenchmarkQuestion> questions = questions();
        List<BenchmarkAnswer> answers = questions.subList(0, 19).stream().map(question -> new BenchmarkAnswer(question.id(), 3)).toList();

        assertThatThrownBy(() -> BenchmarkResult.calculate(questions, answers))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("exactamente las 20");
    }

    private List<BenchmarkQuestion> questions() {
        List<BenchmarkQuestion> questions = new ArrayList<>();
        BenchmarkModule[] modules = BenchmarkModule.values();
        for (int order = 1; order <= 20; order++) {
            questions.add(new BenchmarkQuestion(UUID.randomUUID(), "v1", modules[(order - 1) / 4], order, "Pregunta de prueba " + order, true));
        }
        return questions;
    }
}
