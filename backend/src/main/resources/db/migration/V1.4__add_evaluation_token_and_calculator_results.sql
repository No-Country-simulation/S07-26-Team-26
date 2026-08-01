-- V1.4__add_evaluation_token_and_calculator_results.sql

ALTER TABLE evaluations ADD COLUMN evaluation_token VARCHAR(100);

CREATE TABLE calculator_results (
    evaluation_id UUID PRIMARY KEY REFERENCES evaluations(id),
    total_capacity_mw DOUBLE PRECISION NOT NULL,
    productive_capacity_mw DOUBLE PRECISION NOT NULL,
    non_productive_capacity_mw DOUBLE PRECISION NOT NULL,
    utilization_percentage DOUBLE PRECISION NOT NULL,
    non_productive_percentage DOUBLE PRECISION NOT NULL,
    monthly_cost_per_kw DOUBLE PRECISION NOT NULL,
    estimated_annual_cost DOUBLE PRECISION NOT NULL,
    currency VARCHAR(3) NOT NULL,
    calculated_at TIMESTAMP NOT NULL
);
