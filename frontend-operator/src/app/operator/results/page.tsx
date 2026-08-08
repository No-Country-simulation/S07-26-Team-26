"use client";

import { useRouter } from "next/navigation";
import { TrendingUp, TrendingDown, ArrowRight, Layers } from "lucide-react";
import { PixelChest, PixelBook, PixelExcelBadge } from "@/components/blocks/PixelIcons";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { CategoryRadarChart } from "@/components/charts/CategoryRadarChart";
import { CategoryBarList } from "@/components/charts/CategoryBarList";
import { KpiCard } from "@/components/dashboard/KpiCard";
import { useBenchmarkResults } from "@/store/benchmarkStore";
import { OperatorStepGuard } from "@/components/shared/OperatorStepGuard";
import { useCalculatorStore } from "@/store/calculatorStore";
import { useInvitationStore } from "@/store/invitationStore";
import { useDashboardKpis } from "@/hooks/useDashboard";
import { useCompanies } from "@/hooks/useCompanies";
import { maturityColor } from "@/lib/scoring";
import { computePercentile, generateFindings, generateRecommendations } from "@/lib/insights";
import { downloadCsv } from "@/lib/export";
import { cn } from "@/lib/utils";

export default function OperatorResultsPage() {
  const router = useRouter();
  const invitation = useInvitationStore((s) => s.invitation);
  const evaluationId = useInvitationStore((s) => s.evaluation?.evaluationId);
  const { overallScore, maturityLevel, categoryBreakdown } = useBenchmarkResults();
  const calculatorKpis = useCalculatorStore((s) => s.kpis);
  const { data: kpis } = useDashboardKpis();
  const { data: companies } = useCompanies();

  const industryAverage = kpis?.averageScore ?? 60;
  const aboveAverage = overallScore >= industryAverage;

  const allScores = (companies ?? [])
    .map((c) => c.score)
    .filter((s): s is number => typeof s === "number");
  const percentile = computePercentile(overallScore, allScores.length ? allScores : [industryAverage]);

  const findings = generateFindings(categoryBreakdown);
  const recommendations = generateRecommendations(categoryBreakdown);

  function handleExcelDownload() {
    const rows: (string | number)[][] = [
      ["Ghost Load — Resultado del Benchmark"],
      ["Organización", invitation?.companyName ?? ""],
      ["Puntaje Total", `${overallScore}/100`],
      ["Nivel", maturityLevel],
      ["Percentil Industrial", `${percentile}%`],
      [],
      ["Categoría", "Puntaje"],
      ...categoryBreakdown.map((c) => [c.label, c.score]),
      [],
      ["Hallazgos"],
      ...findings.map((f) => [f.text]),
      [],
      ["Recomendaciones"],
      ...recommendations.map((r) => [r.label, r.text]),
    ];
    downloadCsv(`ghost-load-benchmark-${evaluationId ?? "reporte"}`, rows);
  }

  return (
    <OperatorStepGuard step="results">
    <div className="space-y-6">
      {/* Headline */}
      <div className="flex flex-col items-start justify-between gap-4 sm:flex-row sm:items-end">
        <div>
          <p className="mb-1.5 text-xs font-semibold uppercase tracking-wide text-gold-700">Resultado del Benchmark</p>
          <h1 className="max-w-3xl text-3xl font-semibold leading-tight tracking-tight text-graphite-900 sm:text-4xl">
            {invitation?.companyName ?? "Tu organización"} alcanzó un nivel{" "}
            <span className="text-forest-700">{maturityLevel}</span>
          </h1>
          <p className="mt-3 max-w-2xl text-sm leading-relaxed text-graphite-500">
            Tu infraestructura fue evaluada en energía, GPU, refrigeración, operaciones y capacidad frente a
            operadores comparables.
          </p>
        </div>
        <Button variant="secondary" size="sm" onClick={handleExcelDownload} className="shrink-0">
          <PixelExcelBadge size={16} />
          Descargar Excel
        </Button>
      </div>

      {/* KPI row */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <KpiCard label="Puntaje Total" value={`${overallScore}/100`} dataCenterIcon="trofeo" accent="gold" />
        <KpiCard label="Nivel Actual" value={maturityLevel} icon={Layers} />
        <KpiCard
          label="Percentil Industrial"
          value={`${percentile}%`}
          dataCenterIcon="comparativo"
          hint={aboveAverage ? "Por encima del promedio" : "Por debajo del promedio"}
        />
        <KpiCard
          label="Capacidad Ociosa"
          value={calculatorKpis ? `${calculatorKpis.idleCapacityPct}%` : "—"}
          dataCenterIcon="porcentaje"
          accent="gold"
        />
      </div>

      {/* vs industry banner */}
      <div className="flex items-center gap-2 rounded-md border border-graphite-100 bg-white px-5 py-3 text-sm text-graphite-600 shadow-panel">
        {aboveAverage ? (
          <TrendingUp className="h-4 w-4 text-forest-700" />
        ) : (
          <TrendingDown className="h-4 w-4 text-red-600" />
        )}
        Estás <strong className="text-graphite-900">{aboveAverage ? "por encima" : "por debajo"}</strong> del
        promedio de la industria ({industryAverage}/100)
      </div>

      {/* Category performance */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Desempeño por Categoría</CardTitle>
          </CardHeader>
          <CardContent>
            <CategoryRadarChart data={categoryBreakdown.map((c) => ({ label: c.label, score: c.score }))} />
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Desglose por Categoría</CardTitle>
          </CardHeader>
          <CardContent>
            <CategoryBarList data={categoryBreakdown.map((c) => ({ label: c.label, score: c.score }))} />
          </CardContent>
        </Card>
      </div>

      {/* Findings + Recommendations */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <div className="rounded-lg bg-forest-950 p-7 text-forest-50">
          <span className="inline-flex items-center gap-1.5 rounded-sm bg-gold-500 px-2.5 py-1 text-[11px] font-semibold uppercase tracking-wide text-forest-950">
            <PixelChest size={16} />
            Hallazgo Clave
          </span>
          <div className="mt-5 space-y-4">
            {findings.length === 0 && <p className="text-sm text-forest-50/60">Sin hallazgos suficientes todavía.</p>}
            {findings.map((f) => (
              <p key={f.category} className="text-[15px] leading-relaxed text-forest-50/90">
                {f.text}
              </p>
            ))}
          </div>
        </div>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <PixelBook size={20} />
              Recomendaciones Estratégicas
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {recommendations.map((r, i) => (
              <div key={r.category} className="flex gap-3">
                <span
                  className={cn(
                    "mt-0.5 flex h-5 w-5 shrink-0 items-center justify-center rounded-full text-[11px] font-semibold",
                    "bg-gold-50 text-gold-700"
                  )}
                >
                  {i + 1}
                </span>
                <div>
                  <p className="text-sm font-medium text-graphite-900">{r.label}</p>
                  <p className="mt-0.5 text-sm leading-relaxed text-graphite-500">{r.text}</p>
                </div>
              </div>
            ))}
          </CardContent>
        </Card>
      </div>

      {/* Maturity badge + CTA */}
      <div className="flex items-center justify-between rounded-lg border border-graphite-100 bg-white px-6 py-5 shadow-panel">
        <div className="flex items-center gap-3">
          <span className={cn("inline-flex items-center rounded-sm border px-3 py-1 text-sm font-medium", maturityColor(maturityLevel))}>
            {maturityLevel}
          </span>
          <span className="text-sm text-graphite-500">Nivel de madurez alcanzado</span>
        </div>
        <Button onClick={() => router.push("/operator/pdf")}>
          Generar Reporte Institucional
          <ArrowRight className="h-4 w-4" />
        </Button>
      </div>
    </div>
    </OperatorStepGuard>
  );
}
