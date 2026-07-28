package com.ghostload.api.assessment.adapter.in.web;

import com.ghostload.api.assessment.application.port.in.ListBenchmarkQuestionsUseCase;
import com.ghostload.api.assessment.application.port.in.SubmitBenchmarkUseCase;
import com.ghostload.api.assessment.domain.model.BenchmarkAnswer;
import com.ghostload.api.assessment.domain.model.BenchmarkResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class BenchmarkController {
    private static final List<BenchmarkScaleOptionResponse> SCALE = List.of(
            new BenchmarkScaleOptionResponse(1, "INEXISTENT"),
            new BenchmarkScaleOptionResponse(2, "INITIAL"),
            new BenchmarkScaleOptionResponse(3, "DEFINED"),
            new BenchmarkScaleOptionResponse(4, "MANAGED"),
            new BenchmarkScaleOptionResponse(5, "OPTIMIZED"));
    private final ListBenchmarkQuestionsUseCase listQuestions;
    private final SubmitBenchmarkUseCase submitBenchmark;
    public BenchmarkController(ListBenchmarkQuestionsUseCase listQuestions, SubmitBenchmarkUseCase submitBenchmark) {
        this.listQuestions = listQuestions; this.submitBenchmark = submitBenchmark;
    }
    @GetMapping("/benchmark/questions")
    public List<BenchmarkQuestionResponse> list(@RequestParam(required = false, defaultValue = "v1") String version) {
        return listQuestions.list(version).stream().map(question -> new BenchmarkQuestionResponse(
                question.id(), question.version(), question.module(), question.questionOrder(), question.text(), question.active(), SCALE)).toList();
    }
    @PutMapping("/evaluations/{evaluationId}/benchmark")
    public ResponseEntity<BenchmarkResultResponse> submit(@PathVariable UUID evaluationId,
            @RequestHeader("X-Evaluation-Token") String evaluationToken,
            @Valid @RequestBody BenchmarkSubmissionRequest request) {
        BenchmarkResult result = submitBenchmark.submit(new SubmitBenchmarkUseCase.SubmitBenchmarkCommand(
                evaluationId, evaluationToken, request.questionnaireVersion(), request.answers().stream()
                        .map(answer -> new BenchmarkAnswer(answer.questionId(), answer.value())).toList()));
        return ResponseEntity.ok(toResponse(result));
    }
    private BenchmarkResultResponse toResponse(BenchmarkResult result) {
        return new BenchmarkResultResponse(result.totalScore(), result.maturityLevel(), result.percentile(),
                "Percentil de referencia del MVP; se reemplazará por datos agregados cuando exista una muestra suficiente.",
                result.moduleScores().stream().map(score -> new ModuleScoreResponse(score.module(), score.score())).toList(), result.completedAt());
    }
}
