package com.ghostload.api.assessment.application.service;

import com.ghostload.api.assessment.application.port.in.RegisterEvaluationUseCase;
import com.ghostload.api.assessment.application.port.out.GenerateEvaluationTokenPort;
import com.ghostload.api.assessment.application.port.out.LoadOperatorPort;
import com.ghostload.api.assessment.application.port.out.SaveEvaluationPort;
import com.ghostload.api.assessment.application.port.out.SaveOperatorPort;
import com.ghostload.api.assessment.domain.model.Email;
import com.ghostload.api.assessment.domain.model.Evaluation;
import com.ghostload.api.assessment.domain.model.EvaluationSource;
import com.ghostload.api.assessment.domain.model.Operator;
import com.ghostload.api.outreach.application.port.in.StartInvitationUseCase;
import com.ghostload.api.outreach.domain.exception.InvalidInvitationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;


// Único cambio: el token se genera ANTES de crear la Evaluation, para que
// quede guardado junto con ella (antes se generaba después y se perdía).
@Service
public class RegisterEvaluationService implements RegisterEvaluationUseCase {

    private final LoadOperatorPort loadOperatorPort;
    private final SaveOperatorPort saveOperatorPort;
    private final SaveEvaluationPort saveEvaluationPort;
    private final GenerateEvaluationTokenPort generateEvaluationTokenPort;
    private final StartInvitationUseCase startInvitationUseCase;
    private final Clock clock;

    public RegisterEvaluationService(LoadOperatorPort loadOperatorPort,
                                      SaveOperatorPort saveOperatorPort,
                                      SaveEvaluationPort saveEvaluationPort,
                                      GenerateEvaluationTokenPort generateEvaluationTokenPort,
                                      StartInvitationUseCase startInvitationUseCase,
                                      Clock clock) {
        this.loadOperatorPort = loadOperatorPort;
        this.saveOperatorPort = saveOperatorPort;
        this.saveEvaluationPort = saveEvaluationPort;
        this.generateEvaluationTokenPort = generateEvaluationTokenPort;
        this.startInvitationUseCase = startInvitationUseCase;
        this.clock = clock;
    }

    @Override
    @Transactional
    public RegisterEvaluationResult register(RegisterEvaluationCommand command) {
        validateInvitationSource(command);
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

        if (command.invitationToken() != null) {
            startInvitationUseCase.start(
                    new StartInvitationUseCase.StartInvitationCommand(
                            command.invitationToken(),
                            email.value(),
                            operator.id().value(),
                            evaluation.id().value(),
                            clock.instant()));
        }

        return new RegisterEvaluationResult(
                operator.id().value(),
                evaluation.id().value(),
                token,
                evaluation.state(),
                evaluation.createdAt()
        );
    }

    private void validateInvitationSource(RegisterEvaluationCommand command) {
        boolean outreachSource = command.source() == EvaluationSource.OUTREACH;
        boolean hasInvitation = command.invitationToken() != null;
        if (outreachSource != hasInvitation) {
            throw new InvalidInvitationException(
                    "Las evaluaciones de outreach requieren una invitación válida.");
        }
    }
}
