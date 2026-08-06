'use client';

import React, { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  Download,
  Plus,
  AlertTriangle,
  MessageSquare,
  History,
  TrendingUp,
} from 'lucide-react';
import { useAdminAuthStore } from '@/store/adminAuthStore';
import {
  apiClient,
  PipelineFilters,
  PipelineStatusValue,
} from '@/lib/api';

const STATUS_LABELS: Record<PipelineStatusValue, string> = {
  OUTREACH_PENDING: 'Contacto inicial',
  OUTREACH_SENT: 'Invitación enviada',
  MEETING_SCHEDULED: 'Reunión agendada',
  CONVERTED: 'Convertido',
  LOST: 'Perdido',
};

const STATUS_STYLES: Record<PipelineStatusValue, string> = {
  OUTREACH_PENDING: 'bg-slate-500/10 border-slate-500/25 text-slate-400',
  OUTREACH_SENT: 'bg-sky-500/10 border-sky-500/25 text-sky-400',
  MEETING_SCHEDULED: 'bg-amber-500/10 border-amber-500/25 text-amber-400',
  CONVERTED: 'bg-emerald-500/10 border-emerald-500/25 text-emerald-400',
  LOST: 'bg-red-500/10 border-red-500/25 text-red-400',
};

const TRANSITIONS: Record<PipelineStatusValue, PipelineStatusValue[]> = {
  OUTREACH_PENDING: ['OUTREACH_SENT', 'LOST'],
  OUTREACH_SENT: ['MEETING_SCHEDULED', 'OUTREACH_PENDING', 'LOST'],
  MEETING_SCHEDULED: ['CONVERTED', 'LOST', 'OUTREACH_SENT'],
  CONVERTED: [],
  LOST: [],
};

export default function CrmPipelinePage() {
  const accessToken = useAdminAuthStore((state) => state.accessToken);
  const queryClient = useQueryClient();
  const [filters, setFilters] = useState<PipelineFilters>({});
  const [showCreate, setShowCreate] = useState(false);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [newNote, setNewNote] = useState('');
  const [createForm, setCreateForm] = useState({
    companyName: '',
    contactName: '',
    email: '',
    region: '',
    benchmarkScore: '',
  });
  const [createError, setCreateError] = useState<string | null>(null);

  const { data: entries, isLoading, refetch } = useQuery({
    queryKey: ['crm-pipeline', filters],
    queryFn: () => apiClient.listPipeline(accessToken ?? '', filters),
    enabled: Boolean(accessToken),
  });

  const { data: detail } = useQuery({
    queryKey: ['crm-pipeline-detail', selectedId],
    queryFn: () => apiClient.getPipelineDetail(accessToken ?? '', selectedId!),
    enabled: Boolean(accessToken && selectedId),
  });

  const createMutation = useMutation({
    mutationFn: () =>
      apiClient.createPipelineEntry(accessToken ?? '', {
        companyName: createForm.companyName,
        contactName: createForm.contactName || undefined,
        email: createForm.email || undefined,
        region: createForm.region || undefined,
        benchmarkScore: createForm.benchmarkScore
          ? Number(createForm.benchmarkScore)
          : undefined,
      }),
    onSuccess: () => {
      setShowCreate(false);
      setCreateForm({
        companyName: '',
        contactName: '',
        email: '',
        region: '',
        benchmarkScore: '',
      });
      queryClient.invalidateQueries({ queryKey: ['crm-pipeline'] });
      void refetch();
    },
    onError: () => {
      setCreateError('No se pudo crear la empresa. Verifica los datos.');
    },
  });

  const transitionMutation = useMutation({
    mutationFn: (status: PipelineStatusValue) =>
      apiClient.transitionPipelineStatus(accessToken ?? '', selectedId!, status),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['crm-pipeline'] });
      queryClient.invalidateQueries({ queryKey: ['crm-pipeline-detail', selectedId] });
    },
  });

  const noteMutation = useMutation({
    mutationFn: (note: string) =>
      apiClient.addPipelineNote(accessToken ?? '', selectedId!, note),
    onSuccess: () => {
      setNewNote('');
      queryClient.invalidateQueries({ queryKey: ['crm-pipeline'] });
      queryClient.invalidateQueries({ queryKey: ['crm-pipeline-detail', selectedId] });
    },
  });

  const exportMutation = useMutation({
    mutationFn: () => apiClient.exportPipelineCsv(accessToken ?? '', filters),
    onSuccess: (blob) => {
      const url = URL.createObjectURL(blob);
      const anchor = document.createElement('a');
      anchor.href = url;
      anchor.download = 'pipeline.csv';
      anchor.click();
      URL.revokeObjectURL(url);
    },
  });

  const selected = detail ?? null;
  const counts = (entries ?? []).reduce<Record<string, number>>((acc, entry) => {
    acc[entry.status] = (acc[entry.status] ?? 0) + 1;
    return acc;
  }, {});

  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-extrabold tracking-tight text-white">
            Pipeline comercial
          </h1>
          <p className="text-sm text-slate-400 mt-1">
            Seguimiento de empresas desde el primer contacto hasta la conversión.
          </p>
        </div>
        <div className="flex gap-2">
          <button
            onClick={() => exportMutation.mutate()}
            disabled={exportMutation.isPending}
            className="flex items-center gap-2 px-4 py-2.5 rounded-lg bg-slate-900 border border-slate-800 text-slate-300 text-sm font-semibold hover:bg-slate-800 transition-all disabled:opacity-40"
          >
            <Download className="w-4 h-4" />
            Exportar CSV
          </button>
          <button
            onClick={() => setShowCreate((v) => !v)}
            className="flex items-center gap-2 px-4 py-2.5 rounded-lg bg-indigo-500 text-white text-sm font-semibold hover:bg-indigo-400 transition-all"
          >
            <Plus className="w-4 h-4" />
            Nueva empresa
          </button>
        </div>
      </div>

      <section className="rounded-2xl border border-slate-800 bg-slate-900/40 backdrop-blur-xl p-5">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-3">
          <select
            value={filters.status ?? ''}
            onChange={(e) =>
              setFilters({
                ...filters,
                status: e.target.value
                  ? (e.target.value as PipelineStatusValue)
                  : undefined,
              })
            }
            className="rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-sm text-slate-200 focus:outline-none focus:border-indigo-500"
          >
            <option value="">Todos los estados</option>
            {(Object.keys(STATUS_LABELS) as PipelineStatusValue[]).map((status) => (
              <option key={status} value={status}>
                {STATUS_LABELS[status]} ({counts[status] ?? 0})
              </option>
            ))}
          </select>
          <input
            value={filters.region ?? ''}
            onChange={(e) =>
              setFilters({ ...filters, region: e.target.value || undefined })
            }
            placeholder="Región (ej: Lima)"
            className="rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-sm text-slate-200 placeholder:text-slate-600 focus:outline-none focus:border-indigo-500"
          />
          <input
            type="number"
            min={0}
            max={100}
            value={filters.scoreMin ?? ''}
            onChange={(e) =>
              setFilters({
                ...filters,
                scoreMin: e.target.value ? Number(e.target.value) : undefined,
              })
            }
            placeholder="Score mínimo"
            className="rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-sm text-slate-200 placeholder:text-slate-600 focus:outline-none focus:border-indigo-500"
          />
          <input
            type="number"
            min={0}
            max={100}
            value={filters.scoreMax ?? ''}
            onChange={(e) =>
              setFilters({
                ...filters,
                scoreMax: e.target.value ? Number(e.target.value) : undefined,
              })
            }
            placeholder="Score máximo"
            className="rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-sm text-slate-200 placeholder:text-slate-600 focus:outline-none focus:border-indigo-500"
          />
        </div>
      </section>

      {showCreate && (
        <section className="rounded-2xl border border-slate-800 bg-slate-900/40 backdrop-blur-xl p-6 space-y-4">
          <h2 className="text-lg font-bold text-white">Nueva empresa en pipeline</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-1.5">
              <label className="block text-xs text-slate-400 font-medium">
                Empresa *
              </label>
              <input
                value={createForm.companyName}
                onChange={(e) =>
                  setCreateForm({ ...createForm, companyName: e.target.value })
                }
                className="w-full rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-sm text-slate-200 focus:outline-none focus:border-indigo-500"
              />
            </div>
            <div className="space-y-1.5">
              <label className="block text-xs text-slate-400 font-medium">Contacto</label>
              <input
                value={createForm.contactName}
                onChange={(e) =>
                  setCreateForm({ ...createForm, contactName: e.target.value })
                }
                className="w-full rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-sm text-slate-200 focus:outline-none focus:border-indigo-500"
              />
            </div>
            <div className="space-y-1.5">
              <label className="block text-xs text-slate-400 font-medium">Email</label>
              <input
                value={createForm.email}
                onChange={(e) => setCreateForm({ ...createForm, email: e.target.value })}
                className="w-full rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-sm text-slate-200 focus:outline-none focus:border-indigo-500"
              />
            </div>
            <div className="space-y-1.5">
              <label className="block text-xs text-slate-400 font-medium">Región</label>
              <input
                value={createForm.region}
                onChange={(e) => setCreateForm({ ...createForm, region: e.target.value })}
                className="w-full rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-sm text-slate-200 focus:outline-none focus:border-indigo-500"
              />
            </div>
            <div className="space-y-1.5">
              <label className="block text-xs text-slate-400 font-medium">
                Benchmark score (0-100)
              </label>
              <input
                type="number"
                min={0}
                max={100}
                value={createForm.benchmarkScore}
                onChange={(e) =>
                  setCreateForm({ ...createForm, benchmarkScore: e.target.value })
                }
                className="w-full rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-sm text-slate-200 focus:outline-none focus:border-indigo-500"
              />
            </div>
          </div>
          {createError && (
            <div className="flex items-start gap-2 rounded-lg border border-red-500/25 bg-red-500/10 p-3 text-sm text-red-400">
              <AlertTriangle className="w-4 h-4 mt-0.5 shrink-0" />
              {createError}
            </div>
          )}
          <button
            onClick={() => {
              setCreateError(null);
              createMutation.mutate();
            }}
            disabled={!createForm.companyName || createMutation.isPending}
            className="px-4 py-2.5 rounded-lg bg-indigo-500 text-white text-sm font-semibold hover:bg-indigo-400 disabled:opacity-40 disabled:cursor-not-allowed transition-all"
          >
            {createMutation.isPending ? 'Creando...' : 'Agregar al pipeline'}
          </button>
        </section>
      )}

      <section className="rounded-2xl border border-slate-800 bg-slate-900/40 backdrop-blur-xl overflow-hidden">
        {isLoading ? (
          <div className="p-6 space-y-3">
            {[...Array(4)].map((_, i) => (
              <div key={i} className="h-14 rounded-xl bg-slate-900 animate-pulse" />
            ))}
          </div>
        ) : entries && entries.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-xs text-slate-500 uppercase tracking-wider border-b border-slate-800 bg-slate-950/40">
                  <th className="py-2.5 px-4">Empresa</th>
                  <th className="py-2.5 px-4">Región</th>
                  <th className="py-2.5 px-4">Score</th>
                  <th className="py-2.5 px-4">Estado</th>
                  <th className="py-2.5 px-4">Notas</th>
                  <th className="py-2.5 px-4">Actualizado</th>
                </tr>
              </thead>
              <tbody>
                {entries.map((entry) => (
                  <tr
                    key={entry.id}
                    onClick={() => setSelectedId(entry.id)}
                    className={`border-b border-slate-800/60 text-slate-300 cursor-pointer hover:bg-slate-900/50 transition-colors ${
                      selectedId === entry.id ? 'bg-indigo-500/5' : ''
                    }`}
                  >
                    <td className="py-3 px-4">
                      <p className="font-medium text-slate-200">{entry.companyName}</p>
                      {entry.contactName && (
                        <p className="text-xs text-slate-500">{entry.contactName}</p>
                      )}
                    </td>
                    <td className="py-3 px-4">{entry.region ?? '—'}</td>
                    <td className="py-3 px-4 font-mono">
                      {entry.benchmarkScore != null ? entry.benchmarkScore.toFixed(1) : '—'}
                    </td>
                    <td className="py-3 px-4">
                      <span
                        className={`px-2 py-0.5 rounded border text-xs font-medium ${
                          STATUS_STYLES[entry.status]
                        }`}
                      >
                        {STATUS_LABELS[entry.status]}
                      </span>
                    </td>
                    <td className="py-3 px-4 font-mono text-xs">{entry.noteCount}</td>
                    <td className="py-3 px-4 text-xs text-slate-500">
                      {new Date(entry.updatedAt).toLocaleDateString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="p-10 text-center">
            <TrendingUp className="w-10 h-10 text-slate-600 mx-auto mb-2" />
            <p className="text-sm text-slate-500">
              No hay empresas en el pipeline con estos filtros.
            </p>
          </div>
        )}
      </section>

      {selected && (
        <section className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div className="rounded-2xl border border-slate-800 bg-slate-900/40 backdrop-blur-xl p-6 space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <h2 className="text-lg font-bold text-white">{selected.companyName}</h2>
                <p className="text-sm text-slate-400">
                  {selected.contactName ?? '—'} {selected.email ? `· ${selected.email}` : ''}
                </p>
              </div>
              <span
                className={`px-2.5 py-1 rounded-lg border text-xs font-medium ${
                  STATUS_STYLES[selected.status]
                }`}
              >
                {STATUS_LABELS[selected.status]}
              </span>
            </div>
            <div className="flex flex-wrap gap-2 text-xs text-slate-400">
              <span className="px-2 py-1 rounded bg-slate-950/60 border border-slate-800">
                Región: {selected.region ?? '—'}
              </span>
              <span className="px-2 py-1 rounded bg-slate-950/60 border border-slate-800">
                Score: {selected.benchmarkScore != null ? selected.benchmarkScore.toFixed(1) : '—'}
              </span>
            </div>

            <div>
              <p className="text-xs text-slate-400 font-medium uppercase tracking-wider mb-2">
                Cambiar estado
              </p>
              <div className="flex flex-wrap gap-2">
                {TRANSITIONS[selected.status].map((status) => (
                  <button
                    key={status}
                    onClick={() => transitionMutation.mutate(status)}
                    disabled={transitionMutation.isPending}
                    className="px-3 py-1.5 rounded-lg bg-indigo-500/10 border border-indigo-500/25 text-indigo-400 text-xs font-medium hover:bg-indigo-500/20 transition-all disabled:opacity-40"
                  >
                    → {STATUS_LABELS[status]}
                  </button>
                ))}
                {TRANSITIONS[selected.status].length === 0 && (
                  <p className="text-sm text-slate-500">Estado final alcanzado.</p>
                )}
              </div>
              {transitionMutation.isError && (
                <p className="text-xs text-red-400 mt-2">
                  No se pudo cambiar el estado (transición inválida).
                </p>
              )}
            </div>

            <div>
              <div className="flex items-center gap-2 mb-2">
                <MessageSquare className="w-4 h-4 text-slate-500" />
                <p className="text-sm font-semibold text-slate-200">Notas de seguimiento</p>
              </div>
              <div className="flex gap-2 mb-3">
                <input
                  value={newNote}
                  onChange={(e) => setNewNote(e.target.value)}
                  placeholder="Agregar nota de seguimiento..."
                  className="flex-1 rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-sm text-slate-200 placeholder:text-slate-600 focus:outline-none focus:border-indigo-500"
                />
                <button
                  onClick={() => noteMutation.mutate(newNote)}
                  disabled={!newNote.trim() || noteMutation.isPending}
                  className="px-3 py-2 rounded-lg bg-slate-900 border border-slate-800 text-slate-300 text-xs font-medium hover:bg-slate-800 transition-all disabled:opacity-40"
                >
                  Agregar
                </button>
              </div>
              {selected.notes.length > 0 ? (
                <ul className="space-y-2 max-h-64 overflow-y-auto">
                  {[...selected.notes].reverse().map((note) => (
                    <li
                      key={note.id}
                      className="rounded-lg border border-slate-800 bg-slate-950/40 p-3"
                    >
                      <p className="text-sm text-slate-300">{note.note}</p>
                      <p className="text-xs text-slate-500 mt-1">
                        {new Date(note.createdAt).toLocaleString()}
                      </p>
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="text-sm text-slate-500">Sin notas todavía.</p>
              )}
            </div>
          </div>

          <div className="rounded-2xl border border-slate-800 bg-slate-900/40 backdrop-blur-xl p-6">
            <div className="flex items-center gap-2 mb-4">
              <History className="w-4 h-4 text-slate-500" />
              <h2 className="text-lg font-bold text-white">Historial de cambios</h2>
            </div>
            {selected.history.length > 0 ? (
              <ol className="relative border-l border-slate-800 ml-2 space-y-4">
                {[...selected.history].reverse().map((change) => (
                  <li key={change.id} className="ml-4">
                    <div className="absolute -left-[5px] mt-1.5 w-2.5 h-2.5 rounded-full bg-indigo-500" />
                    <p className="text-sm text-slate-300">
                      <span className="text-slate-500">{STATUS_LABELS[change.fromStatus as PipelineStatusValue]}</span>
                      <span className="mx-2 text-slate-600">→</span>
                      <span className="text-slate-100">
                        {STATUS_LABELS[change.toStatus as PipelineStatusValue]}
                      </span>
                    </p>
                    <p className="text-xs text-slate-500">
                      {new Date(change.changedAt).toLocaleString()}
                    </p>
                  </li>
                ))}
              </ol>
            ) : (
              <p className="text-sm text-slate-500">Sin cambios de estado registrados.</p>
            )}
          </div>
        </section>
      )}
    </div>
  );
}
