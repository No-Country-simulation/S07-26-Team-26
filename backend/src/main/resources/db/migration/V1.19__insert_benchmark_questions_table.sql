-- V1.19__insert_benchmark_questions_table.sql
-- Preguntas reales del "Benchmark de Eficiencia para Data Centers de IA"
-- (documento compartido ), 20 preguntas / 5 categorias.
INSERT INTO benchmark_questions (id, version, module_code, question_order, text, active) VALUES
('10000000-0000-0000-0000-000000000001', 'v1', 'ENERGY', 1, '¿Miden el consumo eléctrico por rack o clúster?', TRUE),
('10000000-0000-0000-0000-000000000002', 'v1', 'ENERGY', 2, '¿Qué porcentaje de la capacidad eléctrica instalada utilizan?', TRUE),
('10000000-0000-0000-0000-000000000003', 'v1', 'ENERGY', 3, '¿Conocen el consumo energético por entrenamiento o inferencia?', TRUE),
('10000000-0000-0000-0000-000000000004', 'v1', 'ENERGY', 4, '¿Tienen alertas por consumo anómalo?', TRUE),
('10000000-0000-0000-0000-000000000005', 'v1', 'GPU_UTILIZATION', 5, '¿Cuál es la utilización promedio de las GPUs?', TRUE),
('10000000-0000-0000-0000-000000000006', 'v1', 'GPU_UTILIZATION', 6, '¿Las GPUs suelen esperar datos antes de procesar?', TRUE),
('10000000-0000-0000-0000-000000000007', 'v1', 'GPU_UTILIZATION', 7, '¿Se reservan GPUs que luego permanecen inactivas?', TRUE),
('10000000-0000-0000-0000-000000000008', 'v1', 'GPU_UTILIZATION', 8, '¿Monitorean la utilización de GPUs en tiempo real?', TRUE),
('10000000-0000-0000-0000-000000000009', 'v1', 'COOLING', 9, '¿Monitorean la temperatura por rack?', TRUE),
('10000000-0000-0000-0000-000000000010', 'v1', 'COOLING', 10, '¿La refrigeración limita la expansión del clúster?', TRUE),
('10000000-0000-0000-0000-000000000011', 'v1', 'COOLING', 11, '¿Existen zonas con sobrecalentamiento frecuente?', TRUE),
('10000000-0000-0000-0000-000000000012', 'v1', 'COOLING', 12, '¿Utilizan refrigeración líquida o solo aire?', TRUE),
('10000000-0000-0000-0000-000000000013', 'v1', 'OPERATIONS', 13, '¿Facilities y Operaciones comparten métricas?', TRUE),
('10000000-0000-0000-0000-000000000014', 'v1', 'OPERATIONS', 14, '¿La asignación de recursos está automatizada?', TRUE),
('10000000-0000-0000-0000-000000000015', 'v1', 'OPERATIONS', 15, '¿Revisan periódicamente la eficiencia operativa?', TRUE),
('10000000-0000-0000-0000-000000000016', 'v1', 'OPERATIONS', 16, '¿Cuentan con dashboards unificados?', TRUE),
('10000000-0000-0000-0000-000000000017', 'v1', 'CAPACITY', 17, '¿Qué porcentaje de la infraestructura está realmente disponible para cargas de IA?', TRUE),
('10000000-0000-0000-0000-000000000018', 'v1', 'CAPACITY', 18, '¿Con qué frecuencia rechazan trabajos por falta de capacidad?', TRUE),
('10000000-0000-0000-0000-000000000019', 'v1', 'CAPACITY', 19, '¿Cuánto tarda incorporar nueva infraestructura?', TRUE),
('10000000-0000-0000-0000-000000000020', 'v1', 'CAPACITY', 20, '¿Realizan planificación y simulaciones de crecimiento?', TRUE);
