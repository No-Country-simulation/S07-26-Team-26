CREATE TABLE benchmark_results (
    evaluation_id UUID PRIMARY KEY REFERENCES evaluations(id),
    questionnaire_version VARCHAR(20) NOT NULL,
    total_score DOUBLE PRECISION NOT NULL,
    maturity_level VARCHAR(20) NOT NULL,
    percentile DOUBLE PRECISION NOT NULL,
    completed_at TIMESTAMP NOT NULL
);
