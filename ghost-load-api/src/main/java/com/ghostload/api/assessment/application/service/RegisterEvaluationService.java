package com.ghostload.api.assessment.application.service;

import com.ghostload.api.assessment.application.port.in.RegisterEvaluationUseCase;
import com.ghostload.api.assessment.application.port.out.GenerateEvaluationTokenPort;
import com.ghostload.api.assessment.application.port.out.LoadOperatorPort;
import com.ghostload.api.assessment.application.port.out.SaveEvaluationPort;
import com.ghostload.api.assessment.application.port.out.SaveOperatorPort;
import com.ghostload.api.assessment.domain.model.Email;
import com.ghostload.api.assessment.domain.model.Evaluation;
import com.ghostload.api.assessment.domain.model.Operator;
import org.springframework.stereotype.Service;

// @Service: esta SÍ es una anotación de Spring, pero fijate que solo está
// en el servicio de aplicación, no en el dominio (Operator, Evaluation, Email
// no tienen ninguna anotación de Spring). Esto es a propósito: el dominio
// tiene que poder probarse sin levantar Spring para nada.
@Service
public class RegisterEvaluationService implements RegisterEvaluationUseCase {

    private final LoadOperatorPort loadOperatorPort;
    private final SaveOperatorPort saveOperatorPort;
    private final SaveEvaluationPort saveEvaluationPort;
    private final GenerateEvaluationTokenPort generateEvaluationTokenPort;

    // Todas las dependencias son PUERTOS (interfaces), nunca clases concretas
    // de JPA o de un proveedor específico. Spring va a inyectar acá la
    // implementación real (el adaptador) automáticamente.
    public RegisterEvaluationService(LoadOperatorPort loadOperatorPort,
                                      SaveOperatorPort saveOperatorPort,
                                      SaveEvaluationPort saveEvaluationPort,
                                      GenerateEvaluationTokenPort generateEvaluationTokenPort) {
        this.loadOperatorPort = loadOperatorPort;
        this.saveOperatorPort = saveOperatorPort;
        this.saveEvaluationPort = saveEvaluationPort;
        this.generateEvaluationTokenPort = generateEvaluationTokenPort;
    }

    @Override
    public RegisterEvaluationResult register(RegisterEvaluationCommand command) {
        Email email = new Email(command.email()); // acá ya se valida el formato

        // "Registra o reutiliza un operador por correo" -- según el openapi.yaml.
        // Si ya existe un operador con ese email, lo reutilizamos en vez de
        // crear uno duplicado.
        Operator operator = loadOperatorPort.findByEmail(email)
                .orElseGet(() -> Operator.register(
                        command.firstName(),
                        command.lastName(),
                        email,
                        command.companyName(),
                        command.position(),
                        command.country()
                ));

        saveOperatorPort.save(operator);

        Evaluation evaluation = Evaluation.start(operator.id(), command.source());
        saveEvaluationPort.save(evaluation);

        String token = generateEvaluationTokenPort.generate();

        return new RegisterEvaluationResult(
                operator.id().value(),
                evaluation.id().value(),
                token,
                evaluation.state(),
                evaluation.createdAt()
        );
    }
}
