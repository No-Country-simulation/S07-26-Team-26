package com.ghostload.api.administration.adapter.in.web;

import com.ghostload.api.administration.application.port.in.AuthenticateAdminCommand;
import com.ghostload.api.administration.application.port.in.AuthenticateAdminResult;
import org.springframework.stereotype.Component;

@Component
public class AdminAuthWebMapper {

    AuthenticateAdminCommand toCommand(AdminLoginRequest request) {
        return new AuthenticateAdminCommand(request.email(), request.password());
    }

    AdminLoginResponse toResponse(AuthenticateAdminResult result) {
        return new AdminLoginResponse(
                result.accessToken(),
                "Bearer",
                result.expiresInSeconds(),
                new AdminSummaryResponse(
                        result.adminId(),
                        result.adminName(),
                        result.adminEmail()));
    }
}
