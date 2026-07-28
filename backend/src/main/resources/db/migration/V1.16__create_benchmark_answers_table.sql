CREATE TABLE benchmark_answers (
    id UUID PRIMARY KEY,
    evaluation_id UUID NOT NULL REFERENCES evaluations(id),
    question_id UUID NOT NULL REFERENCES benchmark_questions(id),
    value INTEGER NOT NULL CHECK (value BETWEEN 1 AND 5),
    CONSTRAINT uq_benchmark_answer UNIQUE (evaluation_id, question_id)
);
