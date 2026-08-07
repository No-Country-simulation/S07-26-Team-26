'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  Plus,
  Send,
  Eye,
  AlertTriangle,
  Megaphone,
} from 'lucide-react';
import { useAdminAuthStore } from '@/store/adminAuthStore';
import { apiClient } from '@/lib/api';

const STATUS_STYLES: Record<string, string> = {
  READY: 'bg-indigo-500/10 border-indigo-500/25 text-indigo-400',
  SENDING: 'bg-amber-500/10 border-amber-500/25 text-amber-400',
  ACTIVE: 'bg-sky-500/10 border-sky-500/25 text-sky-400',
  COMPLETED: 'bg-emerald-500/10 border-emerald-500/25 text-emerald-400',
  FAILED: 'bg-red-500/10 border-red-500/25 text-red-400',
  DRAFT: 'bg-slate-500/10 border-slate-500/25 text-slate-400',
};

export default function OutreachCampaignsPage() {
  const accessToken = useAdminAuthStore((state) => state.accessToken);
  const queryClient = useQueryClient();
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({
    name: '',
    description: '',
    subject: '',
    message: '',
    callToActionText: 'Comenzar evaluación',
    contactImportId: '',
    scheduledAt: '',
    timezone: 'America/Lima',
  });
  const [error, setError] = useState<string | null>(null);

  const { data: campaigns, isLoading } = useQuery({
    queryKey: ['campaigns'],
    queryFn: () => apiClient.listCampaigns(accessToken ?? ''),
    enabled: Boolean(accessToken),
  });

  const { data: imports } = useQuery({
    queryKey: ['contact-imports'],
    queryFn: () => apiClient.listContactImports(accessToken ?? ''),
    enabled: Boolean(accessToken),
  });

  const createMutation = useMutation({
    mutationFn: () =>
      apiClient.createCampaign(accessToken ?? '', {
        name: form.name,
        description: form.description || undefined,
        subject: form.subject,
        message: form.message,
        callToActionText: form.callToActionText,
        contactImportId: form.contactImportId,
        scheduledAt: form.scheduledAt ? new Date(form.scheduledAt).toISOString() : null,
        timezone: form.timezone,
      }),
    onSuccess: () => {
      setShowForm(false);
      setForm({
        name: '',
        description: '',
        subject: '',
        message: '',
        callToActionText: 'Comenzar evaluación',
        contactImportId: '',
        scheduledAt: '',
        timezone: 'America/Lima',
      });
      queryClient.invalidateQueries({ queryKey: ['campaigns'] });
    },
    onError: () => {
      setError('No se pudo crear la campaña. Verifica los datos e inténtalo nuevamente.');
    },
  });

  const sendMutation = useMutation({
    mutationFn: (campaignId: string) =>
      apiClient.sendCampaign(accessToken ?? '', campaignId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['campaigns'] });
    },
  });

  function handleSubmit() {
    setError(null);
    createMutation.mutate();
  }

  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-extrabold tracking-tight text-slate-200">Campañas</h1>
          <p className="text-sm text-slate-400 mt-1">
            Crea y envía campañas de invitación a tus contactos.
          </p>
        </div>
        <button
          onClick={() => setShowForm((v) => !v)}
          className="flex items-center gap-2 px-4 py-2.5 rounded-lg bg-indigo-500 text-white text-sm font-semibold hover:bg-indigo-400 transition-all"
        >
          <Plus className="w-4 h-4" />
          Nueva campaña
        </button>
      </div>

      {showForm && (
        <section className="rounded-2xl border border-slate-800 bg-slate-900/40 backdrop-blur-xl p-6 space-y-4">
          <h2 className="text-lg font-bold text-slate-200">Nueva campaña</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-1.5">
              <label className="block text-xs text-slate-400 font-medium">Nombre</label>
              <input
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
                placeholder="Ej: Benchmark julio 2026"
                className="w-full rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-sm text-slate-200 placeholder:text-slate-600 focus:outline-none focus:border-indigo-500"
              />
            </div>
            <div className="space-y-1.5">
              <label className="block text-xs text-slate-400 font-medium">
                Lista de contactos
              </label>
              <select
                value={form.contactImportId}
                onChange={(e) => setForm({ ...form, contactImportId: e.target.value })}
                className="w-full rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-sm text-slate-200 focus:outline-none focus:border-indigo-500"
              >
                <option value="">Selecciona una importación</option>
                {imports?.map((item) => (
                  <option key={item.importId} value={item.importId}>
                    {item.name} ({item.validContacts})
                  </option>
                ))}
              </select>
            </div>
            <div className="space-y-1.5">
              <label className="block text-xs text-slate-400 font-medium">Asunto</label>
              <input
                value={form.subject}
                onChange={(e) => setForm({ ...form, subject: e.target.value })}
                placeholder="Asunto del correo"
                className="w-full rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-sm text-slate-200 placeholder:text-slate-600 focus:outline-none focus:border-indigo-500"
              />
            </div>
            <div className="space-y-1.5">
              <label className="block text-xs text-slate-400 font-medium">
                Texto del botón
              </label>
              <input
                value={form.callToActionText}
                onChange={(e) => setForm({ ...form, callToActionText: e.target.value })}
                className="w-full rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-sm text-slate-200 focus:outline-none focus:border-indigo-500"
              />
            </div>
            <div className="space-y-1.5">
              <label className="block text-xs text-slate-400 font-medium">
                Fecha de envío (opcional)
              </label>
              <input
                type="datetime-local"
                value={form.scheduledAt}
                onChange={(e) => setForm({ ...form, scheduledAt: e.target.value })}
                className="w-full rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-sm text-slate-200 focus:outline-none focus:border-indigo-500"
              />
            </div>
            <div className="space-y-1.5">
              <label className="block text-xs text-slate-400 font-medium">
                Zona horaria
              </label>
              <input
                value={form.timezone}
                onChange={(e) => setForm({ ...form, timezone: e.target.value })}
                className="w-full rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-sm text-slate-200 focus:outline-none focus:border-indigo-500"
              />
            </div>
          </div>
          <div className="space-y-1.5">
            <label className="block text-xs text-slate-400 font-medium">
              Descripción (opcional)
            </label>
            <textarea
              value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              rows={2}
              className="w-full rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-sm text-slate-200 focus:outline-none focus:border-indigo-500"
            />
          </div>
          <div className="space-y-1.5">
            <label className="block text-xs text-slate-400 font-medium">Mensaje</label>
            <textarea
              value={form.message}
              onChange={(e) => setForm({ ...form, message: e.target.value })}
              rows={4}
              className="w-full rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-sm text-slate-200 focus:outline-none focus:border-indigo-500"
            />
          </div>
          {error && (
            <div className="flex items-start gap-2 rounded-lg border border-red-500/25 bg-red-500/10 p-3 text-sm text-red-400">
              <AlertTriangle className="w-4 h-4 mt-0.5 shrink-0" />
              {error}
            </div>
          )}
          <button
            onClick={handleSubmit}
            disabled={createMutation.isPending}
            className="px-4 py-2.5 rounded-lg bg-indigo-500 text-white text-sm font-semibold hover:bg-indigo-400 disabled:opacity-40 disabled:cursor-not-allowed transition-all"
          >
            {createMutation.isPending ? 'Creando...' : 'Crear campaña'}
          </button>
        </section>
      )}

      <section className="rounded-2xl border border-slate-800 bg-slate-900/40 backdrop-blur-xl p-6">
        <h2 className="text-lg font-bold text-slate-200 mb-4">Todas las campañas</h2>
        {isLoading ? (
          <div className="grid grid-cols-1 gap-4">
            {[...Array(3)].map((_, i) => (
              <div key={i} className="h-20 rounded-xl bg-slate-900 animate-pulse" />
            ))}
          </div>
        ) : campaigns && campaigns.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {campaigns.map((campaign) => (
              <div
                key={campaign.id}
                className="rounded-xl border border-slate-800 bg-slate-950/40 p-5 flex flex-col gap-3"
              >
                <div className="flex items-start justify-between gap-2">
                  <div className="min-w-0">
                    <p className="font-semibold text-slate-200 truncate">{campaign.name}</p>
                    <p className="text-xs text-slate-500 truncate">{campaign.subject}</p>
                  </div>
                  <span
                    className={`shrink-0 px-2 py-0.5 rounded border text-xs font-medium ${
                      STATUS_STYLES[campaign.status] ?? STATUS_STYLES.DRAFT
                    }`}
                  >
                    {campaign.status}
                  </span>
                </div>
                <div className="flex items-center justify-between text-xs text-slate-500">
                  <span className="flex items-center gap-1">
                    <Megaphone className="w-3.5 h-3.5" />
                    {campaign.recipientCount} destinatarios
                  </span>
                  <span>
                    {campaign.scheduledAt
                      ? new Date(campaign.scheduledAt).toLocaleDateString()
                      : 'Envío inmediato'}
                  </span>
                </div>
                <div className="flex gap-2 pt-1">
                  <Link
                    href={`/admin/outreach/campaigns/${campaign.id}`}
                    className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-slate-900 border border-slate-800 text-slate-300 text-xs font-medium hover:bg-slate-800 transition-all"
                  >
                    <Eye className="w-3.5 h-3.5" />
                    Ver tracking
                  </Link>
                  {campaign.status === 'READY' && (
                    <button
                      onClick={() => sendMutation.mutate(campaign.id)}
                      disabled={sendMutation.isPending}
                      className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-indigo-500/10 border border-indigo-500/25 text-indigo-400 text-xs font-medium hover:bg-indigo-500/20 transition-all disabled:opacity-40"
                    >
                      <Send className="w-3.5 h-3.5" />
                      Enviar
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center py-10 space-y-3">
            <Megaphone className="w-10 h-10 text-slate-600 mx-auto" />
            <p className="text-sm text-slate-500">Aún no hay campañas creadas.</p>
          </div>
        )}
      </section>
    </div>
  );
}