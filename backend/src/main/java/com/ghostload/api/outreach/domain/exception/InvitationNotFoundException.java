package com.ghostload.api.outreach.domain.exception;

public class InvitationNotFoundException extends RuntimeException {

    public InvitationNotFoundException() {
        super("La invitación no existe.");
    }
}
