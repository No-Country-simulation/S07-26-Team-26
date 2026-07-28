package com.ghostload.api.assessment.adapter.out.persistence;

import com.ghostload.api.assessment.application.port.out.*;
import com.ghostload.api.assessment.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.Optional;

// Se agregaron: LoadEvaluationPort y SaveCalculatorResultPort, más el
// CalculatorResultJpaRepository como nueva dependencia.
@Component
public class AssessmentPersistenceAdapter implements
        LoadOperatorPort, SaveOperatorPort, SaveEvaluationPort,
        LoadEvaluationPort, SaveCalculatorResultPort {

    private final OperatorJpaRepository operatorJpaRepository;
    private final EvaluationJpaRepository evaluationJpaRepository;
    private final CalculatorResultJpaRepository calculatorResultJpaRepository;

    public AssessmentPersistenceAdapter(OperatorJpaRepository operatorJpaRepository,
                                         EvaluationJpaRepository evaluationJpaRepository,
                                         CalculatorResultJpaRepository calculatorResultJpaRepository) {
        this.operatorJpaRepository = operatorJpaRepository;
        this.evaluationJpaRepository = evaluationJpaRepository;
        this.calculatorResultJpaRepository = calculatorResultJpaRepository;
    }

    @Override
    public Optional<Operator> findByEmail(Email email) {
        return operatorJpaRepository.findByEmail(email.value())
                .map(entity -> Operator.reconstruct(
                        OperatorId.of(entity.getId()),
                        entity.getFirstName(),
                        entity.getLastName(),
                        new Email(entity.getEmail()),
                        entity.getCompanyName(),
                        entity.getPosition(),
                        entity.getCountry()
                ));
    }

    @Override
    public void save(Operator operator) {
        var entity = new OperatorJpaEntity(
                operator.id().value(),
                operator.firstName(),
                operator.lastName(),
                operator.email().value(),
                operator.companyName(),
                operator.position(),
                operator.country()
        );
        operatorJpaRepository.save(entity);
    }

    @Override
    public void save(Evaluation evaluation) {
        var entity = new EvaluationJpaEntity(
                evaluation.id().value(),
                evaluation.operatorId().value(),
                evaluation.state().name(),
                evaluation.source().name(),
                evaluation.evaluationToken(),
                evaluation.createdAt(),
                evaluation.updatedAt()
        );
        evaluationJpaRepository.save(entity);
    }

    @Override
    public Optional<Evaluation> findById(EvaluationId id) {
        return evaluationJpaRepository.findById(id.value())
                .map(entity -> Evaluation.reconstruct(
                        EvaluationId.of(entity.getId()),
                        OperatorId.of(entity.getOperatorId()),
                        EvaluationState.valueOf(entity.getState()),
                        EvaluationSource.valueOf(entity.getSource()),
                        entity.getEvaluationToken(),
                        entity.getCreatedAt(),
                        entity.getUpdatedAt()
                ));
    }

    @Override
    public void save(EvaluationId evaluationId, CalculatorResult result) {
        var entity = new CalculatorResultJpaEntity(
                evaluationId.value(),
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
        calculatorResultJpaRepository.save(entity);
    }
}
