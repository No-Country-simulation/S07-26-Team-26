# System Architecture — Ghost Load

**Project:** Ghost Load
**Documento:** Arquitectura del sistema, módulos, stack tecnológico y diagramas
**Basado en:** CLAUDE_DOC.md (secciones 2, 3, 4, 5, 6)

---

## 1. Arquitectura de alto nivel

```
┌─────────────────────────────────────────────────────────────────────┐
│                       FRONTENDS (3 componentes)                      │
│   Reporte de industria │ Calculadora │ Benchmark de madurez          │
│   (React/Vite + Tailwind)                                           │
└───────────────────────┬─────────────┬───────────────────────────────┘
                        │             │
                        ▼             ▼
            ┌───────────────────────────────────────────────┐
            │              API GATEWAY                      │
            │     (Node.js + Express/Fastify)               │
            │   /v1/identity  /v1/calc  /v1/benchmark       │
            │   validación · CORS · rate limit · JWT        │
            └───────┬──────────┬───────────┬────────────────┘
                    │          │           │
        ┌───────────┴──┐ ┌────┴────┐ ┌────┴──────────┐
        │  Identity    │ │Calculadora│ │  Benchmark    │
        │  Service     │ │ Service  │ │  Service      │
        │  (M1)        │ │ (M4)     │ │ (M5)          │
        └───────┬──────┘ └────┬─────┘ └───────┬────────┘
                │             │               │
                └─────────────┼───────────────┘
                              ▼
                    ┌──────────────────────┐
                    │    PostgreSQL DB      │
                    │  operadores · calc ·  │
                    │  benchmark · outreach  │
                    └──────────┬────────────┘
                               │  benchmark completado (evento)
                               ▼
                    ┌──────────────────────┐    ┌──────────────────┐
                    │   PDF Generator      │───▶│  Email Service   │
                    │ (Puppeteer/Playwright)│   │  (SendGrid/SES)  │
                    │  HTML+CSS → PDF      │    └──────────────────┘
                    └──────────────────────┘

┌────────────────────────────────────────────────────────────────┐
│                    Outreach Pipeline                            │
│  CSV Upload → Queue (PostgreSQL/Bull) → Email → Track Status   │
│  (M7)                                                          │
└────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────┐
│                    Dashboard Interno                           │
│  React + Recharts — respuestas acumuladas + outreach stats     │
│  (M8 — acceso protegido, no magic link)                       │
└────────────────────────────────────────────────────────────────┘
```

---

## 2. Descomposición en 9 módulos

| Módulo | Nombre | Responsabilidad |
|--------|--------|----------------|
| **M1** | Identidad y Captura de Email | Magic link, token JWT, registro de fuente (calculator/benchmark/outreach) |
| **M2** | Modelo de Datos y Base de Datos | Schema unificado, migraciones, datos semilla |
| **M3** | API Gateway | Endpoints REST versionados, validación, CORS, rate limiting |
| **M4** | Motor de Calculadora | Cálculo de KPIs de capacidad desperdiciada, persistencia |
| **M5** | Motor de Benchmark | Cuestionario, percentil vs distribución, disparo de PDF |
| **M6** | PDF Generator | Renderizado institucional HTML+CSS → PDF |
| **M7** | Sistema de Outreach | Upload CSV, cola de invitaciones, tracking de estado |
| **M8** | Dashboard Interno | Visualización de respuestas acumuladas y estado de campañas |
| **M9** | Infraestructura y Deploy | Staging público, CI/CD, variables de entorno, dominios |

---

## 3. Stack tecnológico propuesto

| Capa | Tecnología | Razón |
|------|-----------|-------|
| Frontend | React 18 + Vite + Tailwind CSS | Rapidez de desarrollo, ecosistema amplio |
| Backend API | Node.js + Fastify/Express | Rendimiento, familiaridad del equipo |
| Base de datos | PostgreSQL 15+ | Schema relacional, JSONB para inputs flexibles |
| PDF | Puppeteer / Playwright (HTML+CSS → PDF) | Control tipográfico y de paleta superior a librerías PDF |
| Email | SendGrid / Resend / AWS SES | Entrega confiable, tracking de apertura |
| Auth | JWT + magic link (token firmado HMAC/RSA) | Sin contraseñas, experiencia fluida |
| Outreach Queue | PostgreSQL como cola (o Bull + Redis si escala) | Menos componentes, idempotencia vía unique constraints |
| Dashboard | React + Recharts / Chart.js | Visualización ligera de datos acumulados |
| Deploy | Docker + VPS (Railway / Render / AWS) | Staging público accesible desde semana 1 |

---

## 4. Máquina de estados del operador

```
                     ┌──────────┐
                     │ anónimo  │
                     └────┬─────┘
                          │ email capturado (M1)
                          ▼
                  ┌──────────────────┐
                  │ email_capturado  │ ← fuente: calculator / benchmark / outreach
                  └────────┬─────────┘
                           │ calculadora completa (M4)
                           ▼
                  ┌─────────────────────┐
                  │ calculadora_completa │
                  └────────┬────────────┘
                           │ benchmark completo (M5)
                           ▼
                  ┌──────────────────┐
                  │ benchmark_completo│ ← se dispara PDF (M6)
                  └────────┬─────────┘
                           │ PDF generado
                           ▼
                  ┌──────────────────┐
                  │ pdf_generado     │
                  └────────┬─────────┘
                           │ lead calificado
                           ▼
                  ┌──────────────────┐
                  │ lead_calificado  │ ← visible en dashboard (M8)
                  └──────────────────┘
```

Reglas:
- Un operador puede entrar por cualquier punto del embudo
- El estado avanza, nunca retrocede
- Cada transición puede disparar eventos (ej. benchmark_completo → generar PDF)

---

## 5. Schema de base de datos

```sql
-- Pivote: identidad = email
CREATE TABLE operators (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email       TEXT UNIQUE NOT NULL,
    company     TEXT,
    source      TEXT CHECK (source IN ('calculator', 'benchmark', 'outreach')),
    state       TEXT NOT NULL DEFAULT 'email_capturado',
    created_at  TIMESTAMPTZ DEFAULT now()
);

-- Resultados de calculadora (reusados en PDF)
CREATE TABLE calculator_results (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    operator_id UUID NOT NULL REFERENCES operators(id),
    inputs      JSONB,
    kpis        JSONB,
    created_at  TIMESTAMPTZ DEFAULT now()
);

-- Respuestas de benchmark + percentil
CREATE TABLE benchmark_responses (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    operator_id UUID NOT NULL REFERENCES operators(id),
    answers     JSONB,
    score       NUMERIC,
    percentile  NUMERIC,
    is_seed     BOOLEAN DEFAULT false,
    created_at  TIMESTAMPTZ DEFAULT now()
);

-- PDFs generados
CREATE TABLE generated_pdfs (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    operator_id UUID NOT NULL REFERENCES operators(id),
    file_url    TEXT,
    generated_at TIMESTAMPTZ DEFAULT now()
);

-- Campañas de outreach
CREATE TABLE outreach_campaigns (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        TEXT,
    created_at  TIMESTAMPTZ DEFAULT now()
);

-- Contactos de outreach con máquina de estados
CREATE TABLE outreach_contacts (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    campaign_id UUID NOT NULL REFERENCES outreach_campaigns(id),
    email       TEXT NOT NULL,
    status      TEXT NOT NULL DEFAULT 'pending'
                    CHECK (status IN ('pending', 'invited', 'opened', 'completed')),
    operator_id UUID REFERENCES operators(id),
    invited_at  TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    UNIQUE (campaign_id, email)
);
```

---

## 6. Endpoints REST (API Gateway)

| Método | Ruta | Módulo | Propósito |
|--------|------|--------|-----------|
| POST | `/v1/identity/capture` | M1 | Captura email, devuelve magic link/token |
| GET | `/v1/identity/verify?token=...` | M1 | Valida token, inicia sesión ligera |
| POST | `/v1/calculator/run` | M4 | Calcula y persiste KPIs |
| GET | `/v1/calculator/results/:operatorId` | M4 | Obtiene KPIs guardados (para PDF) |
| POST | `/v1/benchmark/submit` | M5 | Envía respuestas, calcula percentil, dispara PDF |
| GET | `/v1/benchmark/questions` | M5 | Obtiene cuestionario de benchmark |
| GET | `/v1/pdf/:operatorId` | M6 | Descarga PDF generado |
| POST | `/v1/outreach/upload` | M7 | Sube CSV, crea campaña |
| POST | `/v1/outreach/:campaignId/send` | M7 | Encola invitaciones |
| GET | `/v1/dashboard/summary` | M8 | Respuestas acumuladas + completitud (interno) |

---

## 7. Decisiones arquitectónicas (ADRs)

### ADR-01: PDF por HTML+CSS → Puppeteer vs librerías de bajo nivel
**Decisión:** Usar Puppeteer/Playwright con template HTML+CSS.
**Razón:** Control tipográfico y de paleta (forest-green + gold) muy superior. El PDF tiene que ser institucional, no un export genérico.
**Consecuencia:** El servidor necesita Chrome/Chromium headless (aumenta el tamaño del deploy ~300MB).

### ADR-02: Magic link vs OAuth/contraseñas
**Decisión:** JWT firmado enviado por email como magic link.
**Razón:** Elimina fricción de registro. El email es la identidad del sistema.
**Consecuencia:** Dependencia de servicio de email transaccional desde el día 1.

### ADR-03: PostgreSQL como cola de outreach vs Redis/Bull
**Decisión:** PostgreSQL con tabla de contactos y estado.
**Razón:** Un solo stack de datos para staging temprano. Idempotencia garantizada por UNIQUE(campaign_id, email). Redis se introduce solo si el volumen lo requiere.

---

## 8. Supuestos documentados

1. **Arranque en frío del benchmark:** Se siembran datos semilla (distribución artificial) para que el primer operador no sea "1 de 1"
2. **Un único operador por email:** No hay multi-tenant ni usuarios múltiples por empresa
3. **El PDF se genera server-side:** Sin intervención del cliente, completamente backend
4. **Outreach inicial será de bajo volumen:** < 1000 contactos por campaña, no justifica Redis dedicado en staging
