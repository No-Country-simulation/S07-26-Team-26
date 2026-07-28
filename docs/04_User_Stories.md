# 04 — User Stories

## Project Ghost Load — AI-Powered Benchmark Platform

---

## Roles

| Rol             | Descripción                                              |
|-----------------|----------------------------------------------------------|
| `ROLE_ADMIN`    | Administrador — acceso total al sistema y todos los datos|
| `ROLE_OPERATOR` | Operador — acceso solo a su empresa y benchmark          |

---

## ADMIN — Autenticación

### US-01
**Como** Admin  
**Quiero** iniciar sesión con email y contraseña  
**Para** acceder al panel de administración

**Criterios de aceptación:**
- El sistema valida credenciales contra el backend
- Ante credenciales incorrectas muestra mensaje de error claro
- Tras login exitoso redirige al Dashboard
- El JWT se almacena y persiste la sesión

---

### US-02
**Como** Admin  
**Quiero** cerrar sesión desde cualquier pantalla  
**Para** proteger el acceso al sistema

**Criterios de aceptación:**
- El botón de logout invalida el JWT
- Redirige a la pantalla de login
- No es posible navegar hacia atrás tras logout

---

## ADMIN — Dashboard

### US-03
**Como** Admin  
**Quiero** ver un resumen general del sistema al ingresar  
**Para** tener visibilidad del estado de todos los operadores y empresas

**Criterios de aceptación:**
- El dashboard muestra los KPIs principales:
  - Operadores totales (con variación % vs período anterior)
  - Benchmarks completados
  - Score promedio /100
  - Percentil promedio
  - PDFs generados
  - Llamadas agendadas
- Los KPIs muestran comparación con el período anterior
- El dashboard permite filtrar por rango de fechas

---

### US-04
**Como** Admin  
**Quiero** ver la distribución de scores de todos los operadores  
**Para** entender cómo se distribuye la madurez del portfolio

**Criterios de aceptación:**
- Se muestra un histograma de distribución por rangos (0-10, 11-20, ... 91-100)
- El eje Y muestra cantidad de operadores por rango
- Se puede filtrar por segmento

---

### US-05
**Como** Admin  
**Quiero** ver la evolución del score promedio en el tiempo  
**Para** identificar tendencias del portfolio

**Criterios de aceptación:**
- Se muestra un gráfico de línea con puntos mensuales
- El rango por defecto es los últimos 6 meses
- Se puede cambiar el período desde un selector

---

### US-06
**Como** Admin  
**Quiero** ver el score promedio por segmento/región  
**Para** comparar el desempeño entre mercados

**Criterios de aceptación:**
- Se muestra un gráfico de barras horizontales
- Segmentos: Todos los operadores, Norteamérica, Latinoamérica, Europa, Asia Pacífico
- Cada barra muestra el score promedio

---

### US-07
**Como** Admin  
**Quiero** ver la distribución de niveles de madurez  
**Para** entender en qué etapa están la mayoría de las empresas

**Criterios de aceptación:**
- Se muestra un gráfico tipo donut con 4 niveles:
  - 🟢 Orquestado (75-100)
  - 🔵 Coordinado (50-74)
  - 🟡 Reactivo (25-49)
  - 🔴 Fragmentado (0-24)
- Cada nivel muestra % y cantidad de operadores
- El total en el centro es el total de benchmarks completados

---

### US-08
**Como** Admin  
**Quiero** ver el embudo de conversión completo  
**Para** medir la efectividad del proceso de adquisición

**Criterios de aceptación:**
- El embudo muestra cada etapa con cantidad y % de conversión:
  - Visitantes únicos
  - Calculadora iniciada
  - Email capturado
  - Benchmark completado
  - PDF descargado
  - Llamada agendada
- Se visualiza como funnel con degradado

---

### US-09
**Como** Admin  
**Quiero** ver las últimas respuestas del benchmark en una tabla  
**Para** hacer seguimiento rápido de la actividad reciente

**Criterios de aceptación:**
- La tabla muestra: Operator, Empresa, Región, Score, Percentil, Nivel de madurez, Completado (fecha/hora), PDF (ícono de descarga), Acciones
- El nivel de madurez se muestra como badge con color
- El botón "Ver detalle" abre el detalle del operador
- Al pie hay link "Ver todas las respuestas"

---

### US-10
**Como** Admin  
**Quiero** exportar un reporte del dashboard  
**Para** compartirlo con el equipo o presentarlo en reuniones

**Criterios de aceptación:**
- El botón "Exportar reporte" genera un PDF o CSV del estado actual
- El reporte incluye los KPIs principales y la tabla de respuestas

---

## ADMIN — Benchmark

### US-11
**Como** Admin  
**Quiero** ver todas las respuestas del benchmark  
**Para** analizar los datos de cada operador

**Criterios de aceptación:**
- Lista paginada de todas las respuestas con filtros por fecha, región, score, nivel de madurez
- Cada fila tiene acceso al detalle completo

---

### US-12
**Como** Admin  
**Quiero** ver el análisis agregado del benchmark  
**Para** identificar patrones en las respuestas

**Criterios de aceptación:**
- Visualización de respuestas por pregunta
- Distribución de respuestas más comunes

---

### US-13
**Como** Admin  
**Quiero** ver los percentiles del sistema  
**Para** ubicar a cada operador en el contexto del portfolio

**Criterios de aceptación:**
- Tabla de distribución percentil
- Posición de cada operador en el ranking

---

## ADMIN — Calculadora

### US-14
**Como** Admin  
**Quiero** ver los resultados de KPIs calculados de todos los operadores  
**Para** identificar los casos con mayor potencial

**Criterios de aceptación:**
- Tabla con KPIs por empresa: waste index, PUE, capacidad total, capacidad desperdiciada
- Filtrable por rango de waste index y tamaño de infraestructura

---

## ADMIN — Outreach

### US-15
**Como** Admin  
**Quiero** gestionar campañas de outreach  
**Para** organizar el contacto con los founders de las empresas

**Criterios de aceptación:**
- Lista de campañas con estado actual
- Posibilidad de crear nueva campaña desde una empresa con PDF generado

---

### US-16
**Como** Admin  
**Quiero** gestionar los contactos (founders)  
**Para** tener un registro centralizado de los decisores de cada empresa

**Criterios de aceptación:**
- Lista de contactos con empresa, email, estado
- Importación masiva vía CSV con columnas: name, email, company

---

### US-17
**Como** Admin  
**Quiero** enviar invitaciones a operadores  
**Para** que completen el benchmark

**Criterios de aceptación:**
- Envío individual o masivo vía CSV
- El sistema envía email automático con link de acceso
- Estado de la empresa cambia a INVITED

---

### US-18
**Como** Admin  
**Quiero** hacer seguimiento del estado de cada empresa  
**Para** saber en qué etapa del pipeline está cada una

**Criterios de aceptación:**
- Vista del pipeline con todos los estados
- Posibilidad de cambiar estado manualmente
- Registro de historial de cambios con fecha y usuario

---

## ADMIN — Reportes

### US-19
**Como** Admin  
**Quiero** ver todos los PDFs generados  
**Para** acceder y descargar los reportes de cada empresa

**Criterios de aceptación:**
- Lista de PDFs con empresa, fecha de generación, estado
- Botón de descarga por cada PDF
- Posibilidad de regenerar un PDF

---

## ADMIN — Configuración

### US-20
**Como** Admin  
**Quiero** gestionar los usuarios del sistema  
**Para** crear, editar o suspender Admins y Operators

**Criterios de aceptación:**
- Lista de usuarios con rol, estado, fecha de creación
- Crear nuevo Admin o Operator
- Suspender/activar usuario

---

## OPERATOR — Autenticación

### US-21
**Como** Operator  
**Quiero** acceder al sistema con el link que recibí por email  
**Para** completar el benchmark de mi empresa

**Criterios de aceptación:**
- El link de invitación lleva directamente al login
- Tras autenticarse redirige al cuestionario de benchmark
- Si ya completó el benchmark, redirige a sus resultados

---

## OPERATOR — Benchmark

### US-22
**Como** Operator  
**Quiero** completar el cuestionario de benchmark  
**Para** obtener un diagnóstico de mi Data Center

**Criterios de aceptación:**
- El cuestionario se divide en secciones
- El progreso se guarda automáticamente al avanzar
- Puede retomar desde donde lo dejó si cierra la sesión
- Al finalizar muestra un resumen de sus respuestas antes de confirmar

---

### US-23
**Como** Operator  
**Quiero** ver mis resultados tras completar el benchmark  
**Para** entender el estado de mi infraestructura

**Criterios de aceptación:**
- Muestra su score /100
- Muestra su percentil en el ranking
- Muestra su nivel de madurez con descripción (Orquestado / Coordinado / Reactivo / Fragmentado)
- Muestra los KPIs calculados (waste index, PUE, capacidad)

---

## OPERATOR — PDF

### US-24
**Como** Operator  
**Quiero** recibir el PDF institucional por email tras completar el benchmark  
**Para** tener un reporte formal de mi infraestructura

**Criterios de aceptación:**
- El email llega automáticamente tras completar el benchmark
- El PDF incluye KPIs, score, nivel de madurez e insights de IA
- El email tiene asunto y contenido profesional

---

### US-25
**Como** Operator  
**Quiero** descargar el PDF desde el sistema  
**Para** tenerlo disponible sin depender del email

**Criterios de aceptación:**
- Botón de descarga disponible en la vista de resultados
- El PDF se descarga directamente desde S3 con URL firmada
- El link de descarga tiene validez temporal

---

## Niveles de Madurez

| Nivel        | Rango Score | Color | Descripción                                           |
|--------------|-------------|-------|-------------------------------------------------------|
| Orquestado   | 75 - 100    | Verde | Infraestructura optimizada y bien gestionada          |
| Coordinado   | 50 - 74     | Azul  | Gestión adecuada con oportunidades de mejora          |
| Reactivo     | 25 - 49     | Amarillo | Gestión básica, múltiples áreas de mejora          |
| Fragmentado  | 0 - 24      | Rojo  | Infraestructura desorganizada, alto potencial de ahorro|
