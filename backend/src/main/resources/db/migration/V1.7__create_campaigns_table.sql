CREATE TABLE campaigns (
    id UUID PRIMARY KEY,
    contact_import_id UUID NOT NULL REFERENCES contact_imports(id),
    name VARCHAR(160) NOT NULL,
    description VARCHAR(500),
    subject VARCHAR(180) NOT NULL,
    message VARCHAR(5000) NOT NULL,
    call_to_action_text VARCHAR(80) NOT NULL,
    status VARCHAR(20) NOT NULL,
    recipient_count INTEGER NOT NULL CHECK (recipient_count > 0),
    scheduled_at TIMESTAMP WITH TIME ZONE,
    timezone VARCHAR(255),
    sent_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT chk_campaigns_status
        CHECK (status IN ('DRAFT', 'READY', 'SENDING', 'ACTIVE', 'COMPLETED', 'FAILED'))
);
