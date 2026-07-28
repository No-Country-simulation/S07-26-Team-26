# Requirements — Dashboard

## Introduction

The Dashboard is the main control panel for the Admin. It presents global KPIs, score distributions, maturity levels, conversion funnel, and a recent responses table — all derived from the Benchmark, Company, Outreach and PDF Generator modules.

The Dashboard does not own any aggregates. It reads from other bounded contexts and presents aggregated views. The Operator also has a limited personal view showing their own results and PDF download.

---

## Requirements

### Requirement 1 — Admin Access and Navigation

**User Story:** As an Admin, I want to access the Dashboard after login, so that I can immediately see the state of the entire platform.

#### Acceptance Criteria

1. Only an authenticated Admin with ROLE_ADMIN MUST be able to access the global Dashboard.
2. The Dashboard MUST be the default landing page after Admin login.
3. The sidebar MUST provide navigation to: Dashboard, Benchmark (Respuestas, Análisis, Percentiles, Preguntas), Calculadora (Resultados, KPIs, Modelos), Outreach (Campañas, Contactos, Invitaciones, Seguimiento), Reportes (PDF Generados, Descargas), Configuración (Usuarios, Configuración, Integraciones).
4. Dashboard queries MUST NOT modify aggregates of other modules.

---

### Requirement 2 — Global KPIs

**User Story:** As an Admin, I want to see the main platform KPIs with period-over-period variation, so that I can measure adoption and progress at a glance.

#### Acceptance Criteria

1. The Dashboard MUST display the following KPIs:
   - Total operators
   - Benchmarks completed
   - Average score (/100)
   - Average percentile
   - PDFs generated
   - Meetings scheduled
2. Each KPI MUST show percentage variation compared to the previous equivalent period.
3. Positive variation MUST be shown in green, negative in red.
4. When no data exists for a KPI, it MUST be shown as zero and NOT as an invented value.
5. Each KPI MUST define clearly its population, period and timezone.

---

### Requirement 3 — Date Range Filter

**User Story:** As an Admin, I want to filter all dashboard metrics by date range, so that I can analyze specific time periods.

#### Acceptance Criteria

1. The Admin MUST be able to select a start date and end date.
2. The start date MUST NOT be later than the end date.
3. All KPIs, charts and the recent responses table MUST reflect the selected date range consistently.
4. The same filters MUST produce consistent results across all dashboard sections.
5. The contract MUST define timezone, inclusive limits and maximum allowed range.

---

### Requirement 4 — Score Distribution Chart

**User Story:** As an Admin, I want to see the distribution of scores across all operators, so that I can understand how maturity is spread across the portfolio.

#### Acceptance Criteria

1. The system MUST provide data to render a histogram with score ranges: 0-10, 11-20, 21-30, 31-40, 41-50, 51-60, 61-70, 71-80, 81-90, 91-100.
2. Each range MUST show the count of operators in that range.
3. Score ranges MUST NOT overlap and MUST cover all possible values from 0 to 100.
4. The Admin MUST be able to filter by segment from the chart.

---

### Requirement 5 — Score Evolution Chart

**User Story:** As an Admin, I want to see how the average score has evolved over time, so that I can identify trends in the portfolio.

#### Acceptance Criteria

1. The system MUST provide monthly average score data points.
2. The default range MUST be the last 6 months.
3. The Admin MUST be able to change the time period from a selector.
4. Each data point MUST represent the average score of benchmarks completed in that month.

---

### Requirement 6 — Score by Region Chart

**User Story:** As an Admin, I want to see the average score per region or segment, so that I can compare performance across markets.

#### Acceptance Criteria

1. The system MUST provide average score grouped by region/segment.
2. Segments MUST include at minimum: All operators, North America, Latin America, Europe, Asia Pacific.
3. Each segment MUST show its average score value.
4. Segments with no data MUST be excluded from the chart.

---

### Requirement 7 — Maturity Level Distribution

**User Story:** As an Admin, I want to see the distribution of maturity levels across all operators, so that I can understand the overall portfolio maturity.

#### Acceptance Criteria

1. The system MUST display the count and percentage for each maturity level:
   - ORCHESTRATED (75-100) — Green
   - COORDINATED (50-74) — Blue
   - REACTIVE (25-49) — Yellow
   - FRAGMENTED (0-24) — Red
2. The total shown in the center MUST equal the total number of completed benchmarks.
3. Percentages MUST add up to 100%.
4. Levels with zero operators MUST still be shown with 0%.

---

### Requirement 8 — Conversion Funnel

**User Story:** As an Admin, I want to see the full conversion funnel with counts and rates per stage, so that I can measure the effectiveness of the acquisition process.

#### Acceptance Criteria

1. The funnel MUST show the following stages in order with count and conversion rate:
   - Unique visitors
   - Calculator started
   - Email captured
   - Benchmark completed
   - PDF downloaded
   - Meeting scheduled
2. Each stage MUST derive its count from a persisted event or status.
3. Conversion rate MUST be calculated relative to the previous stage.
4. The funnel stages MUST align with the approved user journey.

---

### Requirement 9 — Recent Responses Table

**User Story:** As an Admin, I want to see a table of the most recent benchmark completions, so that I can quickly follow up on the latest activity.

#### Acceptance Criteria

1. The table MUST show paginated results ordered by completion date descending.
2. Each row MUST include: Operator name, Company, Region, Score, Percentile, Maturity level (as colored badge), Completed date/time, PDF availability (icon), and an Actions button (Ver detalle).
3. The maturity level badge MUST use the defined color scheme (green/blue/yellow/red).
4. The "Ver detalle" button MUST navigate to the company detail page.
5. A "Ver todas las respuestas" link MUST be available at the bottom of the table.
6. The table MUST NOT expose secrets or internal persistence identifiers.

---

### Requirement 10 — Dashboard Export

**User Story:** As an Admin, I want to export the dashboard report, so that I can share it with the team or use it in meetings.

#### Acceptance Criteria

1. The Admin MUST be able to export the current dashboard state as a PDF or CSV.
2. The export MUST respect the active date range filter.
3. The export MUST include the main KPIs and the recent responses table.
4. The export MUST NOT include secrets or data outside the Admin's scope.

---

### Requirement 11 — Operator Results View

**User Story:** As an Operator, I want to view my own benchmark results, so that I can understand my Data Center's assessment and download my institutional PDF.

#### Acceptance Criteria

1. The Operator MUST only be able to access their own company's results.
2. The system MUST show results only after the benchmark has been completed.
3. The results view MUST include: score /100, percentile, maturity level with description, and KPIs.
4. The PDF download button MUST only be available when the PDF status is GENERATED.
5. This view consumes data from Benchmark and PDF Generator; it does NOT constitute a global admin query.

---

## Constraints

- The Dashboard MUST use dedicated read models; it MUST NOT directly access JPA repositories from other bounded contexts.
- Cross-module queries MUST respect bounded context boundaries.
- Redis cache and TTL are performance optimizations, not functional MVP requirements.
- Definitive routes and DTOs must come from the OpenAPI contract.

---

## Pending Decisions

- Maximum allowed date range for dashboard filters.
- Whether the conversion funnel unique visitor count comes from analytics or from system events.
- Cache TTL for dashboard metrics.
- Export format: PDF, CSV or both.
