package com.ghostload.api.assessment.adapter.out.persistence;

import com.ghostload.api.assessment.application.port.out.LoadBenchmarkAnswersPort;
import com.ghostload.api.assessment.application.port.out.LoadBenchmarkQuestionsPort;
import com.ghostload.api.assessment.application.port.out.LoadBenchmarkResultPort;
import com.ghostload.api.assessment.application.port.out.SaveBenchmarkProgressPort;
import com.ghostload.api.assessment.application.port.out.SaveBenchmarkResultPort;
import com.ghostload.api.assessment.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class BenchmarkPersistenceAdapter implements LoadBenchmarkQuestionsPort, SaveBenchmarkResultPort,
        SaveBenchmarkProgressPort, LoadBenchmarkAnswersPort, LoadBenchmarkResultPort {
    private final BenchmarkQuestionJpaRepository questions;
    private final BenchmarkResultJpaRepository results;
    private final BenchmarkAnswerJpaRepository answers;
    private final BenchmarkModuleScoreJpaRepository moduleScores;
    public BenchmarkPersistenceAdapter(BenchmarkQuestionJpaRepository questions, BenchmarkResultJpaRepository results,
                                       BenchmarkAnswerJpaRepository answers, BenchmarkModuleScoreJpaRepository moduleScores) {
        this.questions = questions; this.results = results; this.answers = answers; this.moduleScores = moduleScores;
    }
    @Override public List<BenchmarkQuestion> findActiveByVersion(String version) {
        return questions.findByVersionAndActiveTrueOrderByQuestionOrder(version).stream()
                .map(question -> new BenchmarkQuestion(question.getId(), question.getVersion(),
                        BenchmarkModule.valueOf(question.getModuleCode()), question.getQuestionOrder(), question.getText(), question.isActive()))
                .toList();
    }
    @Override public void save(UUID evaluationId, String questionnaireVersion, List<BenchmarkAnswer> answerValues, BenchmarkResult result) {
        results.save(new BenchmarkResultJpaEntity(evaluationId, questionnaireVersion, result.totalScore(), result.maturityLevel().name(), result.percentile(), result.completedAt()));
        answers.deleteByEvaluationId(evaluationId);
        moduleScores.deleteByEvaluationId(evaluationId);
        answers.saveAll(answerValues.stream().map(answer -> new BenchmarkAnswerJpaEntity(evaluationId, answer.questionId(), answer.value())).toList());
        moduleScores.saveAll(result.moduleScores().stream().map(score -> new BenchmarkModuleScoreJpaEntity(evaluationId, score.module().name(), score.score())).toList());
    }

    @Override
    public void saveProgress(UUID evaluationId, List<BenchmarkAnswer> answerValues) {
        answers.deleteByEvaluationId(evaluationId);
        answers.saveAll(answerValues.stream()
                .map(answer -> new BenchmarkAnswerJpaEntity(evaluationId, answer.questionId(), answer.value()))
                .toList());
    }

    @Override
    public List<BenchmarkAnswer> findAnswers(UUID evaluationId) {
        return answers.findByEvaluationId(evaluationId).stream()
                .map(entity -> new BenchmarkAnswer(entity.getQuestionId(), entity.getValue()))
                .toList();
    }

    @Override
    public Optional<BenchmarkResult> findByEvaluationId(UUID evaluationId) {
        return results.findById(evaluationId)
                .map(entity -> new BenchmarkResult(
                        entity.getTotalScore(),
                        MaturityLevel.valueOf(entity.getMaturityLevel()),
                        entity.getPercentile(),
                        moduleScores.findByEvaluationId(evaluationId).stream()
                                .map(score -> new ModuleScore(
                                        BenchmarkModule.valueOf(score.getModuleCode()),
                                        score.getScore()))
                                .toList(),
                        entity.getCompletedAt()));
    }
}