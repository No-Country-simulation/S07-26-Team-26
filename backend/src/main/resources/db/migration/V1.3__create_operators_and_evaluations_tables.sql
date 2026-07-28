-- V1.3__create_operators_and_evaluations_tables.sql
-- Ajustá el número de versión (V1.3) al que siga después de las migraciones
--  (V1.1 y V1.2 eran de admin users).

CREATE TABLE operators (
    id UUID PRIMARY KEY,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    company_name VARCHAR(160) NOT NULL,
    position VARCHAR(120),
    country VARCHAR(100)
);

CREATE TABLE evaluations (
    id UUID PRIMARY KEY,
    operator_id UUID NOT NULL REFERENCES operators(id),
    state VARCHAR(30) NOT NULL,
    source VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_evaluations_operator_id ON evaluations(operator_id);
