# Requirements — Administration

## Introduction

El módulo Administration gestiona el acceso administrativo (login JWT) y el dashboard de métricas globales. Corresponde al Bounded Context `administration` definido en `docs/02_System_Architecture.md`.

El Admin accede con email + contraseña, recibe un Bearer JWT con rol embebido. El primer Admin se crea mediante seed en base de datos.

---

## Requirements

### Requirement 1 — Admin Authentication

**User Story:** As an admin, I want to log in with email and password, so that I can access the admin panel.

#### Acceptance Criteria

1. The system MUST provide a login endpoint accepting email and password.
2. The system MUST validate credentials against the database (bcrypt hash).
3. The system MUST return a Bearer JWT with embedded `adminId` and `role` on success.
4. Invalid credentials MUST return HTTP 401 without revealing internal information.
5. All admin endpoints MUST require a valid JWT (HTTP 401 without token, HTTP 403 with wrong role).
6. The first admin MUST be created via database seed (no public registration).

---

### Requirement 2 — Dashboard KPIs

**User Story:** As an admin, I want to see global platform KPIs, so that I can monitor adoption and progress.

#### Acceptance Criteria

1. The dashboard MUST display: total operators, benchmarks completed, average score, PDFs generated.
2. Each KPI MUST show the current value.
3. The Admin MUST be able to filter by date range.

---

### Requirement 3 — Completed Evaluations List

**User Story:** As an admin, I want to see the list of completed evaluations, so that I can review individual results.

#### Acceptance Criteria

1. The dashboard MUST display a list of completed evaluations (RF-07.2).
2. Each entry MUST show at least: company/operator, score, percentile, maturity level, completion date.
3. The Admin MUST be able to access the list with a valid JWT.

---

### Requirement 4 — Dashboard Charts

**User Story:** As an admin, I want to see score distributions and maturity levels, so that I can understand portfolio health.

#### Acceptance Criteria

1. The system MUST provide score distribution by ranges.
2. The system MUST provide maturity level distribution with counts and percentages.
3. The system MUST provide monthly score evolution.
4. The system MUST provide the conversion funnel stages.

---

## Changed from previous specs

- Eliminado: Clerk MFA/OAuth → el MVP usa JWT directo sin Clerk.
- Eliminado: Redis cache → las consultas del dashboard van directo a PostgreSQL en MVP.
- Eliminado: exportación dashboard a PDF → post-MVP.
- Fusionado: admin auth (antes en identity/) y dashboard (antes en dashboard/).
