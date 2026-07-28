CREATE TABLE contact_imports (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_rows INTEGER NOT NULL CHECK (total_rows >= 0),
    valid_contacts INTEGER NOT NULL CHECK (valid_contacts >= 0),
    duplicates INTEGER NOT NULL CHECK (duplicates >= 0),
    invalid_rows INTEGER NOT NULL CHECK (invalid_rows >= 0),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT chk_contact_imports_status
        CHECK (status IN ('PROCESSING', 'COMPLETED', 'FAILED')),
    CONSTRAINT chk_contact_imports_counts
        CHECK (valid_contacts + duplicates + invalid_rows = total_rows)
);
