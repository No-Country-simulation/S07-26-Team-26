-- Estado de generación del PDF institucional por evaluación.
-- Un solo registro por evaluación (UNIQUE). La generación es asíncrona:
-- el worker reclama registros PROCESSING, reintenta con backoff y deja
-- constancia del último error cuando se agotan los intentos.
CREATE TABLE generated_pdfs (
    id UUID PRIMARY KEY,
    evaluation_id UUID NOT NULL UNIQUE REFERENCES evaluations(id),
    status VARCHAR(20) NOT NULL,
    storage_key VARCHAR(500),
    download_url VARCHAR(1000),
    file_name VARCHAR(255),
    attempt_count INTEGER NOT NULL DEFAULT 0 CHECK (attempt_count >= 0),
    last_error VARCHAR(1000),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    available_at TIMESTAMP WITH TIME ZONE NOT NULL,
    claimed_at TIMESTAMP WITH TIME ZONE,
    generated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT chk_generated_pdfs_status
        CHECK (status IN ('PROCESSING', 'GENERATED', 'FAILED'))
);

CREATE INDEX idx_generated_pdfs_claim
    ON generated_pdfs (status, available_at, created_at);
