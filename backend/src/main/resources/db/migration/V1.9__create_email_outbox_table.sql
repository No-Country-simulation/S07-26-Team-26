CREATE TABLE email_outbox (
    id UUID PRIMARY KEY,
    campaign_id UUID NOT NULL REFERENCES campaigns(id),
    invitation_id UUID NOT NULL UNIQUE REFERENCES invitations(id),
    recipient_email VARCHAR(254) NOT NULL,
    recipient_name VARCHAR(161) NOT NULL,
    subject VARCHAR(180) NOT NULL,
    message VARCHAR(5000) NOT NULL,
    call_to_action_text VARCHAR(80) NOT NULL,
    invitation_token UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    attempt_count INTEGER NOT NULL DEFAULT 0 CHECK (attempt_count >= 0),
    available_at TIMESTAMP WITH TIME ZONE NOT NULL,
    claimed_at TIMESTAMP WITH TIME ZONE,
    sent_at TIMESTAMP WITH TIME ZONE,
    failed_at TIMESTAMP WITH TIME ZONE,
    provider_message_id VARCHAR(255),
    last_error VARCHAR(1000),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT chk_email_outbox_status
        CHECK (status IN ('PENDING', 'PROCESSING', 'SENT', 'FAILED'))
);
