# Design — Outreach

## Architecture
Bounded Context dentro de la arquitectura DDD + Hexagonal.

## Domain Model

### Entities
- `Campaign` — Aggregate Root
  - id: UUID
  - companyId: UUID
  - adminId: UUID
  - status: OutreachStatus
  - sentAt: Timestamp
  - meetingAt: Timestamp
  - convertedAt: Timestamp

- `OutreachNote`
  - id: UUID
  - campaignId: UUID
  - createdBy: UUID
  - note: String
  - createdAt: Timestamp

- `OutreachEvent` — registro de cambio de estado

### Value Objects
- `OutreachStatus` — OUTREACH_PENDING | OUTREACH_SENT | MEETING_SCHEDULED | CONVERTED | LOST
- `ContactInfo` — founderName, founderEmail

## Ports

### In (Use Cases)
- `OutreachUseCase`
  - getPipeline(filters) → Page<CampaignSummary>
  - updateStatus(companyId, status, notes) → Campaign
  - addNote(companyId, note) → OutreachNote
  - getHistory(companyId) → List<OutreachEvent>
- `DashboardUseCase`
  - getPipelineMetrics() → PipelineMetrics

### Out (Infrastructure)
- `CampaignRepository`
- `ContactRepository`
- `EmailService` — notificación al Admin

## Adapters

### In
- `OutreachController` — `/outreach/**`
- `DashboardController` — métricas del pipeline

### Out
- `CampaignJpaAdapter` — PostgreSQL
- `SESOutreachAdapter` — notificaciones

## API Endpoints
- GET   /outreach/pipeline
- PATCH /outreach/{companyId}/status
- POST  /outreach/{companyId}/notes
- GET   /outreach/{companyId}/history

## Database
Tablas: `outreach_campaigns`, `outreach_notes`, `outreach_status_history`
