# 09 — Roadmap

## Project Ghost Load

---

## Versión 1.0 — MVP Core
**Objetivo:** Validar el embudo completo: registro → benchmark → PDF → lead calificado

### Módulos
| Módulo            | Descripción                                                    | Estado     |
|-------------------|----------------------------------------------------------------|------------|
| Identity          | Login con Clerk, 2 roles (Admin, Operator)                     | Pendiente  |
| Company           | Registro manual y carga masiva CSV (name, email, company)      | Pendiente  |
| Benchmark         | Cuestionario con guardado de progreso automático               | Pendiente  |
| Calculator        | Score /100, percentil, nivel de madurez, KPIs, waste index     | Pendiente  |
| PDF Generator     | Generación automática y envío por email (Amazon SES)           | Pendiente  |

### Criterios de éxito V1.0
- Admin puede cargar empresas vía CSV y ver errores de validación
- Operator recibe email de invitación, completa benchmark y recibe PDF con score y nivel de madurez
- Pipeline de estados funciona de REGISTERED a PDF_GENERATED

---

## Versión 1.1 — Dashboard & Outreach
**Objetivo:** Dar al equipo de ventas visibilidad completa y herramientas de seguimiento

### Módulos
| Módulo    | Descripción                                                          | Estado  |
|-----------|----------------------------------------------------------------------|---------|
| Dashboard | KPIs globales, histogramas, evolución score, embudo de conversión    | Futuro  |
| Outreach  | Pipeline de estados, notas de seguimiento, historial de cambios      | Futuro  |

### Criterios de éxito V1.1
- Admin ve KPIs: operadores totales, benchmarks, score promedio, percentil, PDFs, llamadas
- Admin ve distribución de niveles de madurez y embudo de conversión completo
- Admin puede cambiar estado de outreach y agregar notas
- Tabla de últimas respuestas con score, percentil y nivel de madurez por empresa

---

## Versión 1.2 — AI Insights
**Objetivo:** Agregar inteligencia al benchmark y al proceso de priorización de leads

### Módulos
| Módulo                | Descripción                                        | Estado  |
|-----------------------|----------------------------------------------------|---------|
| Benchmark Insights    | Diagnóstico IA incluido en el PDF                  | Futuro  |
| Recommendation Engine | Recomendaciones priorizadas por IA                 | Futuro  |
| Lead Scoring          | Clasificación automática de calidad del lead       | Futuro  |
| AI Dashboard Panel    | Tendencias e insights agregados del portfolio      | Futuro  |

### Criterios de éxito V1.2
- PDF incluye insights y recomendaciones generadas por Google AI Studio
- Admin ve score de lead por empresa en el pipeline con prioridad de outreach

---

## Versión 2.0 — Plataforma Avanzada
**Objetivo:** Transformar Ghost Load en una plataforma de inteligencia operacional

### Módulos
| Módulo                 | Descripción                                       | Estado  |
|------------------------|---------------------------------------------------|---------|
| Predictive Capacity    | Proyección de capacidad basada en tendencias      | Futuro  |
| Digital Twin           | Modelo virtual del Data Center                    | Futuro  |
| Real-Time Monitoring   | Monitoreo en tiempo real de KPIs                  | Futuro  |

---

## Timeline

```
Q3 2026 — V1.0 MVP
├── Identity + Auth (Clerk) — 2 roles
├── Company (manual + CSV)
├── Benchmark + Calculator (score, percentil, madurez)
└── PDF Generator + Amazon SES

Q4 2026 — V1.1 Dashboard & Outreach
├── Dashboard con KPIs, gráficos y embudo
└── Pipeline de outreach con seguimiento

Q1 2027 — V1.2 AI Insights
├── Google AI Studio integration
├── Lead Scoring automático
└── Recomendaciones en PDF

2027+ — V2.0 Plataforma Avanzada
├── Predictive Capacity
├── Digital Twin
└── Real-Time Monitoring
```

---

## Equipo

| Nombre                        | Rol                   |
|-------------------------------|-----------------------|
| Axel Alfredo Mora Estrada     | Frontend Developer    |
| Ezequiel Barretta             | Frontend Developer    |
| Carlos Lobo                   | Full Stack Developer  |
| Karina Kozlowski              | Cloud Engineer        |
| Cristian Andres Vargas Gatica | Backend Developer     |
| Santiago Rios                 | Backend Developer     |
| Andersson Patsy Godoy Garcia  | Software Engineer     |
| Eduin Pino                    | Cloud Developer       |

## Gestión — Trello

Sprint Planning · Product Backlog · User Stories · Tasks · Bugs · Releases · Roadmap · Checklist
