package com.ghostload.api.outreach.adapter.out.security;

import com.ghostload.api.outreach.application.port.out.GenerateInvitationTokenPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidInvitationTokenAdapter implements GenerateInvitationTokenPort {

    @Override
    public UUID generate() {
        return UUID.randomUUID();
    }
}
