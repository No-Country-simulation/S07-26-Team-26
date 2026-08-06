# 03 — Software Requirements Specification (SRS)

## Project Ghost Load — AI-Powered Feedback & Benchmark Platform

**Versión:** 1.1  
**Fecha:** Julio 2026  
**Estado:** Armonizado con `02_System_Architecture.md` y `10_Hexagonal_Architecture_DDD.md`

---

## 1. Introducción

### 1.1 Propósito
Este documento define los requisitos funcionales y no funcionales del sistema Ghost Load. Sirve como referencia oficial para el equipo de desarrollo, diseño y cloud.

### 1.2 Alcance
Ghost Load es una plataforma SaaS que permite a operadores de Data Centers completar una evaluación de capacidad (calculadora + benchmark), recibir un reporte institucional en PDF y convertirse en leads calificados.

### 1.3 Definiciones

| Término         | Definición                                                      |
|-----------------|-----------------------------------------------------------------|
| Admin           | Usuario con acceso al panel administrativo via JWT              |
| Operator        | Usuario que completa la evaluación (sin contraseña, token único)|
| Evaluación      | Proceso que une registro + calculadora + benchmark + resultados |
| Benchmark       | Cuestionario de madurez del Data Center (20 preg, 5 módulos)    |
| KPI             | Indicador clave de rendimiento calculado desde el benchmark     |
| PDF Institucional | Reporte generado tras completar el benchmark (futuro)         |
| Pipeline        | Flujo de estados de invitación (UPLOADED → SENT → VISITED → STARTED → COMPLETED) |

---

## 2. Usuarios del Sistema

El sistema opera con **2 roles únicos**.

### 2.1 Admin (ROLE_ADMIN)
- Acceso total al sistema
- Login con email + contraseña (JWT)
- Importa contactos via CSV y crea campañas de invitación
- Ve resultados de evaluaciones de todos los operadores
- Gestiona usuarios del sistema
- El primer Admin se crea mediante seed en base de datos

### 2.2 Operator (ROLE_OPERATOR)
- Accede mediante un token único (X-Evaluation-Token), sin contraseña
- Completa la calculadora de capacidad y el cuestionario de benchmark
- Ve sus resultados: score /100, percentil, nivel de madurez, KPIs
- Ve únicamente los datos de su propia evaluación

---

## 3. Requisitos Funcionales

---

### RF-01 — Módulo Identity (Autenticación)

| ID      | Requisito                                                                 | Prioridad |
|---------|---------------------------------------------------------------------------|-----------|
| RF-01.1 | El Admin debe poder iniciar sesión con email y contraseña (POST /api/v1/admin/auth/login) | Alta |
| RF-01.2 | El sistema debe generar un JWT con el rol embebido tras autenticación exitosa | Alta |
| RF-01.3 | El primer Admin debe crearse como seed en la base de datos                | Alta      |
| RF-01.4 | El sistema debe validar el JWT en cada request administrativo (HTTP 401 sin token, 403 con rol incorrecto) | Alta |
| RF-01.5 | El Operator debe autenticarse mediante X-Evaluation-Token (sin contraseña) | Alta |
| RF-01.6 | El frontend debe redirigir al Dashboard (Admin) o a la Evaluación (Operator) según rol | Alta |
| RF-01.7 | [future] El sistema debe soportar MFA y OAuth                            | Media     |

---

### RF-02 — Módulo Company (Contexto de Empresa)

| ID      | Requisito                                                                 | Prioridad |
|---------|---------------------------------------------------------------------------|-----------|
| RF-02.1 | El sistema debe capturar el nombre de la empresa durante el registro del operador | Alta |
| RF-02.2 | Cada evaluación está asociada a un operador y su empresa                  | Alta      |
| RF-02.3 | [future] El Admin debe poder registrar empresas manualmente               | Media     |
| RF-02.4 | [future] El Admin debe poder importar empresas masivamente via CSV        | Media     |
| RF-02.5 | [future] El sistema debe soportar un pipeline de estado por empresa       | Media     |

La empresa no es una entidad separada en el MVP. Es un atributo del operador registrado al crear la evaluación.

---

### RF-03 — Módulo Benchmark (Cuestionario)

| ID      | Requisito                                                                 | Prioridad |
|---------|---------------------------------------------------------------------------|-----------|
| RF-03.1 | El Operator debe poder acceder al cuestionario con su X-Evaluation-Token  | Alta      |
| RF-03.2 | El sistema debe calcular KPIs y score al completar el cuestionario        | Alta      |
| RF-03.3 | El sistema debe calcular el score por módulo (5 módulos, 4 preguntas cada uno) | Alta |
| RF-03.4 | El sistema debe cambiar el estado de la evaluación a `BENCHMARK_COMPLETED` al finalizar | Alta |
| RF-03.5 | Los resultados del benchmark deben almacenarse en base de datos           | Alta      |
| RF-03.6 | [future] El cuestionario debe guardar progreso automáticamente            | Media     |
| RF-03.7 | [future] El Admin debe recibir notificación cuando un operador complete el benchmark | Media |

---

### RF-04 — Módulo Calculator

| ID      | Requisito                                                                 | Prioridad |
|---------|---------------------------------------------------------------------------|-----------|
| RF-04.1 | El sistema debe calcular KPIs de capacidad a partir de los inputs del operador | Alta |
| RF-04.2 | El sistema debe calcular: capacidad no productiva, % de utilización, % no productivo, costo anual estimado | Alta |
| RF-04.3 | El sistema debe calcular un score /100 por evaluación                     | Alta      |
| RF-04.4 | El sistema debe asignar un nivel de madurez según el score:               | Alta      |
|         | OPTIMIZED (90-100) / ADVANCED (75-89) / MANAGED (50-74) / DEVELOPING (25-49) / INITIAL (0-24) | |
| RF-04.5 | El sistema debe calcular el percentil (usando datos de demostración con disclaimer en MVP) | Alta |
| RF-04.6 | El sistema debe exponer los resultados calculados al módulo de PDF (futuro) | Alta |
| RF-04.7 | [future] Los cálculos deben cachearse en Redis para evitar recalculo      | Media     |
| RF-04.8 | [future] Los resultados deben incluir comparación contra benchmarks de la industria | Media |

---

### RF-05 — Módulo PDF Generator

| ID      | Requisito                                                                 | Prioridad |
|---------|---------------------------------------------------------------------------|-----------|
| RF-05.1 | [future] El sistema debe generar automáticamente el PDF al completar el benchmark | Alta |
| RF-05.2 | [future] El PDF debe incluir los KPIs y el score                          | Alta      |
| RF-05.3 | [future] El Operator debe poder descargar el PDF desde el sistema         | Alta      |
| RF-05.4 | [future] El sistema debe almacenar el PDF y enviarlo por email al operador | Media    |

El módulo PDF no forma parte del MVP actual. Los estados `REPORT_GENERATING`, `REPORT_COMPLETED` y `REPORT_FAILED` están definidos en el modelo de dominio pero no hay implementación.

---

### RF-06 — Módulo Outreach (Campañas e Invitaciones)

| ID      | Requisito                                                                 | Prioridad |
|---------|---------------------------------------------------------------------------|-----------|
| RF-06.1 | El Admin debe poder importar contactos desde un archivo CSV               | Alta      |
| RF-06.2 | El sistema debe validar cada fila del CSV (campos requeridos, email, duplicados) | Alta |
| RF-06.3 | El Admin debe poder crear campañas de invitación desde contactos importados | Alta |
| RF-06.4 | El Admin debe poder enviar invitaciones a los contactos de una campaña    | Alta      |
| RF-06.5 | Cada invitación debe tener un token único no predecible                   | Alta      |
| RF-06.6 | El sistema debe trackear el estado de cada invitación: UPLOADED → SENT → VISITED → STARTED → COMPLETED | Alta |
| RF-06.7 | Los emails deben procesarse en background sin bloquear la request         | Alta      |
| RF-06.8 | [future] El Admin debe ver el pipeline de invitaciones con filtros        | Media     |
| RF-06.9 | [future] El Admin debe poder registrar notas de seguimiento por contacto  | Media     |

**Estados de invitación:**
`UPLOADED → SENT → VISITED → STARTED → COMPLETED`

**Template CSV:**
```
first_name, last_name, email, company, position
```

---

### RF-07 — Módulo Dashboard (Admin)

| ID      | Requisito                                                                 | Prioridad |
|---------|---------------------------------------------------------------------------|-----------|
| RF-07.1 | El Admin debe poder ver KPIs globales: operadores totales, benchmarks completados, score promedio | Alta |
| RF-07.2 | El Admin debe poder ver el listado de evaluaciones completadas            | Alta      |
| RF-07.3 | [future] El Admin debe ver KPIs con variación vs período anterior         | Media     |
| RF-07.4 | [future] El sistema debe mostrar histograma de distribución de scores     | Media     |
| RF-07.5 | [future] El sistema debe mostrar evolución del score promedio mensual     | Media     |
| RF-07.6 | [future] El sistema debe mostrar distribución de niveles de madurez       | Media     |
| RF-07.7 | [future] El sistema debe mostrar el embudo de conversión                  | Media     |
| RF-07.8 | [future] El Operator debe ver sus propios resultados (score, madurez, KPIs) | Alta    |

El dashboard básico (KPIs simples + listado) es MVP. Los charts y gráficos avanzados son futuros.

---

### RF-08 — Módulo AI Integration (V1.2)

| ID      | Requisito                                                                 | Prioridad |
|---------|---------------------------------------------------------------------------|-----------|
| RF-08.1 | [future] El sistema debe enviar datos del benchmark a Google AI Studio     | Media     |
| RF-08.2 | [future] El sistema debe mostrar insights generados por IA en el dashboard | Media     |
| RF-08.3 | [future] El sistema debe incluir recomendaciones de IA en el PDF           | Media     |
| RF-08.4 | [future] El sistema debe clasificar leads mediante IA                      | Baja      |

---

## 4. Requisitos No Funcionales

### 4.1 Seguridad

| ID     | Requisito                                                                 |
|--------|---------------------------------------------------------------------------|
| RNF-01 | Las contraseñas de admin deben almacenarse con hash seguro (bcrypt)       |
| RNF-02 | Toda comunicación debe realizarse sobre HTTPS                             |
| RNF-03 | El JWT de admin debe expirar y requerir renovación                        |
| RNF-04 | Los endpoints deben validar el rol antes de procesar la solicitud         |
| RNF-05 | [future] El acceso a PDFs debe requerir URLs firmadas temporalmente       |

### 4.2 Performance

| ID     | Requisito                                                                 |
|--------|---------------------------------------------------------------------------|
| RNF-06 | El tiempo de respuesta de los endpoints críticos debe ser menor a 500ms   |
| RNF-07 | El envío de emails debe procesarse en background (cola email_outbox)      |
| RNF-08 | [future] Los cálculos del benchmark deben cachearse en Redis              |
| RNF-09 | [future] La generación del PDF debe procesarse de forma asíncrona         |

### 4.3 Escalabilidad

| ID     | Requisito                                                                 |
|--------|---------------------------------------------------------------------------|
| RNF-10 | La arquitectura debe soportar escalado horizontal                          |
| RNF-11 | La base de datos debe soportar backups automáticos                        |
| RNF-12 | El sistema debe soportar carga masiva de al menos 500 contactos por CSV   |

### 4.4 Observabilidad

| ID     | Requisito                                                                 |
|--------|---------------------------------------------------------------------------|
| RNF-13 | Todos los servicios deben enviar logs                                     |
| RNF-14 | Los endpoints deben estar documentados con OpenAPI                        |

### 4.5 Usabilidad

| ID     | Requisito                                                                 |
|--------|---------------------------------------------------------------------------|
| RNF-15 | La UI debe ser responsiva (mobile y desktop)                              |
| RNF-16 | [future] El cuestionario debe guardar progreso automáticamente            |
| RNF-17 | Los errores de validación del CSV deben ser descriptivos                  |

---

## 5. Restricciones Técnicas

- Frontend: Next.js 15, React, Tailwind CSS, Zustand, React Query
- Backend: Java 21, Spring Boot, Maven
- Autenticación Admin: JWT (HMAC-SHA256) sin Clerk
- Autenticación Operator: X-Evaluation-Token (sin contraseña)
- Base de datos: PostgreSQL con Flyway
- Arquitectura backend: DDD + Hexagonal (Ports & Adapters) — ver `10_Hexagonal_Architecture_DDD.md`
- Gestión del proyecto: Trello

---

## 6. Casos de Uso Principales

### CU-01: Login Admin
**Actor:** Admin  
**Flujo:** Admin ingresa email y contraseña → Backend valida contra `admin_users` → JWT generado con rol ADMIN → Redirección al Dashboard

### CU-02: Crear evaluación (Operator)
**Actor:** Operator  
**Flujo:** Operator ingresa email + empresa → Sistema crea operador + evaluación + token único → Devuelve token en respuesta HTTP

### CU-03: Completar calculadora
**Actor:** Operator  
**Flujo:** Operator envía datos de capacidad → Backend valida y calcula KPIs → Evaluación pasa a CALCULATOR_COMPLETED

### CU-04: Completar benchmark
**Actor:** Operator  
**Flujo:** Operator responde 20 preguntas → Backend calcula score/100, score por módulo, percentil, nivel de madurez → Evaluación pasa a BENCHMARK_COMPLETED

### CU-05: Importar contactos CSV
**Actor:** Admin  
**Flujo:** Admin sube CSV → Backend valida filas → Contactos importados → Resumen con importados, fallidos, errores

### CU-06: Crear y enviar campaña
**Actor:** Admin  
**Flujo:** Admin crea campaña desde contactos → Envía invitaciones → Backend genera tokens únicos, encola emails → Worker envía via SMTP

### CU-07: Ver KPIs globales
**Actor:** Admin  
**Flujo:** Admin accede al dashboard → Ve total de operadores, benchmarks completados, score promedio

---

## 7. Roadmap de Versiones

| Versión | Módulos incluidos                          | Estado actual |
|---------|--------------------------------------------|---------------|
| V1.0    | Assessment (registro, calculadora, benchmark), Admin Auth, Outreach (CSV, campañas, invitaciones) | En desarrollo (~50% backend) |
| V1.1    | Dashboard (KPIs básicos + listado), mejora de Outreach               | No iniciado   |
| V1.2    | PDF Generator, Dashboard avanzado (charts), AI Insights               | No iniciado   |
| V2.0    | Predictive Capacity, Digital Twin, Real-Time Monitoring               | Futuro        |
