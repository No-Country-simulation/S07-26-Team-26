# Design — Outreach

## Architecture
Bounded Context `outreach` dentro de la arquitectura DDD + Hexagonal.

## Domain Model

### Entities
- `Contact` — persona importada desde CSV
- `Campaign` — Aggregate Root, agrupa contactos para invitación
- `Invitation` — invitación enviada, con token único y tracking de estado
- `ContactImport` — registro de una importación CSV

### Value Objects
- `Email`, `ContactId`, `CampaignId`, `InvitationId`
- `InvitationToken` — token único no predecible
- `InvitationStatus` — UPLOADED | SENT | VISITED | STARTED | COMPLETED | FAILED

## Ports

### In (Use Cases)
- `ImportContactsUseCase` — parsea y valida CSV, persiste contactos
- `CreateCampaignUseCase` — crea campaña desde contactos existentes
- `SendCampaignUseCase` — genera invitations y las encola
- `ProcessPendingEmailsUseCase` — envía emails pendientes vía SMTP

### Out (Infrastructure)
- `ContactFilePort` — lectura de archivo CSV
- `LoadContactsPort` / `SaveContactPort`
- `CampaignRepository`
- `InvitationRepository`
- `SendEmailPort` — envío de email transaccional

## Adapters

### In
- `ContactImportController` — POST /api/v1/admin/contacts/import
- `CampaignController` — POST /api/v1/admin/campaigns, POST /api/v1/admin/campaigns/{id}/send

### Out
- `ApacheCommonsCsvContactFileAdapter` — parseo CSV
- `ContactPersistenceAdapter` — PostgreSQL
- `CampaignPersistenceAdapter` — PostgreSQL
- `HostingerSmtpEmailAdapter` — envío SMTP

## API Endpoints
- POST /api/v1/admin/contacts/import — importar CSV
- POST /api/v1/admin/campaigns — crear campaña
- POST /api/v1/admin/campaigns/{id}/send — enviar campaña

## Database
Tablas (Via Flyway):
- `contact_imports` (V1.5)
- `contacts` (V1.6)
- `campaigns` (V1.7)
- `campaign_contacts` (V1.8)
- `invitations` (V1.9)
- `email_outbox` (V1.10)
- `email_outbox_status` (V1.12)

## Invitation State Machine
```
UPLOADED → SENT → VISITED → STARTED → COMPLETED
UPLOADED → FAILED
```
