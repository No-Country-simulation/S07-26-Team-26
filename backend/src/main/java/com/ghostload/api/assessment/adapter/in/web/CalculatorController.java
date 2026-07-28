package com.ghostload.api.assessment.adapter.in.web;

import com.ghostload.api.assessment.application.port.in.SaveCalculatorResultUseCase;
import com.ghostload.api.assessment.application.port.in.SaveCalculatorResultUseCase.SaveCalculatorResultCommand;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evaluations/{evaluationId}/calculator")
public class CalculatorController {

    private final SaveCalculatorResultUseCase saveCalculatorResultUseCase;

    public CalculatorController(SaveCalculatorResultUseCase saveCalculatorResultUseCase) {
        this.saveCalculatorResultUseCase = saveCalculatorResultUseCase;
    }

    @PutMapping
    public ResponseEntity<CalculatorResultResponse> saveCalculatorResult(
            @PathVariable UUID evaluationId,
            // El header X-Evaluation-Token es el que define el contrato para
            // autorizar el acceso a esta evaluación puntual.
            @RequestHeader("X-Evaluation-Token") String evaluationToken,
            @Valid @RequestBody CalculatorRequest request) {

        var command = new SaveCalculatorResultCommand(
                evaluationId,
                evaluationToken,
                request.totalCapacityMw(),
                request.productiveCapacityMw(),
                request.monthlyCostPerKw(),
                request.currency()
        );

        var result = saveCalculatorResultUseCase.save(command);

        var response = new CalculatorResultResponse(
                result.totalCapacityMw(),
                result.productiveCapacityMw(),
                result.nonProductiveCapacityMw(),
                result.utilizationPercentage(),
                result.nonProductivePercentage(),
                result.monthlyCostPerKw(),
                result.estimatedAnnualCost(),
                result.currency(),
                result.calculatedAt()
        );

        return ResponseEntity.ok(response);
    }
}
