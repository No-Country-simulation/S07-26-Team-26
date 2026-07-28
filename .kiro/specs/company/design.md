# Design — Company

## Architecture
Bounded Context dentro de la arquitectura DDD + Hexagonal.

## Domain Model

### Entities
- `Company` — Aggregate Root
  - id: UUID
  - name: String
  - industry: String
  - country: String
  - adminId: UUID
  - founderName: String
  - founderEmail: String
  - status: CompanyStatus
  - notes: String

- `Operator` — referencia al User con ROLE_OPERATOR
- `Founder` — Value Object con name y email

### Value Objects
- `CompanyStatus` — REGISTERED | INVITED | IN_PROGRESS | COMPLETED | PDF_GENERATED | OUTREACH_PENDING | OUTREACH_SENT | MEETING_SCHEDULED | CONVERTED | LOST
- `CompanyName`, `Industry`, `Country`

## Ports

### In (Use Cases)
- `CompanyUseCase`
  - registerCompany(dto) → Company
  - getCompanies(filters) → Page<Company>
  - getCompanyById(id) → Company
  - exportCompanies() → CSV
- `ImportUseCase`
  - importFromCSV(file) → ImportResult

### Out (Infrastructure)
- `CompanyRepository`
- `OperatorRepository`
- `EmailService` — invitación al Operator
- `StorageService` — reporte de errores CSV en S3

## Adapters

### In
- `CompanyController` — `/companies/**`

### Out
- `CompanyJpaAdapter` — PostgreSQL
- `SESInvitationAdapter` — Amazon SES
- `S3StorageAdapter` — reporte de errores

## API Endpoints
- POST /companies
- POST /companies/import
- GET  /companies
- GET  /companies/{companyId}
- GET  /companies/export

## Flujo de importación CSV
```
Upload CSV → Parse → Validate rows → Batch insert → Async email send → Return ImportResult
```

## Database
Tablas: `companies`, `outreach_status_history`
