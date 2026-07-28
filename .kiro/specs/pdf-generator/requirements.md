# Requirements — PDF Generator

## Introduction

The PDF Generator module produces the institutional PDF report after a company's benchmark is completed. This PDF is the primary commercial asset of the system — it is sent to the Operator by email and made available for download. It is the trigger that moves a company into the outreach pipeline.

The PDF generation is asynchronous. The domain does not depend on any specific PDF library, storage provider or email provider — these are adapter decisions.

---

## Requirements

### Requirement 1 — Automatic Generation

**User Story:** As an Operator, I want my institutional PDF to be generated automatically when I complete the benchmark, so that I receive my results without any additional action.

#### Acceptance Criteria

1. The system MUST trigger PDF generation automatically when the benchmark status reaches `COMPLETED`.
2. PDF generation MUST be processed asynchronously and MUST NOT block the benchmark completion response.
3. The PDF record MUST be created with status `PROCESSING` immediately upon triggering.
4. A successful generation MUST set the status to `GENERATED`.
5. A failed generation MUST set the status to `FAILED` and preserve safe diagnostic information.
6. The company status MUST change to `PDF_GENERATED` after successful generation.
7. Triggering generation twice for the same completed benchmark MUST NOT create duplicate PDF records (idempotent).

---

### Requirement 2 — PDF Content

**User Story:** As an Operator, I want the PDF to contain a complete and professional summary of my Data Center assessment, so that I can use it as an institutional document.

#### Acceptance Criteria

1. The PDF MUST include:
   - Company and operator identification data
   - Score /100
   - Percentile (when a valid comparison population exists)
   - Maturity level with its description (ORCHESTRATED / COORDINATED / REACTIVE / FRAGMENTED)
   - KPIs: total capacity, used capacity, wasted capacity, waste index, PUE ratio
   - Industry benchmark comparison
2. Content MUST derive from a consistent snapshot of the benchmark results at the time of generation.
3. When a value is legitimately absent (e.g. percentile with insufficient population), it MUST be represented explicitly and NOT omitted silently.
4. AI-generated insights and recommendations belong to V1.2 and MUST NOT be required for MVP completion.

---

### Requirement 3 — Idempotency and Retries

**User Story:** As a system, I want to avoid duplicate PDFs and support retries after infrastructure failures, so that generation is reliable and consistent.

#### Acceptance Criteria

1. The same completed benchmark MUST NOT produce multiple active PDF records via accidental retries.
2. Processing the same generation request twice MUST reuse the valid result or continue the existing process.
3. A PDF with status `FAILED` MUST be retriable without creating a new record.
4. An authorized Admin MUST be able to request PDF regeneration for a company.
5. Regeneration MUST preserve traceability: version, attempt number, and timestamp.
6. All external side effects including email sending MUST be controlled idempotently on retry.

---

### Requirement 4 — Storage

**User Story:** As a system, I want to store the generated PDF securely, so that it can be retrieved for download at any time.

#### Acceptance Criteria

1. The generated PDF MUST be stored in Amazon S3.
2. The system MUST store a reference to the file (S3 key), NOT a permanent public URL.
3. Access to the file MUST be private and time-limited via S3 presigned URLs.
4. The system MUST record: PDF status, generation timestamp, S3 key, and attempt metadata.
5. An infrastructure reference (S3 key, bucket name) MUST NOT be exposed as a domain model field.

---

### Requirement 5 — Download and Authorization

**User Story:** As an authorized user, I want to download the generated PDF, so that I can use it outside the system.

#### Acceptance Criteria

1. The Operator MUST only be able to access the PDF of their own company.
2. The Admin MUST be able to access and download the PDF of any company.
3. A PDF that is not in `GENERATED` status MUST NOT be presented as available for download.
4. Authorization MUST be verified before issuing a presigned download URL.
5. The presigned URL MUST expire after a configurable duration.
6. The download URL MUST be regenerated on each authorized request (never cached permanently).

---

### Requirement 6 — Email Delivery

**User Story:** As an Operator, I want to receive the PDF by email when it is ready, so that I have it immediately without needing to log back in.

#### Acceptance Criteria

1. The system MUST send the PDF to the Operator's email automatically after successful generation.
2. The email delivery MUST be processed via Amazon SES.
3. The system MUST record the result and timestamp of the email send attempt.
4. A delivery failure MUST NOT change a `GENERATED` PDF to `FAILED`.
5. Email retry MUST NOT re-trigger PDF regeneration.
6. The email MUST include a download link with a valid presigned URL.

---

## Constraints

- PDF generation, storage and delivery MUST be represented as separate intent-oriented ports.
- The domain MUST NOT depend on PDF libraries (iText, PDFBox), AWS SDK, Spring or JPA.
- The domain entity and the JPA entity MUST remain separate.
- AWS Lambda, Amazon S3, Amazon SES, iText and PDFBox are adapter choices, not mandatory requirements.
- The async processing strategy MUST be approved without converting the system into microservices.
- Definitive routes and DTOs must come from the OpenAPI contract.

---

## Pending Decisions

- PDF library selection: iText vs Apache PDFBox.
- PDF template design and branding.
- Presigned URL expiration duration.
- Whether the Admin email also receives a copy of the PDF.
- Async processing mechanism: AWS Lambda vs Spring async vs message queue.
