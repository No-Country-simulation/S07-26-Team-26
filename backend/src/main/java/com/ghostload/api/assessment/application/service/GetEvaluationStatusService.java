package com.ghostload.api.assessment.application.service;

import com.ghostload.api.assessment.application.port.in.GetEvaluationStatusUseCase;
import com.ghostload.api.assessment.application.port.out.LoadBenchmarkAnswersPort;
import com.ghostload.api.assessment.application.port.out.LoadBenchmarkQuestionsPort;
import com.ghostload.api.assessment.application.port.out.LoadBenchmarkResultPort;
import com.ghostload.api.assessment.application.port.out.LoadCalculatorResultPort;
import com.ghostload.api.assessment.application.port.out.LoadEvaluationPort;
import com.ghostload.api.assessment.application.port.out.LoadOperatorByIdPort;
import com.ghostload.api.assessment.domain.exception.InvalidEvaluationTokenException;
import com.ghostload.api.assessment.domain.model.Evaluation;
import com.ghostload.api.assessment.domain.model.EvaluationId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetEvaluationStatusService implements GetEvaluationStatusUseCase {

    private static final String DEFAULT_VERSION = "v1";

    private final LoadEvaluationPort evaluations;
    private final LoadOperatorByIdPort operators;
    private final LoadCalculatorResultPort calculators;
    private final LoadBenchmarkAnswersPort answers;
    private final LoadBenchmarkResultPort results;
    private final LoadBenchmarkQuestionsPort questions;

    public GetEvaluationStatusService(
            LoadEvaluationPort evaluations,
            LoadOperatorByIdPort operators,
            LoadCalculatorResultPort calculators,
            LoadBenchmarkAnswersPort answers,
            LoadBenchmarkResultPort results,
            LoadBenchmarkQuestionsPort questions) {
        this.evaluations = evaluations;
        this.operators = operators;
        this.calculators = calculators;
        this.answers = answers;
        this.results = results;
        this.questions = questions;
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluationStatus getStatus(GetEvaluationStatusCommand command) {
        Evaluation evaluation = evaluations.findById(EvaluationId.of(command.evaluationId()))
                .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada."));
        if (!evaluation.evaluationToken().equals(command.evaluationToken())) {
            throw new InvalidEvaluationTokenException("El token de evaluación no es válido.");
        }

        var operator = operators.findById(evaluation.operatorId())
                .orElseThrow(() -> new IllegalArgumentException("Operador no encontrado."));

        var calculator = calculators.findByEvaluationId(evaluation.id());
        var benchmarkAnswers = answers.findAnswers(command.evaluationId());
        var benchmarkResult = results.findByEvaluationId(command.evaluationId());

        int activeCount = questions.findActiveByVersion(DEFAULT_VERSION).size();
        double completion = activeCount == 0
                ? 0d
                : (benchmarkAnswers.size() * 100.0) / activeCount;

        return new EvaluationStatus(
                evaluation.id().value(),
                operator.id().value().toString(),
                operator.firstName(),
                operator.lastName(),
                operator.email().value(),
                operator.companyName(),
                operator.position(),
                evaluation.state().name(),
                calculator.orElse(null),
                List.copyOf(benchmarkAnswers),
                benchmarkAnswers.size(),
                Math.min(100d, completion),
                benchmarkResult.orElse(null));
    }
}