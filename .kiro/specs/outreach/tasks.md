# Tasks — Outreach

## Status Legend
- [ ] Pendiente
- [x] Completado
- [-] En progreso

---

## Backend

- [ ] Crear entidad `Campaign` con JPA (REQ-4.1, REQ-4.5)
- [ ] Crear entidad `OutreachNote` con JPA (REQ-3.1, REQ-3.2)
- [ ] Crear tablas `outreach_campaigns`, `outreach_notes`, `outreach_status_history` (REQ-2.3, REQ-3)
- [ ] Implementar `CampaignRepository` (JPA Adapter) (REQ-1, REQ-2, REQ-4)
- [ ] Implementar `OutreachUseCase` — getPipeline, updateStatus, addNote, getHistory (REQ-1, REQ-2, REQ-3)
- [ ] Implementar validación de transiciones de estado aprobadas (REQ-2.2)
- [ ] Implementar registro de historial: estado anterior, nuevo, timestamp, Admin (REQ-2.3)
- [ ] Implementar updateStatus idempotente — mismo valor devuelve respuesta definida (REQ-2.4)
- [ ] Implementar autoría de nota desde JWT — no desde datos del cliente (REQ-3.3)
- [ ] Implementar transición automática a OUTREACH_PENDING cuando PDF está listo (REQ-6.1)
- [ ] Implementar idempotencia del PDF-ready signal — no duplicar entradas (REQ-6.2)
- [ ] Implementar deduplicación de contactos en campaña por email normalizado (REQ-4.4)
- [ ] Implementar endpoint GET /outreach/pipeline con filtros por status, región, score, madurez (REQ-1.2)
- [ ] Implementar exportación del pipeline a CSV respetando filtros activos (REQ-1.6)
- [ ] Implementar endpoint PATCH /outreach/{companyId}/status (REQ-2.1, REQ-2.3)
- [ ] Implementar endpoint POST /outreach/{companyId}/notes (REQ-3.1, REQ-3.2)
- [ ] Implementar endpoint GET /outreach/{companyId}/history en orden cronológico (REQ-3.4)

### Tests Backend (REQ coverage)
- [ ] Test: pipeline muestra empresa aunque no tenga resultados de benchmark (REQ-1.4)
- [ ] Test: transición de estado inválida es rechazada (REQ-2.2)
- [ ] Test: cada cambio de estado registra actor y timestamp (REQ-2.3)
- [ ] Test: updateStatus con mismo valor es idempotente (REQ-2.4)
- [ ] Test: nota se registra con autor del JWT — no manipulable por cliente (REQ-3.3)
- [ ] Test: historial devuelve cambios en orden cronológico determinista (REQ-3.4)
- [ ] Test: mismo contacto no puede duplicarse en la misma campaña (REQ-4.3)
- [ ] Test: deduplicación usa email normalizado (REQ-4.4)
- [ ] Test: PDF-ready signal idempotente — no crea duplicados (REQ-6.2)
- [ ] Test: fallo de notificación no modifica estado del PDF (REQ-6.3)
- [ ] Test: empresa aparece con OUTREACH_PENDING cuando PDF se genera (REQ-6.1)
- [ ] Test: exportación CSV respeta filtros activos (REQ-1.6)

## Frontend

- [ ] Crear sección Campañas con listado del pipeline y filtros (REQ-1.2)
- [ ] Crear sección Contactos con datos de founders (REQ-4.2)
- [ ] Crear sección Invitaciones con estado por empresa (REQ-5.3)
- [ ] Crear sección Seguimiento con tabla filtrable (REQ-1.1, REQ-1.2)
- [ ] Implementar cambio de estado de outreach desde la UI (REQ-2.1)
- [ ] Implementar formulario de agregar nota por empresa (REQ-3.1)
- [ ] Crear vista de historial de cambios de estado por empresa (REQ-3.4)
- [ ] Mostrar score, percentil y nivel de madurez en la tabla del pipeline (REQ-1.3)
- [ ] Implementar exportación del pipeline a CSV (REQ-1.6)

## DevOps

- [ ] Configurar SES con template de invitación outreach (REQ-5.6)
- [ ] Configurar Lambda para procesamiento asíncrono de invitaciones masivas (REQ-5.1)
