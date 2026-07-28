# 03 — Software Requirements Specification (SRS)

## Project Ghost Load — AI-Powered Feedback & Benchmark Platform

**Versión:** 1.0  
**Fecha:** Julio 2026  
**Estado:** En desarrollo

---

## 1. Introducción

### 1.1 Propósito
Este documento define los requisitos funcionales y no funcionales del sistema Ghost Load. Sirve como referencia oficial para el equipo de desarrollo, diseño y cloud.

### 1.2 Alcance
Ghost Load es una plataforma SaaS que permite a operadores de Data Centers completar un benchmark de capacidad, recibir un reporte institucional en PDF y convertirse en leads calificados para el equipo de ventas.

### 1.3 Definiciones

| Término         | Definición                                                      |
|-----------------|-----------------------------------------------------------------|
| Root Admin      | Usuario con acceso total al sistema, creado por seed            |
| Admin           | Usuario que gestiona empresas y pipeline de outreach            |
| Operator        | Usuario de la empresa que completa el benchmark                 |
| Founder Contact | Tomador de decisiones de la empresa objetivo                    |
| Benchmark       | Cuestionario de capacidad del Data Center                       |
| KPI             | Indicador clave de rendimiento calculado desde el benchmark     |
| PDF Institucional | Reporte generado automáticamente tras completar el benchmark  |
| Pipeline        | Flujo de estados de una empresa desde registro hasta conversión |

---

## 2. Usuarios del Sistema

El sistema opera con **2 roles únicos**.

### 2.1 Admin (ROLE_ADMIN)
- Acceso total sin restricciones
- Ve todos los operadores y todas las empresas del sistema
- Registra empresas manualmente o vía carga masiva CSV
- Crea y gestiona operadores
- Gestiona el pipeline de outreach completo
- Ve métricas globales: scores, percentiles, niveles de madurez, embudo de conversión
- Descarga y regenera PDFs institucionales
- Gestiona usuarios y configuración del sistema
- El primer Admin se crea mediante seed en base de datos (sin registro público)

### 2.2 Operator (ROLE_OPERATOR)
- Recibe invitación por email
- Completa el cuestionario de benchmark
- Ve sus resultados: score /100, percentil, nivel de madurez, KPIs
- Descarga el PDF institucional generado
- Ve únicamente los datos de su empresa

---

## 3. Requisitos Funcionales

---

### RF-01 — Módulo Identity (Autenticación)

| ID      | Requisito                                                                 | Prioridad |
|---------|---------------------------------------------------------------------------|-----------|
| RF-01.1 | El sistema debe proveer una pantalla de login única para los 2 roles     | Alta      |
| RF-01.2 | El login debe validar email y contraseña                                  | Alta      |
| RF-01.3 | El sistema debe generar un JWT con el rol embebido tras autenticación exitosa | Alta  |
| RF-01.4 | El frontend debe redirigir al Dashboard (Admin) o al Benchmark (Operator) según rol | Alta |
| RF-01.5 | El sistema debe soportar MFA mediante Clerk                               | Media     |
| RF-01.6 | El sistema debe soportar OAuth mediante Clerk                             | Media     |
| RF-01.7 | El primer Admin debe crearse como seed en la base de datos                | Alta      |
| RF-01.8 | El sistema debe invalidar el JWT al hacer logout                          | Alta      |
| RF-01.9 | El sistema debe proteger rutas según el rol (guards en Next.js)           | Alta      |

---

### RF-02 — Módulo Company (Gestión de Empresas)

| ID      | Requisito                                                                 | Prioridad |
|---------|---------------------------------------------------------------------------|-----------|
| RF-02.1 | El Admin debe poder registrar una empresa manualmente                     | Alta      |
| RF-02.2 | El Admin debe poder cargar empresas masivamente mediante CSV              | Alta      |
| RF-02.3 | El sistema debe validar cada fila del CSV antes de importar               | Alta      |
| RF-02.4 | El sistema debe generar un reporte de errores descargable tras la importación | Alta  |
| RF-02.5 | El sistema debe crear automáticamente el usuario Operator al registrar una empresa | Alta |
| RF-02.6 | El sistema debe enviar un email de invitación al Operator tras su creación | Alta     |
| RF-02.7 | El Admin debe poder ver el listado de sus empresas con su estado actual   | Alta      |
| RF-02.8 | El Root Admin debe poder ver todas las empresas del sistema               | Alta      |
| RF-02.9 | El sistema debe soportar exportación del pipeline a CSV                   | Media     |

**Template CSV requerido:**
```
company_name, industry, country, operator_email, founder_name, founder_email, notes
```

---

### RF-03 — Módulo Benchmark (Cuestionario)

| ID      | Requisito                                                                 | Prioridad |
|---------|---------------------------------------------------------------------------|-----------|
| RF-03.1 | El Operator debe poder acceder al cuestionario tras autenticarse          | Alta      |
| RF-03.2 | El cuestionario debe guardar progreso automáticamente                     | Alta      |
| RF-03.3 | El sistema debe calcular KPIs al completar el cuestionario                | Alta      |
| RF-03.4 | El sistema debe calcular el índice de capacidad desperdiciada             | Alta      |
| RF-03.5 | El estado de la empresa debe cambiar a `IN_PROGRESS` al iniciar          | Alta      |
| RF-03.6 | El estado debe cambiar a `COMPLETED` al finalizar el cuestionario        | Alta      |
| RF-03.7 | El Admin debe recibir notificación cuando una empresa complete el benchmark | Alta   |
| RF-03.8 | Los resultados del benchmark deben almacenarse en base de datos           | Alta      |

---

### RF-04 — Módulo Calculator

| ID      | Requisito                                                                 | Prioridad |
|---------|---------------------------------------------------------------------------|-----------|
| RF-04.1 | El sistema debe calcular KPIs a partir de los datos del benchmark         | Alta      |
| RF-04.2 | El sistema debe calcular un score /100 por operador                       | Alta      |
| RF-04.3 | El sistema debe calcular el percentil del operador en el ranking global   | Alta      |
| RF-04.4 | El sistema debe asignar un nivel de madurez según el score:               | Alta      |
|         | Orquestado (75-100) / Coordinado (50-74) / Reactivo (25-49) / Fragmentado (0-24) | |
| RF-04.5 | Los cálculos deben almacenarse en cache (Redis) para evitar recalculo     | Media     |
| RF-04.6 | El sistema debe exponer los resultados calculados al módulo PDF           | Alta      |
| RF-04.7 | Los resultados deben incluir comparación contra benchmarks de la industria | Alta     |

---

### RF-05 — Módulo PDF Generator

| ID      | Requisito                                                                 | Prioridad |
|---------|---------------------------------------------------------------------------|-----------|
| RF-05.1 | El sistema debe generar automáticamente el PDF al completar el benchmark  | Alta      |
| RF-05.2 | El PDF debe incluir los KPIs calculados y el índice de capacidad desperdiciada | Alta |
| RF-05.3 | El PDF debe incluir comparación contra benchmarks de la industria         | Alta      |
| RF-05.4 | El PDF generado debe almacenarse en Amazon S3                             | Alta      |
| RF-05.5 | El sistema debe enviar el PDF al Operator por email (Amazon SES)          | Alta      |
| RF-05.6 | El Operator debe poder descargar el PDF desde el sistema                  | Alta      |
| RF-05.7 | El estado de la empresa debe cambiar a `PDF_GENERATED` tras la generación | Alta     |

---

### RF-06 — Módulo Outreach

| ID      | Requisito                                                                 | Prioridad |
|---------|---------------------------------------------------------------------------|-----------|
| RF-06.1 | El Admin debe poder ver el pipeline de outreach de sus empresas           | Alta      |
| RF-06.2 | El Admin debe poder cambiar manualmente el estado de outreach de una empresa | Alta   |
| RF-06.3 | El sistema debe registrar fecha y hora de cada cambio de estado           | Alta      |
| RF-06.4 | El Admin debe poder registrar notas de seguimiento por empresa            | Media     |
| RF-06.5 | El sistema debe notificar al Admin cuando una empresa esté lista para outreach | Alta |
| RF-06.6 | El Root Admin debe poder ver métricas globales del pipeline               | Alta      |

**Estados del pipeline:**
`REGISTERED → INVITED → IN_PROGRESS → COMPLETED → PDF_GENERATED → OUTREACH_PENDING → OUTREACH_SENT → MEETING_SCHEDULED → CONVERTED / LOST`

---

### RF-07 — Módulo Dashboard (Admin)

| ID      | Requisito                                                                 | Prioridad |
|---------|---------------------------------------------------------------------------|-----------|
| RF-07.1 | El Admin debe ver KPIs globales con variación vs período anterior:        | Alta      |
|         | Operadores totales, Benchmarks completados, Score promedio /100, Percentil promedio, PDFs generados, Llamadas agendadas | |
| RF-07.2 | El Admin debe poder filtrar el dashboard por rango de fechas              | Alta      |
| RF-07.3 | El sistema debe mostrar histograma de distribución de scores (rangos 0-10 a 91-100) | Alta |
| RF-07.4 | El sistema debe mostrar evolución del score promedio en los últimos 6 meses | Alta   |
| RF-07.5 | El sistema debe mostrar score promedio por segmento/región                | Alta      |
| RF-07.6 | El sistema debe mostrar distribución de niveles de madurez como donut chart | Alta   |
| RF-07.7 | El sistema debe mostrar el embudo de conversión completo con % por etapa  | Alta      |
| RF-07.8 | El sistema debe mostrar tabla de últimas respuestas del benchmark         | Alta      |
|         | Columnas: Operator, Empresa, Región, Score, Percentil, Nivel de madurez, Fecha, PDF, Acciones | |
| RF-07.9 | El Admin debe poder exportar el reporte del dashboard                     | Media     |
| RF-07.10| El Operator debe ver su score, percentil, nivel de madurez y KPIs propios| Alta      |

### Embudo de Conversión (métricas)

| Etapa                | Descripción                              |
|----------------------|------------------------------------------|
| Visitantes únicos    | Usuarios que accedieron al sistema       |
| Calculadora iniciada | Iniciaron el proceso de benchmark        |
| Email capturado      | Email registrado                         |
| Benchmark completado | Cuestionario finalizado                  |
| PDF descargado       | PDF institucional descargado             |
| Llamada agendada     | Reunión con founder agendada             |

### Niveles de Madurez

| Nivel       | Rango    | Color    |
|-------------|----------|----------|
| Orquestado  | 75 - 100 | Verde    |
| Coordinado  | 50 - 74  | Azul     |
| Reactivo    | 25 - 49  | Amarillo |
| Fragmentado | 0 - 24   | Rojo     |

---

### RF-08 — Módulo AI Integration

| ID      | Requisito                                                                 | Prioridad |
|---------|---------------------------------------------------------------------------|-----------|
| RF-08.1 | El sistema debe enviar datos del benchmark a Google AI Studio             | Media     |
| RF-08.2 | El sistema debe mostrar insights generados por IA en el dashboard         | Media     |
| RF-08.3 | El sistema debe incluir recomendaciones de IA en el PDF institucional     | Media     |
| RF-08.4 | El sistema debe permitir clasificación de feedback mediante IA            | Baja      |
| RF-08.5 | La UI debe exportarse desde Stitch e importarse en Google AI Studio       | Media     |

---

## 4. Requisitos No Funcionales

### 4.1 Seguridad

| ID     | Requisito                                                                 |
|--------|---------------------------------------------------------------------------|
| RNF-01 | Todas las contraseñas deben almacenarse con hash seguro (bcrypt)          |
| RNF-02 | Toda comunicación debe realizarse sobre HTTPS (AWS Certificate Manager)   |
| RNF-03 | El JWT debe expirar y requerir renovación                                 |
| RNF-04 | Los endpoints deben validar el rol antes de procesar la solicitud         |
| RNF-05 | El acceso a S3 debe requerir URLs firmadas temporalmente                  |
| RNF-06 | Las credenciales de infraestructura deben gestionarse con AWS IAM         |

### 4.2 Performance

| ID     | Requisito                                                                 |
|--------|---------------------------------------------------------------------------|
| RNF-07 | El tiempo de respuesta de los endpoints críticos debe ser menor a 500ms   |
| RNF-08 | Los cálculos del benchmark deben cachearse en Redis                       |
| RNF-09 | La generación del PDF debe procesarse de forma asíncrona (AWS Lambda)     |
| RNF-10 | El envío masivo de emails debe procesarse en background (no bloquear request) |

### 4.3 Escalabilidad

| ID     | Requisito                                                                 |
|--------|---------------------------------------------------------------------------|
| RNF-11 | La arquitectura debe soportar escalado horizontal en EC2                  |
| RNF-12 | La base de datos debe soportar backups automáticos (Amazon RDS)           |
| RNF-13 | El sistema debe soportar carga masiva de al menos 500 empresas por CSV    |

### 4.4 Observabilidad

| ID     | Requisito                                                                 |
|--------|---------------------------------------------------------------------------|
| RNF-14 | Todos los servicios deben enviar logs a Amazon CloudWatch                 |
| RNF-15 | El sistema debe generar alertas ante errores críticos (CloudWatch Alarms) |
| RNF-16 | Los endpoints deben estar documentados con OpenAPI                        |

### 4.5 Usabilidad

| ID     | Requisito                                                                 |
|--------|---------------------------------------------------------------------------|
| RNF-17 | La UI debe ser responsiva (mobile y desktop)                              |
| RNF-18 | El cuestionario debe guardar progreso automáticamente                     |
| RNF-19 | Los errores de validación del CSV deben ser descriptivos y descargables   |

---

## 5. Restricciones Técnicas

- Frontend: Next.js 15, React, Tailwind CSS, Zustand, React Query
- Backend: Java, Spring Boot
- Autenticación: Clerk (OAuth, MFA, Organizations)
- Base de datos: PostgreSQL (Amazon RDS — decisión final pendiente vs Supabase)
- Cloud: AWS
- Arquitectura backend: DDD + Hexagonal (Ports & Adapters)
- Gestión del proyecto: Trello

---

## 6. Casos de Uso Principales

### CU-01: Login
**Actor:** Root Admin / Admin / Operator  
**Flujo:** Usuario ingresa email y contraseña → Clerk valida → JWT generado con rol → Redirección según rol

### CU-02: Registrar empresas masivamente
**Actor:** Admin  
**Flujo:** Admin sube CSV → Sistema valida → Empresas y operadores creados → Emails enviados bulk → Estado: INVITED

### CU-03: Completar benchmark
**Actor:** Operator  
**Flujo:** Operator accede con invitación → Completa cuestionario → KPIs calculados → PDF generado → Email enviado → Estado: PDF_GENERATED

### CU-04: Gestionar pipeline de outreach
**Actor:** Admin  
**Flujo:** Admin ve pipeline → Selecciona empresa con PDF_GENERATED → Envía contacto al founder → Cambia estado a OUTREACH_SENT → Registra seguimiento

### CU-05: Ver métricas globales
**Actor:** Root Admin  
**Flujo:** Root Admin accede al dashboard → Ve métricas globales de todos los Admins → Filtra por estado, período, industria

---

## 7. Roadmap de Versiones

| Versión | Módulos incluidos                          |
|---------|--------------------------------------------|
| V1.0    | Identity, Company, Calculator, Benchmark, PDF Generator |
| V1.1    | Dashboard, Outreach                        |
| V1.2    | AI Insights, Recommendation Engine        |
| V2.0    | Predictive Capacity, Digital Twin, Real-Time Monitoring |
