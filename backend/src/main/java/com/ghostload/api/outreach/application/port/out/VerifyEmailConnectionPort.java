package com.ghostload.api.outreach.application.port.out;

public interface VerifyEmailConnectionPort {

    EmailConnectionDiagnostic verifyConnection();

    record EmailConnectionDiagnostic(
            boolean available,
            String code,
            String message) {
    }
}
