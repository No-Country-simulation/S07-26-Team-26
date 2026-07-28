package com.ghostload.api.administration.domain.exception;

public final class InvalidAdminCredentialsException extends RuntimeException {

    public InvalidAdminCredentialsException() {
        super("El correo o la contraseña son incorrectos.");
    }
}
