package com.ghostload.api.assessment.domain.model;

public enum MaturityLevel {
    INITIAL,
    DEVELOPING,
    MANAGED,
    ADVANCED,
    OPTIMIZED;

    // El puntaje real solo puede ir de 20 (todas las respuestas en 1) a 100
    // (todas las respuestas en 5), nunca menos de 20. Por eso los cortes
    // dividen ese rango real (20-100) en 5 bandas iguales de 16 puntos cada
    // una, en vez de arrancar en 0 como antes (lo que dejaba INITIAL
    // inalcanzable).
    public static MaturityLevel fromScore(double score) {
        if (score < 36) return INITIAL;
        if (score < 52) return DEVELOPING;
        if (score < 68) return MANAGED;
        if (score < 84) return ADVANCED;
        return OPTIMIZED;
    }
}