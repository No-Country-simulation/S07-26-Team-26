# Tasks — Dashboard

## Status Legend
- [ ] Pendiente
- [x] Completado
- [-] En progreso

---

## Backend

- [ ] Implementar `DashboardUseCase` — getMetrics, getRecentResponses, getOperatorResults (REQ-2, REQ-5, REQ-9, REQ-11)
- [ ] Implementar cálculo de KPIs globales con variación vs período anterior (REQ-2.2)
- [ ] Implementar KPIs en 0 cuando no hay datos — nunca inventar valores (REQ-2.4)
- [ ] Implementar validación de rango de fechas — start ≤ end (REQ-3.2)
- [ ] Implementar filtro de fechas consistente en KPIs, gráficos y tabla (REQ-3.3)
- [ ] Implementar cálculo de distribución de scores — rangos sin solapamiento, cobertura 0-100 (REQ-4.3)
- [ ] Implementar cálculo de evolución del score promedio mensual (REQ-5.1)
- [ ] Implementar cálculo de score promedio por región — excluir segmentos sin datos (REQ-6.4)
- [ ] Implementar cálculo de distribución de niveles de madurez con % que suman 100 (REQ-7.3)
- [ ] Implementar niveles con 0 operadores mostrados con 0% (REQ-7.4)
- [ ] Implementar cálculo del embudo de conversión con tasas relativas a etapa anterior (REQ-8.3)
- [ ] Implementar endpoint GET /dashboard/metrics con filtro por fechas (REQ-3.1)
- [ ] Implementar endpoint GET /dashboard/recent-responses paginado, orden desc (REQ-9.1)
- [ ] Implementar endpoint GET /dashboard/operator-results para Operator (REQ-11.1, REQ-11.3)
- [ ] Implementar cache en Redis para métricas (TTL: 5 minutos) (REQ-2, REQ-9)

### Tests Backend (REQ coverage)
- [ ] Test: solo ROLE_ADMIN accede al dashboard global (REQ-1.1)
- [ ] Test: KPIs muestran 0 cuando no hay datos (REQ-2.4)
- [ ] Test: variación % calculada correctamente vs período anterior (REQ-2.2)
- [ ] Test: fecha inicio posterior a fecha fin devuelve error (REQ-3.2)
- [ ] Test: mismos filtros producen resultados consistentes en KPIs y tabla (REQ-3.4)
- [ ] Test: rangos de score no se solapan y cubren 0-100 (REQ-4.3)
- [ ] Test: porcentajes de niveles de madurez suman 100% (REQ-7.3)
- [ ] Test: nivel con 0 operadores muestra 0% (REQ-7.4)
- [ ] Test: total del donut = total de benchmarks completados (REQ-7.2)
- [ ] Test: tabla de respuestas ordenada por fecha desc (REQ-9.1)
- [ ] Test: Operator solo accede a resultados de su empresa (REQ-11.1)
- [ ] Test: Operator no ve resultados si benchmark no está COMPLETED (REQ-11.2)
- [ ] Test: dashboard no modifica agregados de otros módulos (REQ-1.4)

## Frontend

- [ ] Crear layout del Dashboard con sidebar de navegación completo (REQ-1.3)
- [ ] Implementar componente `KPICard` con variación % en color (verde/rojo) (REQ-2.3)
- [ ] Implementar selector de rango de fechas con validación start ≤ end (REQ-3.2)
- [ ] Implementar `ScoreHistogram` — recharts BarChart (REQ-4)
- [ ] Implementar `ScoreLineChart` — recharts LineChart últimos 6 meses (REQ-5)
- [ ] Implementar `RegionBarChart` — recharts horizontal BarChart (REQ-6)
- [ ] Implementar `MaturityDonut` — recharts PieChart con total en centro (REQ-7)
- [ ] Implementar `ConversionFunnel` — funnel chart con % por etapa (REQ-8)
- [ ] Implementar `RecentResponsesTable` con badges de madurez y colores (REQ-9)
- [ ] Implementar botón "Exportar reporte" (REQ-10)
- [ ] Implementar vista de resultados del Operator con PDF download (REQ-11)
- [ ] Mostrar KPIs como 0 cuando no hay datos — nunca vacío (REQ-2.4)

## DevOps

- [ ] Configurar Redis TTL para cache de métricas del dashboard
