CREATE TABLE crm_pipeline_entries (
    id UUID PRIMARY KEY,
    company_name VARCHAR(160) NOT NULL,
    contact_name VARCHAR(160),
    email VARCHAR(254),
    region VARCHAR(120),
    benchmark_score DOUBLE PRECISION,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT chk_crm_pipeline_status
        CHECK (status IN ('OUTREACH_PENDING', 'OUTREACH_SENT', 'MEETING_SCHEDULED', 'CONVERTED', 'LOST'))
);

CREATE TABLE crm_pipeline_notes (
    id UUID PRIMARY KEY,
    entry_id UUID NOT NULL REFERENCES crm_pipeline_entries(id),
    note VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE crm_pipeline_status_changes (
    id UUID PRIMARY KEY,
    entry_id UUID NOT NULL REFERENCES crm_pipeline_entries(id),
    from_status VARCHAR(30) NOT NULL,
    to_status VARCHAR(30) NOT NULL,
    changed_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_crm_pipeline_notes_entry ON crm_pipeline_notes(entry_id);
CREATE INDEX idx_crm_pipeline_status_entry ON crm_pipeline_status_changes(entry_id);