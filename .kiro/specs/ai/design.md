# Design — AI Integration

## Architecture
Capa transversal que consume datos del Bounded Context `assessment` y produce contenido enriquecido. V1.2.

## Domain Model

### Value Objects
- `BenchmarkInsights` — texto de diagnóstico personalizado
- `Recommendation` — categoría, prioridad, descripción
- `LeadScore` — puntuación y prioridad del lead

## Ports

### Out (Infrastructure)
- `GenerateBenchmarkInsightsPort` — genera diagnóstico desde KPIs
- `GenerateRecommendationsPort` — genera recomendaciones priorizadas
- `ClassifyLeadPort` — clasifica calidad del lead

## Adapters

### Out
- `GoogleAIStudioAdapter` — implementación con Google AI Studio

## Estrategia MVP
- V1.0: Sin AI. Resultados basados en reglas determinísticas.
- V1.2: Integración con Google AI Studio mediante puertos de salida.

## Referencia
- `docs/08_AI_Architecture.md` para diseño completo de prompts y flujo.
