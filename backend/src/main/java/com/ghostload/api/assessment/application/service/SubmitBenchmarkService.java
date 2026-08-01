package com.ghostload.api.assessment.application.service;

import com.ghostload.api.assessment.application.port.in.SubmitBenchmarkUseCase;
import com.ghostload.api.assessment.application.port.out.*;
import com.ghostload.api.assessment.domain.exception.InvalidEvaluationTokenException;
import com.ghostload.api.assessment.domain.model.*;
import com.ghostload.api.outreach.application.port.in.CompleteInvitationUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.util.List;

@Service
public class SubmitBenchmarkService implements SubmitBenchmarkUseCase {
    private final LoadEvaluationPort evaluations;
    private final SaveEvaluationPort saveEvaluation;
    private final LoadBenchmarkQuestionsPort questions;
    private final SaveBenchmarkResultPort results;
    private final CompleteInvitationUseCase completeInvitation;
    private final Clock clock;
    public SubmitBenchmarkService(LoadEvaluationPort evaluations, SaveEvaluationPort saveEvaluation,
                                  LoadBenchmarkQuestionsPort questions, SaveBenchmarkResultPort results,
                                  CompleteInvitationUseCase completeInvitation, Clock clock) {
        this.evaluations = evaluations; this.saveEvaluation = saveEvaluation; this.questions = questions; this.results = results;
        this.completeInvitation = completeInvitation; this.clock = clock;
    }
    @Override @Transactional public BenchmarkResult submit(SubmitBenchmarkCommand command) {
        Evaluation evaluation = evaluations.findById(EvaluationId.of(command.evaluationId()))
                .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada."));
        if (!evaluation.evaluationToken().equals(command.evaluationToken())) throw new InvalidEvaluationTokenException("El token de evaluación no es válido.");
        List<BenchmarkQuestion> activeQuestions = questions.findActiveByVersion(command.questionnaireVersion());
        BenchmarkResult result = BenchmarkResult.calculate(activeQuestions, command.answers());
        evaluation.markBenchmarkCompleted();
        results.save(command.evaluationId(), command.questionnaireVersion(), command.answers(), result);
        saveEvaluation.save(evaluation);
        completeInvitation.complete(command.evaluationId(), clock.instant());
        return result;
    }
}
