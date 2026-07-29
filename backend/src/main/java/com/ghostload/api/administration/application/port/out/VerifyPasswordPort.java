package com.ghostload.api.administration.application.port.out;

public interface VerifyPasswordPort {

    boolean matches(String rawPassword, String encodedPassword);
}
