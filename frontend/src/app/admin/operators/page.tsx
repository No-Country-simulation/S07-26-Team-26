'use client';

import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Search } from 'lucide-react';
import { useAdminAuthStore } from '@/store/adminAuthStore';
import { apiClient } from '@/lib/api';

const PAGE_SIZE = 10;

const STATE_LABELS: Record<string, string> = {
  STARTED: 'Iniciada',
  CALCULATOR_COMPLETED: 'Calculadora completada',
  BENCHMARK_COMPLETED: 'Benchmark completado',
  REPORT_GENERATING: 'Generando reporte',
  REPORT_COMPLETED: 'Reporte completado',
  REPORT_FAILED: 'Reporte fallido',
};

export default function AdminOperatorsPage() {
  const accessToken = useAdminAuthStore((state) => state.accessToken);
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');

  const {
    data: operators,
    isLoading,
    isError,
    refetch,
  } = useQuery({
    queryKey: ['operators', page, search],
    queryFn: () =>
      apiClient.listOperators(accessToken ?? '', {
        page,
        size: PAGE_SIZE,
        search: search || undefined,
      }),
    enabled: Boolean(accessToken),
  });

  if (isLoading) {
    return (
      <div className="rounded-2xl border border-slate-800 bg-slate-900/40 animate-pulse h-64" />
    );
  }

  if (isError || !operators) {
    return (
      <div className="p-10 text-center rounded-2xl border border-red-500/20 bg-red-500/5 space-y-3">
        <p className="text-red-400 font-semibold">No se pudieron cargar los operadores</p>
        <button
          onClick={() => refetch()}
          className="px-4 py-2 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 text-xs font-semibold hover:bg-red-500/20 transition-all"
        >
          Reintentar
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold tracking-tight text-white">
            Operadores
          </h1>
          <p className="text-sm text-slate-400 mt-1">
            Evaluaciones completadas por operador.
          </p>
        </div>
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
          <input
            type="text"
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
              setPage(0);
            }}
            placeholder="Buscar por nombre o empresa..."
            className="w-full sm:w-72 pl-10 pr-4 py-2.5 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 placeholder-slate-500 focus:outline-none focus:border-indigo-500/50 focus:ring-1 focus:ring-indigo-500/30 transition-all text-sm"
          />
        </div>
      </div>

      <div className="rounded-2xl border border-slate-800 bg-slate-900/40 backdrop-blur-xl overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-slate-800 text-left text-xs uppercase tracking-wider text-slate-500">
                <th className="px-5 py-3.5">Operador</th>
                <th className="px-5 py-3.5">Empresa</th>
                <th className="px-5 py-3.5">Estado</th>
                <th className="px-5 py-3.5">Puntaje</th>
                <th className="px-5 py-3.5">Madurez</th>
                <th className="px-5 py-3.5">Completada</th>
              </tr>
            </thead>
            <tbody>
              {operators.items.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-5 py-10 text-center text-slate-500">
                    No hay operadores que coincidan.
                  </td>
                </tr>
              ) : (
                operators.items.map((operator) => (
                  <tr
                    key={operator.operatorId}
                    className="border-b border-slate-800/60 hover:bg-slate-900/40 transition-colors"
                  >
                    <td className="px-5 py-4">
                      <p className="font-semibold text-slate-200">{operator.fullName}</p>
                      <p className="text-xs text-slate-500">{operator.email}</p>
                    </td>
                    <td className="px-5 py-4 text-slate-300">
                      {operator.companyName ?? '—'}
                    </td>
                    <td className="px-5 py-4">
                      <span className="px-2 py-1 rounded-md text-xs font-medium bg-slate-800 text-slate-300">
                        {operator.state ? STATE_LABELS[operator.state] ?? operator.state : '—'}
                      </span>
                    </td>
                    <td className="px-5 py-4 font-mono text-slate-300">
                      {operator.benchmarkScore?.toFixed(1) ?? '—'}
                    </td>
                    <td className="px-5 py-4 text-slate-300">
                      {operator.maturityLevel ?? '—'}
                    </td>
                    <td className="px-5 py-4 text-slate-400">
                      {operator.completedAt
                        ? new Date(operator.completedAt).toLocaleDateString()
                        : '—'}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        <div className="flex items-center justify-between px-5 py-4 border-t border-slate-800">
          <p className="text-xs text-slate-500">
            {operators.totalElements} operadores · Página {operators.page + 1} de{' '}
            {Math.max(operators.totalPages, 1)}
          </p>
          <div className="flex items-center gap-2">
            <button
              onClick={() => setPage((p) => Math.max(0, p - 1))}
              disabled={operators.page === 0}
              className="px-3 py-1.5 rounded-lg bg-slate-800 border border-slate-700 text-slate-300 text-xs font-semibold hover:bg-slate-700 hover:text-white transition-all disabled:opacity-40 disabled:cursor-not-allowed"
            >
              Anterior
            </button>
            <button
              onClick={() => setPage((p) => p + 1)}
              disabled={operators.page >= operators.totalPages - 1}
              className="px-3 py-1.5 rounded-lg bg-slate-800 border border-slate-700 text-slate-300 text-xs font-semibold hover:bg-slate-700 hover:text-white transition-all disabled:opacity-40 disabled:cursor-not-allowed"
            >
              Siguiente
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
