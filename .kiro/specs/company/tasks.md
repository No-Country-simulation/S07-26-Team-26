# Tasks — Company

## Status Legend
- [ ] Pendiente
- [x] Completado
- [-] En progreso

---

## Backend

- [ ] Crear entidad `Company` con JPA (REQ-1, REQ-4, REQ-5)
- [ ] Crear tabla `companies` con todos los estados del pipeline (REQ-1.6, REQ-5.1)
- [ ] Crear tabla `outreach_status_history` con actor y timestamp por transición (REQ-5.2)
- [ ] Implementar `CompanyRepository` (JPA Adapter) (REQ-1, REQ-4)
- [ ] Implementar `CompanyUseCase` — registro manual con validación y normalización de email (REQ-1.2)
- [ ] Implementar creación automática de Operator al registrar empresa (REQ-1.4)
- [ ] Implementar `SESInvitationAdapter` — email de invitación al Operator (REQ-1.5, REQ-3.1)
- [ ] Implementar registro de actor en cada transición de estado (user email o "system") (REQ-5.2)
- [ ] Implementar bloqueo de transiciones de estado inválidas (REQ-5.3)
- [ ] Implementar `ImportUseCase` — parseo y validación de CSV (REQ-2.2, REQ-2.4)
- [ ] Implementar validación fila por fila (duplicados, campos requeridos, email inválido) (REQ-2.4)
- [ ] Implementar batch insert de empresas y operadores (REQ-2.10)
- [ ] Implementar job asíncrono para envío masivo de emails (REQ-2.9)
- [ ] Implementar generación de reporte de errores CSV en S3 (REQ-2.8)
- [ ] Implementar endpoint GET /companies con filtros por status, región, industria (REQ-4.2)
- [ ] Implementar endpoint GET /companies/{id} con detalle completo y score/percentil/madurez (REQ-4.3, REQ-4.5, REQ-4.6)
- [ ] Implementar exportación a CSV con todos los campos requeridos (REQ-6.2)

### Tests Backend (REQ coverage)
- [ ] Test: registro manual crea Company + Operator vinculados (REQ-1.4)
- [ ] Test: email duplicado rechaza el registro (REQ-1.3)
- [ ] Test: email normalizado antes de verificar duplicado (REQ-1.2)
- [ ] Test: registro idempotente — mismo request no crea duplicados (REQ-1.8)
- [ ] Test: estado inicial es REGISTERED, cambia a INVITED tras envío de email (REQ-1.6, 1.7)
- [ ] Test: CSV con 0 MB, 5 MB exacto y > 5 MB (REQ-2.1)
- [ ] Test: fila con campo requerido faltante es rechazada (REQ-2.4)
- [ ] Test: fila con email inválido es rechazada (REQ-2.4)
- [ ] Test: fila duplicada dentro del mismo CSV es rechazada (REQ-2.4)
- [ ] Test: filas inválidas no impiden importar filas válidas (REQ-2.6)
- [ ] Test: reimportar el mismo CSV no crea duplicados (REQ-2.7)
- [ ] Test: resumen devuelve total importadas, fallidas y detalle de errores (REQ-2.5)
- [ ] Test: transición de estado inválida es rechazada (REQ-5.3)
- [ ] Test: historial de estado registra actor y timestamp (REQ-5.2)
- [ ] Test: detalle de empresa no incluye benchmark si no está COMPLETED (REQ-4.5)
- [ ] Test: exportación CSV incluye todos los campos requeridos (REQ-6.2)

## Frontend

- [ ] Crear pantalla de listado de empresas con filtros (REQ-4.1, REQ-4.2)
- [ ] Crear formulario de registro manual de empresa (REQ-1.1)
- [ ] Crear componente de upload de CSV con drag & drop (REQ-2.1)
- [ ] Crear panel de validación (contacts available, valid records, needs review) (REQ-2.5)
- [ ] Crear preview de contactos importados con tabla NAME/EMAIL/COMPANY/STATUS (REQ-2.5)
- [ ] Crear botón "Prepare Campaign" post-validación (REQ-2.5)
- [ ] Mostrar resumen de importación (importadas, fallidas, errores) (REQ-2.5, REQ-2.8)
- [ ] Crear pantalla de detalle de empresa con historial de estados (REQ-4.4, REQ-5.4)
- [ ] Implementar exportación de pipeline a CSV (REQ-6.1, REQ-6.3)

## DevOps

- [ ] Configurar Lambda para procesamiento asíncrono de emails masivos (REQ-2.9)
- [ ] Configurar S3 bucket para reportes de errores (REQ-2.8)
- [ ] Configurar SES con templates de invitación (REQ-3.6)
