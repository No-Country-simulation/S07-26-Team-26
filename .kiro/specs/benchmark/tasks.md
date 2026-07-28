# Tasks — Benchmark

## Status Legend
- [ ] Pendiente
- [x] Completado
- [-] En progreso

---

## Backend

- [ ] Crear entidad `BenchmarkResult` con JPA (REQ-2, REQ-3, REQ-4)
- [ ] Crear entidad `Question` y `Answer` (REQ-2.1, REQ-2.2)
- [ ] Crear tablas `benchmark_results` y `benchmark_responses` (REQ-2, REQ-3)
- [ ] Implementar `BenchmarkRepository` (JPA Adapter) (REQ-2, REQ-4)
- [ ] Implementar `BenchmarkUseCase` — getQuestions, saveResponses, submit, getResults (REQ-1, REQ-2, REQ-4)
- [ ] Implementar guardado parcial idempotente — respuesta repetida actualiza sin duplicar (REQ-2.4)
- [ ] Implementar rechazo de acceso si benchmark ya está COMPLETED (REQ-1.3)
- [ ] Implementar rechazo de acceso al benchmark de otra empresa (REQ-1.4)
- [ ] Implementar `CalculatorUseCase` — KPIs, score, percentil, nivel de madurez (REQ-3, REQ-4)
- [ ] Implementar lógica de asignación de MaturityLevel (REQ-4.4)
- [ ] Implementar percentil — no calcular si no existe población válida (REQ-4.3)
- [ ] Implementar cambio de estado: IN_PROGRESS al guardar primera respuesta (REQ-2.7)
- [ ] Implementar cambio de estado: COMPLETED al hacer submit (REQ-5.2)
- [ ] Implementar submit idempotente — repetir no duplica resultados (REQ-5.5)
- [ ] Implementar notificación al Admin al completar benchmark (SES) — fallo no corrompe resultado (REQ-6.4)
- [ ] Implementar endpoint GET /benchmark/analysis para Admin (REQ-7.1, REQ-7.2)
- [ ] Implementar endpoint GET /benchmark/percentiles para Admin (REQ-7.5)
- [ ] Implementar `RedisAdapter` — cache de KPIs calculados (REQ-3, opcional MVP)

### Tests Backend (REQ coverage)
- [ ] Test: Operator solo accede a benchmark de su empresa (REQ-1.4)
- [ ] Test: acceso rechazado si benchmark COMPLETED (REQ-1.3)
- [ ] Test: guardar respuesta duplicada actualiza sin crear duplicado (REQ-2.4)
- [ ] Test: submit bloqueado si hay preguntas requeridas sin responder (REQ-2.6)
- [ ] Test: porcentaje de completado calculado correctamente tras cada save (REQ-2.5)
- [ ] Test: MaturityLevel boundary values — score 0, 24, 25, 49, 50, 74, 75, 100 (REQ-4.5)
- [ ] Test: score siempre entre 0 y 100 (REQ-4.1)
- [ ] Test: percentil devuelve representación explícita cuando no hay población (REQ-4.3)
- [ ] Test: mismas respuestas + misma versión de reglas = mismo resultado (REQ-4.6)
- [ ] Test: submit idempotente — doble submit no duplica resultados (REQ-5.5)
- [ ] Test: estado empresa cambia a IN_PROGRESS en primera respuesta (REQ-2.7)
- [ ] Test: estado empresa cambia a COMPLETED al finalizar (REQ-5.3)
- [ ] Test: fallo de notificación SES no modifica el resultado calculado (REQ-6.4)
- [ ] Test: capacidad total > 0, capacidad usada ≤ total (REQ-3.2, 3.3)
- [ ] Test: histograma de scores cubre todos los rangos sin solapamiento (REQ-7.1)

## Frontend

- [ ] Crear pantalla del cuestionario con secciones (REQ-2.1)
- [ ] Implementar barra de progreso de completado (REQ-2.5)
- [ ] Implementar guardado automático al avanzar entre preguntas (REQ-2.3)
- [ ] Implementar recuperación de progreso al reabrir el cuestionario (REQ-1.5)
- [ ] Crear pantalla de resultados del Operator (score, percentil, nivel de madurez, KPIs) (REQ-4.1, REQ-4.2, REQ-4.4)
- [ ] Implementar badge de nivel de madurez con colores (verde/azul/amarillo/rojo) (REQ-4.4)
- [ ] Mostrar representación explícita cuando percentil no está disponible (REQ-4.3)
- [ ] Crear pantalla de Análisis para Admin (histograma, distribución de madurez) (REQ-7.1, REQ-7.2)
- [ ] Crear pantalla de Percentiles para Admin (REQ-7.5)

## DevOps

- [ ] Configurar Redis (Amazon ElastiCache) para cache de cálculos (REQ-3)
- [ ] Configurar template de email de notificación al Admin (REQ-6.3)
