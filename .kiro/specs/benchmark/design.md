# Design — Benchmark

## Architecture
Bounded Context dentro de la arquitectura DDD + Hexagonal.

## Domain Model

### Entities
- `BenchmarkResult` — Aggregate Root
  - id: UUID
  - companyId: UUID
  - operatorId: UUID
  - status: BenchmarkStatus (IN_PROGRESS | COMPLETED)
  - completionPct: Integer
  - score: Decimal
  - percentile: Integer
  - maturityLevel: MaturityLevel
  - kpis: KPIData (Value Object)
  - aiInsights: String
  - completedAt: Timestamp

- `Question` — pregunta del cuestionario
- `Answer` — respuesta del operador

### Value Objects
- `KPIData` — totalCapacityKw, usedCapacityKw, wastedCapacityKw, wasteIndex, pueRatio, industryBenchmark
- `MaturityLevel` — ORCHESTRATED | COORDINATED | REACTIVE | FRAGMENTED
- `Score` — valor entre 0 y 100
- `Percentile` — valor entre 0 y 100

## Ports

### In (Use Cases)
- `BenchmarkUseCase`
  - getQuestions(operatorId) → BenchmarkForm
  - saveResponses(benchmarkId, responses) → ProgressResponse
  - submitBenchmark(benchmarkId) → BenchmarkResult
  - getResults(operatorId) → BenchmarkResult
- `CalculatorUseCase`
  - calculateKPIs(responses) → KPIData
  - calculateScore(kpis) → Score
  - calculatePercentile(score) → Percentile
  - assignMaturityLevel(score) → MaturityLevel

### Out (Infrastructure)
- `BenchmarkRepository`
- `CacheService` — Redis para KPIs calculados
- `EmailService` — notificación al Admin

## Adapters

### In
- `BenchmarkController` — `/benchmark/**`
- `CalculatorController` — cálculos internos

### Out
- `BenchmarkJpaAdapter` — PostgreSQL
- `RedisAdapter` — cache de cálculos
- `SESNotificationAdapter` — notificación al Admin

## API Endpoints
- GET  /benchmark/questions
- POST /benchmark/responses
- POST /benchmark/submit
- GET  /benchmark/results
- GET  /benchmark/analysis (Admin)
- GET  /benchmark/percentiles (Admin)

## Maturity Level Logic
```
score >= 75 → ORCHESTRATED
score >= 50 → COORDINATED
score >= 25 → REACTIVE
score <  25 → FRAGMENTED
```

## Database
Tablas: `benchmark_results`, `benchmark_responses`
