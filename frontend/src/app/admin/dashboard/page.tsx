'use client';

import React from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  Users,
  Mail,
  MousePointerClick,
  ClipboardList,
  CheckCircle2,
  TrendingUp,
  Cpu,
  Zap,
  Wallet,
  FileText,
} from 'lucide-react';
import { useAdminAuthStore } from '@/store/adminAuthStore';
import { apiClient } from '@/lib/api';

export default function AdminDashboardPage() {
  const accessToken = useAdminAuthStore((state) => state.accessToken);

  const {
    data: summary,
    isLoading,
    isError,
    refetch,
  } = useQuery({
    queryKey: ['dashboard-summary'],
    queryFn: () => apiClient.dashboardSummary(accessToken ?? ''),
    enabled: Boolean(accessToken),
  });

  const { data: recentResponses } = useQuery({
    queryKey: ['recent-responses'],
    queryFn: () => apiClient.recentResponses(accessToken ?? '', { page: 0, size: 10 }),
    enabled: Boolean(accessToken),
  });

  if (isLoading) {
    return (
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {[...Array(8)].map((_, i) => (
          <div
            key={i}
            className="h-28 rounded-2xl border border-slate-800 bg-slate-900/40 animate-pulse"
          />
        ))}
      </div>
    );
  }

  if (isError || !summary) {
    return (
      <div className="p-10 text-center rounded-2xl border border-red-500/20 bg-red-500/5 space-y-3">
        <p className="text-red-400 font-semibold">No se pudieron cargar las métricas</p>
        <button
          onClick={() => refetch()}
          className="px-4 py-2 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 text-xs font-semibold hover:bg-red-500/20 transition-all"
        >
          Reintentar
        </button>
      </div>
    );
  }

  const formatCurrency = (value: number) =>
    new Intl.NumberFormat('es-AR', {
      style: 'currency',
      currency: 'USD',
      maximumFractionDigits: 0,
    }).format(value);

  const kpis = [
    { label: 'Contactos cargados', value: summary.contactsLoaded, icon: Users },
    { label: 'Invitaciones enviadas', value: summary.invitationsSent, icon: Mail },
    { label: 'Enlaces visitados', value: summary.linksVisited, icon: MousePointerClick },
    { label: 'Evaluaciones iniciadas', value: summary.evaluationsStarted, icon: ClipboardList },
    {
      label: 'Evaluaciones completadas',
      value: summary.evaluationsCompleted,
      icon: CheckCircle2,
    },
    {
      label: 'Tasa de finalización',
      value: `${summary.completionRate.toFixed(1)}%`,
      icon: TrendingUp,
    },
    {
      label: 'Puntaje promedio',
      value: summary.averageBenchmarkScore.toFixed(1),
      icon: TrendingUp,
    },
    {
      label: 'Utilización promedio',
      value: `${summary.averageUtilization.toFixed(1)}%`,
      icon: Cpu,
    },
    {
      label: 'Capacidad no productiva',
      value: `${summary.accumulatedNonProductiveCapacityMw.toFixed(1)} MW`,
      icon: Zap,
    },
    {
      label: 'Costo anual estimado',
      value: formatCurrency(summary.accumulatedEstimatedAnnualCost),
      icon: Wallet,
    },
    {
      label: 'Reportes generados',
      value: summary.generatedReports,
      icon: FileText,
    },
  ];

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-extrabold tracking-tight text-white">
          Resumen de actividad
        </h1>
        <p className="text-sm text-slate-400 mt-1">
          Métricas globales del benchmark de madurez.
        </p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
        {kpis.map((kpi) => {
          const Icon = kpi.icon;
          return (
            <div
              key={kpi.label}
              className="rounded-2xl border border-slate-800 bg-slate-900/40 backdrop-blur-xl p-5 relative overflow-hidden group"
            >
              <div className="absolute top-0 right-0 w-20 h-20 bg-indigo-500/5 rounded-full blur-2xl group-hover:bg-indigo-500/10 transition-all duration-300" />
              <div className="flex items-center gap-3 mb-3">
                <div className="p-2 rounded-lg bg-indigo-500/10 border border-indigo-500/20 text-indigo-400">
                  <Icon className="w-4 h-4" />
                </div>
                <p className="text-xs text-slate-400 font-medium uppercase tracking-wider">
                  {kpi.label}
                </p>
              </div>
              <p className="text-2xl font-extrabold text-white">{kpi.value}</p>
            </div>
          );
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <section className="rounded-2xl border border-slate-800 bg-slate-900/40 backdrop-blur-xl p-6">
          <h2 className="text-lg font-bold text-white mb-4">
            Distribución de madurez
          </h2>
          <ul className="space-y-3">
            {summary.maturityDistribution.map((item) => (
              <li key={item.level} className="flex items-center justify-between gap-4">
                <span className="text-sm text-slate-300">{item.level}</span>
                <div className="flex-1 h-2 rounded-full bg-slate-800 overflow-hidden">
                  <div
                    className="h-full rounded-full bg-indigo-500"
                    style={{
                      width: `${Math.min(
                        100,
                        summary.maturityDistribution.length === 0
                          ? 0
                          : (item.count / Math.max(...summary.maturityDistribution.map((m) => m.count))) * 100
                      )}%`,
                    }}
                  />
                </div>
                <span className="text-sm font-mono text-slate-400">{item.count}</span>
              </li>
            ))}
          </ul>
        </section>

        <section className="rounded-2xl border border-slate-800 bg-slate-900/40 backdrop-blur-xl p-6">
          <h2 className="text-lg font-bold text-white mb-4">
            Promedio por módulo
          </h2>
          <ul className="space-y-3">
            {summary.categoryAverages.map((item) => (
              <li key={item.module} className="flex items-center justify-between gap-4">
                <span className="text-sm text-slate-300">{item.module}</span>
                <div className="flex-1 h-2 rounded-full bg-slate-800 overflow-hidden">
                  <div
                    className="h-full rounded-full bg-violet-500"
                    style={{ width: `${item.score}%` }}
                  />
                </div>
                <span className="text-sm font-mono text-slate-400">
                  {item.score.toFixed(1)}
                </span>
              </li>
            ))}
          </ul>
        </section>
      </div>

      <section className="rounded-2xl border border-slate-800 bg-slate-900/40 backdrop-blur-xl p-6">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-bold text-white">Respuestas recientes</h2>
          <span className="text-xs text-slate-500">Últimas evaluaciones completadas</span>
        </div>
        {recentResponses && recentResponses.items.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-xs text-slate-500 uppercase tracking-wider border-b border-slate-800">
                  <th className="py-2 pr-4">Operador</th>
                  <th className="py-2 pr-4">Empresa</th>
                  <th className="py-2 pr-4">Score</th>
                  <th className="py-2 pr-4">Percentil</th>
                  <th className="py-2 pr-4">Nivel</th>
                  <th className="py-2">Fecha</th>
                </tr>
              </thead>
              <tbody>
                {recentResponses.items.map((item) => (
                  <tr
                    key={item.evaluationId}
                    className="border-b border-slate-800/60 text-slate-300"
                  >
                    <td className="py-2.5 pr-4">
                      <p className="font-medium text-slate-200">{item.fullName}</p>
                      <p className="text-xs text-slate-500">{item.email}</p>
                    </td>
                    <td className="py-2.5 pr-4">{item.companyName}</td>
                    <td className="py-2.5 pr-4 font-mono">{item.score.toFixed(1)}</td>
                    <td className="py-2.5 pr-4 font-mono">{item.percentile.toFixed(1)}</td>
                    <td className="py-2.5 pr-4">
                      <span className="px-2 py-0.5 rounded bg-indigo-500/10 border border-indigo-500/25 text-indigo-400 text-xs font-medium">
                        {item.maturityLevel}
                      </span>
                    </td>
                    <td className="py-2.5 text-xs text-slate-500">
                      {new Date(item.completedAt).toLocaleDateString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <p className="text-sm text-slate-500">
            Aún no hay evaluaciones completadas.
          </p>
        )}
      </section>
    </div>
  );
}
