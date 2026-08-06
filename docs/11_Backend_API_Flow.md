# Flujo actual de APIs del backend

Este documento describe únicamente los endpoints que están implementados en el
backend de Ghost Load. No incluye operaciones que aparezcan solamente como
propuesta en OpenAPI o en documentos de alcance.

## 1. URL base

En desarrollo local:

```text
http://localhost:8080
```

Todos los endpoints de negocio utilizan el prefijo:

```text
/api/v1
```

## 2. Tipos de autenticación

| Grupo de APIs | Autenticación |
| --- | --- |
| Administración | `Authorization: Bearer <JWT_ADMIN>` |
| Crear evaluación | Pública |
| Resolver invitación | Pública |
| Consultar preguntas | Pública |
| Calculadora | `X-Evaluation-Token: <TOKEN_EVALUACION>` |
| Benchmark | `X-Evaluation-Token: <TOKEN_EVALUACION>` |
| Estado, descarga y reintento del PDF | `X-Evaluation-Token: <TOKEN_EVALUACION>` |
| Health checks | Pública |

El JWT administrativo y el token de evaluación son credenciales diferentes y
no deben intercambiarse.

---

# 3. Flujo del administrador

```text
Login administrativo
→ prueba de SMTP (opcional)
→ importación de contactos
→ creación de campaña
→ envío de campaña
```

## 3.1. Iniciar sesión

```http
POST /api/v1/admin/auth/login
Content-Type: application/json
```

Ejemplo:

```json
{
  "email": "admin@ghostload.local",
  "password": "contraseña-del-administrador"
}
```

La respuesta contiene `accessToken`. Los siguientes endpoints administrativos
deben enviar:

```http
Authorization: Bearer <ACCESS_TOKEN>
```

## 3.2. Comprobar la conexión SMTP

```http
POST /api/v1/admin/email/test-connection
Authorization: Bearer <ACCESS_TOKEN>
```

No requiere body y no envía un mensaje. Solamente comprueba conexión,
autenticación y configuración SSL/TLS del servidor SMTP.

## 3.3. Importar contactos

```http
POST /api/v1/admin/contact-imports
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: multipart/form-data
```

Campos del formulario:

| Campo | Tipo | Descripción |
| --- | --- | --- |
| `name` | Text | Nombre de la importación |
| `file` | File | Archivo CSV UTF-8 |

Encabezados requeridos:

```csv
first_name,last_name,email,company,position
```

El archivo de ejemplo se encuentra en:

```text
docs/examples/contact-import-example.csv
```

La respuesta contiene `importId`, que se utiliza al crear la campaña. También
informa contactos válidos, nuevos, existentes, duplicados y filas inválidas.

La importación no envía correos.

## 3.4. Crear una campaña

```http
POST /api/v1/admin/campaigns
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json
```

Ejemplo:

```json
{
  "name": "Campaña agosto",
  "description": "Invitación al benchmark",
  "subject": "Completa nuestro benchmark",
  "message": "Completa la evaluación y recibe tu reporte personalizado.",
  "callToActionText": "Comenzar evaluación",
  "contactImportId": "UUID_DE_LA_IMPORTACION",
  "scheduledAt": null,
  "timezone": "America/Lima"
}
```

La respuesta contiene `id`, que corresponde al identificador de la campaña.
La campaña queda en estado `READY` y se genera una invitación única por
contacto. Este endpoint todavía no envía los correos.

## 3.5. Enviar una campaña

```http
POST /api/v1/admin/campaigns/{campaignId}/send
Authorization: Bearer <ACCESS_TOKEN>
```

No requiere body. El UUID se envía solamente en la URL.

La API responde `202 Accepted`, cambia la campaña a `SENDING` y guarda los
mensajes en la outbox. Si `MAIL_WORKER_ENABLED=true`, el worker procesa la cola
y entrega los mensajes mediante SMTP.

---

# 4. Flujo del operador invitado

```text
Correo de invitación
→ abrir invitación
→ crear evaluación
→ completar calculadora
→ completar benchmark
→ generar PDF
→ enviar y/o descargar PDF
```

## 4.1. Resolver la invitación

El enlace del correo contiene un token UUID:

```http
GET /api/v1/invitations/{invitationToken}
```

No requiere autenticación. El backend:

- comprueba que la invitación exista;
- comprueba que siga disponible;
- devuelve nombre, email, empresa, cargo y campaña;
- cambia la invitación de `SENT` a `VISITED`.

## 4.2. Crear la evaluación

```http
POST /api/v1/evaluations
Content-Type: application/json
```

Ejemplo para outreach:

```json
{
  "firstName": "Ana",
  "lastName": "Torres",
  "email": "ana@empresa.com",
  "companyName": "Empresa SAC",
  "position": "Gerente TI",
  "country": "Peru",
  "consentAccepted": true,
  "marketingConsent": false,
  "source": "OUTREACH",
  "invitationToken": "UUID_DE_LA_INVITACION"
}
```

El email debe coincidir con el contacto de la invitación. La respuesta contiene:

```json
{
  "operatorId": "UUID_DEL_OPERADOR",
  "evaluationId": "UUID_DE_LA_EVALUACION",
  "evaluationToken": "TOKEN_SECRETO",
  "state": "STARTED",
  "createdAt": "2026-08-05T12:00:00Z"
}
```

El frontend debe conservar `evaluationId` y `evaluationToken`. La invitación
cambia de `VISITED` a `STARTED`.

## 4.3. Completar la calculadora

```http
PUT /api/v1/evaluations/{evaluationId}/calculator
X-Evaluation-Token: <TOKEN_EVALUACION>
Content-Type: application/json
```

Ejemplo:

```json
{
  "totalCapacityMw": 10,
  "productiveCapacityMw": 6.5,
  "monthlyCostPerKw": 120,
  "currency": "USD"
}
```

El backend guarda las entradas y calcula los KPIs. La evaluación cambia:

```text
STARTED → CALCULATOR_COMPLETED
```

Actualmente la calculadora es obligatoria antes de enviar el benchmark.

## 4.4. Consultar las preguntas del benchmark

```http
GET /api/v1/benchmark/questions?version=v1
```

No requiere autenticación. Devuelve las 20 preguntas activas, sus cinco módulos
y las opciones de respuesta del 1 al 5.

## 4.5. Enviar el benchmark

```http
PUT /api/v1/evaluations/{evaluationId}/benchmark
X-Evaluation-Token: <TOKEN_EVALUACION>
Content-Type: application/json
```

Ejemplo reducido:

```json
{
  "questionnaireVersion": "v1",
  "answers": [
    {
      "questionId": "UUID_DE_LA_PREGUNTA_1",
      "value": 4
    },
    {
      "questionId": "UUID_DE_LA_PREGUNTA_2",
      "value": 3
    }
  ]
}
```

La solicitud real debe contener exactamente las 20 respuestas. Cada valor debe
estar entre 1 y 5.

El backend:

- calcula el puntaje total;
- calcula el puntaje de los cinco módulos;
- asigna el nivel de madurez;
- devuelve el percentil provisional del MVP;
- cambia la evaluación a `BENCHMARK_COMPLETED`;
- cambia la invitación a `COMPLETED`;
- encola automáticamente la generación del PDF.

```text
CALCULATOR_COMPLETED → BENCHMARK_COMPLETED
```

---

# 5. Flujo asíncrono del PDF

```text
Benchmark completado
→ reporte encolado
→ worker genera el PDF
→ archivo almacenado
→ intento de envío por email
```

Si `PDF_WORKER_ENABLED=true`, el worker procesa automáticamente los reportes
pendientes. La generación admite hasta tres intentos.

Un fallo al enviar el email no elimina un PDF generado correctamente. El
operador puede seguir descargándolo mediante la API.

## 5.1. Consultar estado

```http
GET /api/v1/evaluations/{evaluationId}/report
X-Evaluation-Token: <TOKEN_EVALUACION>
```

Estados posibles:

```text
NOT_REQUESTED
REPORT_GENERATING
REPORT_COMPLETED
REPORT_FAILED
```

## 5.2. Descargar el PDF

```http
GET /api/v1/evaluations/{evaluationId}/report/download
X-Evaluation-Token: <TOKEN_EVALUACION>
```

Cuando el reporte está listo, responde con `application/pdf` y una disposición
de descarga.

## 5.3. Reintentar un reporte fallido

```http
POST /api/v1/evaluations/{evaluationId}/report/retry
X-Evaluation-Token: <TOKEN_EVALUACION>
```

No requiere body. Si el reporte se encuentra en un estado reintentable, la API
responde `202 Accepted` y vuelve a encolarlo.

---

# 6. Flujo directo sin campaña

Un operador también puede crear una evaluación sin invitación:

```http
POST /api/v1/evaluations
Content-Type: application/json
```

Debe utilizar:

```json
{
  "source": "CALCULATOR",
  "invitationToken": null
}
```

El resto de los datos personales continúa siendo obligatorio. Después sigue el
mismo recorrido:

```text
Crear evaluación → calculadora → benchmark → PDF
```

El origen `BENCHMARK` también existe, pero la máquina de estados actual sigue
exigiendo completar la calculadora antes del benchmark.

---

# 7. Health checks

```http
GET /actuator/health
GET /actuator/health/liveness
GET /actuator/health/readiness
```

No requieren autenticación.

- `health`: estado general.
- `liveness`: confirma que el proceso de Spring está vivo.
- `readiness`: confirma que el backend está listo y PostgreSQL responde.

---

# 8. Resumen de endpoints implementados

| Método | Endpoint | Uso |
| --- | --- | --- |
| `POST` | `/api/v1/admin/auth/login` | Login administrativo |
| `POST` | `/api/v1/admin/email/test-connection` | Diagnóstico SMTP |
| `POST` | `/api/v1/admin/contact-imports` | Importar contactos CSV |
| `POST` | `/api/v1/admin/campaigns` | Crear campaña |
| `POST` | `/api/v1/admin/campaigns/{campaignId}/send` | Encolar envío de campaña |
| `GET` | `/api/v1/invitations/{invitationToken}` | Resolver invitación |
| `POST` | `/api/v1/evaluations` | Crear evaluación |
| `PUT` | `/api/v1/evaluations/{evaluationId}/calculator` | Guardar calculadora |
| `GET` | `/api/v1/benchmark/questions` | Consultar preguntas |
| `PUT` | `/api/v1/evaluations/{evaluationId}/benchmark` | Completar benchmark |
| `GET` | `/api/v1/evaluations/{evaluationId}/report` | Consultar estado del PDF |
| `GET` | `/api/v1/evaluations/{evaluationId}/report/download` | Descargar PDF |
| `POST` | `/api/v1/evaluations/{evaluationId}/report/retry` | Reintentar PDF fallido |
| `GET` | `/actuator/health` | Health check general |
| `GET` | `/actuator/health/liveness` | Liveness probe |
| `GET` | `/actuator/health/readiness` | Readiness probe |

## 9. Operaciones aún no implementadas

Aunque algunas aparecen en OpenAPI o documentos de alcance, todavía no existen
controllers funcionales para:

- listar y consultar campañas;
- consultar estadísticas de campañas y entregas;
- consultar el progreso general de una evaluación;
- consultar resultados consolidados después del benchmark;
- recuperar un token de evaluación perdido;
- dashboard administrativo;
- percentil real basado en una distribución acumulada.

