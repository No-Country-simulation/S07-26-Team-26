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


// Único cambio: el token se genera ANTES de crear la Evaluation, para que
// quede guardado junto con ella (antes se generaba después y se perdía).
@Service
public class RegisterEvaluationService implements RegisterEvaluationUseCase {

    private final LoadOperatorPort loadOperatorPort;
    private final SaveOperatorPort saveOperatorPort;
    private final SaveEvaluationPort saveEvaluationPort;
    private final GenerateEvaluationTokenPort generateEvaluationTokenPort;

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
        Email email = new Email(command.email());

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

        String token = generateEvaluationTokenPort.generate();
        Evaluation evaluation = Evaluation.start(operator.id(), command.source(), token);
        saveEvaluationPort.save(evaluation);

        return new RegisterEvaluationResult(
                operator.id().value(),
                evaluation.id().value(),
                token,
                evaluation.state(),
                evaluation.createdAt()
        );
    }
}
