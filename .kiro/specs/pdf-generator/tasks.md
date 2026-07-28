# Tasks — PDF Generator

## Status Legend
- [ ] Pendiente
- [x] Completado
- [-] En progreso

---

## Backend

- [ ] Crear entidad `GeneratedPDF` con JPA (REQ-1, REQ-4)
- [ ] Crear tabla `generated_pdfs` con campos: status, s3_key, attempt_count, generation_timestamp (REQ-4.4)
- [ ] Implementar `PDFRepository` (JPA Adapter) (REQ-1, REQ-4)
- [ ] Elegir librería de generación: iText o Apache PDFBox (REQ-1)
- [ ] Crear template del PDF institucional con todos los campos requeridos (REQ-2.1)
- [ ] Implementar snapshot consistente de datos de benchmark al momento de generación (REQ-2.2)
- [ ] Representar explícitamente valores ausentes (ej. percentil sin población) (REQ-2.3)
- [ ] Implementar `PDFGeneratorAdapter` — generación del PDF con datos del benchmark (REQ-1, REQ-2)
- [ ] Implementar `S3StorageAdapter` — upload del PDF, almacenar S3 key (no URL pública) (REQ-4.2)
- [ ] Implementar generación de presigned URL con expiración configurable (REQ-5.5)
- [ ] Regenerar presigned URL en cada request — nunca cachear permanentemente (REQ-5.6)
- [ ] Implementar `SESEmailAdapter` — envío del PDF al Operator con presigned URL (REQ-6.6)
- [ ] Implementar `PDFUseCase` — generatePDF, getPDF, regeneratePDF (REQ-1, REQ-3, REQ-5)
- [ ] Implementar trigger asíncrono post-benchmark (REQ-1.2)
- [ ] Implementar generación idempotente — doble trigger no crea duplicado (REQ-1.7)
- [ ] Implementar estado FAILED con información diagnóstica segura (REQ-1.5)
- [ ] Implementar cambio de estado empresa a PDF_GENERATED tras generación exitosa (REQ-1.6)
- [ ] Implementar regeneración con trazabilidad (versión, intento, fecha) (REQ-3.5)
- [ ] Fallo de email no cambia estado GENERATED a FAILED (REQ-6.4)
- [ ] Email retry no re-genera el PDF (REQ-6.5)
- [ ] Implementar endpoint GET /pdf/{companyId} — solo devuelve URL si GENERATED (REQ-5.3)
- [ ] Implementar endpoint POST /pdf/{companyId}/regenerate con autorización Admin (REQ-3.4)
- [ ] Implementar endpoint GET /pdf para listado Admin (REQ-5.2)

### Tests Backend (REQ coverage)
- [ ] Test: generación se dispara automáticamente al completar benchmark (REQ-1.1)
- [ ] Test: estado inicial es PROCESSING (REQ-1.3)
- [ ] Test: generación exitosa → estado GENERATED (REQ-1.4)
- [ ] Test: generación fallida → estado FAILED con info diagnóstica (REQ-1.5)
- [ ] Test: doble trigger no crea segundo registro PDF (REQ-1.7)
- [ ] Test: estado empresa cambia a PDF_GENERATED tras generación (REQ-1.6)
- [ ] Test: PDF no devuelve URL si status ≠ GENERATED (REQ-5.3)
- [ ] Test: Operator solo accede a PDF de su empresa (REQ-5.1)
- [ ] Test: presigned URL se regenera en cada request (REQ-5.6)
- [ ] Test: fallo de email SES no modifica estado GENERATED (REQ-6.4)
- [ ] Test: retry de email no re-genera el PDF (REQ-6.5)
- [ ] Test: regeneración preserva historial de intentos (REQ-3.5)
- [ ] Test: PDF FAILED puede reintentarse sin crear nuevo registro (REQ-3.3)
- [ ] Test: PDF incluye todos los campos obligatorios del REQ-2.1

## Frontend

- [ ] Crear componente de descarga de PDF en pantalla de resultados del Operator (REQ-5.1)
- [ ] Mostrar botón de descarga solo cuando PDF status es GENERATED (REQ-5.3)
- [ ] Crear listado de PDFs generados en sección Reportes del Admin (REQ-5.2)
- [ ] Implementar botón de regeneración de PDF por empresa (REQ-3.4)
- [ ] Mostrar estado del PDF (PROCESSING / GENERATED / FAILED) en la UI (REQ-1.3, REQ-1.4, REQ-1.5)

## DevOps

- [ ] Configurar Lambda para procesamiento asíncrono de PDF (REQ-1.2)
- [ ] Configurar S3 bucket para almacenamiento de PDFs (REQ-4.1)
- [ ] Configurar políticas IAM para acceso a S3 (REQ-4.3)
- [ ] Configurar expiración de presigned URL como variable de entorno (REQ-5.5)
- [ ] Configurar template de email SES para entrega del PDF (REQ-6.1, REQ-6.2)
