# Design — Dashboard

## Architecture
Bounded Context dentro de la arquitectura DDD + Hexagonal.
El Dashboard consume datos de otros contextos (Benchmark, Company, Outreach, PDF).

## Domain Model

### Value Objects
- `DashboardMetrics` — KPIs globales con variación
- `ScoreDistribution` — histograma de rangos
- `ScoreEvolution` — serie temporal mensual
- `RegionScore` — score promedio por región
- `MaturityDistribution` — conteo por nivel
- `ConversionFunnel` — etapas con cantidad y %
- `RecentResponse` — fila de la tabla de últimas respuestas

## Ports

### In (Use Cases)
- `DashboardUseCase`
  - getMetrics(from, to) → DashboardMetrics
  - getRecentResponses(page, size) → Page<RecentResponse>

### Out (Infrastructure)
- `BenchmarkRepository` — scores, percentiles, madurez
- `CompanyRepository` — total empresas, estados
- `CampaignRepository` — pipeline de outreach
- `PDFRepository` — PDFs generados
- `CacheService` — Redis para métricas calculadas

## Adapters

### In
- `DashboardController` — `/dashboard/**`

### Out
- Lectura desde adapters JPA de otros contextos
- `RedisAdapter` — cache de métricas del dashboard

## API Endpoints
- GET /dashboard/metrics
- GET /dashboard/recent-responses

## Frontend Components
- `KPICard` — tarjeta con métrica y variación %
- `ScoreHistogram` — gráfico de barras (distribución de scores)
- `ScoreLineChart` — evolución del score promedio
- `RegionBarChart` — score por región
- `MaturityDonut` — distribución de niveles de madurez
- `ConversionFunnel` — embudo de conversión
- `RecentResponsesTable` — tabla de últimas respuestas con badges

## Database
No tiene tablas propias. Lee desde otros contextos.
Las métricas calculadas se cachean en Redis.
