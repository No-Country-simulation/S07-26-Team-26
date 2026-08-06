# Requirements — Outreach

## Introduction

El módulo Outreach gestiona la importación de contactos vía CSV, campañas de invitación, envío de emails y tracking del estado de las invitaciones. Corresponde al Bounded Context `outreach` definido en `docs/02_System_Architecture.md`.

El flujo actual implementa un modelo de campañas de invitación (no un pipeline CRM comercial como describía la versión anterior de este spec).

---

## Requirements

### Requirement 1 — Contact Import (CSV)

**User Story:** As an admin, I want to import contacts from a CSV file, so that I can create campaigns.

#### Acceptance Criteria

1. The system MUST accept a CSV with columns: `first_name, last_name, email, company, position`.
2. The system MUST validate each row: required fields, email format, duplicates.
3. Invalid rows MUST NOT prevent valid rows from being imported.
4. Duplicate detection MUST use normalized email.
5. The system MUST return: total imported, total failed, error detail per row.

### Requirement 2 — Campaign Management

**User Story:** As an admin, I want to create campaigns from imported contacts, so that I can send invitations.

#### Acceptance Criteria

1. The system MUST allow creating a campaign with selected contacts.
2. The system MUST deduplicate contacts within a campaign by email.
3. The system MUST record who created the campaign and when.
4. [future] Duplicate detection within campaign must use normalized email.

### Requirement 3 — Invitation Sending

**User Story:** As an admin, I want to send invitations to contacts, so that operators can access their benchmark.

#### Acceptance Criteria

1. Each invitation MUST have a unique access token.
2. The system MUST record the result and timestamp of each send attempt.
3. A retry MUST NOT create duplicate invitations.
4. Delivery failures MUST be logged.
5. [future] The email provider SHOULD be SES (currently using Hostinger SMTP).

### Requirement 4 — Invitation Tracking

**User Story:** As an admin, I want to track invitation status, so that I know which contacts have responded.

#### Acceptance Criteria

1. The invitation state machine MUST support: `UPLOADED → SENT → VISITED → STARTED → COMPLETED`.
2. Failed sends MUST transition to `FAILED`.
3. [future] The admin MUST see a pipeline view with invitation status per contact.
4. [future] The system MUST support exporting the pipeline to CSV.

---

## Changed from previous specs

- **Modelo cambiado**: El spec anterior describía un pipeline CRM comercial con estados OUTREACH_PENDING/OUTREACH_SENT/MEETING_SCHEDULED/CONVERTED/LOST y notas de seguimiento por empresa. El código implementa un modelo de campañas de invitación por email con estados UPLOADED→SENT→VISITED→STARTED→COMPLETED.
- Eliminado: pipeline de outreach sobre estados de empresa (no implementado).
- Eliminado: notas de seguimiento por empresa (no implementado).
- Eliminado: AWS SES (actualmente Hostinger SMTP).
- Eliminado: AWS Lambda para envío masivo (actualmente scheduler + worker con cola email_outbox).
- Actualizado: schema CSV real (`first_name, last_name, email, company, position`) vs el spec anterior (`name, email, company`).
