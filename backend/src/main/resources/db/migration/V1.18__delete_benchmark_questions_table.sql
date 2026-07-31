-- V1.18__delete_benchmark_questions_table.sql
-- Borra las preguntas placeholder de la version v1, para reemplazarlas por
-- las preguntas reales del benchmark que compartio Karina (documento oficial).
DELETE FROM benchmark_questions WHERE version = 'v1';
