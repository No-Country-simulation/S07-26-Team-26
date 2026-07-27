# Implementation Plan — Ghost Load

**Project:** Ghost Load
**Documento:** Roadmap por sprints, dependencias técnicas y plan de riesgos
**Versión:** 1.0
**Duración:** 5 semanas

---

## 1. Diagrama de dependencias entre módulos

```
M2 (Base de Datos)
  ├── M3 (API Gateway) — depende de schema congelado
  │     ├── M1 (Identidad) — depende de M2, M3
  │     ├── M4 (Calculadora) — depende de M2, M3
  │     └── M5 (Benchmark) — depende de M2, M3, M1
  │           └── M6 (PDF Generator) — depende de M4, M5
  ├── M7 (Outreach) — depende de M2, M3, M1
  └── M8 (Dashboard) — depende de M2, M3 (lectura)

M9 (Infraestructura) — habilitador transversal, arranca semana 1
```

---

## 2. Roadmap por semanas

### Sprint 1 (S1): Fundaciones

| Módulo | Actividades | Entregables |
|--------|------------|-------------|
| **M2** | Definir schema final, escribir migraciones, crear datos semilla del benchmark | Schema SQL versionado, migraciones ejecutables, script de seed |
| **M9** | Configurar VPS/cloud, dominios, CI/CD, variables de entorno | Staging público accesible (página de "en construcción"), repo con CI |
| **M3** | Crear API Gateway esqueleto con rutas placeholder | API responde 200 en /health, CORS configurado |

**Dependencias:** Ninguna (arranque desde 0)
**Hito:** Pipeline CI/CD funcionando + DB con schema + API responde

---

### Sprint 2 (S2): Identidad y calculadora

| Módulo | Actividades | Entregables |
|--------|------------|-------------|
| **M1** | Endpoint de captura de email, generación de magic link, validación de token, deduplicación | POST /v1/identity/capture, GET /v1/identity/verify |
| **M4** | Motor de cálculo de KPIs, persistencia de resultados, endpoints | POST /v1/calculator/run, GET /v1/calculator/results/:id |

**Dependencias:** M2 (schema), M3 (gateway)
**Hito:** Un operador puede capturar email y obtener KPIs de calculadora

---

### Sprint 3 (S3): Benchmark

| Módulo | Actividades | Entregables |
|--------|------------|-------------|
| **M5** | Cuestionario endpoint, lógica de score, cálculo de percentil vs distribución (incluye datos semilla), disparo de evento al completar | GET /v1/benchmark/questions, POST /v1/benchmark/submit |

**Dependencias:** M1 (para asociar respuestas al operador), M2 (para persistir)
**Hito:** Primer operador real completa benchmark y obtiene percentil contra datos semilla

---

### Sprint 4 (S4): PDF Generator (la pieza estrella)

| Módulo | Actividades | Entregables |
|--------|------------|-------------|
| **M6** | Template HTML+CSS con branding forest-green + gold, renderizado con Puppeteer/Playwright, endpoint de descarga, integración con evento M5 | Template institucional, endpoint GET /v1/pdf/:id, PDF se genera automáticamente al completar benchmark |

**Dependencias:** M4 (KPIs), M5 (percentil)
**Hito:** PDF generado automáticamente con branding correcto, posición de benchmark y KPIs

---

### Sprint 5 (S5): Outreach + Dashboard + Integración

| Módulo | Actividades | Entregables |
|--------|------------|-------------|
| **M7** | Upload CSV, cola de invitaciones, envío de emails, tracking de estado, idempotencia | POST /v1/outreach/upload, POST /v1/outreach/:id/send, seguimiento de estados |
| **M8** | Dashboard con gráficos de respuestas acumuladas, estado de campañas, acceso protegido | GET /v1/dashboard/summary, UI protegida con login interno |
| **Integración E2E** | Probar flujo completo: calculadora → email → benchmark → PDF → outreach → dashboard | Test de integración ejecutándose, sistema listo para demo |

**Dependencias:** Todo lo anterior
**Hito:** Sistema integrado funcionando en staging, demo ejecutable

---

## 3. Hitos críticos

| Semana | Hito | Indicador de éxito |
|--------|------|-------------------|
| S1 | Staging vivo + DB con schema | `curl https://staging.ghostload.io/health` → 200 |
| S2 | Captura de email + cálculo | Operador de prueba queda registrado en DB |
| S3 | Benchmark completo con percentil | Primer percentil calculado contra datos semilla |
| S4 | PDF generado automáticamente | PDF con branding visible y datos correctos |
| S5 | Sistema integrado + demo | Operador recorre flujo completo sin intervención |

---

## 4. Plan de riesgos

### Riesgo 1: Outreach se complica más de lo estimado
- **Probabilidad:** Media
- **Impacto:** Alto
- **Plan de mitigación:** Implementar versión simplificada sin cola (enviar emails sincrónicamente en lote) si el tiempo no alcanza para la cola con tracking. La idempotencia (UNIQUE constraint) sigue protegiendo contra duplicados.
- **Disparador:** Si al final de S3 no hay avance claro en outreach.

### Riesgo 2: PDF no queda con calidad institucional
- **Probabilidad:** Media
- **Impacto:** Alto (es la pieza estrella)
- **Plan de mitigación:** Dedicar tiempo extra en S4 a iterar el diseño del template. Priorizar calidad visual sobre features. Usar Figma/Canva para diseñar el template primero, luego codificarlo.
- **Disparador:** Si el primer template genera un PDF que "no se ve institucional".

### Riesgo 3: Arranque en frío del benchmark
- **Probabilidad:** Alta (es inevitable)
- **Impacto:** Medio
- **Plan de mitigación:** Sembrar datos semilla desde el deploy (S1). La función de percentil debe funcionar con n >= 10 datos semilla.
- **Disparador:** Primer operador real completa benchmark y el sistema no tiene datos de comparación.

### Riesgo 4: Magic link / email no llega
- **Probabilidad:** Baja
- **Impacto:** Medio (el email es el pivote)
- **Plan de mitigación:** Configurar SPF, DKIM, DMARC desde S1. Tener logs de entrega visibles. Servicio de email transaccional con dashboard de entregabilidad.
- **Disparador:** Reportes de operadores que no reciben el magic link.

---

## 5. Prioridades si el tiempo aprieta

```
1. INTEGRACIÓN E2E (imprescindible)
   Calculadora → email → benchmark → PDF → dashboard
   
2. CALIDAD DEL PDF (no negociable)
   Es el activo comercial que cierra el embudo

3. OUTREACH (candidato a simplificar)
   Puede empezar sin cola ni tracking avanzado
   Mínimo: subir CSV y enviar emails en lote
```

---

## 6. Criterio de éxito final

> Los cinco módulos del sistema funcionan como sistema integrado en staging. Un operador puede entrar por la calculadora, completar el benchmark, recibir su PDF y el equipo de la startup puede ver las respuestas acumuladas en su dashboard. El sistema de outreach puede recibir una lista y disparar invitaciones sin intervención manual.
