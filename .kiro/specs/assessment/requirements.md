# Requirements — Assessment

## Introduction

El módulo Assessment agrupa la gestión del operador, la evaluación, la calculadora de capacidad, el benchmark y los resultados. Corresponde al Bounded Context `assessment` definido en `docs/02_System_Architecture.md` y `docs/10_Hexagonal_Architecture_DDD.md`.

El operador es identificado por email y autorizado mediante un `X-Evaluation-Token`. No existe registro público ni contraseña para operadores.

---

## Requirements

### Requirement 1 — Operator Registration (antes identity + company)

**User Story:** As an operator, I want to register with my email and company data, so that I can start the evaluation.

#### Acceptance Criteria

1. The system MUST accept: operator email, company name, position.
2. The system MUST validate and normalize the email before persisting.
3. The system MUST return a unique evaluation token in the response.
4. The system MUST create an operator record and an evaluation in `STARTED` state.

---

### Requirement 2 — Calculator

**User Story:** As an operator, I want to input my Data Center capacity data, so that the system calculates my capacity KPIs.

#### Acceptance Criteria

1. The system MUST accept: total capacity (MW), productive capacity (MW), monthly cost per kW, currency.
2. Total capacity MUST be greater than zero.
3. Productive capacity MUST NOT exceed total capacity.
4. The system MUST calculate:
   - Non-productive capacity (MW)
   - Utilization percentage
   - Non-productive percentage
   - Estimated annual cost (USD)
5. Values MUST be calculated with decimal precision.
6. The evaluation state MUST change to `CALCULATOR_COMPLETED`.

---

### Requirement 3 — Benchmark Questionnaire

**User Story:** As an operator, I want to complete a benchmark questionnaire, so that I get a maturity assessment.

#### Acceptance Criteria

1. The questionnaire MUST contain 12–20 questions organized in modules/categories.
2. Each question MUST have: unique ID, text, type, required flag.
3. Each answer MUST be a value between 1 and 5.
4. The system MUST reject duplicate or unknown question IDs.
5. The system MUST calculate completion percentage.
6. The system MUST NOT allow final submission until all required questions are answered.
7. The evaluation state MUST change to `BENCHMARK_COMPLETED` upon valid submission.

---

### Requirement 4 — Score, Percentile and Maturity Level

**User Story:** As an operator, I want to know my score, percentile and maturity level after completing the benchmark.

#### Acceptance Criteria

1. The system MUST calculate a score between 0 and 100.
2. The system MUST calculate module scores per category.
3. The system MUST assign a maturity level based on the score:
   - `OPTIMIZED`: 90–100
   - `ADVANCED`: 75–89
   - `MANAGED`: 50–74
   - `DEVELOPING`: 25–49
   - `INITIAL`: 0–24
4. The percentile MUST use demo/staging data during MVP with a disclaimer.
5. Results MUST be reproducible with the same answers.

---

### Requirement 5 — Company Context (antes company)

**User Story:** As an admin, I want companies to be associated with evaluations, so that I can track which organization each operator represents.

#### Acceptance Criteria

1. Company information (name) is captured during operator registration.
2. Each evaluation is linked to exactly one operator and one company context.

---

## Changed from previous specs

- Eliminado: Clerk, MFA, OAuth, contraseña de operador → el MVP usa `X-Evaluation-Token`.
- Eliminado: entidad `Company` separada con pipeline propio → la empresa es un atributo del operador.
- Eliminado: import CSV de empresas → el alcance CSV pertenece a Outreach (contactos).
- Cambiado: los nombres de niveles de madurez siguen `10_Hexagonal_Architecture_DDD.md` (INITIAL/DEVELOPING/MANAGED/ADVANCED/OPTIMIZED).
