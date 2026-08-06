# Requirements — Reporting

## Introduction

El módulo Reporting gestiona la preparación, generación, almacenamiento y descarga del reporte PDF institucional. Corresponde al Bounded Context `reporting` definido en `docs/02_System_Architecture.md`.

La generación del PDF se dispara al completar el benchmark. Es asíncrona y no bloquea la respuesta al operador.

---

## Requirements

### Requirement 1 — PDF Generation

**User Story:** As an operator, I want my institutional PDF to be generated when I complete the benchmark.

#### Acceptance Criteria

1. The system MUST trigger PDF generation when benchmark reaches `BENCHMARK_COMPLETED`.
2. The PDF record MUST be created with status `PROCESSING`.
3. A successful generation MUST set status to `GENERATED`.
4. A failed generation MUST set status to `FAILED`.
5. Triggering twice MUST NOT create duplicate PDF records (idempotent).

---

### Requirement 2 — PDF Content

**User Story:** As an operator, I want the PDF to contain a professional summary of my assessment.

#### Acceptance Criteria

1. The PDF MUST include: company/operator info, score/100, maturity level, module scores, KPIs.
2. Content MUST be a consistent snapshot from the benchmark results.
3. AI insights and recommendations belong to V1.2.

---

### Requirement 3 — Download

**User Story:** As an operator, I want to download the PDF from the system.

#### Acceptance Criteria

1. The operator MUST only access their own company's PDF.
2. The admin MUST access any company's PDF.
3. A PDF not in `GENERATED` status MUST NOT be downloadable.
4. [future] PDFs stored in S3 with presigned URLs.
5. [future] For MVP, PDF served directly from the backend filesystem.

---

### Requirement 4 — Email Delivery

**User Story:** As an operator, I want to receive the PDF by email when ready.

#### Acceptance Criteria

1. [future] The system MUST send the PDF automatically after successful generation.
2. [future] Delivery via SES or configured SMTP provider.
3. [future] A delivery failure MUST NOT change PDF status from GENERATED to FAILED.

---

## Changed from previous specs

- Eliminado: AWS S3 obligatorio → MVP puede usar filesystem local.
- Eliminado: AWS Lambda para async → MVP puede usar Spring @Async.
- Eliminado: Amazon SES obligatorio → MVP puede seguir con Hostinger SMTP.
- Agregado: criterio explícito de que PDF por email es post-MVP.
