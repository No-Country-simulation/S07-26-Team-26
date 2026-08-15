package com.ghostload.api.reporting.domain.model;

import com.ghostload.api.assessment.domain.model.MaturityLevel;
import com.ghostload.api.assessment.domain.model.ModuleScore;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

// Value object que reúne TODO lo que necesita la plantilla institucional:
// datos del operador y su empresa, KPIs de la calculadora y el resultado
// del benchmark. Lo arma el adaptador de persistencia (LoadReportDataPort)
// y lo consume el renderer de PDF, sin tocar la base de datos.
public record ReportData(
        UUID evaluationId,
        String questionnaireVersion,
        OperatorInfo operator,
        CalculatorMetrics calculator,
        BenchmarkSummary benchmark
) {

    public record OperatorInfo(String firstName, String lastName, String email,
                               String companyName, String position, String country) {
        public String fullName() {
            return firstName + " " + lastName;
        }
    }

    public record CalculatorMetrics(
            double totalCapacityMw,
            double productiveCapacityMw,
            double nonProductiveCapacityMw,
            double utilizationPercentage,
            double nonProductivePercentage,
            double monthlyCostPerKw,
            double estimatedAnnualCost,
            String currency
    ) {
    }

    public record BenchmarkSummary(
            double totalScore,
            MaturityLevel maturityLevel,
            double percentile,
            List<ModuleScore> moduleScores,
            Instant completedAt
    ) {
    }
}
