# Design — Reporting

## Architecture
Bounded Context `reporting` dentro de la arquitectura DDD + Hexagonal.

## Domain Model

### Entities
- `GeneratedReport` — Aggregate Root
  - id, companyId, benchmarkId, status (PROCESSING | GENERATED | FAILED)
  - fileReference, generatedAt, attemptCount

### Value Objects
- `ReportStatus`, `ReportData`, `FileReference`

## Ports

### In (Use Cases)
- `GenerateReportUseCase` — trigger de generación
- `GetReportQuery` — obtener estado y URL de descarga
- `RegenerateReportUseCase` — admin puede regenerar

### Out (Infrastructure)
- `GeneratePdfPort` — genera el PDF desde ReportData
- `StoreReportPort` — guarda el archivo (S3 o filesystem)
- `SendEmailPort` — envía el PDF al operador

## Adapters

### In
- *(futuro)* ReportController — GET /api/v1/reports/{companyId}

### Out
- *(futuro)* `OpenHtmlToPdfAdapter` — generación con Thymeleaf + Flying Saucer
- *(futuro)* `S3ReportStorageAdapter` — S3 o filesystem local
- *(futuro)* `SesEmailAdapter` — email transaccional

## API Endpoints
- *(futuro)* GET /api/v1/reports/{companyId}
- *(futuro)* POST /api/v1/reports/{companyId}/regenerate

## Database
- *(pendiente)* Tabla `generated_reports`

## Nota MVP
- La generación de PDF no está implementada en backend ni frontend.
- El estado `REPORT_GENERATING`/`REPORT_COMPLETED`/`REPORT_FAILED` existe en `EvaluationState` pero nunca se transiciona.
- No hay controlador, servicio ni adaptador de PDF.
