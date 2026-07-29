package com.ghostload.api.assessment.application.port.out;

// El openapi.yaml dice: "evaluationToken: min 32 caracteres, solo se devuelve
// al crear la evaluación". Ese token es lo que el operador va a usar después
// como header X-Evaluation-Token para seguir su evaluación. Cómo se genera
// exactamente (random seguro, JWT, etc.) es un detalle de infraestructura,
// por eso es un puerto de salida y no algo que decida el dominio.
public interface GenerateEvaluationTokenPort {

    String generate();
}
