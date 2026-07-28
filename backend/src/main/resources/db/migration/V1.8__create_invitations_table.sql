CREATE TABLE invitations (
    id UUID PRIMARY KEY,
    campaign_id UUID NOT NULL REFERENCES campaigns(id),
    contact_id UUID NOT NULL REFERENCES contacts(id),
    token UUID NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE,
    sent_at TIMESTAMP WITH TIME ZONE,
    visited_at TIMESTAMP WITH TIME ZONE,
    started_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    failed_at TIMESTAMP WITH TIME ZONE,
    failure_reason VARCHAR(1000),
    operator_id UUID REFERENCES operators(id),
    evaluation_id UUID REFERENCES evaluations(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uq_invitations_campaign_contact UNIQUE (campaign_id, contact_id),
    CONSTRAINT chk_invitations_status
        CHECK (status IN ('UPLOADED', 'SENT', 'VISITED', 'STARTED', 'COMPLETED', 'FAILED'))
);
