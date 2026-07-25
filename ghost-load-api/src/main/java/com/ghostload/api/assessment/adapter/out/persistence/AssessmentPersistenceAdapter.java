package com.ghostload.api.assessment.adapter.out.persistence;

import com.ghostload.api.assessment.application.port.out.LoadOperatorPort;
import com.ghostload.api.assessment.application.port.out.SaveEvaluationPort;
import com.ghostload.api.assessment.application.port.out.SaveOperatorPort;
import com.ghostload.api.assessment.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.Optional;

// Este adaptador implementa TRES puertos de salida a la vez, porque los tres
// hablan de la misma tecnología (JPA/PostgreSQL). Acá es donde se traduce
// entre el mundo del dominio (Operator, Evaluation) y el mundo de JPA
// (OperatorJpaEntity, EvaluationJpaEntity). Esta traducción se llama "mapeo".
@Component
public class AssessmentPersistenceAdapter implements LoadOperatorPort, SaveOperatorPort, SaveEvaluationPort {

    private final OperatorJpaRepository operatorJpaRepository;
    private final EvaluationJpaRepository evaluationJpaRepository;

    public AssessmentPersistenceAdapter(OperatorJpaRepository operatorJpaRepository,
                                         EvaluationJpaRepository evaluationJpaRepository) {
        this.operatorJpaRepository = operatorJpaRepository;
        this.evaluationJpaRepository = evaluationJpaRepository;
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
                evaluation.createdAt(),
                evaluation.updatedAt()
        );
        evaluationJpaRepository.save(entity);
    }
}
