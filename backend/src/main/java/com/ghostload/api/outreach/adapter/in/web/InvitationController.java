package com.ghostload.api.outreach.adapter.in.web;

import com.ghostload.api.outreach.application.port.in.ResolveInvitationUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invitations")
public class InvitationController {

    private final ResolveInvitationUseCase resolveInvitationUseCase;

    public InvitationController(ResolveInvitationUseCase resolveInvitationUseCase) {
        this.resolveInvitationUseCase = resolveInvitationUseCase;
    }

    @GetMapping("/{invitationToken}")
    public ResponseEntity<InvitationResolutionResponse> resolve(
            @PathVariable UUID invitationToken) {
        var result = resolveInvitationUseCase.resolve(invitationToken);
        return ResponseEntity.ok(new InvitationResolutionResponse(
                result.valid(),
                result.status(),
                result.email(),
                result.firstName(),
                result.lastName(),
                result.companyName(),
                result.position(),
                result.campaignName(),
                result.expiresAt()));
    }
}
