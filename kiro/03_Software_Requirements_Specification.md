# Software Requirements Specification — Ghost Load

**Project:** Ghost Load
**Documento:** SRS basado en IEEE 830
**Versión:** 1.0

---

## 1. Introducción

### 1.1 Propósito
Este documento define los requisitos funcionales y no funcionales del sistema Ghost Load, una plataforma integrada que conecta tres componentes (reporte de industria, calculadora de estimación y benchmark de madurez) para posicionar a la startup como autoridad en eficiencia de data centers.

### 1.2 Alcance
El sistema comprende 9 módulos (M1-M9) que cubren: captura de identidad, modelo de datos, API Gateway, motor de calculadora, motor de benchmark, generación de PDF, outreach, dashboard interno e infraestructura.

### 1.3 Definiciones
| Término | Definición |
|---------|-----------|
| Operador | Persona que opera un data center (facility manager, CTO de infra) |
| Magic link | Enlace firmado con JWT que autentica sin contraseña |
| Percentil | Posición del operador en la distribución acumulada del benchmark |
| Datos semilla | Respuestas artificiales para resolver el arranque en frío del benchmark |
| Lead calificado | Operador que completó benchmark y tiene PDF generado |

### 1.4 Referencias
- CLAUDE_DOC.md — Documento de diseño y requisitos inicial
- `02_System_Architecture.md` — Arquitectura del sistema

### 1.5 Vista general
Secciones 2-4 describen el producto. La sección 5 contiene requisitos funcionales por módulo. La sección 6 cubre requisitos no funcionales.

---

## 2. Descripción general

### 2.1 Perspectiva del producto
Sistema integrado de 3 componentes públicos sostenidos por una infraestructura común (API, base de datos, autenticación, pipeline de datos). Genera un PDF institucional como entregable principal.

### 2.2 Funciones del producto
1. Captura de email y autenticación ligera (magic link)
2. Cálculo de KPIs de capacidad desperdiciada
3. Benchmark de madurez con percentil vs industria
4. Generación automática de PDF institucional
5. Outreach: upload CSV → cola de invitaciones → tracking
6. Dashboard interno con respuestas acumuladas

### 2.3 Stakeholders y características
| Stakeholder | Necesidad | Solución |
|-------------|-----------|----------|
| Operador | Medir capacidad desperdiciada | Calculadora + Benchmark |
| Operador | Justificar inversiones con data | PDF institucional |
| Startup | Generar leads calificados | Embudo de captura |
| Startup | Outreach automatizado | Pipeline de invitaciones |
| Inversores | Evidencia de mercado | Dashboard con datos acumulados |

### 2.4 Restricciones
- Staging público accesible desde semana 1
- Sin intervención manual en outreach y generación de PDF
- Idempotencia en envíos de outreach y generación de PDF
- Branding fiel (forest-green #2D5A27 + gold #C9A84C)

### 2.5 Supuestos
- El operador tiene acceso a internet y email
- Volumen inicial de outreach < 1000 contactos
- El benchmark usa datos semilla para resolver arranque en frío

---

## 3. Requisitos específicos por módulo

### M1 — Identidad y Captura de Email

**RF-01: Captura de email**
- **Descripción:** El sistema debe capturar un email desde la calculadora, el benchmark o una invitación de outreach.
- **Entrada:** Email válido, fuente (calculator | benchmark | outreach)
- **Procesamiento:** Validar formato, deduplicar por email, registrar fuente y estado inicial
- **Salida:** Magic link enviado al email
- **Criterio de aceptación:**
  - Given un operador ingresa "test@domain.com" en la calculadora
  - When envía el formulario
  - Then el sistema registra al operador con estado "email_capturado" y fuente "calculator"
  - And envía un email con magic link a "test@domain.com"

**RF-02: Verificación de magic link**
- **Descripción:** El sistema debe verificar un token JWT firmado y autenticar al operador.
- **Entrada:** Token en query string
- **Procesamiento:** Validar firma, verificar expiración, actualizar sesión ligera
- **Salida:** Sesión autenticada (cookie/token de acceso)
- **Criterio de aceptación:**
  - Given un operador con token válido
  - When accede a /v1/identity/verify?token=VALID_TOKEN
  - Then el sistema retorna 200 con datos del operador
  - When accede con token expirado
  - Then el sistema retorna 401

### M2 — Modelo de Datos y Base de Datos

**RF-03: Schema de base de datos**
- **Descripción:** La base de datos debe implementar el schema definido en `02_System_Architecture.md` sección 5.
- **Entrada:** Migraciones SQL versionadas
- **Procesamiento:** Ejecutar migraciones en orden, mantener integridad referencial
- **Salida:** Base de datos con tablas: operators, calculator_results, benchmark_responses, generated_pdfs, outreach_campaigns, outreach_contacts
- **Criterio de aceptación:**
  - Given la base de datos desplegada
  - When se ejecutan las migraciones
  - Then todas las tablas existen con sus constraints y relaciones

### M3 — API Gateway

**RF-04: Endpoints REST versionados**
- **Descripción:** El API Gateway debe exponer endpoints bajo /v1/ con validación, CORS y rate limiting.
- **Entrada:** Requests HTTP a rutas definidas
- **Procesamiento:** Enrutar al servicio correspondiente, validar entrada, aplicar rate limit
- **Salida:** Respuestas JSON con códigos HTTP estándar
- **Criterio de aceptación:**
  - Given el API Gateway desplegado
  - When se hace GET a /v1/benchmark/questions
  - Then retorna 200 con el listado de preguntas
  - When se hace POST a /v1/identity/capture sin body
  - Then retorna 400 con error de validación

### M4 — Motor de Calculadora

**RF-05: Cálculo de KPIs**
- **Descripción:** El motor debe recibir inputs del facility del operador y calcular KPIs de capacidad desperdiciada.
- **Entrada:** Parámetros del facility (JSON): capacidad_total, capacidad_usada, costo_energia, etc.
- **Procesamiento:** Ejecutar modelo de cálculo, persistir resultados asociados al operador
- **Salida:** KPIs calculados (capacidad desperdiciada en $, %, etc.)
- **Criterio de aceptación:**
  - Given un operador autenticado con inputs válidos
  - When POST a /v1/calculator/run
  - Then retorna KPIs calculados
  - And los resultados se persisten en calculator_results

**RF-06: Profundidad desbloqueada por email**
- **Descripción:** El operador solo accede a KPIs detallados si tiene email capturado.
- **Criterio de aceptación:**
  - Given un operador anónimo
  - When solicita KPIs detallados
  - Then el sistema solicita captura de email antes de mostrar resultados completos

### M5 — Motor de Benchmark

**RF-07: Cuestionario de madurez**
- **Descripción:** El sistema debe presentar un cuestionario de madurez al operador.
- **Entrada:** GET /v1/benchmark/questions
- **Procesamiento:** Servir preguntas predefinidas con opciones de respuesta
- **Salida:** JSON con preguntas, opciones y escala

**RF-08: Cálculo de percentil**
- **Descripción:** Al completar el benchmark, calcular el percentil del operador vs distribución acumulada (incluyendo datos semilla).
- **Entrada:** Respuestas del cuestionario
- **Procesamiento:** Calcular score, determinar percentil contra distribución existente + semilla
- **Salida:** Score numérico y percentil
- **Criterio de aceptación:**
  - Given datos semilla cargados
  - When un operador completa el benchmark
  - Then el sistema calcula percentil y lo persiste en benchmark_responses
  - When es el primer operador real
  - Then el percentil se calcula contra datos semilla (nunca "1 de 1")

**RF-09: Disparo de generación de PDF**
- **Descripción:** Al completar el benchmark, el sistema debe disparar la generación del PDF automáticamente.
- **Criterio de aceptación:**
  - Given un operador completa el benchmark
  - When se persiste benchmark_responses
  - Then el sistema encola la generación del PDF para ese operador

### M6 — PDF Generator

**RF-10: Generación automática de PDF**
- **Descripción:** El sistema debe generar un PDF al completarse el benchmark, sin intervención manual.
- **Entrada:** operator_id, datos de benchmark y calculadora
- **Procesamiento:** Renderizar template HTML+CSS, convertir a PDF via Puppeteer/Playwright
- **Salida:** PDF almacenado y disponible para descarga

**RF-11: Contenido del PDF**
- **Descripción:** El PDF debe incluir: posición en benchmark, KPIs de calculadora, branding (logo, forest-green + gold), bloque de contacto del fundador (teléfono, LinkedIn, email, link para agendar llamada).
- **Criterio de aceptación:**
  - Given un PDF generado
  - When se inspecciona visualmente
  - Then incluye branding forest-green (#2D5A27) y gold (#C9A84C)
  - And incluye logo de la empresa
  - And incluye bloque de contacto del fundador con: teléfono, LinkedIn, email y link de calendly/calendario
  - And incluye posición en benchmark y KPIs de calculadora
  - And se lee como documento institucional (no como export genérico)

### M7 — Sistema de Outreach

**RF-12: Upload de lista de contactos**
- **Descripción:** El sistema debe aceptar upload de un archivo CSV con contactos.
- **Criterio de aceptación:**
  - Given un archivo CSV válido con emails
  - When POST a /v1/outreach/upload
  - Then se crea una campaña
  - And los contactos se registran con estado "pending"

**RF-13: Envío de invitaciones**
- **Descripción:** El sistema debe enviar invitaciones al benchmark a los contactos de una campaña.
- **Criterio de aceptación:**
  - Given una campaña con contactos en estado "pending"
  - When POST a /v1/outreach/:campaignId/send
  - Then los contactos pasan a estado "invited"
  - And se envía email de invitación a cada contacto
  - And ningún contacto recibe invitación duplicada

**RF-14: Tracking de completitud**
- **Descripción:** El sistema debe trackear el estado de cada contacto (pending → invited → opened → completed).
- **Criterio de aceptación:**
  - Given un contacto en estado "invited"
  - When hace clic en el link de invitación
  - Then pasa a estado "opened"
  - When completa el benchmark
  - Then pasa a estado "completed"
  - And se enlaza al operator_id correspondiente

### M8 — Dashboard Interno

**RF-15: Visualización de respuestas acumuladas**
- **Descripción:** El dashboard debe mostrar respuestas acumuladas del benchmark y KPIs de calculadora.
- **Criterio de aceptación:**
  - Given el equipo autenticado en el dashboard
  - When accede a la vista principal
  - Then ve: total de operadores, distribución de scores, KPIs promedio, estado de cada operador

**RF-16: Estado de campañas de outreach**
- **Criterio de aceptación:**
  - Given una campaña con contactos en diversos estados
  - When accede a la vista de outreach
  - Then ve: total contactos, tasa de apertura, tasa de completitud por campaña

### M9 — Infraestructura y Deploy

**RF-17: Staging público desde semana 1**
- **Descripción:** El sistema debe estar desplegado en un entorno de staging accesible públicamente.
- **Criterio de aceptación:**
  - Given el entorno de staging configurado
  - When se accede desde internet
  - Then el sistema responde en un dominio/subdominio público

---

## 4. Requisitos no funcionales

| ID | Categoría | Requisito |
|----|-----------|-----------|
| RNF-01 | Rendimiento | El PDF debe generarse en menos de 10 segundos |
| RNF-02 | Seguridad | Magic link expira en 15 minutos |
| RNF-03 | Seguridad | Rate limiting: 100 requests/min por IP |
| RNF-04 | Seguridad | CORS restringido a dominios conocidos en producción |
| RNF-05 | Disponibilidad | Staging público 99% disponible en horario laboral (L-V 8-20) |
| RNF-06 | Branding | Paleta exacta: forest-green #2D5A27, gold #C9A84C |
| RNF-07 | Escalabilidad | La cola de outreach debe procesar 1000 contactos sin timeout |
| RNF-08 | Mantenibilidad | Migraciones de base de datos versionadas y ejecutables en orden |
| RNF-09 | Idempotencia | UNIQUE(campaign_id, email) en outreach_contacts — nunca invitar dos veces |
| RNF-10 | Idempotencia | El PDF no debe regenerarse si ya existe para ese operador |

---

## 5. Apéndices

### A. Schema SQL completo
Ver `02_System_Architecture.md` sección 5.

### B. Tabla de endpoints
Ver `02_System_Architecture.md` sección 6.

### C. Máquina de estados del operador
Ver `02_System_Architecture.md` sección 4.
