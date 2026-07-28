package com.ghostload.api.outreach.application.port.in;

public interface VerifyEmailConnectionUseCase {

    EmailConnectionTestResult verify();

    record EmailConnectionTestResult(
            boolean available,
            String code,
            String message) {
    }
}
