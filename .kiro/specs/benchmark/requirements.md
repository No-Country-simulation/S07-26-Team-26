# Requirements — Benchmark

## Introduction

The Benchmark is the core module of the product. It allows the Operator to complete a Data Center capacity questionnaire. The system calculates KPIs, a score /100, a percentile and a maturity level. The result is the basis for the institutional PDF and the outreach process.

Formulas, categories, questions, maturity limits and percentile rules must be confirmed via approved acceptance criteria. They must not be modified during implementation without recording the decision.

---

## Requirements

### Requirement 1 — Questionnaire Access

**User Story:** As an authenticated Operator, I want to access my company's benchmark questionnaire, to complete the Data Center assessment.

#### Acceptance Criteria

1. The system MUST identify the Operator's company from the JWT.
2. The system MUST allow access to the questionnaire if the benchmark is `IN_PROGRESS` or not yet started.
3. The system MUST reject access if the benchmark is already `COMPLETED`.
4. The Operator MUST NOT be able to access another company's benchmark.
5. The system MUST return the questionnaire with previously saved answers to allow continuation.

---

### Requirement 2 — Sectioned Questionnaire

**User Story:** As an Operator, I want to answer the benchmark organized by sections, to complete the assessment in an orderly manner.

#### Acceptance Criteria

1. The questionnaire MUST be organized in thematic sections.
2. Each question MUST have a unique identifier, text, type and required flag.
3. The system MUST accept partial answers (progressive save without requiring all sections to be complete).
4. Saving a repeated answer for the same question MUST update the previous answer without creating a duplicate (idempotent).
5. The system MUST calculate and return the completion percentage after each save.
6. The system MUST NOT allow final submission until all required questions are answered.
7. The company status MUST change to `IN_PROGRESS` on saving the first answer.

---

### Requirement 3 — KPI Calculator

**User Story:** As an Operator, I want the system to calculate my Data Center's capacity KPIs upon completing the benchmark, to understand the real state of my infrastructure.

#### Acceptance Criteria

1. Upon completing the benchmark, the system MUST calculate:
   - Total installed capacity (kW)
   - Used capacity (kW)
   - Wasted capacity (kW)
   - Waste Index (% wasted capacity)
   - PUE Ratio (Power Usage Effectiveness)
   - Industry benchmark (sector average)
2. Total capacity MUST be greater than zero.
3. Used capacity MUST NOT exceed total capacity.
4. Values MUST be calculated with decimal precision.
5. Formulas and rounding rules MUST be defined and approved before closing implementation.

---

### Requirement 4 — Score, Percentile and Maturity Level

**User Story:** As an Operator, I want to know my score, percentile and maturity level after completing the benchmark, to understand my Data Center's position relative to the industry.

#### Acceptance Criteria

1. The system MUST calculate a score between 0 and 100.
2. The system MUST calculate the Operator's percentile in the global ranking of all completed benchmarks.
3. The percentile MUST NOT be calculated if there is no valid comparison population; the contract MUST define how to represent this case.
4. The system MUST assign a maturity level based on the score:
   - `ORCHESTRATED`: 75 — 100
   - `COORDINATED`: 50 — 74
   - `REACTIVE`: 25 — 49
   - `FRAGMENTED`: 0 — 24
5. Maturity level limits MUST be unique, exhaustive and tested at boundary values.
6. Results MUST be reproducible with the same answers and the same version of rules.
7. The Operator MUST ONLY consult results from their own company.

---

### Requirement 5 — States and Completion

**User Story:** As a system, I want to control the benchmark lifecycle, to prevent out-of-order operations and guarantee consistency.

#### Acceptance Criteria

1. The benchmark initial state MUST be `IN_PROGRESS` upon saving the first answer.
2. Upon validly completing the benchmark, the state MUST change to `COMPLETED`.
3. The company status MUST change to `COMPLETED` upon finishing the benchmark.
4. A transition that skips a mandatory stage MUST NOT be allowed.
5. Repeating the completion request MUST NOT duplicate results or external effects (idempotent).
6. Completion MUST leave the necessary data available to initiate PDF generation.

---

### Requirement 6 — Admin Notification

**User Story:** As an Admin, I want to be notified when an Operator completes the benchmark, to follow up on the lead in a timely manner.

#### Acceptance Criteria

1. The system MUST record the exact completion time of the benchmark.
2. The completed company MUST appear in the Admin's pipeline with status `COMPLETED`.
3. The system MUST send a notification to the Admin upon benchmark completion.
4. Failure of the external notification MUST NOT corrupt the already-calculated result.
5. The notification channel is Amazon SES.

---

### Requirement 7 — Admin Analytics

**User Story:** As an Admin, I want to see aggregated analytics of all completed benchmarks, to make strategic decisions about the portfolio.

#### Acceptance Criteria

1. The Admin MUST be able to query score distribution (histogram by ranges 0-10 to 91-100).
2. The Admin MUST be able to query maturity level distribution with count and percentage.
3. The Admin MUST be able to query average score by region/segment.
4. The Admin MUST be able to query monthly average score evolution.
5. The Admin MUST be able to query the system percentile table.

---

## Constraints

- Calculation and state rules belong to the domain and MUST be testable without Spring, HTTP, PostgreSQL or external services.
- Score and KPI formulas must be approved before implementation.
- Cache adapters (Redis) are optional in the MVP; logic must not depend on them.
- Definitive routes and DTOs must come from the OpenAPI contract.

---

## Pending Decisions

- Exact formula for calculating score /100.
- Final number and content of questionnaire questions.
- Minimum completed benchmarks required to calculate a valid percentile.
- Scale and rounding rules for KPIs.
