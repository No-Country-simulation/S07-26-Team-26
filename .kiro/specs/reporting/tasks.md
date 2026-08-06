# Tasks — Reporting

## Status Legend
- [x] Completado
- [-] En progreso
- [ ] Pendiente
- [future] Fuera de MVP (visión futura)

---

## Backend

- [future] Elegir librería de generación (iText, PDFBox, Flying Saucer + Thymeleaf)
- [future] Diseñar template institucional del PDF
- [future] Crear tabla `generated_reports`
- [future] Implementar `ReportData` — ensamblar datos de benchmark + KPI
- [future] Implementar `GeneratePdfPort` — adapter de generación
- [future] Implementar `StoreReportPort` — filesystem local para MVP
- [future] Implementar trigger automático post-benchmark
- [future] Implementar endpoint GET /api/v1/reports/{companyId}
- [future] Implementar endpoint POST /api/v1/reports/{companyId}/regenerate
- [future] Implementar cambio de estado a REPORT_COMPLETED al generar
- [future] Implementar generación idempotente
- [future] Implementar descarga del PDF
- [future] Implementar envío por email
- [future] Implementar endpoint GET /api/v1/reports (listado admin)

### Tests Backend
- [future] Test: generación automática al completar benchmark
- [future] Test: estado PROCESSING → GENERATED / FAILED
- [future] Test: doble trigger no duplica registros
- [future] Test: operator solo ve su PDF, admin ve todos
- [future] Test: PDF no disponible si status ≠ GENERATED

## Frontend
- [future] Crear botón de descarga de PDF en resultados del operador
- [future] Crear listado de PDFs en sección Reportes del admin
- [future] Mostrar estado del PDF (PROCESSING / GENERATED / FAILED)

## DevOps
- [future] Configurar template de email para entrega de PDF
