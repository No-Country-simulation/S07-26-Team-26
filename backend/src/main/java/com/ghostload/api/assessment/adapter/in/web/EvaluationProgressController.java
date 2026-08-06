package com.ghostload.api.assessment.adapter.in.web;

import com.ghostload.api.assessment.application.port.in.GetEvaluationStatusUseCase;
import com.ghostload.api.assessment.application.port.in.GetEvaluationStatusUseCase.GetEvaluationStatusCommand;
import com.ghostload.api.assessment.application.port.in.SaveBenchmarkProgressUseCase;
import com.ghostload.api.assessment.application.port.in.SaveBenchmarkProgressUseCase.SaveBenchmarkProgressCommand;
import com.ghostload.api.assessment.domain.model.BenchmarkAnswer;
import com.ghostload.api.assessment.domain.model.BenchmarkResult;
import com.ghostload.api.assessment.domain.model.CalculatorResult;
import com.ghostload.api.assessment.domain.model.ModuleScore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evaluations/{evaluationId}")
public class EvaluationProgressController {

    private final SaveBenchmarkProgressUseCase saveProgress;
    private final GetEvaluationStatusUseCase getStatus;

    public EvaluationProgressController(SaveBenchmarkProgressUseCase saveProgress,
                                        GetEvaluationStatusUseCase getStatus) {
        this.saveProgress = saveProgress;
        this.getStatus = getStatus;
    }

    @PutMapping("/benchmark/progress")
    public ResponseEntity<BenchmarkProgressResponse> saveProgress(
            @PathVariable UUID evaluationId,
            @RequestHeader("X-Evaluation-Token") String evaluationToken,
            @Valid @RequestBody SaveBenchmarkProgressRequest request) {

        var result = saveProgress.saveProgress(new SaveBenchmarkProgressCommand(
                evaluationId,
                evaluationToken,
                request.questionnaireVersion(),
                request.answers().stream()
                        .map(answer -> new BenchmarkAnswer(answer.questionId(), answer.value()))
                        .toList()));

        return ResponseEntity.ok(new BenchmarkProgressResponse(
                result.answeredCount(), result.completionPercentage()));
    }

    @GetMapping("/status")
    public ResponseEntity<EvaluationStatusResponse> getStatus(
            @PathVariable UUID evaluationId,
            @RequestHeader("X-Evaluation-Token") String evaluationToken) {

        var status = getStatus.getStatus(new GetEvaluationStatusCommand(evaluationId, evaluationToken));
        return ResponseEntity.ok(toResponse(status));
    }

    private EvaluationStatusResponse toResponse(GetEvaluationStatusUseCase.EvaluationStatus status) {
        return new EvaluationStatusResponse(
                status.evaluationId(),
                status.operatorId(),
                status.firstName(),
                status.lastName(),
                status.email(),
                status.companyName(),
                status.position(),
                status.state(),
                status.calculatorResult() == null ? null : new CalculatorResultWeb(
                        status.calculatorResult().totalCapacityMw(),
                        status.calculatorResult().productiveCapacityMw(),
                        status.calculatorResult().nonProductiveCapacityMw(),
                        status.calculatorResult().utilizationPercentage(),
                        status.calculatorResult().nonProductivePercentage(),
                        status.calculatorResult().monthlyCostPerKw(),
                        status.calculatorResult().estimatedAnnualCost(),
                        status.calculatorResult().currency(),
                        status.calculatorResult().calculatedAt()),
                status.answers().stream()
                        .map(answer -> new BenchmarkAnswerWeb(answer.questionId(), answer.value()))
                        .toList(),
                status.answeredCount(),
                status.completionPercentage(),
                status.benchmarkResult() == null ? null : new BenchmarkResultWeb(
                        status.benchmarkResult().totalScore(),
                        status.benchmarkResult().maturityLevel().name(),
                        status.benchmarkResult().percentile(),
                        status.benchmarkResult().moduleScores().stream()
                                .map(score -> new ModuleScoreWeb(score.module().name(), score.score()))
                                .toList(),
                        status.benchmarkResult().completedAt()));
    }

    public record SaveBenchmarkProgressRequest(String questionnaireVersion,
                                               @NotNull @Size(max = 20) List<@Valid BenchmarkAnswerRequest> answers) {
    }

    public record BenchmarkProgressResponse(int answeredCount, double completionPercentage) {
    }

    public record EvaluationStatusResponse(UUID evaluationId, String operatorId, String firstName, String lastName,
                                           String email, String companyName, String position, String state,
                                           CalculatorResultWeb calculatorResult, List<BenchmarkAnswerWeb> answers,
                                           int answeredCount, double completionPercentage,
                                           BenchmarkResultWeb benchmarkResult) {
    }

    public record CalculatorResultWeb(double totalCapacityMw, double productiveCapacityMw, double nonProductiveCapacityMw,
                                      double utilizationPercentage, double nonProductivePercentage, double monthlyCostPerKw,
                                      double estimatedAnnualCost, String currency, Instant calculatedAt) {
    }

    public record BenchmarkAnswerWeb(UUID questionId, int value) {
    }

    public record BenchmarkResultWeb(double totalScore, String maturityLevel, double percentile,
                                     List<ModuleScoreWeb> moduleScores, Instant completedAt) {
    }

    public record ModuleScoreWeb(String module, double score) {
    }
}