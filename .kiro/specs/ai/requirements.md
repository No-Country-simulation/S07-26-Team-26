# Requirements — AI Integration

## Introduction

El módulo AI integra Google AI Studio para generar insights, recomendaciones y clasificación de leads a partir de los datos del benchmark. Corresponde al documento `docs/08_AI_Architecture.md`.

Este módulo está planificado para V1.2. No forma parte del MVP.

---

## Requirements

### Requirement 1 — Benchmark Insights

**User Story:** As an operator, I want AI-generated insights about my benchmark results.

#### Acceptance Criteria
1. The system MUST send benchmark data to Google AI Studio.
2. The system MUST display personalized textual insights on the results page.
3. Insights MUST be in Spanish and technically specific.

### Requirement 2 — Recommendations in PDF

**User Story:** As an operator, I want AI-generated recommendations in my PDF.

#### Acceptance Criteria
1. Recommendations MUST be categorized (Capacity, Energy, Cooling, Resilience, Management).
2. Each recommendation MUST have a priority level and estimated impact.

### Requirement 3 — Lead Classification

**User Story:** As an admin, I want AI-classified lead scores in the outreach pipeline.

#### Acceptance Criteria
1. Each company MUST have a lead score based on benchmark data.
2. Priority levels (HIGH / MEDIUM / LOW) MUST guide outreach sequencing.

### Requirement 4 — Dashboard AI Panel

**User Story:** As an admin, I want AI-powered aggregated insights on the dashboard.

#### Acceptance Criteria
1. The dashboard MUST show AI-generated portfolio trends.
2. Lead score distribution MUST be available.

---

## Constraints
- Los puertos de salida deben expresar intención: `GenerateBenchmarkInsightsPort`, `ClassifyLeadPort`.
- El adaptador concreto (`GoogleAIStudioAdapter`) es reemplazable.
- El dominio no depende del SDK de Google AI.
