package com.ghostload.api.assessment.application.service;

import com.ghostload.api.assessment.application.port.in.SaveCalculatorResultUseCase;
import com.ghostload.api.assessment.application.port.out.LoadEvaluationPort;
import com.ghostload.api.assessment.application.port.out.SaveCalculatorResultPort;
import com.ghostload.api.assessment.application.port.out.SaveEvaluationPort;
import com.ghostload.api.assessment.domain.exception.InvalidEvaluationTokenException;
import com.ghostload.api.assessment.domain.model.CalculatorResult;
import com.ghostload.api.assessment.domain.model.Evaluation;
import com.ghostload.api.assessment.domain.model.EvaluationId;
import org.springframework.stereotype.Service;

@Service
public class SaveCalculatorResultService implements SaveCalculatorResultUseCase {

    private final LoadEvaluationPort loadEvaluationPort;
    private final SaveEvaluationPort saveEvaluationPort;
    private final SaveCalculatorResultPort saveCalculatorResultPort;

    public SaveCalculatorResultService(LoadEvaluationPort loadEvaluationPort,
                                        SaveEvaluationPort saveEvaluationPort,
                                        SaveCalculatorResultPort saveCalculatorResultPort) {
        this.loadEvaluationPort = loadEvaluationPort;
        this.saveEvaluationPort = saveEvaluationPort;
        this.saveCalculatorResultPort = saveCalculatorResultPort;
    }

    @Override
    public SaveCalculatorResultResult save(SaveCalculatorResultCommand command) {
        EvaluationId evaluationId = EvaluationId.of(command.evaluationId());

        Evaluation evaluation = loadEvaluationPort.findById(evaluationId)
                .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));

        // Validamos que el token del header coincida con el que se generó
        // al crear la evaluación -- así nos aseguramos de que solo el
        // operador dueño de esta evaluación puede completarla.
        if (!evaluation.evaluationToken().equals(command.evaluationToken())) {
            throw new InvalidEvaluationTokenException("El token de evaluación no es válido");
        }

        // Acá se aplican las 4 fórmulas oficiales (ver CalculatorResult.compute)
        CalculatorResult result = CalculatorResult.compute(
                command.totalCapacityMw(),
                command.productiveCapacityMw(),
                command.monthlyCostPerKw(),
                command.currency()
        );

        // Esto lanza InvalidEvaluationStateException si la evaluación ya
        // no está en STARTED (por ejemplo, si ya se completó antes).
        evaluation.markCalculatorCompleted();

        saveCalculatorResultPort.save(evaluationId, result);
        saveEvaluationPort.save(evaluation);

        return new SaveCalculatorResultResult(
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
    }
}
