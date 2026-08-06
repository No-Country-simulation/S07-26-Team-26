# Design — Assessment

## Architecture
Bounded Context `assessment` dentro de la arquitectura DDD + Hexagonal (`docs/10_Hexagonal_Architecture_DDD.md`).

## Domain Model

### Entities
- `Evaluation` — Aggregate Root
  - state machine: STARTED → CALCULATOR_COMPLETED → BENCHMARK_COMPLETED → REPORT_GENERATING → REPORT_COMPLETED / REPORT_FAILED
  - contiene CalculatorResult y BenchmarkResult
- `Operator` — persona que completa la evaluación
  - email, companyName, position
- `BenchmarkQuestion` — pregunta del cuestionario
- `BenchmarkAnswer` — respuesta del operador

### Value Objects
- `EvaluationId`, `OperatorId`, `Email`
- `EvaluationToken` — token único de acceso (X-Evaluation-Token)
- `CapacityInput`, `CalculatorResult` — capacidad total, productiva, costo, fórmulas
- `BenchmarkResult` — score/100, percentile, maturityLevel, moduleScores
- `MaturityLevel` — INITIAL | DEVELOPING | MANAGED | ADVANCED | OPTIMIZED
- `Capacity`, `Money`, `Percentage`

## Ports

### In (Use Cases)
- `RegisterEvaluationUseCase` — crea operador + evaluación + token
- `CalculateCapacityUseCase` — recibe inputs de capacidad, calcula KPIs
- `SubmitBenchmarkUseCase` — valida respuestas, calcula score y madurez
- `ListBenchmarkQuestionsQuery` — devuelve el cuestionario

### Out (Infrastructure)
- `LoadEvaluationPort` / `SaveEvaluationPort`
- `LoadOperatorPort` / `SaveOperatorPort`
- `GenerateEvaluationTokenPort`
- `SendEvaluationInvitationPort` — envía el token de acceso por email (tolerante a fallos)
- _No hay `PublishDomainEventPort` implementado (eventos de dominio fuera del MVP)._

Nota de implementación: la validación del token se realiza dentro de los servicios
(`SubmitBenchmarkService`, `SaveCalculatorResultService`) comparando contra
`evaluation.evaluationToken()`, no mediante un filtro Spring Security.

## Adapters

### In
- `EvaluationController` — POST /api/v1/evaluations
- `CalculatorController` — PUT /api/v1/evaluations/{id}/calculator
- `BenchmarkController` — GET /api/v1/benchmark/questions, PUT /api/v1/evaluations/{id}/benchmark

### Out
- `EvaluationPersistenceAdapter` — PostgreSQL
- `SecureRandomEvaluationTokenAdapter` — generación de tokens
- `EvaluationInvitationEmailAdapter` — SMTP para el correo de invitación (tolerante a fallos)

## API Endpoints
- POST /api/v1/evaluations — crear evaluación
- PUT /api/v1/evaluations/{id}/calculator — guardar calculadora
- PUT /api/v1/evaluations/{id}/benchmark — enviar benchmark
- GET /api/v1/benchmark/questions — obtener cuestionario

## Database
Tablas (via Flyway):
- `operators` (V1.1)
- `evaluations` (V1.3) — incluye la columna `evaluation_token` (agregada en V1.4; no hay tabla `evaluation_tokens` separada)
- `calculator_results` (V1.4)
- `benchmark_questions` (V1.13)
- `benchmark_results` (V1.15)
- `benchmark_answers` (V1.16)
- `benchmark_module_scores` (V1.17)

## Security
- Operador: `X-Evaluation-Token` en header
- La validación del token ocurre **en el servicio** (caso de uso), comparando contra el token persistido de la evaluación — no hay filtro Spring Security dedicado.
- El dominio no conoce headers HTTP
