package com.ghostload.api.assessment.application.service;

import com.ghostload.api.assessment.application.port.in.RegisterEvaluationUseCase;
import com.ghostload.api.assessment.application.port.out.GenerateEvaluationTokenPort;
import com.ghostload.api.assessment.application.port.out.LoadOperatorPort;
import com.ghostload.api.assessment.application.port.out.SaveEvaluationPort;
import com.ghostload.api.assessment.application.port.out.SaveOperatorPort;
import com.ghostload.api.assessment.application.port.out.SendEvaluationInvitationPort;
import com.ghostload.api.assessment.domain.model.Email;
import com.ghostload.api.assessment.domain.model.Evaluation;
import com.ghostload.api.assessment.domain.model.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


// El token se genera ANTES de crear la Evaluation, para que quede guardado
// junto con ella (antes se generaba después y se perdía). Además se envía
// por email al operador; si el envío falla el registro igual se completa
// (el token se devuelve en la respuesta HTTP).
@Service
public class RegisterEvaluationService implements RegisterEvaluationUseCase {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(RegisterEvaluationService.class);

    private final LoadOperatorPort loadOperatorPort;
    private final SaveOperatorPort saveOperatorPort;
    private final SaveEvaluationPort saveEvaluationPort;
    private final GenerateEvaluationTokenPort generateEvaluationTokenPort;
    private final SendEvaluationInvitationPort sendEvaluationInvitationPort;

    public RegisterEvaluationService(LoadOperatorPort loadOperatorPort,
                                      SaveOperatorPort saveOperatorPort,
                                      SaveEvaluationPort saveEvaluationPort,
                                      GenerateEvaluationTokenPort generateEvaluationTokenPort,
                                      SendEvaluationInvitationPort sendEvaluationInvitationPort) {
        this.loadOperatorPort = loadOperatorPort;
        this.saveOperatorPort = saveOperatorPort;
        this.saveEvaluationPort = saveEvaluationPort;
        this.generateEvaluationTokenPort = generateEvaluationTokenPort;
        this.sendEvaluationInvitationPort = sendEvaluationInvitationPort;
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

        sendInvitation(operator, token);

        return new RegisterEvaluationResult(
                operator.id().value(),
                evaluation.id().value(),
                token,
                evaluation.state(),
                evaluation.createdAt()
        );
    }

    private void sendInvitation(Operator operator, String token) {
        var email = new SendEvaluationInvitationPort.InvitationEmail(
                operator.email().value(),
                operator.firstName(),
                "Tu acceso a la evaluación Ghost Load",
                "Recibimos tu registro. Tenés tu token de acceso para completar la evaluación:",
                "Comenzar evaluación",
                token);
        SendEvaluationInvitationPort.EmailSendResult result =
                sendEvaluationInvitationPort.send(email);
        if (!result.sent()) {
            LOGGER.info("El correo de invitación no pudo enviarse a {}. El token viaja en la respuesta HTTP.",
                    operator.email().value());
        }
    }
}
