CREATE TABLE contact_import_contacts (
    contact_import_id UUID NOT NULL REFERENCES contact_imports(id) ON DELETE CASCADE,
    contact_id UUID NOT NULL REFERENCES contacts(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (contact_import_id, contact_id)
);
