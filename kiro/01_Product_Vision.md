# Product Vision — Ghost Load

**Project:** Ghost Load
**Documento:** Visión de producto y embudo de generación de leads
**Basado en:** CLAUDE_DOC.md

---

## 1. Contexto del problema

Los data centers modernos enfrentan un problema crítico y silencioso: **capacidad pagada y encendida que no produce nada**. Las capas físicas (energía, refrigeración, espacio) y operativas (software, workloads, orquestación) del facility no se coordinan entre sí, generando servidores activos que consumen electricidad y mantenimiento sin generar output útil.

No existe una herramienta de industria estandarizada que cuantifique este desperdicio ni ponga a cada operador en contexto con sus pares.

---

## 2. Propósito del sistema

El sistema es un **embudo de generación de leads** disfrazado de herramientas gratuitas. Todo existe para llevar a un operador de data center desde la curiosidad hasta una llamada agendada con el fundador.

---

## 3. Los tres componentes públicos

| Componente | Propósito | Nivel de compromiso |
|---|---|---|
| **Reporte de industria** | Contenido educativo que atrae tráfico | Bajo (lectura) |
| **Calculadora de estimación** | Cuantifica en $ la capacidad desperdiciada del facility | Bajo → Medio (requiere email para profundidad) |
| **Benchmark de madurez** | Posiciona al operador vs. la industria | Alto (cuestionario completo) |

---

## 4. El embudo completo

```
                      ┌──────────────────┐
                      │  Reporte / SEO   │  (tráfico orgánico)
                      └────────┬─────────┘
                               ▼
                      ┌──────────────────┐
                      │   Calculadora     │  (compromiso bajo)
                      │   KPIs básicos   │
                      └────────┬─────────┘
                               │  "Para ver más..."
                               ▼
                      ┌──────────────────┐
                      │  Captura de email │  ← EL PIVOTE DEL EMBUDO
                      │  (magic link)    │
                      └────────┬─────────┘
                               ▼
                      ┌──────────────────┐
                      │   Benchmark de    │  (compromiso alto)
                      │   Madurez         │
                      └────────┬─────────┘
                               │  Al completar...
                               ▼
                      ┌──────────────────┐
                      │  PDF Institucional│  ← ACTIVO DE VENTAS
                      │  (branding, KPIs, │
                      │   benchmark,      │
                      │   contacto        │
                      │   fundador)       │
                      └────────┬─────────┘
                               │
                      ┌────────▼─────────┐
                      │  Llamada agendada │  (conversión final)
                      └──────────────────┘
```

---

## 5. Principios de diseño

### Principio 1: El PDF no es un export, es una pieza de ventas
- Su calidad institucional es un requisito de primer orden
- Debe leerse como documento de consultora, no como export de SaaS
- Branding forest-green + gold, logo, tipografía profesional

### Principio 2: El email es la identidad
- No hay login con contraseña
- Autenticación ligera vía magic link / token firmado
- El email tiene doble uso: desbloquear calculadora + invitar al benchmark
- Un email = un operador (deduplicación y resolución de identidad)

---

## 6. Métrica de éxito

> Un operador puede entrar por la calculadora, completar el benchmark, recibir su PDF y el equipo de la startup puede ver las respuestas acumuladas en su dashboard. El sistema de outreach puede recibir una lista y disparar invitaciones sin intervención manual.
