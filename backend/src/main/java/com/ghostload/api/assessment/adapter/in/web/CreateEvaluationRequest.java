package com.ghostload.api.assessment.adapter.in.web;

import jakarta.validation.constraints.*;
import java.util.UUID;

// Este DTO vive en el adaptador web, NO en el dominio. Sus anotaciones
// (@NotBlank, @Email) son de validación HTTP, algo que el dominio no debe
// conocer. Los campos son exactamente los de CreateEvaluationRequest
// en el openapi.yaml.
public record CreateEvaluationRequest(

        @NotBlank @Size(min = 1, max = 80)
        String firstName,

        @NotBlank @Size(min = 1, max = 80)
        String lastName,

        @NotBlank @Email @Size(max = 254)
        String email,

        @NotBlank @Size(min = 2, max = 160)
        String companyName,

        @Size(max = 120)
        String position,

        @Size(max = 100)
        String country,

        @AssertTrue(message = "Debe aceptar el consentimiento")
        boolean consentAccepted,

        boolean marketingConsent,

        @NotBlank
        String source, // "CALCULATOR" | "BENCHMARK" | "OUTREACH"

        UUID invitationToken // opcional
) {}
