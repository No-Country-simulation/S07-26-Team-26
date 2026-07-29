package com.ghostload.api.administration.adapter.out.security;

import com.ghostload.api.administration.application.port.out.VerifyPasswordPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordAdapter implements VerifyPasswordPort {

    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
