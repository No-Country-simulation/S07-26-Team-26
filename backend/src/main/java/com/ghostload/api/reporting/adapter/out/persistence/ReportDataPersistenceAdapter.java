package com.ghostload.api.reporting.adapter.out.persistence;

import com.ghostload.api.assessment.adapter.out.persistence.BenchmarkModuleScoreJpaRepository;
import com.ghostload.api.assessment.adapter.out.persistence.BenchmarkResultJpaRepository;
import com.ghostload.api.assessment.adapter.out.persistence.CalculatorResultJpaRepository;
import com.ghostload.api.assessment.adapter.out.persistence.EvaluationJpaRepository;
import com.ghostload.api.assessment.adapter.out.persistence.OperatorJpaRepository;
import com.ghostload.api.assessment.domain.model.BenchmarkModule;
import com.ghostload.api.assessment.domain.model.MaturityLevel;
import com.ghostload.api.assessment.domain.model.ModuleScore;
import com.ghostload.api.reporting.application.port.out.LoadReportDataPort;
import com.ghostload.api.reporting.domain.exception.PdfNotFoundException;
import com.ghostload.api.reporting.domain.model.ReportData;
import com.ghostload.api.reporting.domain.model.ReportData.CalculatorMetrics;
import com.ghostload.api.reporting.domain.model.ReportData.OperatorInfo;
import com.ghostload.api.reporting.domain.model.ReportData.BenchmarkSummary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

// Arma el ReportData que consume la plantilla del PDF leyendo las tablas
// existentes: operador, evaluación, calculadora, benchmark y puntajes por módulo.
@Component
public class ReportDataPersistenceAdapter implements LoadReportDataPort {

    private final EvaluationJpaRepository evaluations;
    private final OperatorJpaRepository operators;
    private final CalculatorResultJpaRepository calculatorResults;
    private final BenchmarkResultJpaRepository benchmarkResults;
    private final BenchmarkModuleScoreJpaRepository moduleScores;

    public ReportDataPersistenceAdapter(EvaluationJpaRepository evaluations,
                                        OperatorJpaRepository operators,
                                        CalculatorResultJpaRepository calculatorResults,
                                        BenchmarkResultJpaRepository benchmarkResults,
                                        BenchmarkModuleScoreJpaRepository moduleScores) {
        this.evaluations = evaluations;
        this.operators = operators;
        this.calculatorResults = calculatorResults;
        this.benchmarkResults = benchmarkResults;
        this.moduleScores = moduleScores;
    }

    @Override
    public ReportData load(UUID evaluationId) {
        var evaluation = evaluations.findById(evaluationId)
                .orElseThrow(() -> new PdfNotFoundException("Evaluación no encontrada."));
        var operator = operators.findById(evaluation.getOperatorId())
                .orElseThrow(() -> new PdfNotFoundException("Operador no encontrado."));
        var calculator = calculatorResults.findById(evaluationId)
                .orElseThrow(() -> new PdfNotFoundException("La calculadora no fue completada."));
        var benchmark = benchmarkResults.findById(evaluationId)
                .orElseThrow(() -> new PdfNotFoundException("El benchmark no fue completado."));
        List<ModuleScore> scores = moduleScores.findByEvaluationId(evaluationId).stream()
                .map(score -> new ModuleScore(
                        BenchmarkModule.valueOf(score.getModuleCode()),
                        score.getScore()))
                .toList();

        return new ReportData(
                evaluationId,
                benchmark.getQuestionnaireVersion(),
                new OperatorInfo(
                        operator.getFirstName(),
                        operator.getLastName(),
                        operator.getEmail(),
                        operator.getCompanyName(),
                        operator.getPosition(),
                        operator.getCountry()),
                new CalculatorMetrics(
                        calculator.getTotalCapacityMw(),
                        calculator.getProductiveCapacityMw(),
                        calculator.getNonProductiveCapacityMw(),
                        calculator.getUtilizationPercentage(),
                        calculator.getNonProductivePercentage(),
                        calculator.getMonthlyCostPerKw(),
                        calculator.getEstimatedAnnualCost(),
                        calculator.getCurrency()),
                new BenchmarkSummary(
                        benchmark.getTotalScore(),
                        MaturityLevel.valueOf(benchmark.getMaturityLevel()),
                        benchmark.getPercentile(),
                        scores,
                        benchmark.getCompletedAt()));
    }
}
