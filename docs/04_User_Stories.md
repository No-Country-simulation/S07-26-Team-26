# 04 — User Stories

## Project Ghost Load — AI-Powered Benchmark Platform

---

## Roles

| Rol             | Descripción                                              |
|-----------------|----------------------------------------------------------|
| `ROLE_ADMIN`    | Administrador — acceso total al sistema via JWT          |
| `ROLE_OPERATOR` | Operador — acceso solo a su evaluación via token único   |

---

## ADMIN — Autenticación

### US-01
**Como** Admin  
**Quiero** iniciar sesión con email y contraseña  
**Para** acceder al panel de administración

**Criterios de aceptación:**
- El sistema valida credenciales contra `admin_users`
- Ante credenciales incorrectas devuelve HTTP 401
- Tras login exitoso redirige al Dashboard
- El JWT se almacena en el frontend

---

### US-02
**Como** Admin  
**Quiero** cerrar sesión  
**Para** proteger el acceso al sistema

**Criterios de aceptación:**
- El botón de logout redirige a la pantalla de login
- No es posible navegar hacia atrás tras logout

---

## ADMIN — Dashboard

### US-03
**Como** Admin  
**Quiero** ver un resumen general del sistema al ingresar  
**Para** tener visibilidad del estado de las evaluaciones

**Criterios de aceptación:**
- El dashboard muestra: operadores totales, benchmarks completados, score promedio
- [future] Los KPIs muestran variación % vs período anterior
- [future] El dashboard permite filtrar por rango de fechas

---

### US-04
**Como** Admin  
**Quiero** ver la distribución de scores de los operadores  
**Para** entender la madurez del portfolio

**Criterios de aceptación:**
- [future] Se muestra un histograma de distribución por rangos
- [future] Los rangos cubren de 0 a 100 sin solapamiento

---

### US-05
**Como** Admin  
**Quiero** ver la evolución del score promedio en el tiempo  
**Para** identificar tendencias

**Criterios de aceptación:**
- [future] Se muestra un gráfico de línea con puntos mensuales
- [future] El rango por defecto son los últimos 6 meses

---

### US-06
**Como** Admin  
**Quiero** ver el score promedio por segmento/región  
**Para** comparar desempeño entre mercados

**Criterios de aceptación:**
- [future] Se muestra un gráfico de barras por región

---

### US-07
**Como** Admin  
**Quiero** ver la distribución de niveles de madurez  
**Para** entender la etapa del portfolio

**Criterios de aceptación:**
- [future] Se muestra un gráfico tipo donut con 5 niveles:
  - 🟢 Optimized (90-100)
  - 🔵 Advanced (75-89)
  - 🟡 Managed (50-74)
  - 🟠 Developing (25-49)
  - 🔴 Initial (0-24)

---

### US-08
**Como** Admin  
**Quiero** ver el embudo de conversión completo  
**Para** medir la efectividad del proceso

**Criterios de aceptación:**
- [future] El embudo muestra cada etapa con cantidad y % de conversión

---

### US-09
**Como** Admin  
**Quiero** ver las últimas respuestas del benchmark en una tabla  
**Para** hacer seguimiento rápido

**Criterios de aceptación:**
- [future] La tabla muestra: Operator, Empresa, Score, Percentil, Nivel de madurez, Fecha
- [future] El nivel de madurez se muestra como badge con color

---

## ADMIN — Evaluaciones

### US-10
**Como** Admin  
**Quiero** ver el listado de evaluaciones completadas  
**Para** revisar los resultados de los operadores

**Criterios de aceptación:**
- Lista paginada de evaluaciones con score, madurez, operador, fecha
- [future] Filtros por fecha, score, nivel de madurez

---

## ADMIN — Outreach (Campañas)

### US-11
**Como** Admin  
**Quiero** importar contactos desde un archivo CSV  
**Para** crear campañas de invitación

**Criterios de aceptación:**
- El sistema acepta CSV con columnas: first_name, last_name, email, company, position
- Valida cada fila (campos requeridos, email, duplicados)
- Las filas inválidas no bloquean las válidas
- Devuelve resumen: importados, fallidos, errores

---

### US-12
**Como** Admin  
**Quiero** crear campañas desde los contactos importados  
**Para** organizar las invitaciones

**Criterios de aceptación:**
- Puedo seleccionar contactos para una campaña
- El sistema registra quién creó la campaña y cuándo

---

### US-13
**Como** Admin  
**Quiero** enviar invitaciones a los contactos de una campaña  
**Para** que accedan al benchmark

**Criterios de aceptación:**
- Cada invitación tiene un token único no predecible
- El sistema encola los emails y los envía en background
- Se registra el resultado de cada envío

---

### US-14
**Como** Admin  
**Quiero** ver el estado de las invitaciones enviadas  
**Para** saber quiénes han respondido

**Criterios de aceptación:**
- Estados: UPLOADED → SENT → VISITED → STARTED → COMPLETED
- [future] Vista de pipeline con filtros

---

## ADMIN — Configuración

### US-15
**Como** Admin  
**Quiero** gestionar los usuarios del sistema  
**Para** crear o suspender Admins

**Criterios de aceptación:**
- [future] Lista de usuarios admin con estado
- [future] Crear nuevo Admin
- [future] Suspender/activar Admin

---

## OPERATOR — Evaluación

### US-16
**Como** Operator  
**Quiero** registrarme con mi email y empresa  
**Para** iniciar la evaluación de mi Data Center

**Criterios de aceptación:**
- Ingreso email y nombre de empresa
- El sistema devuelve un token único para continuar
- Se crea la evaluación en estado STARTED

---

### US-17
**Como** Operator  
**Quiero** completar la calculadora de capacidad  
**Para** conocer mis KPIs de infraestructura

**Criterios de aceptación:**
- Ingreso capacidad total (MW), capacidad productiva (MW), costo mensual por kW
- El sistema calcula: capacidad no productiva, % utilización, % no productivo, costo anual
- La evaluación pasa a CALCULATOR_COMPLETED

---

### US-18
**Como** Operator  
**Quiero** completar el cuestionario de benchmark  
**Para** obtener un diagnóstico de madurez

**Criterios de aceptación:**
- El cuestionario tiene 20 preguntas en 5 módulos temáticos
- Cada respuesta es un valor entre 1 y 5
- Al enviar, el sistema calcula score/100, score por módulo, percentil y nivel de madurez
- La evaluación pasa a BENCHMARK_COMPLETED
- [future] El progreso se guarda automáticamente

---

### US-19
**Como** Operator  
**Quiero** ver mis resultados tras completar el benchmark  
**Para** entender el estado de mi infraestructura

**Criterios de aceptación:**
- Muestra score /100
- Muestra percentil (con disclaimer si es dato de demostración)
- Muestra nivel de madurez: OPTIMIZED / ADVANCED / MANAGED / DEVELOPING / INITIAL
- Muestra KPIs calculados (capacidad no productiva, % utilización, costo anual)

---

## OPERATOR — PDF

### US-20
**Como** Operator  
**Quiero** recibir el PDF institucional por email  
**Para** tener un reporte formal

**Criterios de aceptación:**
- [future] El email llega automáticamente tras completar el benchmark
- [future] El PDF incluye KPIs, score y nivel de madurez

---

### US-21
**Como** Operator  
**Quiero** descargar el PDF desde el sistema  
**Para** tenerlo disponible sin depender del email

**Criterios de aceptación:**
- [future] Botón de descarga disponible en la vista de resultados

---

## Niveles de Madurez

| Nivel        | Rango Score | Color | Descripción                                           |
|--------------|-------------|-------|-------------------------------------------------------|
| Optimized    | 90 - 100    | Verde | Infraestructura optimizada y bien gestionada          |
| Advanced     | 75 - 89     | Azul  | Gestión avanzada con oportunidades de mejora          |
| Managed      | 50 - 74     | Amarillo | Gestión adecuada, áreas de mejora identificadas    |
| Developing   | 25 - 49     | Naranja | Gestión básica, múltiples áreas de mejora           |
| Initial      | 0 - 24      | Rojo  | Infraestructura desorganizada, alto potencial de ahorro|
