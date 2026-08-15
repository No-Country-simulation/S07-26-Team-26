"use client";

import { useRef, useState } from "react";
import {
  CheckCircle2,
  Share2,
  Mail,
  Building2,
  Phone,
  Award,
  ExternalLink,
  CalendarClock,
  TrendingUp,
  TrendingDown,
  AlertTriangle,
} from "lucide-react";
import {
  PixelPdfBadge,
  PixelDocumentOpen,
  PixelMail,
  PixelExcelBadge,
} from "@/components/blocks/PixelIcons";
import { BrandCube } from "@/components/blocks/BrandCube";
import { Card, CardContent } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { useBenchmarkResults, useBenchmarkStore } from "@/store/benchmarkStore";
import { useCalculatorStore } from "@/store/calculatorStore";
import { useInvitationStore } from "@/store/invitationStore";
import { useReportStatusPolling } from "@/hooks/useInvitation";
import { downloadReportPdfBlob, USE_MOCKS } from "@/services/api";
import { maturityColor } from "@/lib/scoring";
import { cn, formatNumber, formatDate } from "@/lib/utils";
import {
  FOUNDER_CONTACT,
  REPORT_BRAND,
  buildReportReference,
} from "@/lib/brand";
import { OperatorStepGuard } from "@/components/shared/OperatorStepGuard";

export default function OperatorPdfPage() {
  const [linkCopied, setLinkCopied] = useState(false);
  // Captured once on mount so the "Generado el ..." timestamp and document
  // reference stay stable across re-renders instead of drifting by a few
  // milliseconds every time this component re-renders.
  const [generatedAt] = useState(() => new Date().toISOString());
  const previewRef = useRef<HTMLDivElement>(null);
  const invitation = useInvitationStore((s) => s.invitation);
  const evaluationId = useInvitationStore((s) => s.evaluation?.evaluationId);
  const { overallScore, maturityLevel } = useBenchmarkResults();
  const kpis = useCalculatorStore((s) => s.kpis);

  // GET /api/v1/evaluations/{evaluationId}/report, polled until it leaves
  // REPORT_GENERATING -- see NUEVO FLUJO OFICIAL: no mostramos el PDF de
  // inmediato al terminar el benchmark, primero una pantalla de carga.
  const { data: report } = useReportStatusPolling(evaluationId);
  const reportStatus = report?.status ?? "REPORT_GENERATING";

  // Use the percentile returned by the backend benchmark result.
  const backendResult = useBenchmarkStore((s) => s.backendResult);
  const percentile = backendResult?.percentile != null
    ? Math.round(backendResult.percentile)
    : null;

  const reportRef = buildReportReference(evaluationId, generatedAt);

  async function handleDownload() {
    if (evaluationId && !USE_MOCKS) {
      try {
        await downloadReportPdfBlob(evaluationId);
        return;
      } catch (err) {
        console.error("Falló la descarga del PDF del backend, fallback a impresión", err);
      }
    }
    window.print();
  }

  function handleExcelDownload() {
    if (report?.excelUrl) window.open(report.excelUrl, "_blank");
  }

  function handlePreview() {
    previewRef.current?.scrollIntoView({ behavior: "smooth", block: "start" });
  }

  async function handleShare() {
    const shareData = {
      title: "Reporte Ghost Load",
      text: `${invitation?.companyName ?? "Nuestra organización"} obtuvo ${overallScore}/100 (${maturityLevel}) en el benchmark de Project Ghost Load.`,
      url: typeof window !== "undefined" ? window.location.href : "",
    };
    if (typeof navigator !== "undefined" && navigator.share) {
      try {
        await navigator.share(shareData);
      } catch {
        // user cancelled -- no-op
      }
      return;
    }
    if (typeof navigator !== "undefined" && navigator.clipboard) {
      await navigator.clipboard.writeText(shareData.url);
      setLinkCopied(true);
      setTimeout(() => setLinkCopied(false), 2000);
    }
  }

  function handleEmail() {
    const subject = encodeURIComponent(
      "Reporte de Benchmark — Project Ghost Load",
    );
    const body = encodeURIComponent(
      `Hola,\n\nAdjunto el resultado de nuestro benchmark de infraestructura: ${overallScore}/100 (${maturityLevel}).\n\nSaludos.`,
    );
    window.location.href = `mailto:?subject=${subject}&body=${body}`;
  }

  const isGenerating =
    reportStatus === "NOT_REQUESTED" || reportStatus === "REPORT_GENERATING";
  const isFailed = reportStatus === "REPORT_FAILED";
  const isReady = reportStatus === "REPORT_COMPLETED";

  return (
    <OperatorStepGuard step="report">
      <div className="space-y-6">
        {/* Loading gate -- shown while the backend job is running, before any
          part of the actual document renders. */}
        {isGenerating && (
          <div className="flex flex-col items-center gap-3 rounded-lg border border-forest-100 bg-forest-50/60 px-8 py-16 text-center">
            <div className="h-12 w-12 animate-pulse rounded-full bg-forest-100" />
            <h1 className="text-xl font-semibold tracking-tight text-graphite-900">
              Estamos generando tu reporte…
            </h1>
            <p className="max-w-sm text-sm text-graphite-500">
              Esto puede tardar unos segundos. No cierres esta pantalla.
            </p>
          </div>
        )}

        {isFailed && (
          <div className="flex flex-col items-center gap-3 rounded-lg border border-red-100 bg-red-50/60 px-8 py-16 text-center">
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-red-600 text-white">
              <AlertTriangle className="h-6 w-6" strokeWidth={1.75} />
            </div>
            <h1 className="text-xl font-semibold tracking-tight text-graphite-900">
              No pudimos generar tu reporte
            </h1>
            <p className="max-w-sm text-sm text-graphite-500">
              Ocurrió un problema generando el documento. Vuelve a intentarlo en
              unos minutos.
            </p>
          </div>
        )}

        {isReady && (
          <>
            {/* Ready banner -- UI chrome, not part of the document itself */}
            <div className="flex flex-col items-center gap-3 rounded-lg border border-forest-100 bg-forest-50/60 px-8 py-10 text-center print:hidden">
              <div className="flex h-12 w-12 items-center justify-center rounded-full bg-forest-700 text-gold-400">
                <CheckCircle2 className="h-6 w-6" strokeWidth={1.75} />
              </div>
              <h1 className="text-2xl font-semibold tracking-tight text-graphite-900">
                Tu reporte institucional está listo
              </h1>
              <p className="max-w-md text-sm text-graphite-500">
                 Puntaje {overallScore}/100 · Nivel {maturityLevel}{percentile != null ? ` · Percentil ${percentile} de la industria` : ""}. Descárgalo, compártelo o envíalo
                directamente a tu equipo.
              </p>
            </div>

            {/* Action buttons -- also UI chrome, hidden on print */}
            <div className="flex flex-wrap items-center justify-center gap-3 print:hidden">
              <Button variant="gold" onClick={handleDownload}>
                <PixelPdfBadge size={18} />
                Descargar PDF
              </Button>
              {report?.excelUrl && (
                <Button variant="secondary" onClick={handleExcelDownload}>
                  <PixelExcelBadge size={18} />
                  Descargar Excel
                </Button>
              )}
              <Button variant="secondary" onClick={handlePreview}>
                <PixelDocumentOpen size={18} />
                Ver Vista Previa
              </Button>
              <Button variant="secondary" onClick={handleShare}>
                <Share2 className="h-4 w-4" />
                {linkCopied ? "Enlace copiado" : "Compartir"}
              </Button>
              <Button variant="secondary" onClick={handleEmail}>
                <PixelMail size={18} />
                Enviar por Correo
              </Button>
            </div>

            {/* PDF preview -- this card IS the institutional document. It's the
              only thing left visible when printing (border/shadow stripped
              too, so it doesn't look like a screen widget on paper). */}
            <div ref={previewRef}>
              <Card className="overflow-hidden print:border-none print:shadow-none">
                {/* Header: branding */}
                <div className="bg-forest-950 px-8 py-6 text-forest-50">
                  <div className="flex items-start justify-between gap-4">
                    <div className="flex items-center gap-3">
                      <BrandCube variant="grass" size={36} />
                      <div>
                        <p className="text-sm font-semibold">
                          {invitation?.companyName ?? "Tu empresa"}
                        </p>
                        <p className="text-xs text-forest-50/50">
                          {REPORT_BRAND.reportTitle} —{" "}
                          {REPORT_BRAND.documentLabel}
                        </p>
                      </div>
                    </div>
                    <div className="shrink-0 text-right">
                      <span className="font-mono text-[10px] uppercase tracking-widest text-gold-500">
                        {REPORT_BRAND.productName}
                      </span>
                      <p className="mt-1 font-mono text-[10px] text-forest-50/40">
                        Ref. {reportRef}
                      </p>
                    </div>
                  </div>
                </div>

                <CardContent className="space-y-6 p-8">
                  {/* Overall score */}
                  <div className="flex flex-wrap items-center gap-8 border-b border-graphite-100 pb-6">
                    <div>
                      <p className="text-xs uppercase tracking-wide text-graphite-400">
                        Puntaje de Benchmark
                      </p>
                      <p className="font-tabular text-4xl font-semibold text-forest-800">
                        {overallScore}/100
                      </p>
                    </div>
                    <span
                      className={cn(
                        "rounded-sm border px-3 py-1 text-sm font-medium",
                        maturityColor(maturityLevel),
                      )}
                    >
                      {maturityLevel}
                    </span>
                  </div>

                  {/* Industry position */}
                  <div>
                    <p className="mb-3 flex items-center gap-1.5 text-xs font-semibold uppercase tracking-wide text-graphite-500">
                      <Award className="h-3.5 w-3.5" />
                      Posición en el Benchmark de la Industria
                    </p>
                    <div className="flex flex-wrap items-center gap-x-8 gap-y-3 rounded-md border border-graphite-100 bg-graphite-50/50 px-5 py-4">
                      <div>
                        <p className="font-tabular text-2xl font-semibold text-graphite-900">
                          {percentile != null ? `Percentil ${percentile}` : "—"}
                        </p>
                        <p className="text-xs text-graphite-500">
                          frente a los operadores evaluados en el benchmark
                        </p>
                      </div>
                      {percentile != null && (
                        <div className="flex items-center gap-1.5 text-sm">
                          {percentile >= 50 ? (
                            <TrendingUp className="h-4 w-4 text-forest-700" />
                          ) : (
                            <TrendingDown className="h-4 w-4 text-red-600" />
                          )}
                          <span className="text-graphite-600">
                            <strong className="text-graphite-900">
                              {percentile >= 50 ? "Por encima" : "Por debajo"}
                            </strong>{" "}
                            del promedio de la industria
                          </span>
                        </div>
                      )}
                    </div>
                  </div>

                  {/* Calculator KPI summary */}
                  <div>
                    <p className="mb-3 text-xs font-semibold uppercase tracking-wide text-graphite-500">
                      Indicadores de Infraestructura
                    </p>
                    {kpis ? (
                      <div className="grid grid-cols-2 gap-4 sm:grid-cols-3">
                        <MiniKpi
                          label="Capacidad Ociosa"
                          value={`${formatNumber(kpis.idleCapacityKw)} kW`}
                        />
                        <MiniKpi
                          label="Capacidad Ociosa %"
                          value={`${kpis.idleCapacityPct}%`}
                        />
                        <MiniKpi
                          label="Potencia / GPU"
                          value={`${kpis.powerPerGpuKw} kW`}
                        />
                        <MiniKpi
                          label="Margen de Refrigeración"
                          value={`${formatNumber(kpis.coolingHeadroomKw)} kW`}
                        />
                        <MiniKpi
                          label="Necesidad Proyectada"
                          value={`${formatNumber(kpis.projectedCapacityNeedKw)} kW`}
                        />
                      </div>
                    ) : (
                      <p className="text-sm text-graphite-400">
                        Completa la calculadora para ver esta sección.
                      </p>
                    )}
                  </div>

                  {/* Founder contact block */}
                  <div className="rounded-md border border-gold-100 bg-gold-50/40 p-5">
                    <p className="mb-3 flex items-center gap-1.5 text-xs font-semibold uppercase tracking-wide text-gold-700">
                      <Building2 className="h-3.5 w-3.5" />
                      Contacto del Fundador
                    </p>
                    <p className="text-sm text-graphite-700">
                      {FOUNDER_CONTACT.role} · Listos para conversar sobre lo
                      que este reporte significa para tu roadmap.
                    </p>
                    <div className="mt-3 flex flex-wrap gap-x-5 gap-y-2 text-sm text-graphite-600">
                      <span className="flex items-center gap-1.5">
                        <Mail className="h-3.5 w-3.5" /> {FOUNDER_CONTACT.email}
                      </span>
                      <span className="flex items-center gap-1.5">
                        <Phone className="h-3.5 w-3.5" />{" "}
                        {FOUNDER_CONTACT.phone}
                      </span>
                      <a
                        href={FOUNDER_CONTACT.linkedinUrl}
                        target="_blank"
                        rel="noreferrer"
                        className="flex items-center gap-1.5 text-forest-700 hover:underline"
                      >
                        <ExternalLink className="h-3.5 w-3.5" /> LinkedIn
                      </a>
                    </div>
                    <a
                      href={FOUNDER_CONTACT.scheduleCallUrl}
                      target="_blank"
                      rel="noreferrer"
                      className="mt-4 inline-flex items-center gap-1.5 rounded-sm bg-forest-700 px-3.5 py-1.5 text-xs font-medium text-white print:bg-transparent print:px-0 print:py-0 print:text-forest-700 print:underline"
                    >
                      <CalendarClock className="h-3.5 w-3.5" />
                      Agendar una llamada
                    </a>
                  </div>

                  {/* Institutional footer */}
                  <p className="border-t border-graphite-100 pt-4 text-center text-[11px] text-graphite-400">
                    {REPORT_BRAND.confidentialityNote} · Generado el{" "}
                    {formatDate(generatedAt)} · Ref. {reportRef}
                  </p>
                </CardContent>
              </Card>
            </div>
          </>
        )}
      </div>
    </OperatorStepGuard>
  );
}

function MiniKpi({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-md border border-graphite-100 bg-graphite-50/50 px-4 py-3">
      <p className="text-xs text-graphite-500">{label}</p>
      <p className="font-tabular text-base font-semibold text-graphite-900">
        {value}
      </p>
    </div>
  );
}
