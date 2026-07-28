# 05 — Database Design

## Project Ghost Load

**Motor:** PostgreSQL  
**Hosting:** Amazon RDS (decisión final pendiente vs Supabase)  
**ORM:** Spring Data JPA / Hibernate  
**Arquitectura:** DDD — cada Bounded Context gestiona sus propias tablas

---

## Diagrama de Entidades

```
users
  │
  ├──< companies (admin_id → users.id)
  │       │
  │       ├──< benchmark_results (company_id)
  │       │         │
  │       │         └──< benchmark_responses (benchmark_id)
  │       │
  │       ├──< generated_pdfs (company_id)
  │       │
  │       └──< outreach_campaigns (company_id)
  │                 │
  │                 └──< outreach_notes (campaign_id)
  │
  └──< outreach_contacts (company_id)
```

---

## Tablas

---

### users

| Columna      | Tipo           | Restricciones              | Descripción                          |
|--------------|----------------|----------------------------|--------------------------------------|
| id           | UUID           | PK, NOT NULL               | Identificador único                  |
| email        | VARCHAR(255)   | UNIQUE, NOT NULL           | Email del usuario                    |
| password     | VARCHAR(255)   | NOT NULL                   | Hash bcrypt                          |
| name         | VARCHAR(255)   | NOT NULL                   | Nombre completo                      |
| role         | VARCHAR(50)    | NOT NULL                   | ROLE_ADMIN / ROLE_OPERATOR           |
| company_id   | UUID           | FK → companies.id, NULLABLE| Solo para ROLE_OPERATOR              |
| status       | VARCHAR(50)    | NOT NULL, DEFAULT 'ACTIVE' | ACTIVE / SUSPENDED                   |
| created_at   | TIMESTAMP      | NOT NULL                   | Fecha de creación                    |
| updated_at   | TIMESTAMP      | NOT NULL                   | Última actualización                 |

```sql
CREATE TABLE users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    role        VARCHAR(50)  NOT NULL CHECK (role IN ('ROLE_ADMIN', 'ROLE_OPERATOR')),
    company_id  UUID REFERENCES companies(id) ON DELETE SET NULL,
    status      VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);
```

---

### companies

| Columna       | Tipo         | Restricciones              | Descripción                          |
|---------------|--------------|----------------------------|--------------------------------------|
| id            | UUID         | PK, NOT NULL               | Identificador único                  |
| name          | VARCHAR(255) | NOT NULL                   | Nombre de la empresa                 |
| industry      | VARCHAR(100) | NULLABLE                   | Sector (Colocation, Hyperscale, etc.)|
| country       | VARCHAR(100) | NULLABLE                   | País                                 |
| admin_id      | UUID         | FK → users.id, NOT NULL    | Admin responsable                    |
| founder_name  | VARCHAR(255) | NULLABLE                   | Nombre del founder/decisor           |
| founder_email | VARCHAR(255) | NULLABLE                   | Email del founder                    |
| status        | VARCHAR(50)  | NOT NULL                   | Estado del pipeline                  |
| notes         | TEXT         | NULLABLE                   | Notas libres del Admin               |
| created_at    | TIMESTAMP    | NOT NULL                   | Fecha de creación                    |
| updated_at    | TIMESTAMP    | NOT NULL                   | Última actualización                 |

```sql
CREATE TABLE companies (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name           VARCHAR(255) NOT NULL,
    industry       VARCHAR(100),
    country        VARCHAR(100),
    admin_id       UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    founder_name   VARCHAR(255),
    founder_email  VARCHAR(255),
    status         VARCHAR(50) NOT NULL DEFAULT 'REGISTERED'
                   CHECK (status IN (
                     'REGISTERED','INVITED','IN_PROGRESS','COMPLETED',
                     'PDF_GENERATED','OUTREACH_PENDING','OUTREACH_SENT',
                     'MEETING_SCHEDULED','CONVERTED','LOST'
                   )),
    notes          TEXT,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP NOT NULL DEFAULT NOW()
);
```

---

### benchmark_results

| Columna              | Tipo         | Restricciones           | Descripción                         |
|----------------------|--------------|-------------------------|-------------------------------------|
| id                   | UUID         | PK, NOT NULL            | Identificador único                 |
| company_id           | UUID         | FK → companies.id       | Empresa asociada                    |
| operator_id          | UUID         | FK → users.id           | Operador que completó               |
| status               | VARCHAR(50)  | NOT NULL                | IN_PROGRESS / COMPLETED             |
| completion_pct       | INTEGER      | DEFAULT 0               | Porcentaje completado (0-100)       |
| score                | DECIMAL(5,2) | NULLABLE                | Score /100 calculado                |
| percentile           | INTEGER      | NULLABLE                | Percentil en el ranking global      |
| maturity_level       | VARCHAR(50)  | NULLABLE                | ORCHESTRATED / COORDINATED / REACTIVE / FRAGMENTED |
| total_capacity_kw    | DECIMAL(10,2)| NULLABLE                | KPI: capacidad total instalada      |
| used_capacity_kw     | DECIMAL(10,2)| NULLABLE                | KPI: capacidad utilizada            |
| wasted_capacity_kw   | DECIMAL(10,2)| NULLABLE                | KPI: capacidad desperdiciada        |
| waste_index          | DECIMAL(5,2) | NULLABLE                | % de capacidad desperdiciada        |
| pue_ratio            | DECIMAL(5,2) | NULLABLE                | Power Usage Effectiveness           |
| industry_benchmark   | DECIMAL(5,2) | NULLABLE                | Benchmark promedio de la industria  |
| ai_insights          | TEXT         | NULLABLE                | Insights generados por Google AI Studio |
| completed_at         | TIMESTAMP    | NULLABLE                | Fecha de finalización               |
| created_at           | TIMESTAMP    | NOT NULL                | Fecha de creación                   |
| updated_at           | TIMESTAMP    | NOT NULL                | Última actualización                |

```sql
CREATE TABLE benchmark_results (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id          UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    operator_id         UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    status              VARCHAR(50) NOT NULL DEFAULT 'IN_PROGRESS'
                        CHECK (status IN ('IN_PROGRESS', 'COMPLETED')),
    completion_pct      INTEGER NOT NULL DEFAULT 0,
    score               DECIMAL(5,2),
    percentile          INTEGER,
    maturity_level      VARCHAR(50)
                        CHECK (maturity_level IN ('ORCHESTRATED','COORDINATED','REACTIVE','FRAGMENTED')),
    total_capacity_kw   DECIMAL(10,2),
    used_capacity_kw    DECIMAL(10,2),
    wasted_capacity_kw  DECIMAL(10,2),
    waste_index         DECIMAL(5,2),
    pue_ratio           DECIMAL(5,2),
    industry_benchmark  DECIMAL(5,2),
    ai_insights         TEXT,
    completed_at        TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);
```

---

### benchmark_responses

| Columna      | Tipo         | Restricciones             | Descripción                    |
|--------------|--------------|---------------------------|--------------------------------|
| id           | UUID         | PK, NOT NULL              | Identificador único            |
| benchmark_id | UUID         | FK → benchmark_results.id | Benchmark asociado             |
| question_id  | VARCHAR(100) | NOT NULL                  | ID de la pregunta              |
| value        | TEXT         | NULLABLE                  | Respuesta del operador         |
| answered_at  | TIMESTAMP    | NOT NULL                  | Momento de la respuesta        |

```sql
CREATE TABLE benchmark_responses (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    benchmark_id  UUID NOT NULL REFERENCES benchmark_results(id) ON DELETE CASCADE,
    question_id   VARCHAR(100) NOT NULL,
    value         TEXT,
    answered_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (benchmark_id, question_id)
);
```

---

### generated_pdfs

| Columna       | Tipo         | Restricciones           | Descripción                       |
|---------------|--------------|-------------------------|-----------------------------------|
| id            | UUID         | PK, NOT NULL            | Identificador único               |
| company_id    | UUID         | FK → companies.id       | Empresa asociada                  |
| benchmark_id  | UUID         | FK → benchmark_results.id | Benchmark origen                |
| status        | VARCHAR(50)  | NOT NULL                | PROCESSING / GENERATED / FAILED   |
| s3_key        | VARCHAR(500) | NULLABLE                | Ruta del archivo en S3            |
| download_url  | TEXT         | NULLABLE                | URL firmada (temporal)            |
| generated_at  | TIMESTAMP    | NULLABLE                | Fecha de generación               |
| created_at    | TIMESTAMP    | NOT NULL                | Fecha de creación                 |

```sql
CREATE TABLE generated_pdfs (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id    UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    benchmark_id  UUID NOT NULL REFERENCES benchmark_results(id) ON DELETE RESTRICT,
    status        VARCHAR(50) NOT NULL DEFAULT 'PROCESSING'
                  CHECK (status IN ('PROCESSING', 'GENERATED', 'FAILED')),
    s3_key        VARCHAR(500),
    download_url  TEXT,
    generated_at  TIMESTAMP,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW()
);
```

---

### outreach_campaigns

| Columna       | Tipo         | Restricciones           | Descripción                          |
|---------------|--------------|-------------------------|--------------------------------------|
| id            | UUID         | PK, NOT NULL            | Identificador único                  |
| company_id    | UUID         | FK → companies.id       | Empresa asociada                     |
| admin_id      | UUID         | FK → users.id           | Admin responsable del outreach       |
| status        | VARCHAR(50)  | NOT NULL                | Estado del outreach                  |
| sent_at       | TIMESTAMP    | NULLABLE                | Fecha de envío del primer contacto   |
| meeting_at    | TIMESTAMP    | NULLABLE                | Fecha de reunión agendada            |
| converted_at  | TIMESTAMP    | NULLABLE                | Fecha de conversión                  |
| created_at    | TIMESTAMP    | NOT NULL                | Fecha de creación                    |
| updated_at    | TIMESTAMP    | NOT NULL                | Última actualización                 |

```sql
CREATE TABLE outreach_campaigns (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id    UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    admin_id      UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    status        VARCHAR(50) NOT NULL DEFAULT 'OUTREACH_PENDING'
                  CHECK (status IN (
                    'OUTREACH_PENDING','OUTREACH_SENT',
                    'MEETING_SCHEDULED','CONVERTED','LOST'
                  )),
    sent_at       TIMESTAMP,
    meeting_at    TIMESTAMP,
    converted_at  TIMESTAMP,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);
```

---

### outreach_notes

| Columna      | Tipo         | Restricciones              | Descripción                    |
|--------------|--------------|----------------------------|--------------------------------|
| id           | UUID         | PK, NOT NULL               | Identificador único            |
| campaign_id  | UUID         | FK → outreach_campaigns.id | Campaña asociada               |
| created_by   | UUID         | FK → users.id              | Admin que escribió la nota     |
| note         | TEXT         | NOT NULL                   | Contenido de la nota           |
| created_at   | TIMESTAMP    | NOT NULL                   | Fecha de creación              |

```sql
CREATE TABLE outreach_notes (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    campaign_id  UUID NOT NULL REFERENCES outreach_campaigns(id) ON DELETE CASCADE,
    created_by   UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    note         TEXT NOT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW()
);
```

---

### outreach_status_history

| Columna      | Tipo         | Restricciones              | Descripción                       |
|--------------|--------------|----------------------------|-----------------------------------|
| id           | UUID         | PK, NOT NULL               | Identificador único               |
| company_id   | UUID         | FK → companies.id          | Empresa asociada                  |
| status       | VARCHAR(50)  | NOT NULL                   | Nuevo estado                      |
| changed_by   | VARCHAR(255) | NOT NULL                   | Email del usuario o "system"      |
| changed_at   | TIMESTAMP    | NOT NULL                   | Fecha del cambio                  |

```sql
CREATE TABLE outreach_status_history (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id  UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    status      VARCHAR(50) NOT NULL,
    changed_by  VARCHAR(255) NOT NULL,
    changed_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
```

---

## Índices Recomendados

```sql
-- Búsqueda de empresas por admin y estado
CREATE INDEX idx_companies_admin_id ON companies(admin_id);
CREATE INDEX idx_companies_status ON companies(status);

-- Benchmark por empresa
CREATE INDEX idx_benchmark_results_company_id ON benchmark_results(company_id);

-- PDFs por empresa
CREATE INDEX idx_generated_pdfs_company_id ON generated_pdfs(company_id);

-- Outreach por empresa y estado
CREATE INDEX idx_outreach_campaigns_company_id ON outreach_campaigns(company_id);
CREATE INDEX idx_outreach_campaigns_status ON outreach_campaigns(status);

-- Historial de estados
CREATE INDEX idx_status_history_company_id ON outreach_status_history(company_id);

-- Usuarios por rol
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_company_id ON users(company_id);
```

---

## Seed Inicial

```sql
-- Seed inicial (primer Admin)
INSERT INTO users (id, email, password, name, role, status)
VALUES (
    gen_random_uuid(),
    'admin@ghostload.com',
    '$2a$12$...hash_bcrypt...',
    'Administrator',
    'ROLE_ADMIN',
    'ACTIVE'
);
```

---

## Decisión Pendiente

| Item                  | Opción A              | Opción B            | Estado       |
|-----------------------|-----------------------|---------------------|--------------|
| Hosting PostgreSQL    | Amazon RDS PostgreSQL | Supabase PostgreSQL | Pendiente    |

Ambas opciones son compatibles con Spring Data JPA sin cambios en el dominio (adaptador de persistencia intercambiable por arquitectura hexagonal).
