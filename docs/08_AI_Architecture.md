# 08 — AI Architecture

## Project Ghost Load

---

## Visión General

La capa de IA de Ghost Load tiene como objetivo convertir los datos del benchmark en insights accionables para los operadores de Data Centers y en señales de calificación de leads para el equipo de ventas.

```
Benchmark Data
      │
      ▼
Google AI Studio
      │
      ├──→ Benchmark Insights (para el Operator)
      ├──→ Recommendation Engine (incluido en el PDF)
      ├──→ Feedback Classification (señales de calidad del lead)
      └──→ Dashboard AI Panel (para el Admin y Root)
```

---

## Componentes de IA

### 1. Stitch → Google AI Studio (UI Export)

**Propósito:** Exportar el diseño de interfaz desde Stitch e importarlo en Google AI Studio para acelerar el desarrollo del frontend con asistencia de IA.

**Flujo:**
```
Diseño en Stitch
      │
      ▼
Exportación de componentes UI
      │
      ▼
Importación en Google AI Studio (Next.js)
      │
      ▼
Generación de código de componentes asistida por IA
```

---

### 2. Benchmark Insights

**Propósito:** Analizar los KPIs del benchmark y generar un diagnóstico textual personalizado para el operador.

**Input:**
```json
{
  "totalCapacityKw": 500,
  "usedCapacityKw": 320,
  "wastedCapacityKw": 180,
  "wasteIndex": 36.0,
  "pueRatio": 1.8,
  "industryBenchmark": 22.5,
  "industry": "Colocation",
  "country": "Argentina"
}
```

**Output esperado:**
```
"Tu Data Center opera con un índice de desperdicio del 36%, 
significativamente por encima del benchmark de la industria (22.5%). 
El PUE de 1.8 indica oportunidades de optimización energética. 
Se identificaron 180 kW de capacidad subutilizada..."
```

**Implementación:**
- Adaptador: `GoogleAIStudioAdapter` (implements `AIService` port)
- Trigger: automático al completar el benchmark
- Resultado almacenado en `benchmark_results.ai_insights` (TEXT)

---

### 3. Recommendation Engine

**Propósito:** Generar recomendaciones específicas y priorizadas basadas en los gaps identificados en el benchmark.

**Categorías de recomendaciones:**
| Categoría              | Descripción                                    |
|------------------------|------------------------------------------------|
| Capacidad              | Optimización de racks y densidad de potencia   |
| Energía                | Mejora del PUE y eficiencia energética         |
| Cooling                | Optimización del sistema de refrigeración      |
| Resiliencia            | Mejora de redundancia y disponibilidad         |
| Gestión                | DCIM y herramientas de monitoreo               |

**Output esperado en PDF:**
```
Recomendación #1 (Alta Prioridad):
Consolidar cargas de trabajo en 3 racks para liberar 
capacidad subutilizada y reducir costos de cooling.

Recomendación #2 (Media Prioridad):
Implementar free cooling para reducir PUE de 1.8 a 1.4,
con ROI estimado de 18 meses.
```

---

### 4. Feedback Classification

**Propósito:** Clasificar la calidad del lead basándose en los datos del benchmark para priorizar el outreach.

**Señales analizadas:**
- Índice de capacidad desperdiciada (mayor = más urgencia)
- PUE vs benchmark de industria
- Tamaño de la infraestructura (kW total)
- Industria y región

**Output:**
```json
{
  "leadScore": 85,
  "priority": "HIGH",
  "reason": "Waste index 60% above industry average. Large infrastructure (500kW). Immediate optimization potential.",
  "suggestedAction": "Contact founder within 48 hours"
}
```

**Uso en Dashboard:**
- El Admin ve el score de cada empresa en el pipeline
- El Root Admin puede filtrar por prioridad de lead

---

### 5. Dashboard AI Panel

**Propósito:** Mostrar insights agregados y tendencias del portfolio de empresas al Admin y Root Admin.

**Métricas IA en dashboard:**
- Distribución de lead scores
- Promedio de waste index del portfolio
- Empresas con mayor potencial de conversión
- Tendencias por industria y región

---

## Arquitectura del Adaptador

```
domain/
└── ports/
    └── out/
        └── AIService.java          ← Puerto (interfaz)
            ├── generateInsights(BenchmarkResult) → String
            ├── generateRecommendations(BenchmarkResult) → List<Recommendation>
            └── classifyLead(BenchmarkResult) → LeadScore

adapters/
└── out/
    └── ai/
        └── GoogleAIStudioAdapter.java   ← Implementación
```

**Beneficio hexagonal:** Si en V2 se cambia Google AI Studio por OpenAI, Anthropic u otro proveedor, solo se reemplaza el adaptador. El dominio no se toca.

---

## Prompt Engineering

### Estructura base de prompts

```
[CONTEXTO DEL SISTEMA]
Eres un experto en infraestructura de Data Centers con 20 años de experiencia.
Analizas datos de benchmarking y generas insights accionables para operadores.
Responde siempre en español. Sé directo y técnico.

[DATOS DEL BENCHMARK]
{benchmark_data_json}

[TAREA]
{tarea_específica: insights | recommendations | lead_classification}

[FORMATO DE RESPUESTA]
{formato_json_o_texto}
```

---

## Roadmap de IA por Versión

| Versión | Componente                    | Estado     |
|---------|-------------------------------|------------|
| V1.0    | Stitch → AI Studio (UI)       | Planificado|
| V1.2    | Benchmark Insights            | Planificado|
| V1.2    | Recommendation Engine (PDF)   | Planificado|
| V1.2    | Feedback Classification       | Planificado|
| V2.0    | Predictive Capacity           | Futuro     |
| V2.0    | Digital Twin                  | Futuro     |
| V2.0    | Real-Time Monitoring AI       | Futuro     |
