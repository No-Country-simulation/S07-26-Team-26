CREATE TABLE contacts (
    id UUID PRIMARY KEY,
    contact_import_id UUID NOT NULL REFERENCES contact_imports(id),
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    company_name VARCHAR(160) NOT NULL,
    position VARCHAR(120),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
