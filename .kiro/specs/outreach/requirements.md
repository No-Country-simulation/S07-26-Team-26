# Requirements — Outreach

## Introduction

The Outreach module manages the commercial follow-up pipeline after a company completes the benchmark and receives its institutional PDF. The Admin tracks the status of each company from PDF_GENERATED through to CONVERTED or LOST, logs notes, and manages campaigns and contacts.

Outreach does not own Evaluation or benchmark results. It holds references and derives context from other bounded contexts through authorized read ports.

---

## Requirements

### Requirement 1 — Pipeline Visibility

**User Story:** As an Admin, I want to see the full outreach pipeline for all companies, so that I can prioritize follow-up actions effectively.

#### Acceptance Criteria

1. The Admin MUST be able to retrieve a paginated list of all companies in the outreach pipeline.
2. The list MUST support filtering by: outreach status, region, score range, maturity level.
3. Each row MUST include at minimum: company name, founder name, founder email, outreach status, score, percentile, maturity level, and PDF availability.
4. The absence of benchmark results MUST NOT prevent a company from appearing in the pipeline.
5. The pipeline MUST distinguish between the invitation status and the commercial outreach status.
6. The Admin MUST be able to export the pipeline to CSV respecting active filters.

---

### Requirement 2 — Outreach Status Management

**User Story:** As an Admin, I want to update the commercial status of a company, so that I can reflect the real progress of each lead in the pipeline.

#### Acceptance Criteria

1. The pipeline MUST support the following commercial states:
   - `OUTREACH_PENDING` — PDF generated, not yet contacted
   - `OUTREACH_SENT` — Admin sent contact to founder
   - `MEETING_SCHEDULED` — Meeting has been scheduled
   - `CONVERTED` — Lead converted
   - `LOST` — No response or discarded
2. Only approved transitions MUST be accepted.
3. Each status change MUST record: previous status, new status, timestamp, and the Admin who made the change.
4. A repeated status update with the same value MUST be idempotent or return a defined conflict response.
5. Evaluation or benchmark statuses MUST NOT be stored as commercial outreach statuses.

---

### Requirement 3 — Notes and Follow-up History

**User Story:** As an Admin, I want to add notes and view the full status history of a company, so that I can preserve context across the follow-up process.

#### Acceptance Criteria

1. The Admin MUST be able to add a text note to any company in the pipeline.
2. Each note MUST record: author (Admin email), content, and timestamp.
3. Notes MUST NOT be attributable to a different Admin via client-manipulated data.
4. The status history MUST be queryable per company in deterministic chronological order.
5. Edit and delete rules for notes MUST be defined before exposing those operations.

---

### Requirement 4 — Campaign Management

**User Story:** As an Admin, I want to organize contacts into campaigns, so that I can manage bulk outreach initiatives.

#### Acceptance Criteria

1. The Admin MUST be able to create a campaign with the fields required by the contract.
2. The system MUST allow associating valid contacts with a campaign.
3. The same contact MUST NOT be duplicated within the same campaign.
4. Duplicate detection MUST use normalized email and MUST be tested at boundary values.
5. The system MUST record who created the campaign and when.

---

### Requirement 5 — Invitation Sending

**User Story:** As an Admin, I want to send invitations to contacts, so that operators can access their benchmark evaluation.

#### Acceptance Criteria

1. Each invitation MUST be associated with one campaign, one contact, and one company.
2. The invitation MUST contain or resolve to a unique and non-predictable access token.
3. The system MUST record the result and timestamp of each send attempt.
4. A retry MUST NOT create a duplicate invitation or a second operator for the same company.
5. Delivery failures MUST be logged and allow retry without losing history.
6. The email delivery provider is Amazon SES.

---

### Requirement 6 — PDF Ready Notification

**User Story:** As an Admin, I want to be notified when a company's PDF is ready, so that I can initiate outreach at the right moment.

#### Acceptance Criteria

1. When a PDF is generated for a company, that company MUST automatically appear in the pipeline with status `OUTREACH_PENDING`.
2. Processing the same PDF-ready signal twice MUST NOT create duplicate campaigns, invitations or history entries (idempotent).
3. A failure of the external notification MUST NOT incorrectly modify the PDF generation status.

---

## Constraints

- Outreach controllers MUST NOT directly access repositories from the Benchmark or PDF Generator bounded contexts.
- Output ports MUST express intent (e.g. `SendInvitationPort`) and MUST NOT reference provider names.
- Amazon SES and async processing are the chosen adapters but MUST be replaceable via hexagonal ports.
- Definitive routes and DTOs must come from the OpenAPI contract.

---

## Pending Decisions

- Maximum number of notes per company.
- Whether Admins can edit or delete their own notes.
- Retry policy for failed invitation emails.
- Whether campaign creation is required before inviting or can be done inline.
