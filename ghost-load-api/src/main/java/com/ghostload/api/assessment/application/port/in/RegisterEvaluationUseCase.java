package com.ghostload.api.assessment.application.port.in;

import com.ghostload.api.assessment.domain.model.EvaluationSource;
import com.ghostload.api.assessment.domain.model.EvaluationState;

import java.time.Instant;
import java.util.UUID;

// Este es el "puerto de entrada": la capacidad que Ghost Load ofrece al mundo
// exterior. Habla en términos de negocio (Command, Result), no de HTTP.
// Podría ser llamado desde un controller, desde un test, o desde otro adaptador,
// y no cambiaría nada de esta interfaz.
public interface RegisterEvaluationUseCase {

    RegisterEvaluationResult register(RegisterEvaluationCommand command);

    // "Command": lo que necesita el caso de uso para ejecutarse.
    // Son exactamente los mismos campos que pide el CreateEvaluationRequest
    // del openapi.yaml, pero en un objeto de negocio, no un DTO de HTTP.
    record RegisterEvaluationCommand(
            String firstName,
            String lastName,
            String email,
            String companyName,
            String position,
            String country,
            boolean consentAccepted,
            boolean marketingConsent,
            EvaluationSource source,
            UUID invitationToken // puede ser null
    ) {}

    // "Result": lo que devuelve el caso de uso. Coincide con CreateEvaluationResponse.
    record RegisterEvaluationResult(
            UUID operatorId,
            UUID evaluationId,
            String evaluationToken,
            EvaluationState state,
            Instant createdAt
    ) {}
}
