package com.ghostload.api.administration.application.port.in;

public interface AuthenticateAdminUseCase {

    AuthenticateAdminResult authenticate(AuthenticateAdminCommand command);
}
