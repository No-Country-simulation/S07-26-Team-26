# 02 — System Architecture

## Project Ghost Load — AI-Powered Feedback & Benchmark Platform

---

## Diagrama de Arquitectura General

```
USUARIOS (Operadores / Empresas / Equipos)
   │
   ▼
FRONT END — Web SaaS (Next.js 15)
Next.js · Tailwind CSS · Zustand · React Query · UI/UX
   │
   ▼
AUTHENTICATION — Clerk
User Authentication · Session Management · OAuth · MFA · Organizations
Login: Correo
   │
   ▼
BACK END (API) — Java Spring Boot
RESTful API · Business Logic · Integraciones · Seguridad · Validaciones
   │
   ├──────────────────────┬──────────────────────┐
   ▼                      ▼                      ▼
API GATEWAY          SERVICIOS SERVERLESS    MONITOREO
Javazon API Gateway  AWS Lambda              Amazon CloudWatch
Enrutamiento         Procesos asíncronos     Logs · Métricas · Alarmas
Rate Limiting        Tareas programadas
Autenticación        Integraciones
   │
   ▼
BASE DE DATOS & ALMACENAMIENTO
   │
   ├─────────────────┬──────────────────┐
   ▼                 ▼                  ▼
DATABASE          STORAGE            CACHE
Amazon RDS        Amazon S3          Amazon ElastiCache (Redis)
PostgreSQL        Archivos           Cache de sesiones
Datos relacionales Reportes          Cache de consultas
Alta disponibilidad Exportaciones    Colas de tareas
Backups automáticos Backups
   │
   ├─────────────────────────────────────┐
   ▼                                     ▼
EXPORTACIÓN & IA                    EMAIL SERVICE
Exportar UI desde Stitch            Amazon SES
Importar a IA Studio (Next.js)      Envío de emails
                                    Plantillas
                                    Notificaciones
                                    Tracking
   │
   ▼
CDN & DNS
Amazon CloudFront (CDN)
Amazon Route 53 (DNS)
```

---

## DDD + Arquitectura Hexagonal (Backend)

DDD y Hexagonal se combinan naturalmente:

| DDD define QUÉ              | Hexagonal define CÓMO se conecta          |
|-----------------------------|-------------------------------------------|
| Bounded Contexts            | Cada contexto es un hexágono independiente|
| Entities / Value Objects    | Viven en el Domain Core                   |
| Aggregates                  | Raíz del dominio por contexto             |
| Domain Services             | Use Cases (ports in)                      |
| Repositories (interface)    | Ports out                                 |
| Infrastructure              | Adapters out                              |

---

## Bounded Contexts (DDD)

### Identity Context
```
Aggregate Root: User
├── Entities:        User, Role, Session
├── Value Objects:   Email, Password, Token
└── Domain Service:  AuthService
    ├── Port In:     AuthUseCase
    └── Port Out:    UserRepository, SessionRepository
```

### Company Context
```
Aggregate Root: Company
├── Entities:        Company, Operator, Founder
├── Value Objects:   CompanyName, Industry, Country
└── Domain Service:  CompanyService
    ├── Port In:     CompanyUseCase, ImportUseCase
    └── Port Out:    CompanyRepository, OperatorRepository
```

### Benchmark Context
```
Aggregate Root: BenchmarkResult
├── Entities:        BenchmarkResult, Question, Answer
├── Value Objects:   KPI, CapacityScore, WasteIndex
└── Domain Service:  BenchmarkService
    ├── Port In:     BenchmarkUseCase, CalculatorUseCase
    └── Port Out:    BenchmarkRepository, CacheService
```

### PDF Context
```
Aggregate Root: GeneratedPDF
├── Entities:        GeneratedPDF, ReportData, ReportSection
├── Value Objects:   PDFStatus, ReportTemplate
└── Domain Service:  PDFService
    ├── Port In:     PDFUseCase
    └── Port Out:    PDFGenerator, StorageService, EmailService
```

### Outreach Context
```
Aggregate Root: Campaign
├── Entities:        Campaign, Contact, OutreachEvent
├── Value Objects:   OutreachStatus, ContactInfo
└── Domain Service:  OutreachService
    ├── Port In:     OutreachUseCase, DashboardUseCase
    └── Port Out:    CampaignRepository, ContactRepository, EmailService
```

---

## Estructura de Paquetes (Spring Boot)

```
com.ghostload
│
├── identity/
│   ├── domain/
│   │   ├── model/           → User, Role, Session
│   │   ├── ports/
│   │   │   ├── in/          → AuthUseCase
│   │   │   └── out/         → UserRepository, SessionRepository
│   │   └── service/         → AuthUseCaseImpl
│   └── adapters/
│       ├── in/rest/         → AuthController
│       └── out/
│           ├── persistence/ → UserJpaAdapter
│           └── clerk/       → ClerkAuthAdapter
│
├── company/
│   ├── domain/
│   │   ├── model/           → Company, Operator, Founder
│   │   ├── ports/
│   │   │   ├── in/          → CompanyUseCase, ImportUseCase
│   │   │   └── out/         → CompanyRepository, OperatorRepository
│   │   └── service/         → CompanyUseCaseImpl, ImportUseCaseImpl
│   └── adapters/
│       ├── in/rest/         → CompanyController
│       └── out/
│           ├── persistence/ → CompanyJpaAdapter
│           └── email/       → SESInvitationAdapter
│
├── benchmark/
│   ├── domain/
│   │   ├── model/           → BenchmarkResult, Question, Answer
│   │   ├── ports/
│   │   │   ├── in/          → BenchmarkUseCase, CalculatorUseCase
│   │   │   └── out/         → BenchmarkRepository, CacheService
│   │   └── service/         → BenchmarkUseCaseImpl, CalculatorUseCaseImpl
│   └── adapters/
│       ├── in/rest/         → BenchmarkController, CalculatorController
│       └── out/
│           ├── persistence/ → BenchmarkJpaAdapter
│           └── cache/       → RedisAdapter
│
├── pdf/
│   ├── domain/
│   │   ├── model/           → GeneratedPDF, ReportData
│   │   ├── ports/
│   │   │   ├── in/          → PDFUseCase
│   │   │   └── out/         → PDFGenerator, StorageService, EmailService
│   │   └── service/         → PDFUseCaseImpl
│   └── adapters/
│       ├── in/rest/         → PDFController
│       └── out/
│           ├── pdf/         → PDFGeneratorAdapter
│           ├── storage/     → S3StorageAdapter
│           └── email/       → SESEmailAdapter
│
├── outreach/
│   ├── domain/
│   │   ├── model/           → Campaign, Contact, OutreachEvent
│   │   ├── ports/
│   │   │   ├── in/          → OutreachUseCase, DashboardUseCase
│   │   │   └── out/         → CampaignRepository, ContactRepository
│   │   └── service/         → OutreachUseCaseImpl, DashboardUseCaseImpl
│   └── adapters/
│       ├── in/rest/         → OutreachController, DashboardController
│       └── out/
│           ├── persistence/ → CampaignJpaAdapter
│           └── email/       → SESOutreachAdapter
│
└── shared/
    ├── domain/              → Value Objects compartidos
    ├── infrastructure/      → Config Spring, JWT Filter, Exception Handler
    └── adapters/
        └── out/
            ├── lambda/      → AWSLambdaAdapter (tareas asíncronas)
            └── ai/          → GoogleAIStudioAdapter
```

---

## Roles y Autenticación

### Roles del Sistema (2 roles)

```
ADMIN (ROLE_ADMIN)
   │
   ├→ Acceso total al sistema
   ├→ Ve todos los operadores y todas las empresas
   ├→ Registra empresas (manual o CSV masivo)
   ├→ Crea y gestiona operadores
   ├→ Gestiona pipeline de outreach completo
   ├→ Ve métricas globales, scores, percentiles, niveles de madurez
   ├→ Descarga y regenera PDFs
   └→ Gestiona usuarios y configuración

OPERATOR (ROLE_OPERATOR)
   │
   ├→ Completa el benchmark / cuestionario
   ├→ Ve sus resultados (score /100, percentil, nivel de madurez)
   ├→ Descarga su PDF institucional
   └→ Ve únicamente los datos de su empresa
```

### Flujo de Autenticación

```
Login Screen (única pantalla compartida)
         │
         ▼
Clerk Authentication
User Auth · Session Management · OAuth · MFA · Organizations
         │
         ▼
JWT generado con rol embebido (ROLE_ADMIN | ROLE_OPERATOR)
         │
    ┌────┴──────────┐
    ▼               ▼
 Dashboard        Form
  Admin         Benchmark
```

- El primer Admin se crea como seed en base de datos (sin registro público)
- JWT almacenado en Zustand (frontend)
- Guards de ruta por rol en Next.js

---

## Pipeline de Outreach

### Flujo Masivo (CSV)

```
Admin descarga template CSV
         │
         ▼
Completa datos: company_name, industry, country,
operator_email, founder_name, founder_email
         │
         ▼
POST /admin/companies/import
         │
    ┌────┴────┐
    ▼         ▼
 Válidas    Errores → Reporte descargable
    │
    ▼
Empresas + Operadores creados en batch
         │
         ▼
Emails de invitación enviados bulk (Amazon SES)
         │
         ▼
Estado inicial: INVITED
```

### Estados de Tracking

| Estado              | Descripción                              |
|---------------------|------------------------------------------|
| `REGISTERED`        | Admin registró la empresa                |
| `INVITED`           | Email enviado al operador                |
| `IN_PROGRESS`       | Operador empezó el benchmark             |
| `COMPLETED`         | Benchmark finalizado                     |
| `PDF_GENERATED`     | PDF institucional creado                 |
| `OUTREACH_PENDING`  | Admin aún no contactó al founder         |
| `OUTREACH_SENT`     | Admin envió contacto al founder          |
| `MEETING_SCHEDULED` | Reunión agendada                         |
| `CONVERTED`         | Lead convertido                          |
| `LOST`              | Sin respuesta o descartado               |

---

## AWS Stack Completo

| Servicio                | Rol                                      |
|-------------------------|------------------------------------------|
| Amazon EC2              | Virtual Machines / Compute               |
| Amazon RDS              | PostgreSQL — Base de datos               |
| Amazon S3               | Object Storage — archivos, PDFs, backups |
| Amazon ElastiCache      | Redis — cache y colas                    |
| AWS Lambda              | Serverless — procesos asíncronos, tareas programadas |
| Amazon API Gateway      | API Management — enrutamiento, rate limiting |
| Amazon SES              | Email Service — invitaciones, notificaciones, tracking |
| Amazon CloudWatch       | Monitoring & Logs — métricas, alarmas    |
| Amazon Route 53         | DNS Management                           |
| Amazon CloudFront       | CDN                                      |
| AWS Certificate Manager | SSL/TLS Certificates                     |
| AWS IAM                 | Identity & Access Management             |

---

## Stack Tecnológico

### Frontend
| Tecnología    | Rol                         |
|---------------|-----------------------------|
| Next.js 15    | Framework principal         |
| React         | UI Components               |
| Tailwind CSS  | Estilos                     |
| Zustand       | Estado global + JWT storage |
| React Query   | Cache y estado del servidor |
| Clerk         | Authentication              |

### Backend
| Tecnología   | Rol                          |
|--------------|------------------------------|
| Java         | Lenguaje principal           |
| Spring Boot  | Framework REST + Security    |

### Diseño & AI
| Herramienta       | Rol                                      |
|-------------------|------------------------------------------|
| Stitch            | Exportación de UI                        |
| Google AI Studio  | IA — Insights, recomendaciones, benchmark|

---

## Equipo

| Nombre                       | Rol                   |
|------------------------------|-----------------------|
| Axel Alfredo Mora Estrada    | Frontend Developer    |
| Ezequiel Barretta            | Frontend Developer    |
| Carlos Lobo                  | Full Stack Developer  |
| Karina Kozlowski             | Cloud Engineer        |
| Cristian Andres Vargas Gatica| Backend Developer     |
| Santiago Rios                | Backend Developer     |
| Andersson Patsy Godoy Garcia | Software Engineer     |
| Eduin Pino                   | Cloud Developer       |

---

## Gestión del Proyecto — Trello

Sprint Planning · Product Backlog · User Stories · Tasks · Bugs · Releases · Roadmap · Checklist

> Planificar. Ejecutar. Entregar. Visibilidad total del proyecto en un solo lugar.

---

## Principios de Desarrollo

- Domain-Driven Design (DDD)
- Hexagonal Architecture (Ports & Adapters)
- SOLID
- REST First
- DTO Pattern
- Repository Pattern
- Conventional Commits
- Feature Branch Workflow
- Pull Requests Required
- Code Reviews
- OpenAPI Documentation

---

## Atributos de Calidad

| Atributo       | Descripción                                    |
|----------------|------------------------------------------------|
| Secure         | Seguridad de nivel empresarial                 |
| Scalable       | Arquitectura escalable en AWS                  |
| Automatizado   | Procesos automatizados y eficientes            |
| IA-Potenciado  | Inteligencia artificial al servicio de tus datos|
| Observabilidad | Monitoreo y observabilidad end-to-end          |
