package com.ghostload.api.administration.application.port.in;

public record AuthenticateAdminCommand(String email, String password) {
}
