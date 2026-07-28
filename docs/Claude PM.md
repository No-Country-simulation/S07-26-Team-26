# Infraestructura y PDF Generator — Requisitos y Diseño

**Proyecto:** Columna vertebral de una serie de 5 componentes (reporte, calculadora, benchmark, PDF, outreach)
**Objetivo real:** No es un set de features sueltas. Es un **embudo de generación de leads** disfrazado de herramientas gratuitas. Todo el sistema existe para llevar a un operador de data center desde la curiosidad hasta una llamada agendada con el fundador.
**Duración:** 5 semanas · Entrega en staging público

---

## 1. Lectura del problema

Una startup de infraestructura de IA quiere posicionarse como la voz autorizada sobre un problema caro y real: **capacidad de data center pagada y encendida que no produce nada** porque las capas física y operativa del facility no se coordinan.

El embudo completo:

```
Calculadora (compromiso bajo)
   → captura de email (el pivote)
      → Benchmark (compromiso alto)
         → PDF personalizado (el activo de ventas)
            → contacto del fundador + agendar llamada
```

Dos consecuencias de diseño que ordenan todo lo demás:

- **El PDF no es un export, es una pieza de ventas.** Su calidad institucional es un requisito de primer orden, no un detalle estético.
- **El email es la identidad.** No hay login con contraseña. "Autenticación ligera" = magic link / token. El email tiene doble uso explícito: desbloquear la calculadora e invitar al benchmark.

---

## 2. Descomposición en módulos

Nueve módulos. El orden de la lista es aproximadamente el orden en que conviene construirlos.

### M1 · Identidad y Captura de Email
El pivote del embudo. Todo cuelga de aquí.
- Captura de email desde calculadora y benchmark.
- Magic link / token firmado como mecanismo de "sesión ligera" (sin contraseñas).
- Un email = un operador. Deduplicación y resolución de identidad.
- Doble propósito registrado: origen de la captura (`calculator` | `benchmark` | `outreach`).

### M2 · Modelo de Datos y Base de Datos
El contrato del que dependen los otros 4 proyectos de la serie. **Congelar temprano.**
- Schema unificado para operadores, calculadora, benchmark y outreach.
- Máquina de estados del operador (ver sección 4).
- Datos semilla del benchmark para resolver el arranque en frío.

### M3 · API Gateway
La capa que conecta el frontend de los tres componentes con los datos.
- Endpoints REST versionados (`/v1/...`).
- Validación de entrada, rate limiting básico, CORS para el staging público.
- Punto único de integración para reporte, calculadora y benchmark.

### M4 · Motor de la Calculadora (backend)
- Recibe inputs del operador, calcula KPIs de capacidad desperdiciada.
- Persiste resultados asociados al operador (para reusarlos en el PDF).
- "Profundidad" desbloqueada tras captura de email.

### M5 · Motor del Benchmark
- Recibe respuestas del cuestionario de madurez.
- Calcula **percentil / posición del operador** contra la distribución acumulada.
- Resuelve el arranque en frío con distribución semilla + función de percentil explícita.
- Dispara la generación del PDF al completarse (evento).

### M6 · PDF Generator (el entregable estrella)
- Se genera **automáticamente** al completar el benchmark.
- Contenido: posición en el benchmark, resumen de KPIs de la calculadora, branding completo (logo, paleta forest-green + gold), bloque de contacto del fundador (teléfono, LinkedIn, email, link para agendar).
- Renderizado como **documento institucional**, no como export genérico.
- Recomendación técnica: HTML/CSS → navegador headless (mejor control tipográfico y de paleta que las librerías PDF de bajo nivel).

### M7 · Sistema de Outreach
- Upload de lista de contactos (CSV).
- Envío de invitaciones al benchmark **sin intervención manual** = cola + estado, no un `for` sobre un archivo.
- Tracking de completitud por contacto (máquina de estados: `pending → invited → opened → completed`).
- Idempotencia: nunca invitar dos veces al mismo contacto.

### M8 · Dashboard Interno
- El equipo ve respuestas acumuladas de benchmark y calculadora.
- Estado de campañas de outreach y tasa de completitud.
- Protegido (este sí necesita algo más que magic link — un acceso interno).

### M9 · Infraestructura y Deploy
- Staging público desplegado **desde la semana 1**, aunque casi vacío.
- Variables de entorno, dominios, envío real de emails, CORS resueltos temprano.
- Diagrama de arquitectura + registro de supuestos como entregable formal.

---

## 3. Diagrama de arquitectura (alto nivel)

```
┌─────────────────────────────────────────────────────────────┐
│                     FRONTENDS (3 componentes)                │
│   Reporte de industria │ Calculadora │ Benchmark de madurez  │
└───────────────┬─────────────┬──────────────┬────────────────┘
                │             │              │
                ▼             ▼              ▼
        ┌───────────────────────────────────────────┐
        │            M3 · API GATEWAY               │
        │   /v1/identity  /v1/calc  /v1/benchmark   │
        │   validación · CORS · rate limit          │
        └───┬──────────┬───────────┬──────────┬─────┘
            │          │           │          │
            ▼          ▼           ▼          ▼
       ┌────────┐ ┌────────┐ ┌──────────┐ ┌──────────┐
       │M1      │ │M4      │ │M5        │ │M7        │
       │Identi- │ │Calcu-  │ │Bench-    │ │Outreach  │
       │dad     │ │ladora  │ │mark      │ │(cola)    │
       └───┬────┘ └───┬────┘ └────┬─────┘ └────┬─────┘
           │          │           │            │
           └──────────┴─────┬─────┴────────────┘
                            ▼
                 ┌────────────────────┐
                 │  M2 · BASE DE DATOS │
                 │ operadores · calc · │
                 │ benchmark · outreach│
                 └─────────┬───────────┘
                           │
          benchmark completado (evento)
                           ▼
                 ┌────────────────────┐        ┌──────────────┐
                 │ M6 · PDF GENERATOR │───────▶│ Email al     │
                 │ template forest/gold│        │ operador     │
                 └────────────────────┘        └──────────────┘

                 ┌────────────────────┐
                 │ M8 · DASHBOARD      │  (acceso interno del equipo)
                 │ respuestas + outreach│
                 └────────────────────┘
```

---

## 4. Máquina de estados del operador

Casi todo el sistema cuelga de este ciclo de vida. Modelarlo bien es la mitad del proyecto.

```
anónimo
  → email_capturado        (M1: entra por calculadora o invitación)
    → calculadora_completa (M4: KPIs guardados)
      → benchmark_completo (M5: percentil calculado)
        → pdf_generado     (M6: documento enviado)
          → [lead calificado → visible en dashboard M8]
```

Un operador puede entrar por cualquier punto; el estado avanza, nunca retrocede.

---

## 5. Schema base de la base de datos (borrador)

```sql
-- El pivote: identidad = email
operators (
  id              uuid primary key,
  email           text unique not null,
  company         text,
  source          text,          -- calculator | benchmark | outreach
  state           text not null, -- máquina de estados (sección 4)
  created_at      timestamptz default now()
)

-- Resultados de la calculadora (reusados en el PDF)
calculator_results (
  id              uuid primary key,
  operator_id     uuid references operators(id),
  inputs          jsonb,         -- parámetros del facility
  kpis            jsonb,         -- capacidad desperdiciada, costos, etc.
  created_at      timestamptz default now()
)

-- Respuestas del benchmark + posición calculada
benchmark_responses (
  id              uuid primary key,
  operator_id     uuid references operators(id),
  answers         jsonb,
  score           numeric,
  percentile      numeric,       -- posición vs. distribución (incluye semilla)
  is_seed         boolean default false, -- distingue datos sembrados
  created_at      timestamptz default now()
)

-- PDFs generados
generated_pdfs (
  id              uuid primary key,
  operator_id     uuid references operators(id),
  file_url        text,
  generated_at    timestamptz default now()
)

-- Outreach: cada contacto es un registro CON ESTADO
outreach_campaigns (
  id              uuid primary key,
  name            text,
  created_at      timestamptz default now()
)

outreach_contacts (
  id              uuid primary key,
  campaign_id     uuid references outreach_campaigns(id),
  email           text not null,
  status          text not null, -- pending | invited | opened | completed
  operator_id     uuid references operators(id), -- se enlaza al completar
  invited_at      timestamptz,
  completed_at    timestamptz,
  unique (campaign_id, email)     -- idempotencia
)
```

---

## 6. Endpoints principales (borrador de API Gateway)

| Método | Ruta | Módulo | Propósito |
|--------|------|--------|-----------|
| POST | `/v1/identity/capture` | M1 | Captura email, devuelve magic link/token |
| GET  | `/v1/identity/verify` | M1 | Valida token, "inicia sesión" ligera |
| POST | `/v1/calculator/run` | M4 | Calcula y persiste KPIs |
| POST | `/v1/benchmark/submit` | M5 | Envía respuestas, calcula percentil, dispara PDF |
| GET  | `/v1/pdf/:operatorId` | M6 | Descarga/estado del PDF generado |
| POST | `/v1/outreach/upload` | M7 | Sube CSV, crea campaña |
| POST | `/v1/outreach/:id/send` | M7 | Encola invitaciones |
| GET  | `/v1/dashboard/summary` | M8 | Respuestas acumuladas + completitud (interno) |

---

## 7. Requisitos no funcionales

- **Staging público** accesible desde la semana 1 (requisito duro del criterio de éxito).
- **Automatización real:** outreach y generación de PDF sin intervención manual.
- **Idempotencia** en outreach y en generación de PDF (no duplicar envíos).
- **Arranque en frío resuelto:** el benchmark nunca devuelve "1 de 1" al primer operador.
- **Branding fiel:** la paleta forest-green + gold y el logo deben verse institucionales en el PDF.
- **Supuestos documentados:** el brief da permiso explícito para llenar vacíos con criterio; cada supuesto se registra en la arquitectura.

---

## 8. Prioridades si el tiempo aprieta

Alcance ambicioso para 5 semanas. Si algo se recorta, proteger **dos cosas** por encima de todo:

1. **La integración de punta a punta** — que un operador recorra de verdad calculadora → benchmark → PDF, y que el equipo lo vea en el dashboard.
2. **La calidad del PDF** — es el activo comercial que cierra el embudo.

Candidato natural a versión simplificada: el **outreach** (por ejemplo, envío en lote más básico) sin comprometer el resto.

---

## 9. Orden de trabajo sugerido

| Semana | Foco |
|--------|------|
| S1 | M2 (schema congelado) + M9 (staging público vacío) + M3 (gateway esqueleto) |
| S2 | M1 (identidad/captura) + M4 (calculadora backend) |
| S3 | M5 (benchmark + percentiles + datos semilla) |
| S4 | M6 (PDF generator institucional) — la pieza estrella |
| S5 | M7 (outreach) + M8 (dashboard) + integración end-to-end |
