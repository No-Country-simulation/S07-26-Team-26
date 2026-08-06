# 06 — API Specification

## Project Ghost Load

**Base URL:** `https://api.ghostload.com/api/v1`  
**Autenticación Admin:** Bearer JWT en header `Authorization`  
**Autenticación Operator:** `X-Evaluation-Token` en header  
**Formato:** JSON  
**Documentación interactiva:** `/swagger-ui.html` (Springdoc OpenAPI)

---

## Convenciones

### Roles
| Rol             | Descripción                              |
|-----------------|------------------------------------------|
| `ADMIN`         | Admin — acceso total al sistema          |
| `X-Evaluation-Token` | Operator — scoped a su evaluación    |

### Códigos de respuesta estándar
| Código | Descripción                              |
|--------|------------------------------------------|
| 200    | OK                                       |
| 201    | Creado exitosamente                      |
| 204    | Sin contenido                            |
| 400    | Bad Request — validación fallida         |
| 401    | Unauthorized — token inválido o expirado |
| 403    | Forbidden — rol insuficiente             |
| 404    | Not Found                                |
| 409    | Conflict — duplicado                     |
| 422    | Unprocessable Entity — error de negocio  |
| 500    | Internal Server Error                    |

---

## Módulo 1 — Administration (Auth)

### POST /admin/auth/login
Autentica un Admin y retorna JWT.

**Acceso:** PUBLIC

**Request:**
```json
{
  "email": "admin@ghostload.local",
  "password": "secreto123"
}
```

**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "role": "ADMIN",
  "adminId": "uuid-1234",
  "email": "admin@ghostload.local",
  "expiresIn": 86400
}
```

---

## Módulo 2 — Assessment (Evaluaciones)

### POST /evaluations
Crea una nueva evaluación. Retorna un token único para el operador.

**Acceso:** PUBLIC

**Request:**
```json
{
  "email": "operator@dcandina.com",
  "companyName": "DC Andina S.A.",
  "position": "Data Center Manager"
}
```

**Response 201:**
```json
{
  "evaluationId": "uuid-eval-001",
  "token": "tok_abc123def456",
  "expiresIn": 604800,
  "state": "STARTED",
  "createdAt": "2026-07-23T21:00:00Z"
}
```

---

### PUT /evaluations/{evaluationId}/calculator
Guarda los datos de la calculadora de capacidad.

**Acceso:** X-Evaluation-Token

**Request:**
```json
{
  "totalCapacityMw": 20.0,
  "productiveCapacityMw": 13.0,
  "monthlyCostPerKw": 90.0,
  "currency": "USD"
}
```

**Response 200:**
```json
{
  "evaluationId": "uuid-eval-001",
  "state": "CALCULATOR_COMPLETED",
  "calculatorResult": {
    "totalCapacityMw": 20.0,
    "productiveCapacityMw": 13.0,
    "nonProductiveCapacityMw": 7.0,
    "utilizationPct": 65.0,
    "nonProductivePct": 35.0,
    "monthlyCostPerKw": 90.0,
    "currency": "USD",
    "estimatedAnnualCost": 7560000.0
  }
}
```

---

### GET /benchmark/questions
Obtiene el cuestionario de benchmark completo.

**Acceso:** X-Evaluation-Token

**Response 200:**
```json
{
  "questions": [
    {
      "questionId": "CV-01",
      "module": "CAPACITY_VISIBILITY",
      "text": "¿Tiene visibilidad en tiempo real de la capacidad instalada?",
      "type": "SCALE",
      "required": true
    }
  ],
  "totalQuestions": 20,
  "modules": [
    "CAPACITY_VISIBILITY",
    "OPERATIONAL_COORDINATION",
    "AUTOMATION",
    "GOVERNANCE",
    "CONTINUOUS_IMPROVEMENT"
  ]
}
```

---

### PUT /evaluations/{evaluationId}/benchmark
Envía las respuestas del benchmark y obtiene resultados.

**Acceso:** X-Evaluation-Token

**Request:**
```json
{
  "responses": [
    { "questionId": "CV-01", "value": 4 },
    { "questionId": "CV-02", "value": 3 }
  ]
}
```

**Response 200:**
```json
{
  "evaluationId": "uuid-eval-001",
  "state": "BENCHMARK_COMPLETED",
  "result": {
    "score": 72.0,
    "percentile": 72,
    "maturityLevel": "MANAGED",
    "moduleScores": [
      { "module": "CAPACITY_VISIBILITY", "score": 68.0 },
      { "module": "OPERATIONAL_COORDINATION", "score": 75.0 },
      { "module": "AUTOMATION", "score": 70.0 },
      { "module": "GOVERNANCE", "score": 65.0 },
      { "module": "CONTINUOUS_IMPROVEMENT", "score": 82.0 }
    ]
  },
  "completedAt": "2026-07-23T21:05:00Z"
}
```

---

## Módulo 3 — Outreach (Campañas)

### POST /admin/contacts/import
Importa contactos desde un archivo CSV.

**Acceso:** ADMIN  
**Content-Type:** `multipart/form-data`

**Request:**
```
file: contacts.csv (max 5MB)
```

**Columnas esperadas del CSV:**
```
first_name, last_name, email, company, position
```

**Response 200:**
```json
{
  "importId": "uuid-import-001",
  "totalRows": 50,
  "imported": 47,
  "failed": 3,
  "errors": [
    {
      "row": 5,
      "field": "email",
      "message": "Email duplicado: test@test.com"
    }
  ]
}
```

---

### POST /admin/campaigns
Crea una nueva campaña desde contactos existentes.

**Acceso:** ADMIN

**Request:**
```json
{
  "name": "Campaña DC Latinoamérica",
  "contactIds": ["uuid-contact-001", "uuid-contact-002"]
}
```

**Response 201:**
```json
{
  "campaignId": "uuid-campaign-001",
  "name": "Campaña DC Latinoamérica",
  "contactCount": 2,
  "createdAt": "2026-07-23T21:00:00Z"
}
```

---

### POST /admin/campaigns/{campaignId}/send
Envía las invitaciones de una campaña.

**Acceso:** ADMIN

**Response 202:**
```json
{
  "campaignId": "uuid-campaign-001",
  "status": "SENDING",
  "totalInvitations": 47,
  "queuedAt": "2026-07-23T21:00:00Z"
}
```

---

## Módulo 4 — Reportes (futuro)

### GET /reports/{companyId}
[future] Retorna el PDF generado para una evaluación.

**Acceso:** ADMIN / X-Evaluation-Token (propia)

### POST /reports/{companyId}/regenerate
[future] Regenera el PDF de una evaluación.

**Acceso:** ADMIN

### GET /reports
[future] Lista todos los PDFs generados.

**Acceso:** ADMIN

---

## Módulo 5 — Dashboard

### GET /api/v1/admin/dashboard/summary
KPIs globales del sistema: operadores totales, benchmarks completados, score promedio.

**Acceso:** ADMIN

### GET /api/v1/admin/dashboard/recent-responses
Últimas respuestas del benchmark.

**Acceso:** ADMIN

---

## Resumen de Endpoints (MVP)

| Método | Endpoint                            | Módulo    | Auth              |
|--------|-------------------------------------|-----------|-------------------|
| POST   | /api/v1/admin/auth/login            | Admin     | PUBLIC            |
| POST   | /api/v1/evaluations                 | Assessment| PUBLIC            |
| PUT    | /api/v1/evaluations/{id}/calculator | Assessment| X-Evaluation-Token|
| GET    | /api/v1/benchmark/questions         | Assessment| X-Evaluation-Token|
| PUT    | /api/v1/evaluations/{id}/benchmark  | Assessment| X-Evaluation-Token|
| POST   | /api/v1/admin/contacts/import       | Outreach  | ADMIN             |
| POST   | /api/v1/admin/campaigns             | Outreach  | ADMIN             |
| POST   | /api/v1/admin/campaigns/{id}/send   | Outreach  | ADMIN             |
| GET    | /api/v1/admin/dashboard/summary     | Admin     | ADMIN             |
| GET    | /api/v1/admin/dashboard/recent-responses | Admin | ADMIN          |

## Resumen de Endpoints (Futuro)

| Método | Endpoint                            | Módulo    | Auth              |
|--------|-------------------------------------|-----------|-------------------|
| GET    | /api/v1/reports/{companyId}         | Reporting | ADMIN / Token     |
| POST   | /api/v1/reports/{companyId}/regenerate | Reporting | ADMIN           |
| GET    | /api/v1/reports                     | Reporting | ADMIN             |

---

## Dependencias actuales del pom.xml

```xml
<!-- Spring Boot Starter Web -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Boot Starter Data JPA -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- PostgreSQL Driver -->
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <scope>runtime</scope>
</dependency>

<!-- Flyway -->
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-database-postgresql</artifactId>
</dependency>

<!-- Spring Boot Starter Validation -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

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

<!-- Apache Commons CSV -->
<dependency>
  <groupId>org.apache.commons</groupId>
  <artifactId>commons-csv</artifactId>
  <version>1.11.0</version>
</dependency>

<!-- Spring Boot Starter Mail -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<!-- Springdoc OpenAPI -->
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.8.9</version>
</dependency>

<!-- Spring Boot Starter Test -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-test</artifactId>
  <scope>test</scope>
</dependency>
```

Nota: Redis, MapStruct, AWS SDK y librerías PDF no están incluidas en el MVP actual.
