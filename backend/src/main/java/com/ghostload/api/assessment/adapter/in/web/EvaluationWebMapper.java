package com.ghostload.api.assessment.adapter.in.web;

import com.ghostload.api.assessment.application.port.in.RegisterEvaluationUseCase.RegisterEvaluationCommand;
import com.ghostload.api.assessment.application.port.in.RegisterEvaluationUseCase.RegisterEvaluationResult;
import com.ghostload.api.assessment.domain.model.EvaluationSource;
import org.springframework.stereotype.Component;

// Esta clase es la única que sabe traducir entre "lenguaje HTTP" (los DTOs)
// y "lenguaje de negocio" (el Command/Result del caso de uso). Así el
// controller no tiene que saber armar un Command a mano, y el service
// nunca ve un objeto que venga directo de una petición HTTP.
@Component
public class EvaluationWebMapper {

    public RegisterEvaluationCommand toCommand(CreateEvaluationRequest request) {
        return new RegisterEvaluationCommand(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.companyName(),
                request.position(),
                request.country(),
                request.consentAccepted(),
                request.marketingConsent(),
                EvaluationSource.valueOf(request.source()),
                request.invitationToken()
        );
    }

    public CreateEvaluationResponse toResponse(RegisterEvaluationResult result) {
        return new CreateEvaluationResponse(
                result.operatorId(),
                result.evaluationId(),
                result.evaluationToken(),
                result.state().name(),
                result.createdAt()
        );
    }
}
