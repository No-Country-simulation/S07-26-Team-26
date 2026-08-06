# Tasks — Outreach

## Status Legend
- [x] Completado
- [-] En progreso
- [ ] Pendiente
- [future] Fuera de MVP (visión futura)

---

## Backend

### Entities & Database
- [x] Crear entidad `Campaign` con JPA + tabla `campaigns` (V1.7)
- [x] Crear entidad `Contact` con JPA + tabla `contacts` (V1.6, alter V1.12)
- [x] Crear entidad `Invitation` con JPA + tabla `invitations` (V1.8)
- [x] Crear entidad `ContactImport` con JPA + tabla `contact_imports` (V1.5)
- [x] Crear tabla `contact_import_contacts` (V1.10) + seed (V1.11)
- [x] Crear tabla `email_outbox` (V1.9)
  - Nota: `email_outbox_status` NO es tabla separada; el estado es la columna `status` con CHECK (PENDING/PROCESSING/SENT/FAILED) dentro de `email_outbox`.
  - Nota: no existe tabla `campaign_contacts`; la relación campaña↔invitación se modela vía `campaign_id` en `invitations`/`email_outbox`.

### Contact Import
- [x] Implementar `ContactImportController` — POST /api/v1/admin/contact-imports
- [x] Implementar `ImportContactsService` — validación y persistencia
- [x] Implementar `ApacheCommonsCsvContactFileAdapter` — parseo CSV
- [x] Implementar deduplicación por email
- [x] Implementar validación de filas: campos requeridos, email inválido

### Campaigns
- [x] Implementar `CampaignController` — POST /api/v1/admin/campaigns
- [x] Implementar `CreateCampaignService` — crear campaña desde contactos
- [x] Implementar envío de campaña — POST /api/v1/admin/campaigns/{id}/send
- [x] Implementar `SendCampaignService` — crear invitations + encolar emails
- [x] Implementar `EmailOutboxWorker` — scheduler que envía emails pendientes
- [x] Implementar `ProcessPendingEmailsService` — envía vía SMTP y actualiza estado
- [x] Implementar `HostingerSmtpEmailAdapter` — integración SMTP

### Invitation Tracking
- [x] Implementar state machine: UPLOADED → SENT → VISITED → STARTED → COMPLETED | FAILED
- [x] Implementar generación de token único por invitación
- [x] Implementar registro de timestamp de cada cambio de estado

### Por implementar
- [x] Pipeline CRM comercial con estados OUTREACH_PENDING → OUTREACH_SENT → MEETING_SCHEDULED → CONVERTED/LOST
- [x] Notas de seguimiento por empresa
- [x] Historial de cambios de estado por empresa
- [x] Vista de pipeline con filtros (status, región, score)
- [x] Exportación del pipeline a CSV
- [future] Migrar de Hostinger SMTP a AWS SES
- [future] Envío masivo asíncrono con Lambda/SQS (actualmente scheduler + worker)

### Tests Backend
- [x] Test: importación CSV con datos válidos (`ImportContactsServiceTest`)
- [x] Test: CSV con filas inválidas no bloquea filas válidas (`ImportContactsServiceTest`)
- [x] Test: deduplicación por email normalizado (`ImportContactsServiceTest`)
- [x] Test: validación de campos requeridos en CSV (`ImportContactsServiceTest`)
- [x] Test: creación de campaña desde contactos (`CreateCampaignServiceTest`)
- [x] Test: envío de campaña genera invitations y encola emails (`SendCampaignServiceTest`)
- [x] Test: worker envía emails pendientes y actualiza estado (`ProcessPendingEmailsServiceTest`)
- [x] Test: adaptador SMTP clasifica errores (`HostingerSmtpEmailAdapterTest`, `EmailDeliveryExceptionTest`)
- [x] Test: transiciones de estado inválidas son rechazadas (`CampaignStateTransitionTest`)
- [x] Test: email retry no duplica invitations (`SendCampaignServiceTest.shouldNotDuplicateEmailsOnRetryAfterQueueing`)
- [future] Test: migración a SES

## Frontend
- [x] Crear sección de importación CSV con drag & drop y preview
- [x] Crear sección de campañas con listado y creación
- [x] Crear vista de tracking de invitaciones por campaña
- [x] Crear pipeline CRM con cambio de estado, notas, historial, filtros y export CSV
- [ ] Conectar pipeline CRM a datos reales de evaluación/score por empresa

## DevOps
- [future] Configurar SES con templates de invitación
- [future] Configurar cola asíncrona para envío masivo
