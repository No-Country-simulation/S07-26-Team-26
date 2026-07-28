CREATE TABLE benchmark_module_scores (
    id UUID PRIMARY KEY,
    evaluation_id UUID NOT NULL REFERENCES evaluations(id),
    module_code VARCHAR(40) NOT NULL,
    score DOUBLE PRECISION NOT NULL,
    CONSTRAINT uq_benchmark_module_score UNIQUE (evaluation_id, module_code)
);
