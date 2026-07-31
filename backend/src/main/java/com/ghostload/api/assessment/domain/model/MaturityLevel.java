package com.ghostload.api.assessment.domain.model;

// Niveles de madurez según el documento oficial , sobre un puntaje
// de 0 a 100 (ver la fórmula en BenchmarkResult.calculate, que ahora
// normaliza el promedio de respuestas 1-5 a una escala real de 0-100).
public enum MaturityLevel {
    CRITICAL,           // Crítico: 0-39
    OPERATIONAL_RISK,   // Riesgo operativo: 40-59
    GROWING,            // En crecimiento: 60-74
    MATURE,             // Maduro: 75-89
    LEADER;             // Líder: 90-100

    public static MaturityLevel fromScore(double score) {
        if (score < 40) return CRITICAL;
        if (score < 60) return OPERATIONAL_RISK;
        if (score < 75) return GROWING;
        if (score < 90) return MATURE;
        return LEADER;
    }
}
