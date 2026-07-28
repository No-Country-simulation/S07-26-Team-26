# Design — PDF Generator

## Architecture
Bounded Context dentro de la arquitectura DDD + Hexagonal.
La generación es asíncrona via AWS Lambda.

## Domain Model

### Entities
- `GeneratedPDF` — Aggregate Root
  - id: UUID
  - companyId: UUID
  - benchmarkId: UUID
  - status: PDFStatus (PROCESSING | GENERATED | FAILED)
  - s3Key: String
  - downloadUrl: String (presigned)
  - generatedAt: Timestamp

### Value Objects
- `PDFStatus` — PROCESSING | GENERATED | FAILED
- `ReportTemplate` — template del PDF institucional
- `ReportData` — datos ensamblados para el PDF

## Ports

### In (Use Cases)
- `PDFUseCase`
  - generatePDF(companyId, benchmarkId) → PDFJob
  - getPDF(companyId) → GeneratedPDF
  - regeneratePDF(companyId) → PDFJob

### Out (Infrastructure)
- `PDFGenerator` — librería de generación (iText / Apache PDFBox)
- `StorageService` — Amazon S3
- `EmailService` — Amazon SES
- `PDFRepository` — persistencia

## Adapters

### In
- `PDFController` — `/pdf/**`

### Out
- `PDFGeneratorAdapter` — iText o Apache PDFBox
- `S3StorageAdapter` — upload y presigned URL
- `SESEmailAdapter` — envío del PDF al Operator
- `PDFJpaAdapter` — PostgreSQL

## Flujo asíncrono
```
Benchmark completado
      │
      ▼
Trigger AWS Lambda (PDF Job)
      │
      ▼
Ensamblar ReportData (benchmark + kpis + company)
      │
      ▼
Generar PDF (PDFGeneratorAdapter)
      │
      ▼
Upload a S3 (S3StorageAdapter)
      │
      ▼
Guardar s3Key en DB + estado GENERATED
      │
      ▼
Enviar email al Operator (SESEmailAdapter)
      │
      ▼
Cambiar estado empresa a PDF_GENERATED
```

## API Endpoints
- GET  /pdf/{companyId}
- POST /pdf/{companyId}/regenerate
- GET  /pdf (listado Admin)

## Database
Tabla: `generated_pdfs`
