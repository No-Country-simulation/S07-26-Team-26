# 05 — Database Design

## Project Ghost Load

**Motor:** PostgreSQL  
**ORM:** Spring Data JPA / Hibernate  
**Migraciones:** Flyway  
**Arquitectura:** DDD — cada Bounded Context gestiona sus propias tablas

---

## Diagrama de Entidades (MVP)

```
assessment
  ├── operators
  ├── evaluations
  ├── evaluation_tokens
  ├── calculator_results
  ├── benchmark_questions
  ├── benchmark_results
  ├── benchmark_answers
  └── benchmark_module_scores

administration
  └── admin_users

outreach
  ├── contact_imports
  ├── contacts
  ├── campaigns
  ├── campaign_contacts
  ├── invitations
  ├── email_outbox
  └── email_outbox_status
```

---

## assessment — operators

| Columna      | Tipo           | Restricciones              | Descripción                          |
|--------------|----------------|----------------------------|--------------------------------------|
| id           | UUID           | PK, NOT NULL               | Identificador único                  |
| email        | VARCHAR(255)   | UNIQUE, NOT NULL           | Email del operador                   |
| company_name | VARCHAR(255)   | NOT NULL                   | Nombre de la empresa                 |
| position     | VARCHAR(255)   | NULLABLE                   | Cargo del operador                   |
| created_at   | TIMESTAMP      | NOT NULL                   | Fecha de creación                    |
| updated_at   | TIMESTAMP      | NOT NULL                   | Última actualización                 |

---

## assessment — evaluations

| Columna      | Tipo           | Restricciones              | Descripción                          |
|--------------|----------------|----------------------------|--------------------------------------|
| id           | UUID           | PK, NOT NULL               | Identificador único                  |
| operator_id  | UUID           | FK → operators.id          | Operador asociado                    |
| state        | VARCHAR(50)    | NOT NULL                   | STARTED / CALCULATOR_COMPLETED / BENCHMARK_COMPLETED / REPORT_GENERATING / REPORT_COMPLETED / REPORT_FAILED |
| created_at   | TIMESTAMP      | NOT NULL                   | Fecha de creación                    |
| updated_at   | TIMESTAMP      | NOT NULL                   | Última actualización                 |

---

## assessment — evaluation_tokens

| Columna      | Tipo           | Restricciones              | Descripción                          |
|--------------|----------------|----------------------------|--------------------------------------|
| id           | UUID           | PK, NOT NULL               | Identificador único                  |
| evaluation_id| UUID           | FK → evaluations.id        | Evaluación asociada                  |
| token        | VARCHAR(255)   | NOT NULL                   | Token único de acceso                |
| expires_at   | TIMESTAMP      | NULLABLE                   | Fecha de expiración                  |
| created_at   | TIMESTAMP      | NOT NULL                   | Fecha de creación                    |

---

## assessment — calculator_results

| Columna                | Tipo           | Restricciones           | Descripción                         |
|------------------------|----------------|-------------------------|-------------------------------------|
| id                     | UUID           | PK, NOT NULL            | Identificador único                 |
| evaluation_id          | UUID           | FK → evaluations.id     | Evaluación asociada                 |
| total_capacity_mw      | DECIMAL(10,2)  | NOT NULL                | Capacidad total en MW               |
| productive_capacity_mw | DECIMAL(10,2)  | NOT NULL                | Capacidad productiva en MW          |
| non_productive_capacity_mw | DECIMAL(10,2) | NOT NULL              | Capacidad no productiva en MW       |
| utilization_pct        | DECIMAL(5,2)   | NOT NULL                | Porcentaje de utilización           |
| non_productive_pct     | DECIMAL(5,2)   | NOT NULL                | Porcentaje no productivo            |
| monthly_cost_per_kw    | DECIMAL(10,2)  | NOT NULL                | Costo mensual por kW                |
| currency               | VARCHAR(10)    | NOT NULL                | Moneda (USD, EUR, etc.)            |
| estimated_annual_cost  | DECIMAL(15,2)  | NOT NULL                | Costo anual estimado                |
| created_at             | TIMESTAMP      | NOT NULL                | Fecha de creación                   |

---

## assessment — benchmark_questions

| Columna       | Tipo           | Restricciones           | Descripción                         |
|---------------|----------------|-------------------------|-------------------------------------|
| id            | UUID           | PK, NOT NULL            | Identificador único                 |
| question_id   | VARCHAR(100)   | NOT NULL                | ID legible de la pregunta           |
| module        | VARCHAR(100)   | NOT NULL                | Módulo: CAPACITY_VISIBILITY, OPERATIONAL_COORDINATION, AUTOMATION, GOVERNANCE, CONTINUOUS_IMPROVEMENT |
| text          | TEXT           | NOT NULL                | Texto de la pregunta                |
| version       | INTEGER        | NOT NULL, DEFAULT 1     | Versión del cuestionario            |
| created_at    | TIMESTAMP      | NOT NULL                | Fecha de creación                   |

---

## assessment — benchmark_results

| Columna              | Tipo           | Restricciones           | Descripción                         |
|----------------------|----------------|-------------------------|-------------------------------------|
| id                   | UUID           | PK, NOT NULL            | Identificador único                 |
| evaluation_id        | UUID           | FK → evaluations.id     | Evaluación asociada                 |
| status               | VARCHAR(50)    | NOT NULL                | COMPLETED                           |
| score                | DECIMAL(5,2)   | NULLABLE                | Score /100 calculado                |
| percentile           | INTEGER        | NULLABLE                | Percentil (MVP: determinístico)     |
| maturity_level       | VARCHAR(50)    | NULLABLE                | INITIAL / DEVELOPING / MANAGED / ADVANCED / OPTIMIZED |
| completed_at         | TIMESTAMP      | NULLABLE                | Fecha de finalización               |
| created_at           | TIMESTAMP      | NOT NULL                | Fecha de creación                   |
| updated_at           | TIMESTAMP      | NOT NULL                | Última actualización                |

---

## assessment — benchmark_answers

| Columna      | Tipo         | Restricciones             | Descripción                    |
|--------------|--------------|---------------------------|--------------------------------|
| id           | UUID         | PK, NOT NULL              | Identificador único            |
| result_id    | UUID         | FK → benchmark_results.id | Resultado asociado             |
| question_id  | VARCHAR(100) | NOT NULL                  | ID de la pregunta              |
| value        | INTEGER      | NOT NULL                  | Valor entre 1 y 5              |
| answered_at  | TIMESTAMP    | NOT NULL                  | Momento de la respuesta        |

---

## assessment — benchmark_module_scores

| Columna       | Tipo         | Restricciones             | Descripción                    |
|---------------|--------------|---------------------------|--------------------------------|
| id            | UUID         | PK, NOT NULL              | Identificador único            |
| result_id     | UUID         | FK → benchmark_results.id | Resultado asociado             |
| module        | VARCHAR(100) | NOT NULL                  | Nombre del módulo              |
| score         | DECIMAL(5,2) | NOT NULL                  | Score del módulo /100          |
| created_at    | TIMESTAMP    | NOT NULL                  | Fecha de creación              |

---

## administration — admin_users

| Columna      | Tipo           | Restricciones              | Descripción                          |
|--------------|----------------|----------------------------|--------------------------------------|
| id           | UUID           | PK, NOT NULL               | Identificador único                  |
| email        | VARCHAR(255)   | UNIQUE, NOT NULL           | Email del admin                      |
| password     | VARCHAR(255)   | NOT NULL                   | Hash bcrypt                          |
| name         | VARCHAR(255)   | NOT NULL                   | Nombre completo                      |
| role         | VARCHAR(50)    | NOT NULL, DEFAULT 'ADMIN'  | ADMIN                                |
| created_at   | TIMESTAMP      | NOT NULL                   | Fecha de creación                    |
| updated_at   | TIMESTAMP      | NOT NULL                   | Última actualización                 |

---

## outreach — contact_imports

| Columna      | Tipo           | Restricciones           | Descripción                          |
|--------------|----------------|-------------------------|--------------------------------------|
| id           | UUID           | PK, NOT NULL            | Identificador único                  |
| filename     | VARCHAR(255)   | NOT NULL                | Nombre del archivo                   |
| total_rows   | INTEGER        | NOT NULL                | Total de filas                       |
| imported     | INTEGER        | NOT NULL                | Filas importadas                     |
| failed       | INTEGER        | NOT NULL                | Filas fallidas                       |
| created_by   | UUID           | FK → admin_users.id     | Admin que importó                    |
| created_at   | TIMESTAMP      | NOT NULL                | Fecha de importación                 |

---

## outreach — contacts

| Columna      | Tipo           | Restricciones           | Descripción                          |
|--------------|----------------|-------------------------|--------------------------------------|
| id           | UUID           | PK, NOT NULL            | Identificador único                  |
| import_id    | UUID           | FK → contact_imports.id | Importación asociada                 |
| first_name   | VARCHAR(255)   | NOT NULL                | Nombre                               |
| last_name    | VARCHAR(255)   | NOT NULL                | Apellido                             |
| email        | VARCHAR(255)   | UNIQUE, NOT NULL        | Email                                |
| company      | VARCHAR(255)   | NULLABLE                | Empresa                              |
| position     | VARCHAR(255)   | NULLABLE                | Cargo                                |
| created_at   | TIMESTAMP      | NOT NULL                | Fecha de creación                    |

---

## outreach — campaigns

| Columna      | Tipo           | Restricciones           | Descripción                          |
|--------------|----------------|-------------------------|--------------------------------------|
| id           | UUID           | PK, NOT NULL            | Identificador único                  |
| name         | VARCHAR(255)   | NOT NULL                | Nombre de la campaña                 |
| created_by   | UUID           | FK → admin_users.id     | Admin que creó                       |
| sent_at      | TIMESTAMP      | NULLABLE                | Fecha de envío                       |
| created_at   | TIMESTAMP      | NOT NULL                | Fecha de creación                    |
| updated_at   | TIMESTAMP      | NOT NULL                | Última actualización                 |

---

## outreach — campaign_contacts

| Columna      | Tipo           | Restricciones           | Descripción                          |
|--------------|----------------|-------------------------|--------------------------------------|
| id           | UUID           | PK, NOT NULL            | Identificador único                  |
| campaign_id  | UUID           | FK → campaigns.id       | Campaña asociada                     |
| contact_id   | UUID           | FK → contacts.id        | Contacto asociado                    |
| UNIQUE       | (campaign_id, contact_id) |                  | Sin duplicados                       |

---

## outreach — invitations

| Columna         | Tipo           | Restricciones           | Descripción                          |
|-----------------|----------------|-------------------------|--------------------------------------|
| id              | UUID           | PK, NOT NULL            | Identificador único                  |
| campaign_id     | UUID           | FK → campaigns.id       | Campaña asociada                     |
| contact_id      | UUID           | FK → contacts.id        | Contacto asociado                    |
| token           | VARCHAR(255)   | UNIQUE, NOT NULL        | Token único de acceso                |
| status          | VARCHAR(50)    | NOT NULL                | UPLOADED / SENT / VISITED / STARTED / COMPLETED / FAILED |
| sent_at         | TIMESTAMP      | NULLABLE                | Fecha de envío                       |
| visited_at      | TIMESTAMP      | NULLABLE                | Fecha de primer visita               |
| started_at      | TIMESTAMP      | NULLABLE                | Fecha de inicio de evaluación        |
| completed_at    | TIMESTAMP      | NULLABLE                | Fecha de finalización                |
| error_message   | TEXT           | NULLABLE                | Mensaje de error si falló            |
| created_at      | TIMESTAMP      | NOT NULL                | Fecha de creación                    |
| updated_at      | TIMESTAMP      | NOT NULL                | Última actualización                 |

---

## outreach — email_outbox

| Columna      | Tipo           | Restricciones           | Descripción                          |
|--------------|----------------|-------------------------|--------------------------------------|
| id           | UUID           | PK, NOT NULL            | Identificador único                  |
| invitation_id| UUID           | FK → invitations.id     | Invitación asociada                  |
| to_email     | VARCHAR(255)   | NOT NULL                | Destinatario                         |
| subject      | VARCHAR(255)   | NOT NULL                | Asunto                               |
| body         | TEXT           | NOT NULL                | Cuerpo del email                     |
| status       | VARCHAR(50)    | NOT NULL                | PENDING / SENT / FAILED              |
| sent_at      | TIMESTAMP      | NULLABLE                | Fecha de envío                       |
| error_message| TEXT           | NULLABLE                | Mensaje de error                     |
| retry_count  | INTEGER        | DEFAULT 0               | Contador de reintentos               |
| created_at   | TIMESTAMP      | NOT NULL                | Fecha de creación                    |

---

## Índices Recomendados

```sql
-- Evaluaciones por operador
CREATE INDEX idx_evaluations_operator_id ON evaluations(operator_id);

-- Evaluaciones por estado
CREATE INDEX idx_evaluations_state ON evaluations(state);

-- Resultados por evaluación
CREATE INDEX idx_benchmark_results_evaluation_id ON benchmark_results(evaluation_id);

-- Contactos por email (deduplicación)
CREATE INDEX idx_contacts_email ON contacts(email);

-- Invitaciones por token
CREATE INDEX idx_invitations_token ON invitations(token);

-- Invitaciones por campaña
CREATE INDEX idx_invitations_campaign_id ON invitations(campaign_id);

-- Email outbox por estado
CREATE INDEX idx_email_outbox_status ON email_outbox(status);

-- Admin users por email
CREATE INDEX idx_admin_users_email ON admin_users(email);
```

---

## Seed Inicial

```sql
-- Seed del primer Admin (V1.2)
INSERT INTO admin_users (id, email, password, name, role)
VALUES (
    gen_random_uuid(),
    'admin@ghostload.local',
    '$2a$12$...hash_bcrypt...',
    'Administrator',
    'ADMIN'
);
```

---

## Migraciones Flyway (orden实际)

| Versión | Tabla(s) creada(s) |
|---------|---------------------|
| V1.1    | `admin_users` |
| V1.2    | Seed admin |
| V1.3    | `operators`, `evaluations` |
| V1.4    | `evaluation_tokens`, `calculator_results` |
| V1.5    | `contact_imports` |
| V1.6    | `contacts` |
| V1.7    | `campaigns` |
| V1.8    | `campaign_contacts` |
| V1.9    | `invitations` |
| V1.10   | `email_outbox` |
| V1.11   | (events log, opcional) |
| V1.12   | `email_outbox_status` |
| V1.13   | `benchmark_questions` |
| V1.14   | Seed preguntas benchmark |
| V1.15   | `benchmark_results` |
| V1.16   | `benchmark_answers` |
| V1.17   | `benchmark_module_scores` |

---

## Tablas futuras (post-MVP)

| Tabla | Módulo | Descripción |
|-------|--------|-------------|
| `generated_reports` | reporting | PDFs generados |
| `report_store` | reporting | Metadata de almacenamiento |
| `outreach_pipeline` | outreach | Pipeline CRM comercial |
| `outreach_notes` | outreach | Notas de seguimiento |
| `dashboard_cache` | administration | Métricas cacheadas |
