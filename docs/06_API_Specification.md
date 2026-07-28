# 06 — API Specification

## Project Ghost Load

**Base URL:** `https://api.ghostload.com/v1`  
**Autenticación:** Bearer JWT en header `Authorization`  
**Formato:** JSON  
**Documentación interactiva:** `/swagger-ui.html` (Springdoc OpenAPI)

---

## Convenciones

### Roles
| Rol             | Descripción                              |
|-----------------|------------------------------------------|
| `ROLE_ADMIN`    | Admin — acceso total al sistema          |
| `ROLE_OPERATOR` | Operator — scoped a su empresa           |
| `PUBLIC`        | Sin autenticación requerida              |

### Códigos de respuesta estándar
| Código | Descripción                              |
|--------|------------------------------------------|
| 200    | OK                                       |
| 201    | Creado exitosamente                      |
| 204    | Sin contenido (delete, logout)           |
| 400    | Bad Request — validación fallida         |
| 401    | Unauthorized — token inválido o expirado |
| 403    | Forbidden — rol insuficiente             |
| 404    | Not Found                                |
| 409    | Conflict — duplicado                     |
| 422    | Unprocessable Entity — error de negocio  |
| 500    | Internal Server Error                    |

### Estructura de error estándar
```json
{
  "timestamp": "2026-07-23T21:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "El campo email es requerido",
  "path": "/v1/auth/login"
}
```

### Paginación estándar
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8
}
```

---

## Módulo 1 — Identity (Auth)

### POST /auth/login
Autentica un usuario y retorna JWT.

**Acceso:** PUBLIC

**Request:**
```json
{
  "email": "admin@ghostload.com",
  "password": "secreto123"
}
```

**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "role": "ROLE_ADMIN",
  "userId": "uuid-1234",
  "email": "admin@ghostload.com",
  "expiresIn": 86400
}
```

---

### POST /auth/logout
Invalida el JWT del usuario autenticado.

**Acceso:** Todos los roles autenticados

**Response:** 204 No Content

---

### GET /auth/me
Retorna el perfil del usuario autenticado.

**Acceso:** Todos los roles autenticados

**Response 200:**
```json
{
  "userId": "uuid-1234",
  "email": "admin@ghostload.com",
  "role": "ROLE_ADMIN",
  "createdAt": "2026-07-01T10:00:00Z"
}
```

---

### POST /auth/refresh
Renueva el JWT antes de su expiración.

**Acceso:** Todos los roles autenticados

**Request:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 86400
}
```

---

## Módulo 2 — Users (Gestión de usuarios)

### POST /users
Crea un nuevo usuario (Admin u Operator).

**Acceso:** `ROLE_ADMIN`

**Request:**
```json
{
  "email": "operator@empresa.com",
  "password": "temporal123",
  "name": "Carlos Operador",
  "role": "ROLE_OPERATOR",
  "companyId": "uuid-company-001"
}
```

**Response 201:**
```json
{
  "userId": "uuid-5678",
  "email": "operator@empresa.com",
  "role": "ROLE_OPERATOR",
  "createdAt": "2026-07-23T21:00:00Z"
}
```

---

### GET /users
Lista todos los usuarios del sistema.

**Acceso:** `ROLE_ADMIN`

**Query params:**
- `role` — filtro por rol (ROLE_ADMIN, ROLE_OPERATOR)
- `status` — filtro por estado (ACTIVE, SUSPENDED)
- `page`, `size`

**Response 200:**
```json
{
  "content": [
    {
      "userId": "uuid-5678",
      "email": "operator@empresa.com",
      "name": "Carlos Operador",
      "role": "ROLE_OPERATOR",
      "status": "ACTIVE",
      "companyId": "uuid-company-001",
      "createdAt": "2026-07-01T10:00:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1248,
  "totalPages": 63
}
```

---

### PATCH /users/{userId}/status
Activa o suspende un usuario.

**Acceso:** `ROLE_ADMIN`

**Request:**
```json
{
  "status": "SUSPENDED"
}
```

**Response 200:**
```json
{
  "userId": "uuid-5678",
  "status": "SUSPENDED"
}
```

---

## Módulo 3 — Companies (Empresas)

### POST /companies
Registra una empresa manualmente.

**Acceso:** `ROLE_ADMIN`

**Request:**
```json
{
  "name": "DC Andina S.A.",
  "industry": "Colocation",
  "country": "Argentina",
  "operatorEmail": "ops@dcandina.com",
  "founderName": "Juan Pérez",
  "founderEmail": "juan@dcandina.com",
  "notes": ""
}
```

**Response 201:**
```json
{
  "companyId": "uuid-company-001",
  "name": "DC Andina S.A.",
  "status": "REGISTERED",
  "operatorId": "uuid-operator-001",
  "createdAt": "2026-07-23T21:00:00Z"
}
```

---

### POST /companies/import
Carga masiva de empresas mediante CSV.

**Acceso:** `ROLE_ADMIN`  
**Content-Type:** `multipart/form-data`

**Request:**
```
file: contacts.csv (max 5MB)
```

**Columnas esperadas del CSV:**
```
name, email, company
```

**Response 200:**
```json
{
  "imported": 47,
  "failed": 3,
  "errors": [
    {
      "row": 5,
      "field": "email",
      "message": "Email duplicado: ops@empresa.com"
    }
  ],
  "errorReportUrl": "https://s3.amazonaws.com/ghostload/reports/import-errors-uuid.csv"
}
```

---

### GET /companies
Lista todas las empresas.

**Acceso:** `ROLE_ADMIN`

**Query params:**
- `status` — filtro por estado del pipeline
- `region` — filtro por región
- `page`, `size`

**Response 200:**
```json
{
  "content": [
    {
      "companyId": "uuid-company-001",
      "name": "DC Andina S.A.",
      "industry": "Colocation",
      "country": "Argentina",
      "status": "PDF_GENERATED",
      "operatorEmail": "ops@dcandina.com",
      "score": 78,
      "percentile": 88,
      "maturityLevel": "COORDINATED",
      "createdAt": "2026-07-01T10:00:00Z",
      "updatedAt": "2026-07-20T15:30:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 476,
  "totalPages": 24
}
```

---

### GET /companies/{companyId}
Detalle de una empresa.

**Acceso:** `ROLE_ADMIN`

**Response 200:**
```json
{
  "companyId": "uuid-company-001",
  "name": "DC Andina S.A.",
  "industry": "Colocation",
  "country": "Argentina",
  "status": "PDF_GENERATED",
  "operator": {
    "operatorId": "uuid-operator-001",
    "email": "ops@dcandina.com",
    "name": "Carlos Operador"
  },
  "founder": {
    "name": "Juan Pérez",
    "email": "juan@dcandina.com"
  },
  "benchmarkResult": {
    "score": 78,
    "percentile": 88,
    "maturityLevel": "COORDINATED",
    "wasteIndex": 22.5,
    "completedAt": "2026-07-20T15:00:00Z"
  },
  "pdfUrl": "https://s3.amazonaws.com/ghostload/pdfs/uuid-company-001.pdf"
}
```

---

### GET /companies/export
Exporta el pipeline actual a CSV.

**Acceso:** `ROLE_ADMIN`  
**Response:** `text/csv` — archivo descargable

---

## Módulo 4 — Benchmark

### GET /benchmark/questions
Obtiene el cuestionario completo.

**Acceso:** `ROLE_OPERATOR`

**Response 200:**
```json
{
  "benchmarkId": "uuid-benchmark-001",
  "sections": [
    {
      "sectionId": "section-01",
      "title": "Capacidad Instalada",
      "questions": [
        {
          "questionId": "q-001",
          "text": "¿Cuál es la capacidad total instalada en kW?",
          "type": "NUMBER",
          "required": true
        }
      ]
    }
  ]
}
```

---

### POST /benchmark/responses
Guarda o actualiza respuestas del cuestionario (guardado parcial).

**Acceso:** `ROLE_OPERATOR`

**Request:**
```json
{
  "benchmarkId": "uuid-benchmark-001",
  "responses": [
    { "questionId": "q-001", "value": "500" },
    { "questionId": "q-002", "value": "75" }
  ]
}
```

**Response 200:**
```json
{
  "benchmarkId": "uuid-benchmark-001",
  "status": "IN_PROGRESS",
  "completionPercentage": 45,
  "savedAt": "2026-07-23T21:00:00Z"
}
```

---

### POST /benchmark/submit
Finaliza y envía el benchmark completo.

**Acceso:** `ROLE_OPERATOR`

**Request:**
```json
{
  "benchmarkId": "uuid-benchmark-001"
}
```

**Response 200:**
```json
{
  "benchmarkId": "uuid-benchmark-001",
  "status": "COMPLETED",
  "score": 78.0,
  "percentile": 88,
  "maturityLevel": "COORDINATED",
  "kpis": {
    "totalCapacityKw": 500,
    "usedCapacityKw": 320,
    "wastedCapacityKw": 180,
    "wasteIndex": 36.0,
    "pueRatio": 1.8,
    "industryBenchmark": 22.5
  },
  "pdfGenerationStatus": "PROCESSING",
  "completedAt": "2026-07-23T21:05:00Z"
}
```

---

### GET /benchmark/results
Retorna los resultados y KPIs del benchmark del operador.

**Acceso:** `ROLE_OPERATOR`

**Response 200:**
```json
{
  "benchmarkId": "uuid-benchmark-001",
  "status": "COMPLETED",
  "score": 78.0,
  "percentile": 88,
  "maturityLevel": "COORDINATED",
  "kpis": {
    "totalCapacityKw": 500,
    "usedCapacityKw": 320,
    "wastedCapacityKw": 180,
    "wasteIndex": 36.0,
    "pueRatio": 1.8,
    "industryBenchmark": 22.5
  },
  "completedAt": "2026-07-23T21:05:00Z"
}
```

---

### GET /benchmark/analysis
Análisis agregado de todas las respuestas del benchmark.

**Acceso:** `ROLE_ADMIN`

**Response 200:**
```json
{
  "totalResponses": 476,
  "averageScore": 62.7,
  "averagePercentile": 68,
  "byMaturityLevel": {
    "ORCHESTRATED": { "count": 110, "percentage": 23 },
    "COORDINATED": { "count": 200, "percentage": 42 },
    "REACTIVE": { "count": 114, "percentage": 24 },
    "FRAGMENTED": { "count": 52, "percentage": 11 }
  },
  "scoreDistribution": [
    { "range": "0-10", "count": 5 },
    { "range": "11-20", "count": 12 },
    { "range": "21-30", "count": 28 },
    { "range": "31-40", "count": 45 },
    { "range": "41-50", "count": 78 },
    { "range": "51-60", "count": 120 },
    { "range": "61-70", "count": 168 },
    { "range": "71-80", "count": 154 },
    { "range": "81-90", "count": 82 },
    { "range": "91-100", "count": 24 }
  ]
}
```

---

### GET /benchmark/percentiles
Distribución de percentiles del sistema.

**Acceso:** `ROLE_ADMIN`

---

## Módulo 5 — PDF

### GET /pdf/{companyId}
Retorna el PDF generado para una empresa.

**Acceso:** `ROLE_OPERATOR` (su empresa), `ROLE_ADMIN`

**Response 200:**
```json
{
  "pdfId": "uuid-pdf-001",
  "companyId": "uuid-company-001",
  "status": "GENERATED",
  "downloadUrl": "https://s3.amazonaws.com/ghostload/pdfs/uuid-company-001.pdf",
  "generatedAt": "2026-07-23T21:10:00Z",
  "expiresAt": "2026-07-24T21:10:00Z"
}
```

---

### POST /pdf/{companyId}/regenerate
Regenera el PDF de una empresa.

**Acceso:** `ROLE_ADMIN`

**Response 202:**
```json
{
  "pdfId": "uuid-pdf-002",
  "status": "PROCESSING",
  "message": "El PDF está siendo regenerado"
}
```

---

### GET /pdf
Lista todos los PDFs generados.

**Acceso:** `ROLE_ADMIN`

**Query params:** `page`, `size`, `status`

---

## Módulo 6 — Outreach

### GET /outreach/pipeline
Lista el pipeline de outreach completo.

**Acceso:** `ROLE_ADMIN`

**Query params:**
- `status` — filtro por estado
- `page`, `size`

**Response 200:**
```json
{
  "content": [
    {
      "companyId": "uuid-company-001",
      "companyName": "DC Andina S.A.",
      "founderName": "Juan Pérez",
      "founderEmail": "juan@dcandina.com",
      "status": "PDF_GENERATED",
      "score": 78,
      "maturityLevel": "COORDINATED",
      "pdfUrl": "https://s3.amazonaws.com/ghostload/pdfs/uuid-company-001.pdf",
      "lastUpdated": "2026-07-20T15:30:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 476,
  "totalPages": 24
}
```

---

### PATCH /outreach/{companyId}/status
Actualiza el estado de outreach de una empresa.

**Acceso:** `ROLE_ADMIN`

**Request:**
```json
{
  "status": "OUTREACH_SENT",
  "notes": "Envié email al founder el 23/07"
}
```

**Response 200:**
```json
{
  "companyId": "uuid-company-001",
  "status": "OUTREACH_SENT",
  "updatedAt": "2026-07-23T21:15:00Z"
}
```

---

### POST /outreach/{companyId}/notes
Agrega una nota de seguimiento.

**Acceso:** `ROLE_ADMIN`

**Request:**
```json
{
  "note": "Founder muy interesado, llamada agendada para el viernes"
}
```

**Response 201:**
```json
{
  "noteId": "uuid-note-001",
  "companyId": "uuid-company-001",
  "note": "Founder muy interesado, llamada agendada para el viernes",
  "createdBy": "admin@ghostload.com",
  "createdAt": "2026-07-23T21:20:00Z"
}
```

---

### GET /outreach/{companyId}/history
Historial de cambios de estado.

**Acceso:** `ROLE_ADMIN`

**Response 200:**
```json
[
  {
    "status": "REGISTERED",
    "changedAt": "2026-07-01T10:00:00Z",
    "changedBy": "admin@ghostload.com"
  },
  {
    "status": "PDF_GENERATED",
    "changedAt": "2026-07-20T15:10:00Z",
    "changedBy": "system"
  }
]
```

---

## Módulo 7 — Dashboard

### GET /dashboard/metrics
Métricas globales del sistema con KPIs del dashboard.

**Acceso:** `ROLE_ADMIN`

**Query params:**
- `from` — fecha inicio (ISO 8601)
- `to` — fecha fin (ISO 8601)

**Response 200:**
```json
{
  "period": {
    "from": "2025-05-01",
    "to": "2025-05-31"
  },
  "kpis": {
    "totalOperators": 1248,
    "totalOperatorsGrowth": 18.6,
    "benchmarksCompleted": 476,
    "benchmarksGrowth": 24.3,
    "averageScore": 62.7,
    "averageScoreGrowth": 5.7,
    "averagePercentile": 68,
    "averagePercentileGrowth": 7.3,
    "pdfsGenerated": 392,
    "pdfsGrowth": 30.2,
    "meetingsScheduled": 34,
    "meetingsGrowth": 21.4
  },
  "scoreEvolution": [
    { "month": "2024-12", "avgScore": 54.2 },
    { "month": "2025-01", "avgScore": 56.1 },
    { "month": "2025-02", "avgScore": 57.8 },
    { "month": "2025-03", "avgScore": 59.3 },
    { "month": "2025-04", "avgScore": 60.9 },
    { "month": "2025-05", "avgScore": 62.7 }
  ],
  "scoreByRegion": [
    { "region": "Todos los operadores", "avgScore": 62.7 },
    { "region": "Norteamérica", "avgScore": 65.3 },
    { "region": "Latinoamérica", "avgScore": 60.8 },
    { "region": "Europa", "avgScore": 64.1 },
    { "region": "Asia Pacífico", "avgScore": 58.9 }
  ],
  "maturityDistribution": {
    "ORCHESTRATED": { "count": 110, "percentage": 23 },
    "COORDINATED": { "count": 200, "percentage": 42 },
    "REACTIVE": { "count": 114, "percentage": 24 },
    "FRAGMENTED": { "count": 52, "percentage": 11 }
  },
  "conversionFunnel": [
    { "stage": "unique_visitors", "count": 12840 },
    { "stage": "calculator_started", "count": 4682, "conversionRate": 36.5 },
    { "stage": "email_captured", "count": 2988, "conversionRate": 63.8 },
    { "stage": "benchmark_completed", "count": 476, "conversionRate": 15.9 },
    { "stage": "pdf_downloaded", "count": 392, "conversionRate": 82.4 },
    { "stage": "meeting_scheduled", "count": 34, "conversionRate": 8.7 }
  ]
}
```

---

### GET /dashboard/recent-responses
Últimas respuestas del benchmark para la tabla del dashboard.

**Acceso:** `ROLE_ADMIN`

**Query params:** `page`, `size` (default: 10)

**Response 200:**
```json
{
  "content": [
    {
      "operatorName": "Carlos Méndez",
      "companyName": "DataCenter Norte",
      "region": "México",
      "score": 78,
      "percentile": 88,
      "maturityLevel": "ORCHESTRATED",
      "completedAt": "2025-05-29T10:24:00Z",
      "pdfAvailable": true,
      "companyId": "uuid-company-001"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 476
}
```

---

## Resumen de Endpoints

| Método | Endpoint                            | Módulo    | Rol requerido    |
|--------|-------------------------------------|-----------|------------------|
| POST   | /auth/login                         | Identity  | PUBLIC           |
| POST   | /auth/logout                        | Identity  | Autenticado      |
| GET    | /auth/me                            | Identity  | Autenticado      |
| POST   | /auth/refresh                       | Identity  | Autenticado      |
| POST   | /users                              | Users     | ROLE_ADMIN       |
| GET    | /users                              | Users     | ROLE_ADMIN       |
| PATCH  | /users/{userId}/status              | Users     | ROLE_ADMIN       |
| POST   | /companies                          | Companies | ROLE_ADMIN       |
| POST   | /companies/import                   | Companies | ROLE_ADMIN       |
| GET    | /companies                          | Companies | ROLE_ADMIN       |
| GET    | /companies/{companyId}              | Companies | ROLE_ADMIN       |
| GET    | /companies/export                   | Companies | ROLE_ADMIN       |
| GET    | /benchmark/questions                | Benchmark | ROLE_OPERATOR    |
| POST   | /benchmark/responses                | Benchmark | ROLE_OPERATOR    |
| POST   | /benchmark/submit                   | Benchmark | ROLE_OPERATOR    |
| GET    | /benchmark/results                  | Benchmark | ROLE_OPERATOR    |
| GET    | /benchmark/analysis                 | Benchmark | ROLE_ADMIN       |
| GET    | /benchmark/percentiles              | Benchmark | ROLE_ADMIN       |
| GET    | /pdf/{companyId}                    | PDF       | ROLE_OPERATOR, ROLE_ADMIN |
| POST   | /pdf/{companyId}/regenerate         | PDF       | ROLE_ADMIN       |
| GET    | /pdf                                | PDF       | ROLE_ADMIN       |
| GET    | /outreach/pipeline                  | Outreach  | ROLE_ADMIN       |
| PATCH  | /outreach/{companyId}/status        | Outreach  | ROLE_ADMIN       |
| POST   | /outreach/{companyId}/notes         | Outreach  | ROLE_ADMIN       |
| GET    | /outreach/{companyId}/history       | Outreach  | ROLE_ADMIN       |
| GET    | /dashboard/metrics                  | Dashboard | ROLE_ADMIN       |
| GET    | /dashboard/recent-responses         | Dashboard | ROLE_ADMIN       |

---

## Dependencias a agregar al pom.xml

```xml
<!-- Spring Security + JWT -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-api</artifactId>
  <version>0.12.6</version>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-impl</artifactId>
  <version>0.12.6</version>
  <scope>runtime</scope>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-jackson</artifactId>
  <version>0.12.6</version>
  <scope>runtime</scope>
</dependency>

<!-- OpenAPI / Swagger -->
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.8.9</version>
</dependency>

<!-- MapStruct (DTOs) -->
<dependency>
  <groupId>org.mapstruct</groupId>
  <artifactId>mapstruct</artifactId>
  <version>1.6.3</version>
</dependency>

<!-- Redis Cache -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```
