'use client';

import React from 'react';
import Link from 'next/link';
import { useParams } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import {
  ArrowLeft,
  Mail,
  MousePointerClick,
  ClipboardList,
  CheckCircle2,
  XCircle,
  Eye,
} from 'lucide-react';
import { useAdminAuthStore } from '@/store/adminAuthStore';
import { apiClient } from '@/lib/api';

const STATUS_STYLES: Record<string, string> = {
  UPLOADED: 'bg-slate-500/10 border-slate-500/25 text-slate-400',
  SENT: 'bg-sky-500/10 border-sky-500/25 text-sky-400',
  VISITED: 'bg-violet-500/10 border-violet-500/25 text-violet-400',
  STARTED: 'bg-amber-500/10 border-amber-500/25 text-amber-400',
  COMPLETED: 'bg-emerald-500/10 border-emerald-500/25 text-emerald-400',
  FAILED: 'bg-red-500/10 border-red-500/25 text-red-400',
};

export default function CampaignTrackingPage() {
  const params = useParams<{ id: string }>();
  const accessToken = useAdminAuthStore((state) => state.accessToken);

  const {
    data: tracking,
    isLoading,
    isError,
    refetch,
  } = useQuery({
    queryKey: ['campaign-tracking', params.id],
    queryFn: () => apiClient.getCampaignTracking(accessToken ?? '', params.id),
    enabled: Boolean(accessToken && params.id),
  });

  if (isLoading) {
    return (
      <div className="space-y-4">
        <div className="h-8 w-48 rounded bg-slate-900 animate-pulse" />
        <div className="h-32 rounded-2xl bg-slate-900/40 animate-pulse" />
        <div className="h-64 rounded-2xl bg-slate-900/40 animate-pulse" />
      </div>
    );
  }

  if (isError || !tracking) {
    return (
      <div className="p-10 text-center rounded-2xl border border-red-500/20 bg-red-500/5 space-y-3">
        <p className="text-red-400 font-semibold">No se pudo cargar la campaña</p>
        <button
          onClick={() => refetch()}
          className="px-4 py-2 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 text-xs font-semibold hover:bg-red-500/20 transition-all"
        >
          Reintentar
        </button>
      </div>
    );
  }

  const sent = tracking.invitations.filter((i) => i.sentAt).length;
  const visited = tracking.invitations.filter((i) => i.visitedAt).length;
  const started = tracking.invitations.filter((i) => i.startedAt).length;
  const completed = tracking.invitations.filter((i) => i.completedAt).length;
  const failed = tracking.invitations.filter((i) => i.status === 'FAILED').length;

  const metrics = [
    { label: 'Enviadas', value: sent, icon: Mail },
    { label: 'Enlace visitado', value: visited, icon: Eye },
    { label: 'Evaluación iniciada', value: started, icon: ClipboardList },
    { label: 'Completadas', value: completed, icon: CheckCircle2 },
    { label: 'Fallidas', value: failed, icon: XCircle },
  ];

  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <Link
            href="/admin/outreach/campaigns"
            className="inline-flex items-center gap-1.5 text-xs text-slate-500 hover:text-slate-300 transition-colors"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            Volver a campañas
          </Link>
          <h1 className="text-2xl font-extrabold tracking-tight text-white mt-2">
            {tracking.name}
          </h1>
          <p className="text-sm text-slate-400 mt-1">{tracking.subject}</p>
        </div>
        <span
          className={`px-2.5 py-1 rounded-lg border text-xs font-medium ${
            STATUS_STYLES[tracking.status] ?? 'bg-slate-500/10 border-slate-500/25 text-slate-400'
          }`}
        >
          {tracking.status}
        </span>
      </div>

      {tracking.description && (
        <p className="text-sm text-slate-400">{tracking.description}</p>
      )}

      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-4">
        {metrics.map((metric) => {
          const Icon = metric.icon;
          return (
            <div
              key={metric.label}
              className="rounded-2xl border border-slate-800 bg-slate-900/40 backdrop-blur-xl p-4"
            >
              <div className="flex items-center gap-2 mb-2 text-slate-400">
                <Icon className="w-4 h-4" />
                <p className="text-xs font-medium">{metric.label}</p>
              </div>
              <p className="text-2xl font-extrabold text-white">
                {metric.value}
                <span className="text-sm text-slate-500 font-normal">
                  {' '}
                  / {tracking.recipientCount}
                </span>
              </p>
            </div>
          );
        })}
      </div>

      <section className="rounded-2xl border border-slate-800 bg-slate-900/40 backdrop-blur-xl p-6">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-bold text-white">Tracking de invitaciones</h2>
          <span className="text-xs text-slate-500">
            {tracking.invitations.length} destinatarios
          </span>
        </div>
        {tracking.invitations.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-xs text-slate-500 uppercase tracking-wider border-b border-slate-800">
                  <th className="py-2 pr-4">Contacto</th>
                  <th className="py-2 pr-4">Estado</th>
                  <th className="py-2 pr-4">Enviado</th>
                  <th className="py-2 pr-4">Visitado</th>
                  <th className="py-2 pr-4">Completado</th>
                  <th className="py-2">Detalle</th>
                </tr>
              </thead>
              <tbody>
                {tracking.invitations.map((invitation) => (
                  <tr
                    key={invitation.invitationId}
                    className="border-b border-slate-800/60 text-slate-300"
                  >
                    <td className="py-2.5 pr-4">
                      <p className="font-medium text-slate-200">
                        {invitation.firstName} {invitation.lastName}
                      </p>
                      <p className="text-xs text-slate-500">{invitation.email}</p>
                    </td>
                    <td className="py-2.5 pr-4">
                      <span
                        className={`px-2 py-0.5 rounded border text-xs font-medium ${
                          STATUS_STYLES[invitation.status] ?? STATUS_STYLES.UPLOADED
                        }`}
                      >
                        {invitation.status}
                      </span>
                    </td>
                    <td className="py-2.5 pr-4 text-xs text-slate-500">
                      {invitation.sentAt
                        ? new Date(invitation.sentAt).toLocaleDateString()
                        : '—'}
                    </td>
                    <td className="py-2.5 pr-4 text-xs text-slate-500">
                      {invitation.visitedAt
                        ? new Date(invitation.visitedAt).toLocaleDateString()
                        : '—'}
                    </td>
                    <td className="py-2.5 pr-4 text-xs text-slate-500">
                      {invitation.completedAt
                        ? new Date(invitation.completedAt).toLocaleDateString()
                        : '—'}
                    </td>
                    <td className="py-2.5 text-xs text-slate-500">
                      {invitation.failureReason ?? '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="text-center py-10 space-y-3">
            <MousePointerClick className="w-10 h-10 text-slate-600 mx-auto" />
            <p className="text-sm text-slate-500">
              Esta campaña aún no tiene invitaciones registradas.
            </p>
          </div>
        )}
      </section>
    </div>
  );
}
