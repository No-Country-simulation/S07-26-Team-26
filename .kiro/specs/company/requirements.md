# Requirements — Company

## Introduction

The Company module manages the full lifecycle of a company within Ghost Load. The Admin registers companies manually or via bulk CSV import. Each company has one assigned Operator who completes the benchmark, and one Founder Contact who is the target of the outreach process.

This module is the entry point of the product funnel: a company must exist and its Operator must be invited before any benchmark can be completed.

---

## Requirements

### Requirement 1 — Manual Company Registration

**User Story:** As an Admin, I want to register a company manually, so that I can invite its Operator to complete the benchmark.

#### Acceptance Criteria

1. The system MUST accept at minimum: company name, industry, country, operator email, founder name and founder email.
2. The system MUST validate and normalize the operator email before checking for duplicates.
3. The system MUST prevent registration of a company with an already existing operator email.
4. The system MUST automatically create a user with role ROLE_OPERATOR linked to the company.
5. The system MUST automatically send an invitation email to the Operator via Amazon SES upon creation.
6. The company status MUST be set to REGISTERED upon creation.
7. The company status MUST change to INVITED after the invitation email is sent successfully.
8. Repeating the same registration request MUST NOT create duplicate companies or operators (idempotent).
9. The response MUST NOT expose internal secrets or persistence details.

---

### Requirement 2 — Bulk CSV Import

**User Story:** As an Admin, I want to import multiple companies from a CSV file, so that I can efficiently onboard large batches of operators.

#### Acceptance Criteria

1. The system MUST accept a CSV file up to 5 MB.
2. The expected CSV columns are: name, email, company.
3. The system MUST validate each row before importing.
4. Validation MUST detect at minimum:
   - Missing required fields.
   - Invalid email format.
   - Duplicate emails within the file.
   - Duplicate emails already existing in the system.
5. The system MUST return a summary with: total imported, total failed, and error detail per row.
6. Invalid rows MUST NOT prevent valid rows from being imported.
7. Reprocessing the same file MUST NOT create duplicate companies or operators (idempotent).
8. The system MUST generate a downloadable error report for failed rows.
9. The bulk email sending MUST be processed asynchronously to avoid blocking the request.
10. The company and operator creation MUST be processed as a batch.

---

### Requirement 3 — Operator Invitation

**User Story:** As an Admin, I want operators to receive an invitation email with access credentials, so that they can log in and complete the benchmark.

#### Acceptance Criteria

1. Each invitation MUST be associated with one company and one operator.
2. The invitation email MUST contain valid login credentials for the operator.
3. The system MUST record the status and timestamp of each invitation sent.
4. A retry MUST NOT create a second operator for the same company.
5. A delivery failure MUST be logged and allow manual retry by the Admin.
6. The email delivery provider is Amazon SES.

---

### Requirement 4 — Company Listing and Detail

**User Story:** As an Admin, I want to view all companies with their current pipeline status, so that I can monitor progress across the entire portfolio.

#### Acceptance Criteria

1. The Admin MUST be able to retrieve a paginated list of all companies.
2. The list MUST support filtering by: pipeline status, region, industry.
3. Each company in the list MUST show at minimum: name, industry, country, operator email, pipeline status, and benchmark results if completed (score, percentile, maturity level).
4. The Admin MUST be able to view the full detail of a single company.
5. The detail MUST include benchmark results only when the benchmark has been completed.
6. The detail MUST include the PDF download URL when the PDF has been generated.

---

### Requirement 5 — Pipeline Status Tracking

**User Story:** As a system, I want to track the status of each company throughout the funnel, so that the Admin has full visibility of every company's progress.

#### Acceptance Criteria

1. The pipeline MUST support the following states in order:
   REGISTERED → INVITED → IN_PROGRESS → COMPLETED → PDF_GENERATED → OUTREACH_PENDING → OUTREACH_SENT → MEETING_SCHEDULED → CONVERTED / LOST
2. Each status transition MUST be recorded with timestamp and the actor who triggered it (user email or "system").
3. The system MUST NOT allow skipping mandatory stages.
4. The status history MUST be queryable per company.

---

### Requirement 6 — Pipeline Export

**User Story:** As an Admin, I want to export the company pipeline to CSV, so that I can share progress reports with the team or use them in external meetings.

#### Acceptance Criteria

1. The Admin MUST be able to export all companies to CSV.
2. The export MUST include: company name, industry, country, operator email, founder email, pipeline status, score, percentile, maturity level, and last updated date.
3. The export MUST reflect the current state of the pipeline at the time of the request.

---

## Constraints

- CSV parsing DTOs MUST NOT enter the domain layer.
- The domain MUST NOT depend on MultipartFile, JPA, storage or email providers.
- Async processing (AWS Lambda, Amazon SES) are the chosen adapters but MUST be replaceable via the hexagonal port.
- The error report storage MUST use the simplest implementation compatible with security and expiration for the MVP.
- Definitive routes and DTOs must come from the OpenAPI contract.

---

## Pending Decisions

- Maximum number of companies per CSV import batch.
- Retry policy for failed invitation emails.
- Whether the Admin can re-send an invitation to an existing operator.
- Expiration policy for operator credentials sent via invitation.
