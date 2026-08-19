"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { useAuthStore } from "@/store/authStore";
import { dashboardFakeData } from "@/data/dashboardFakeData";
import { AdminOutreachAreaChart } from "@/components/charts/AdminOutreachAreaChart";
import { AdminEmailDeliveryDonut } from "@/components/charts/AdminEmailDeliveryDonut";
import { AdminCampaignStatusChart } from "@/components/charts/AdminCampaignStatusChart";

export default function AdminDashboardPage() {
  const router = useRouter();
  const { isAuthenticated } = useAuthStore();
  const [hasMounted, setHasMounted] = useState(false);

  useEffect(() => {
    setHasMounted(true);
  }, []);

  useEffect(() => {
    if (!isAuthenticated) {
      router.replace("/login");
    }
  }, [isAuthenticated, router]);

  if (!hasMounted || !isAuthenticated) {
    return null;
  }

  return (
    <div className="min-h-[calc(100vh-2rem)] bg-slate-50 px-4 py-6 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-6xl space-y-8 rounded-[2.5rem] border border-slate-200 bg-white p-8 shadow-sm shadow-slate-200">
        {/* Header */}
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <p className="text-sm uppercase tracking-[0.3em] font-semibold text-emerald-700">
              Panel Administrativo
            </p>
            <h1 className="mt-2 text-4xl font-semibold text-slate-950">
              Dashboard Admin
            </h1>
            <p className="mt-1 text-slate-600">
              Métricas clave, analítica de envíos y conversión del sistema.
            </p>
          </div>
          <div className="flex items-center gap-3">
            <Link
              href="/dashboard/admin/campaign"
              className="inline-flex items-center justify-center rounded-3xl bg-emerald-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-emerald-500 shadow-sm"
            >
              + Nueva Campaña
            </Link>
            <Link
              href="/dashboard/admin/contact-import"
              className="inline-flex items-center justify-center rounded-3xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm font-semibold text-emerald-700 transition hover:bg-emerald-100"
            >
              Importar CSV
            </Link>
          </div>
        </div>

        {/* Top Numeric KPI Grid */}
        <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
          <div className="rounded-[2rem] border border-slate-200 bg-slate-50 p-5 transition hover:bg-slate-100/70">
            <div className="flex items-center gap-3">
              <img
                src="/icons/12_servidor.png"
                alt="Importaciones"
                className="h-8 w-8"
              />
              <div>
                <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                  Importaciones
                </p>
                <p className="mt-1 text-2xl font-semibold text-slate-950">
                  {dashboardFakeData.outreach.totalContactImports}
                </p>
              </div>
            </div>
            <p className="mt-3 text-xs text-slate-500">
              {dashboardFakeData.outreach.uniqueContacts} contactos únicos
            </p>
          </div>

          <div className="rounded-[2rem] border border-slate-200 bg-slate-50 p-5 transition hover:bg-slate-100/70">
            <div className="flex items-center gap-3">
              <img
                src="/icons/09_trofeo.png"
                alt="Campañas"
                className="h-8 w-8"
              />
              <div>
                <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                  Campañas Totales
                </p>
                <p className="mt-1 text-2xl font-semibold text-slate-950">
                  {dashboardFakeData.outreach.totalCampaigns}
                </p>
              </div>
            </div>
            <p className="mt-3 text-xs text-slate-500">
              {dashboardFakeData.outreach.activeCampaigns} activas en curso
            </p>
          </div>

          <div className="rounded-[2rem] border border-slate-200 bg-slate-50 p-5 transition hover:bg-slate-100/70">
            <div className="flex items-center gap-3">
              <img
                src="/icons/07_notificacion.png"
                alt="Emails Enviados"
                className="h-8 w-8"
              />
              <div>
                <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                  Emails Enviados
                </p>
                <p className="mt-1 text-2xl font-semibold text-slate-950">
                  {dashboardFakeData.emailDelivery.sentEmails}
                </p>
              </div>
            </div>
            <p className="mt-3 text-xs text-slate-500">
              {dashboardFakeData.emailDelivery.deliveryRate}% tasa de entrega
            </p>
          </div>

          <div className="rounded-[2rem] border border-slate-200 bg-slate-50 p-5 transition hover:bg-slate-100/70">
            <div className="flex items-center gap-3">
              <img
                src="/icons/11_porcentaje.png"
                alt="Completion Rate"
                className="h-8 w-8"
              />
              <div>
                <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                  Completion Rate
                </p>
                <p className="mt-1 text-2xl font-semibold text-slate-950">
                  {dashboardFakeData.completionTracking.completionRate}%
                </p>
              </div>
            </div>
            <p className="mt-3 text-xs text-slate-500">
              {dashboardFakeData.completionTracking.evaluationsCompleted} evaluaciones listas
            </p>
          </div>
        </div>

        {/* Main Chart 1: Outreach & Funnel Trend */}
        <section className="rounded-[2rem] border border-slate-200 bg-white p-6 shadow-sm shadow-slate-100">
          <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between mb-6">
            <div>
              <p className="text-xs uppercase tracking-[0.2em] font-semibold text-emerald-700">
                Tendencia Semanal
              </p>
              <h2 className="text-2xl font-semibold text-slate-950 mt-1">
                Evolución de Outreach y Conversión
              </h2>
            </div>
            <div className="flex items-center gap-4 text-xs font-medium text-slate-600">
              <span className="flex items-center gap-1.5">
                <span className="h-3 w-3 rounded-full bg-emerald-600 inline-block" /> Envíos
              </span>
              <span className="flex items-center gap-1.5">
                <span className="h-3 w-3 rounded-full bg-sky-600 inline-block" /> Visitas
              </span>
              <span className="flex items-center gap-1.5">
                <span className="h-3 w-3 rounded-full bg-purple-600 inline-block" /> Completados
              </span>
            </div>
          </div>
          <AdminOutreachAreaChart />
        </section>

        {/* Charts Row 2: Delivery Donut & Campaign Status */}
        <div className="grid gap-6 lg:grid-cols-2">
          {/* Email Delivery Donut Chart */}
          <section className="rounded-[2rem] border border-slate-200 bg-white p-6 shadow-sm shadow-slate-100 flex flex-col justify-between">
            <div>
              <p className="text-xs uppercase tracking-[0.2em] font-semibold text-emerald-700">
                Entrega de Emails
              </p>
              <h3 className="text-xl font-semibold text-slate-950 mt-1 mb-4">
                Estado de Envíos
              </h3>
            </div>
            <AdminEmailDeliveryDonut />
          </section>

          {/* Campaign Status Bar Chart */}
          <section className="rounded-[2rem] border border-slate-200 bg-white p-6 shadow-sm shadow-slate-100 flex flex-col justify-between">
            <div>
              <p className="text-xs uppercase tracking-[0.2em] font-semibold text-emerald-700">
                Distribución de Campañas
              </p>
              <h3 className="text-xl font-semibold text-slate-950 mt-1 mb-4">
                Campañas por Estado
              </h3>
            </div>
            <AdminCampaignStatusChart />
          </section>
        </div>

        {/* Bottom Section: Benchmark & Completion Metrics */}
        <div className="grid gap-6 lg:grid-cols-2">
          {/* Completion Funnel Summary */}
          <div className="rounded-[2rem] border border-slate-200 bg-white p-6 shadow-sm shadow-slate-100">
            <h3 className="text-xl font-semibold text-slate-950 mb-4">
              Funnel de Embudos de Invitación
            </h3>
            <div className="space-y-4">
              <div>
                <div className="flex justify-between text-xs font-semibold text-slate-700 mb-1">
                  <span>Invitaciones Enviadas</span>
                  <span>{dashboardFakeData.completionTracking.invitationsSent} (100%)</span>
                </div>
                <div className="h-2.5 w-full rounded-full bg-slate-100 overflow-hidden">
                  <div className="h-full rounded-full bg-emerald-500 w-full" />
                </div>
              </div>
              <div>
                <div className="flex justify-between text-xs font-semibold text-slate-700 mb-1">
                  <span>Links Visitados</span>
                  <span>{dashboardFakeData.completionTracking.linksVisited} (53.4%)</span>
                </div>
                <div className="h-2.5 w-full rounded-full bg-slate-100 overflow-hidden">
                  <div className="h-full rounded-full bg-sky-500 w-[53.4%]" />
                </div>
              </div>
              <div>
                <div className="flex justify-between text-xs font-semibold text-slate-700 mb-1">
                  <span>Evaluaciones Iniciadas</span>
                  <span>{dashboardFakeData.completionTracking.evaluationsStarted} (32.7%)</span>
                </div>
                <div className="h-2.5 w-full rounded-full bg-slate-100 overflow-hidden">
                  <div className="h-full rounded-full bg-amber-500 w-[32.7%]" />
                </div>
              </div>
              <div>
                <div className="flex justify-between text-xs font-semibold text-slate-700 mb-1">
                  <span>Evaluaciones Completadas</span>
                  <span>{dashboardFakeData.completionTracking.evaluationsCompleted} (18.1%)</span>
                </div>
                <div className="h-2.5 w-full rounded-full bg-slate-100 overflow-hidden">
                  <div className="h-full rounded-full bg-purple-500 w-[18.1%]" />
                </div>
              </div>
            </div>
          </div>

          {/* Results Benchmark & Reports */}
          <div className="rounded-[2rem] border border-slate-200 bg-white p-6 shadow-sm shadow-slate-100 flex flex-col justify-between">
            <h3 className="text-xl font-semibold text-slate-950 mb-4">
              Resultados de Benchmark & PDF
            </h3>
            <div className="grid gap-4 sm:grid-cols-2">
              <div className="rounded-[1.75rem] bg-emerald-50 border border-emerald-100 p-5">
                <p className="text-xs font-semibold uppercase tracking-wider text-emerald-800">
                  Puntaje Promedio
                </p>
                <p className="mt-2 text-3xl font-bold text-emerald-950">
                  {dashboardFakeData.results.averageBenchmarkScore}%
                </p>
                <p className="mt-2 text-xs text-emerald-700">
                  Calculado sobre evaluaciones completadas
                </p>
              </div>

              <div className="rounded-[1.75rem] bg-slate-50 border border-slate-200 p-5">
                <p className="text-xs font-semibold uppercase tracking-wider text-slate-600">
                  Reportes PDF
                </p>
                <p className="mt-2 text-3xl font-bold text-slate-950">
                  {dashboardFakeData.results.generatedPdfs}
                </p>
                <p className="mt-2 text-xs text-slate-500">
                  Documentos PDF generados
                </p>
              </div>
            </div>
            <div className="mt-6 pt-4 border-t border-slate-100 flex justify-end">
              <Link
                href="/dashboard/admin/campaign"
                className="text-xs font-semibold text-emerald-700 hover:underline inline-flex items-center gap-1"
              >
                Ver todas las campañas &rarr;
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

