package com.ghostload.api.outreach.adapter.in.web;

import com.ghostload.api.outreach.application.port.in.VerifyEmailConnectionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/email")
public class EmailDiagnosticsController {

    private final VerifyEmailConnectionUseCase verifyEmailConnectionUseCase;

    public EmailDiagnosticsController(
            VerifyEmailConnectionUseCase verifyEmailConnectionUseCase) {
        this.verifyEmailConnectionUseCase = verifyEmailConnectionUseCase;
    }

    @PostMapping("/test-connection")
    public ResponseEntity<EmailConnectionTestResponse> testConnection() {
        var result = verifyEmailConnectionUseCase.verify();
        return ResponseEntity.ok(new EmailConnectionTestResponse(
                result.available(),
                result.code(),
                result.message()));
    }

    public record EmailConnectionTestResponse(
            boolean available,
            String code,
            String message) {
    }
}
