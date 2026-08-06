# Tasks — Assessment

## Status Legend
- [x] Completado
- [-] En progreso
- [ ] Pendiente
- [future] Fuera de MVP (visión futura)

---

## Backend

### Operator & Evaluation
- [x] Crear entidad `Operator` con JPA
- [x] Crear tabla `operators` (V1.1)
- [x] Crear entidad `Evaluation` con JPA (V1.3)
- [x] Crear tabla `evaluations` (V1.3)
- [x] Agregar columna `evaluation_token` a `evaluations` (V1.4)
  - Nota: no existe tabla `evaluation_tokens` separada; el token es columna de `evaluations`
- [x] Implementar `RegisterEvaluationService` — crea operador + evaluación + token
- [x] Implementar `GenerateEvaluationTokenPort` + `SecureRandomEvaluationTokenAdapter`
- [x] Implementar evaluación con token único retornado en respuesta HTTP
- [x] Implementar envío de token por email al operador
  - Puerto `SendEvaluationInvitationPort` + adaptador SMTP `EvaluationInvitationEmailAdapter`
  - Config `assessment.email.*` (from-address, from-name, invitation-base-url)
  - Tolerante a fallos: si SMTP no está configurado o falla, el registro se completa igual y el token se devuelve en HTTP

### Autenticación del operador (X-Evaluation-Token)
- [x] Validar el token del header `X-Evaluation-Token` en los servicios
  - `SubmitBenchmarkService` y `SaveCalculatorResultService` comparan contra `evaluation.evaluationToken()`
  - Nota: no hay filtro Spring Security dedicado; la validación ocurre en el caso de uso

### Calculator
- [x] Crear entidad `CalculatorResult` + tabla (V1.4)
- [x] Implementar `CalculatorController` — PUT /api/v1/evaluations/{id}/calculator
- [x] Implementar `SaveCalculatorResultService` con fórmulas del dominio
- [x] Implementar validaciones: total > 0, productiva ≤ total
- [x] Manejar transición de estado a CALCULATOR_COMPLETED

### Benchmark
- [x] Crear tabla `benchmark_questions` (V1.13)
- [x] Insertar preguntas semilla (V1.14) — 20 preguntas en 5 módulos
- [x] Crear tabla `benchmark_results` (V1.15)
- [x] Crear tabla `benchmark_answers` (V1.16)
- [x] Crear tabla `benchmark_module_scores` (V1.17)
- [x] Implementar `BenchmarkController` — GET /api/v1/benchmark/questions
- [x] Implementar `SubmitBenchmarkService` — validación, cálculo, persistencia
- [x] Implementar cálculo de score/100 y score por módulo
- [x] Implementar asignación de MaturityLevel
- [x] Manejar transición de estado a BENCHMARK_COMPLETED

### Por implementar
- [ ] Implementar guardado parcial de progreso (actualmente es submit completo)
- [x] Implementar rechazo de acceso a evaluación de otra empresa

### Tests Backend
- [x] Test: registro crea operador + evaluación con token (`RegisterEvaluationServiceTest`)
- [x] Test: reutiliza operador existente por email (`RegisterEvaluationServiceTest`)
- [x] Test: fórmulas de calculadora con valores válidos
- [x] Test: validaciones de calculadora (total ≤ 0, productiva > total)
- [x] Test: cálculo de score/100
- [x] Test: boundary values de MaturityLevel
- [x] Test: submit bloqueado si faltan preguntas
- [x] Test: submit idempotente no duplica resultados
- [x] Test: percentil con disclaimer cuando no hay población
  - Nota: el percentile MVP es determinístico (percentile = total score) hasta tener dataset de referencia; cubierto en `BenchmarkResultTest`.

## Frontend
- [ ] Crear pantalla de registro/evaluación del operador
- [ ] Crear formulario de calculadora de capacidad
- [ ] Crear cuestionario de benchmark con secciones y progreso
- [ ] Crear pantalla de resultados (score, madurez, KPIs)
