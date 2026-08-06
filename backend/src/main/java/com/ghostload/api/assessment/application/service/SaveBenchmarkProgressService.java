package com.ghostload.api.assessment.application.service;

import com.ghostload.api.assessment.application.port.in.SaveBenchmarkProgressUseCase;
import com.ghostload.api.assessment.application.port.out.LoadBenchmarkQuestionsPort;
import com.ghostload.api.assessment.application.port.out.LoadEvaluationPort;
import com.ghostload.api.assessment.application.port.out.SaveBenchmarkProgressPort;
import com.ghostload.api.assessment.domain.exception.InvalidEvaluationStateException;
import com.ghostload.api.assessment.domain.exception.InvalidEvaluationTokenException;
import com.ghostload.api.assessment.domain.model.Evaluation;
import com.ghostload.api.assessment.domain.model.EvaluationId;
import com.ghostload.api.assessment.domain.model.EvaluationState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SaveBenchmarkProgressService implements SaveBenchmarkProgressUseCase {

    private static final String DEFAULT_VERSION = "v1";

    private final LoadEvaluationPort evaluations;
    private final LoadBenchmarkQuestionsPort questions;
    private final SaveBenchmarkProgressPort progress;

    public SaveBenchmarkProgressService(
            LoadEvaluationPort evaluations,
            LoadBenchmarkQuestionsPort questions,
            SaveBenchmarkProgressPort progress) {
        this.evaluations = evaluations;
        this.questions = questions;
        this.progress = progress;
    }

    @Override
    @Transactional
    public ProgressResult saveProgress(SaveBenchmarkProgressCommand command) {
        Evaluation evaluation = evaluations.findById(EvaluationId.of(command.evaluationId()))
                .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada."));
        if (!evaluation.evaluationToken().equals(command.evaluationToken())) {
            throw new InvalidEvaluationTokenException("El token de evaluación no es válido.");
        }
        if (evaluation.state() == EvaluationState.BENCHMARK_COMPLETED) {
            throw new InvalidEvaluationStateException(
                    "No se puede guardar progreso: el benchmark ya fue completado.");
        }

        String version = command.questionnaireVersion() == null
                ? DEFAULT_VERSION
                : command.questionnaireVersion();
        int activeCount = questions.findActiveByVersion(version).size();

        List<com.ghostload.api.assessment.domain.model.BenchmarkAnswer> answers =
                command.answers().stream().distinct().toList();
        progress.saveProgress(command.evaluationId(), answers);

        int answered = answers.size();
        double completion = activeCount == 0 ? 0d : (answered * 100.0) / activeCount;
        return new ProgressResult(answered, Math.min(100d, completion));
    }
}