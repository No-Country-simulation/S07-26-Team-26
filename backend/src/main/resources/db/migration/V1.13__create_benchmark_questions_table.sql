CREATE TABLE benchmark_questions (
    id UUID PRIMARY KEY,
    version VARCHAR(20) NOT NULL,
    module_code VARCHAR(40) NOT NULL,
    question_order INTEGER NOT NULL,
    text VARCHAR(500) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_benchmark_question_version_order UNIQUE (version, question_order)
);

CREATE INDEX idx_benchmark_questions_version_active
    ON benchmark_questions(version, active);
