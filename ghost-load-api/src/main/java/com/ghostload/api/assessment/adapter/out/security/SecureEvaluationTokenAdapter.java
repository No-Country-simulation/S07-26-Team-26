package com.ghostload.api.assessment.adapter.out.security;

import com.ghostload.api.assessment.application.port.out.GenerateEvaluationTokenPort;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

// Implementación simple y segura: un token random de 48 bytes, codificado
// en Base64 (queda un string bien largo, más que los 32 caracteres mínimos
// que pide el openapi.yaml). No es un JWT -- no necesita serlo, es solo un
// identificador opaco que se guarda y se compara tal cual.
@Component
public class SecureEvaluationTokenAdapter implements GenerateEvaluationTokenPort {

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generate() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
