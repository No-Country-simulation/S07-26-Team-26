package com.ghostload.api.assessment.domain.model;

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public record BenchmarkResult(double totalScore, MaturityLevel maturityLevel, double percentile,
                              List<ModuleScore> moduleScores, Instant completedAt) {

    public static BenchmarkResult calculate(List<BenchmarkQuestion> questions, List<BenchmarkAnswer> answers) {
        if (questions.size() != 20) {
            throw new IllegalArgumentException("La versión activa del benchmark debe tener exactamente 20 preguntas.");
        }
        Map<java.util.UUID, Integer> answerValues = new java.util.HashMap<>();
        for (BenchmarkAnswer answer : answers) {
            if (answerValues.put(answer.questionId(), answer.value()) != null) {
                throw new IllegalArgumentException("No se puede responder la misma pregunta más de una vez.");
            }
        }
        if (answerValues.size() != 20 || !answerValues.keySet().containsAll(questions.stream().map(BenchmarkQuestion::id).collect(java.util.stream.Collectors.toSet()))) {
            throw new IllegalArgumentException("Debes responder exactamente las 20 preguntas activas del benchmark.");
        }

        Map<BenchmarkModule, Double> sums = new EnumMap<>(BenchmarkModule.class);
        Map<BenchmarkModule, Integer> counts = new EnumMap<>(BenchmarkModule.class);
        for (BenchmarkQuestion question : questions) {
            sums.merge(question.module(), (double) answerValues.get(question.id()), Double::sum);
            counts.merge(question.module(), 1, Integer::sum);
        }
        List<ModuleScore> moduleScores = java.util.Arrays.stream(BenchmarkModule.values())
                .map(module -> {
                    if (counts.getOrDefault(module, 0) != 4) {
                        throw new IllegalArgumentException("Cada módulo debe tener exactamente cuatro preguntas activas.");
                    }
                    // Promedio de respuestas (1 a 5) normalizado a una escala real de 0 a 100:
                    // (promedio - 1) / (5 - 1) * 100. Así 1=0, 3=50, 5=100, alcanzando
                    // el rango completo que piden los niveles de madurez (Crítico 0-39
                    // hasta Líder 90-100).
                    double average = sums.get(module) / counts.get(module);
                    double normalized = (average - 1) / 4 * 100;
                    return new ModuleScore(module, normalized);
                }).toList();
        double total = moduleScores.stream().mapToDouble(ModuleScore::score).average().orElseThrow();
        // MVP percentile: deterministic comparison scale until a production reference dataset exists.
        return new BenchmarkResult(total, MaturityLevel.fromScore(total), total, moduleScores, Instant.now());
    }
}