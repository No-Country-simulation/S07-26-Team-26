package com.ghostload.api.administration.adapter.in.web;

import com.ghostload.api.administration.application.port.in.AuthenticateAdminResult;
import com.ghostload.api.administration.application.port.in.AuthenticateAdminUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/auth")
public class AdminAuthController {

    private final AuthenticateAdminUseCase authenticateAdminUseCase;
    private final AdminAuthWebMapper mapper;

    public AdminAuthController(
            AuthenticateAdminUseCase authenticateAdminUseCase,
            AdminAuthWebMapper mapper) {
        this.authenticateAdminUseCase = authenticateAdminUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(
            @Valid @RequestBody AdminLoginRequest request) {
        AuthenticateAdminResult result =
                authenticateAdminUseCase.authenticate(mapper.toCommand(request));
        return ResponseEntity.ok(mapper.toResponse(result));
    }
}
