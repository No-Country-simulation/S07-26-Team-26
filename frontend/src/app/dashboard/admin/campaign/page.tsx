'use client';

import { FormEvent, useEffect, useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import Link from 'next/link';
import { useAuthStore } from '@/store/authStore';

const API_URL = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080';

export default function AdminCampaignPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { isAuthenticated, accessToken } = useAuthStore();
  const [hasMounted, setHasMounted] = useState(false);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [subject, setSubject] = useState('');
  const [message, setMessage] = useState('');
  const [callToActionText, setCallToActionText] = useState('Abrir invitación');
  const [contactImportId, setContactImportId] = useState('');
  const [scheduledAt, setScheduledAt] = useState(() => {
    const now = new Date();
    const local = new Date(now.getTime() - now.getTimezoneOffset() * 60000);
    return local.toISOString().slice(0, 16);
  });
  const [timezone, setTimezone] = useState('America/Argentina/Buenos_Aires');
  const [status, setStatus] = useState<'idle' | 'submitting' | 'success' | 'error'>('idle');
  const [responseMessage, setResponseMessage] = useState('');

  useEffect(() => {
    setHasMounted(true);
  }, []);

  useEffect(() => {
    if (!isAuthenticated) {
      router.replace('/login');
      return;
    }

    const paramId = searchParams?.get('contactImportId');
    if (paramId) {
      setContactImportId(paramId);
    }
  }, [isAuthenticated, router, searchParams]);

  if (!hasMounted || !isAuthenticated) {
    return null;
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setStatus('submitting');
    setResponseMessage('');

    const trimmedName = name.trim();
    const trimmedSubject = subject.trim();
    const trimmedMessage = message.trim();
    const trimmedCallToActionText = callToActionText.trim();
    const normalizedImportId = contactImportId.trim();
    const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;

    if (trimmedName.length < 3 || trimmedName.length > 160) {
      setStatus('error');
      setResponseMessage('El nombre debe tener entre 3 y 160 caracteres.');
      return;
    }
    if (trimmedSubject.length < 3 || trimmedSubject.length > 180) {
      setStatus('error');
      setResponseMessage('El asunto debe tener entre 3 y 180 caracteres.');
      return;
    }
    if (trimmedMessage.length < 10 || trimmedMessage.length > 5000) {
      setStatus('error');
      setResponseMessage('El mensaje debe tener entre 10 y 5000 caracteres.');
      return;
    }
    if (trimmedCallToActionText.length < 2 || trimmedCallToActionText.length > 80) {
      setStatus('error');
      setResponseMessage('El texto del botón debe tener entre 2 y 80 caracteres.');
      return;
    }
    if (!uuidRegex.test(normalizedImportId)) {
      setStatus('error');
      setResponseMessage('El ID de importación debe ser un UUID válido.');
      return;
    }

    try {
      if (!accessToken) {
        throw new Error('No se encontró el token de sesión. Inicia sesión nuevamente.');
      }

      const scheduledAtIso = scheduledAt
        ? new Date(scheduledAt).toISOString()
        : null;

      const payloadBody = {
        name: trimmedName,
        description: description.trim() || null,
        subject: trimmedSubject,
        message: trimmedMessage,
        callToActionText: trimmedCallToActionText,
        contactImportId: normalizedImportId,
        scheduledAt: scheduledAtIso,
        timezone: timezone.trim() || null,
      };

      console.debug('Crear campaña payload', payloadBody);

      const result = await fetch(`${API_URL}/api/v1/admin/campaigns`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${accessToken}`,
        },
        body: JSON.stringify(payloadBody),
      });

      if (!result.ok) {
        const text = await result.text();
        let errorDetail: string | null = null;
        let fieldErrors: string[] = [];
        try {
          const payload = JSON.parse(text);
          errorDetail = payload?.message || payload?.error || null;
          if (Array.isArray(payload?.fields)) {
            fieldErrors = payload.fields.map((field: any) => `${field.field}: ${field.message}`);
          }
        } catch {
          errorDetail = text;
        }
        const fullError = [errorDetail, ...fieldErrors].filter(Boolean).join(' | ');
        throw new Error(fullError || result.statusText || 'Error al crear la campaña');
      }

      const payload = await result.json();
      setStatus('success');
      setResponseMessage(`Campaña creada correctamente. ID: ${payload.id ?? payload.campaignId ?? ''}`);
    } catch (error) {
      setStatus('error');
      setResponseMessage(error instanceof Error ? error.message : 'Error desconocido');
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 px-4 py-12 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-4xl rounded-3xl border border-slate-800/80 bg-slate-900/95 p-8 shadow-2xl shadow-black/20">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <p className="text-sm uppercase tracking-[0.3em] text-indigo-400">Outreach campaign</p>
            <h1 className="mt-3 text-4xl font-semibold text-white">Crear campaña de outreach</h1>
            <p className="mt-2 text-slate-400">Completa los datos de la campaña y el ID de importación para generar invitaciones.</p>
          </div>
          <Link
            href="/dashboard/admin"
            className="inline-flex items-center justify-center rounded-2xl border border-slate-700 bg-slate-950/80 px-4 py-3 text-sm font-semibold text-slate-200 transition hover:border-indigo-500 hover:text-white"
          >
            Volver al dashboard
          </Link>
        </div>

        <form className="mt-10 space-y-6" onSubmit={handleSubmit}>
          <div className="grid gap-6 sm:grid-cols-2">
            <label className="block">
              <span className="text-sm font-semibold text-slate-300">Nombre de campaña</span>
              <input
                value={name}
                onChange={(event) => setName(event.target.value)}
                required
                placeholder="Campaña de invitaciones"
                className="mt-2 w-full rounded-3xl border border-slate-800 bg-slate-950 px-4 py-3 text-slate-100 placeholder:text-slate-500 focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
              />
            </label>
            <label className="block">
              <span className="text-sm font-semibold text-slate-300">Asunto del email</span>
              <input
                value={subject}
                onChange={(event) => setSubject(event.target.value)}
                required
                placeholder="Completa tu evaluación Ghost Load"
                className="mt-2 w-full rounded-3xl border border-slate-800 bg-slate-950 px-4 py-3 text-slate-100 placeholder:text-slate-500 focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
              />
            </label>
          </div>

          <div className="grid gap-6 sm:grid-cols-2">
            <label className="block">
              <span className="text-sm font-semibold text-slate-300">Texto del botón</span>
              <input
                value={callToActionText}
                onChange={(event) => setCallToActionText(event.target.value)}
                required
                placeholder="Abrir invitación"
                className="mt-2 w-full rounded-3xl border border-slate-800 bg-slate-950 px-4 py-3 text-slate-100 placeholder:text-slate-500 focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
              />
            </label>
            <label className="block">
              <span className="text-sm font-semibold text-slate-300">ID de importación</span>
              <input
                value={contactImportId}
                onChange={(event) => setContactImportId(event.target.value)}
                required
                placeholder="UUID de importación de contactos"
                className="mt-2 w-full rounded-3xl border border-slate-800 bg-slate-950 px-4 py-3 text-slate-100 placeholder:text-slate-500 focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
              />
            </label>
          </div>

          <label className="block">
            <span className="text-sm font-semibold text-slate-300">Mensaje del email</span>
            <textarea
              value={message}
              onChange={(event) => setMessage(event.target.value)}
              required
              rows={6}
              placeholder="Hola, completá tu evaluación para recibir el reporte personalizado."
              className="mt-2 w-full rounded-3xl border border-slate-800 bg-slate-950 px-4 py-3 text-slate-100 placeholder:text-slate-500 focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
            />
          </label>

          <label className="block">
            <span className="text-sm font-semibold text-slate-300">Descripción</span>
            <textarea
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              rows={3}
              placeholder="Descripción breve de la campaña"
              className="mt-2 w-full rounded-3xl border border-slate-800 bg-slate-950 px-4 py-3 text-slate-100 placeholder:text-slate-500 focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
            />
          </label>

          <div className="grid gap-6 sm:grid-cols-2">
            <label className="block">
              <span className="text-sm font-semibold text-slate-300">Fecha de envío</span>
              <input
                type="datetime-local"
                value={scheduledAt}
                onChange={(event) => setScheduledAt(event.target.value)}
                className="mt-2 w-full rounded-3xl border border-slate-800 bg-slate-950 px-4 py-3 text-slate-100 placeholder:text-slate-500 focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
              />
            </label>
            <label className="block">
              <span className="text-sm font-semibold text-slate-300">Zona horaria</span>
              <input
                value={timezone}
                onChange={(event) => setTimezone(event.target.value)}
                className="mt-2 w-full rounded-3xl border border-slate-800 bg-slate-950 px-4 py-3 text-slate-100 placeholder:text-slate-500 focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
              />
            </label>
          </div>

          <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <button
              type="submit"
              disabled={status === 'submitting'}
              className="inline-flex justify-center rounded-3xl bg-indigo-500 px-6 py-3 text-sm font-semibold text-white transition hover:bg-indigo-400 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {status === 'submitting' ? 'Creando campaña...' : 'Crear campaña'}
            </button>
            <p className="text-sm text-slate-400">Necesitas un `contactImportId` válido para enviar invitaciones.</p>
          </div>

          {status === 'success' && (
            <div className="rounded-3xl border border-emerald-500/20 bg-emerald-500/10 p-4 text-emerald-200">
              {responseMessage}
            </div>
          )}
          {status === 'error' && (
            <div className="rounded-3xl border border-rose-500/20 bg-rose-500/10 p-4 text-rose-200">
              {responseMessage}
            </div>
          )}
        </form>
      </div>
    </div>
  );
}
