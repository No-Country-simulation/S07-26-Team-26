package com.ghostload.api.assessment.adapter.in.web;

import com.ghostload.api.assessment.application.port.in.RegisterEvaluationUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// El controller es un "Driving Adapter": recibe HTTP y llama al puerto de
// entrada. Fijate que NO llama a ningún Repository directo -- eso sería
// el anti-patrón que marca el documento de Ander (sección 37).
@RestController
@RequestMapping("/api/v1/evaluations")
public class EvaluationController {

    private final RegisterEvaluationUseCase registerEvaluationUseCase;
    private final EvaluationWebMapper mapper;

    public EvaluationController(RegisterEvaluationUseCase registerEvaluationUseCase,
                                 EvaluationWebMapper mapper) {
        this.registerEvaluationUseCase = registerEvaluationUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<CreateEvaluationResponse> createEvaluation(
            @Valid @RequestBody CreateEvaluationRequest request) {

        var command = mapper.toCommand(request);
        var result = registerEvaluationUseCase.register(command);
        var response = mapper.toResponse(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
