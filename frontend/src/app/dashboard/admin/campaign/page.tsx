"use client";

import { FormEvent, useEffect, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { useAuthStore } from "@/store/authStore";
import {
  fetchAdminContactImports,
  fetchAdminCampaigns,
  ContactImport,
  AdminCampaign,
  AdminCampaignStatus,
} from "@/services/api";

const API_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

const STATUS_FILTERS = [
  { label: "Todas", value: "ALL" },
  { label: "Borrador (DRAFT)", value: "DRAFT" },
  { label: "Lista (READY)", value: "READY" },
  { label: "Enviando (SENDING)", value: "SENDING" },
  { label: "Activa (ACTIVE)", value: "ACTIVE" },
  { label: "Completada (COMPLETED)", value: "COMPLETED" },
  { label: "Fallida (FAILED)", value: "FAILED" },
];

export default function AdminCampaignPage() {
  const router = useRouter();
  const { isAuthenticated, accessToken } = useAuthStore();

  // Navigation / View mode
  const [viewMode, setViewMode] = useState<"create" | "list">("create");

  // Create Campaign Form State
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [subject, setSubject] = useState("");
  const [message, setMessage] = useState("");
  const [callToActionText, setCallToActionText] = useState("Abrir invitación");
  const [contactImportId, setContactImportId] = useState("");
  const [scheduledAt, setScheduledAt] = useState(() => {
    const now = new Date();
    const local = new Date(now.getTime() - now.getTimezoneOffset() * 60000);
    return local.toISOString().slice(0, 16);
  });
  const [timezone, setTimezone] = useState("America/Argentina/Buenos_Aires");
  const [status, setStatus] = useState<
    "idle" | "submitting" | "success" | "error"
  >("idle");
  const [responseMessage, setResponseMessage] = useState("");

  // Contact Imports state
  const [contactImports, setContactImports] = useState<ContactImport[]>([]);
  const [loadingImports, setLoadingImports] = useState(false);
  const [importsError, setImportsError] = useState<string | null>(null);

  // Campaign List state
  const [campaignsList, setCampaignsList] = useState<AdminCampaign[]>([]);
  const [loadingCampaigns, setLoadingCampaigns] = useState(false);
  const [campaignsError, setCampaignsError] = useState<string | null>(null);
  const [selectedStatusFilter, setSelectedStatusFilter] = useState("ALL");

  // Modal State
  const [showModal, setShowModal] = useState(false);
  const [modalTitle, setModalTitle] = useState("");
  const [modalMessage, setModalMessage] = useState("");
  const [modalIsError, setModalIsError] = useState(false);

  useEffect(() => {
    if (!isAuthenticated) {
      router.replace("/login");
    }
  }, [isAuthenticated, router]);

  // Load Contact Imports
  const loadContactImports = useCallback(async () => {
    if (!accessToken) return;
    setLoadingImports(true);
    setImportsError(null);
    try {
      const data = await fetchAdminContactImports(accessToken);
      setContactImports(data);

      // Check query param from URL
      const paramId =
        typeof window !== "undefined"
          ? new URLSearchParams(window.location.search).get("contactImportId")
          : null;

      if (paramId && data.some((i) => i.importId === paramId)) {
        setContactImportId(paramId);
      } else if (data.length > 0 && !contactImportId) {
        setContactImportId(data[0].importId);
      }
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Error al cargar importaciones.";
      setImportsError(msg);
    } finally {
      setLoadingImports(false);
    }
  }, [accessToken, contactImportId]);

  useEffect(() => {
    if (isAuthenticated && accessToken) {
      loadContactImports();
    }
  }, [isAuthenticated, accessToken, loadContactImports]);

  // Load Campaigns List
  const loadCampaigns = useCallback(async () => {
    if (!accessToken) return;
    setLoadingCampaigns(true);
    setCampaignsError(null);
    try {
      const data = await fetchAdminCampaigns(accessToken, selectedStatusFilter);
      setCampaignsList(data);
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Error al cargar campañas.";
      setCampaignsError(msg);
    } finally {
      setLoadingCampaigns(false);
    }
  }, [accessToken, selectedStatusFilter]);

  useEffect(() => {
    if (isAuthenticated && accessToken && viewMode === "list") {
      loadCampaigns();
    }
  }, [isAuthenticated, accessToken, viewMode, selectedStatusFilter, loadCampaigns]);

  if (!isAuthenticated) {
    return null;
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setStatus("submitting");
    setResponseMessage("");

    const trimmedName = name.trim();
    const trimmedSubject = subject.trim();
    const trimmedMessage = message.trim();
    const trimmedCallToActionText = callToActionText.trim();
    const normalizedImportId = contactImportId.trim();
    const uuidRegex =
      /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;

    if (trimmedName.length < 3 || trimmedName.length > 160) {
      setStatus("error");
      setResponseMessage("El nombre debe tener entre 3 y 160 caracteres.");
      return;
    }
    if (trimmedSubject.length < 3 || trimmedSubject.length > 180) {
      setStatus("error");
      setResponseMessage("El asunto debe tener entre 3 y 180 caracteres.");
      return;
    }
    if (trimmedMessage.length < 10 || trimmedMessage.length > 5000) {
      setStatus("error");
      setResponseMessage("El mensaje debe tener entre 10 y 5000 caracteres.");
      return;
    }
    if (
      trimmedCallToActionText.length < 2 ||
      trimmedCallToActionText.length > 80
    ) {
      setStatus("error");
      setResponseMessage(
        "El texto del botón debe tener entre 2 y 80 caracteres.",
      );
      return;
    }
    if (!uuidRegex.test(normalizedImportId)) {
      setStatus("error");
      setResponseMessage("Por favor selecciona una importación válida.");
      return;
    }

    try {
      if (!accessToken) {
        throw new Error(
          "No se encontró el token de sesión. Inicia sesión nuevamente.",
        );
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

      console.debug("Crear campaña payload", payloadBody);

      const createResult = await fetch(`${API_URL}/api/v1/admin/campaigns`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${accessToken}`,
        },
        body: JSON.stringify(payloadBody),
      });

      if (!createResult.ok) {
        const text = await createResult.text();
        let errorDetail: string | null = null;
        let fieldErrors: string[] = [];
        try {
          const payload = JSON.parse(text);
          errorDetail = payload?.message || payload?.error || null;
          if (Array.isArray(payload?.fields)) {
            const fields = payload.fields as Array<{
              field?: string;
              message?: string;
            }>;
            fieldErrors = fields.map(
              (field) =>
                `${field.field ?? "field"}: ${field.message ?? "error"}`,
            );
          }
        } catch {
          errorDetail = text;
        }
        const fullError = [errorDetail, ...fieldErrors]
          .filter(Boolean)
          .join(" | ");
        throw new Error(
          fullError || createResult.statusText || "Error al crear la campaña",
        );
      }

      const payload = await createResult.json();
      const campaignId = payload.id ?? payload.campaignId;
      if (!campaignId) {
        throw new Error("No se recibió el ID de campaña luego de crearla.");
      }

      const sendResult = await fetch(
        `${API_URL}/api/v1/admin/campaigns/${campaignId}/send`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        },
      );

      if (!sendResult.ok) {
        const text = await sendResult.text();
        let errorDetail: string | null = null;
        try {
          const payload = JSON.parse(text);
          errorDetail = payload?.message || payload?.error || null;
        } catch {
          errorDetail = text;
        }
        throw new Error(
          errorDetail || sendResult.statusText || "Error al enviar la campaña",
        );
      }

      setStatus("success");
      setResponseMessage(
        `Campaña creada y enviada correctamente. ID: ${campaignId}`,
      );
      setModalTitle("Campaña enviada");
      setModalMessage(
        `La campaña se creó y se envió correctamente. ID: ${campaignId}`,
      );
      setModalIsError(false);
      setShowModal(true);
      // Reset main inputs
      setName("");
      setSubject("");
      setMessage("");
      setDescription("");
    } catch (error) {
      setStatus("error");
      const message =
        error instanceof Error ? error.message : "Error desconocido";
      setResponseMessage(message);
      setModalTitle("Error");
      setModalMessage(`No se pudo enviar la campaña: ${message}`);
      setModalIsError(true);
      setShowModal(true);
    }
  };

  const getStatusBadgeStyle = (status: AdminCampaignStatus) => {
    switch (status) {
      case "DRAFT":
        return "bg-slate-100 text-slate-700 border-slate-200";
      case "READY":
        return "bg-indigo-50 text-indigo-700 border-indigo-200";
      case "SENDING":
        return "bg-amber-50 text-amber-700 border-amber-200 animate-pulse";
      case "ACTIVE":
        return "bg-emerald-50 text-emerald-700 border-emerald-200";
      case "COMPLETED":
        return "bg-teal-50 text-teal-700 border-teal-200";
      case "FAILED":
        return "bg-rose-50 text-rose-700 border-rose-200";
      default:
        return "bg-slate-100 text-slate-700 border-slate-200";
    }
  };

  return (
    <div className="min-h-[calc(100vh-2rem)] bg-slate-50 px-4 py-6 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-5xl rounded-[2.5rem] border border-slate-200 bg-white p-8 shadow-sm shadow-slate-300">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <p className="text-sm uppercase tracking-[0.3em] text-emerald-700 font-semibold">
              Outreach Campaign Manager
            </p>
            <h1 className="mt-3 text-4xl font-semibold text-slate-950">
              Gestión de Campañas
            </h1>
            <p className="mt-2 text-slate-600">
              Crea nuevas campañas de outreach o consulta las campañas existentes.
            </p>
          </div>
          <Link
            href="/dashboard/admin"
            className="inline-flex items-center justify-center rounded-3xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm font-semibold text-emerald-700 transition hover:bg-emerald-100"
          >
            Volver al dashboard
          </Link>
        </div>

        {/* View Toggle Tabs */}
        <div className="mt-8 flex border-b border-slate-200 gap-6">
          <button
            type="button"
            onClick={() => setViewMode("create")}
            className={`pb-4 text-base font-semibold transition border-b-2 ${
              viewMode === "create"
                ? "border-emerald-600 text-emerald-700"
                : "border-transparent text-slate-500 hover:text-slate-800"
            }`}
          >
            Crear Campaña
          </button>
          <button
            type="button"
            onClick={() => setViewMode("list")}
            className={`pb-4 text-base font-semibold transition border-b-2 ${
              viewMode === "list"
                ? "border-emerald-600 text-emerald-700"
                : "border-transparent text-slate-500 hover:text-slate-800"
            }`}
          >
            Campañas Existentes
          </button>
        </div>

        {/* CREATE VIEW */}
        {viewMode === "create" && (
          <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
            <div className="grid gap-6 sm:grid-cols-2">
              <label className="block">
                <span className="text-sm font-semibold text-slate-700">
                  Nombre de campaña
                </span>
                <input
                  value={name}
                  onChange={(event) => setName(event.target.value)}
                  required
                  placeholder="Campaña de invitaciones"
                  className="mt-2 w-full rounded-3xl border border-slate-200 bg-slate-50 px-4 py-3 text-slate-950 placeholder:text-slate-400 focus:border-emerald-500 focus:outline-none focus:ring-2 focus:ring-emerald-500/20"
                />
              </label>
              <label className="block">
                <span className="text-sm font-semibold text-slate-700">
                  Asunto del email
                </span>
                <input
                  value={subject}
                  onChange={(event) => setSubject(event.target.value)}
                  required
                  placeholder="Completa tu evaluación Ghost Load"
                  className="mt-2 w-full rounded-3xl border border-slate-200 bg-slate-50 px-4 py-3 text-slate-950 placeholder:text-slate-400 focus:border-emerald-500 focus:outline-none focus:ring-2 focus:ring-emerald-500/20"
                />
              </label>
            </div>

            <div className="grid gap-6 sm:grid-cols-2">
              <label className="block">
                <span className="text-sm font-semibold text-slate-700">
                  Texto del botón
                </span>
                <input
                  value={callToActionText}
                  onChange={(event) => setCallToActionText(event.target.value)}
                  required
                  placeholder="Abrir invitación"
                  className="mt-2 w-full rounded-3xl border border-slate-200 bg-slate-50 px-4 py-3 text-slate-950 placeholder:text-slate-400 focus:border-emerald-500 focus:outline-none focus:ring-2 focus:ring-emerald-500/20"
                />
              </label>
              <label className="block">
                <span className="text-sm font-semibold text-slate-700">
                  Importación de contactos
                </span>
                {loadingImports ? (
                  <div className="mt-2 rounded-3xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-500 animate-pulse">
                    Cargando importaciones disponibles...
                  </div>
                ) : contactImports.length > 0 ? (
                  <select
                    value={contactImportId}
                    onChange={(event) => setContactImportId(event.target.value)}
                    required
                    className="mt-2 w-full rounded-3xl border border-slate-200 bg-slate-50 px-4 py-3 text-slate-950 focus:border-emerald-500 focus:outline-none focus:ring-2 focus:ring-emerald-500/20"
                  >
                    <option value="" disabled>
                      -- Selecciona una importación --
                    </option>
                    {contactImports.map((imp) => (
                      <option key={imp.importId} value={imp.importId}>
                        {imp.name} ({imp.validContacts} contactos válidos)
                      </option>
                    ))}
                  </select>
                ) : (
                  <div className="mt-2 space-y-2">
                    <div className="rounded-2xl border border-amber-200 bg-amber-50 p-3 text-xs text-amber-800">
                      {importsError || "No se encontraron importaciones de contactos creadas."}
                    </div>
                    <Link
                      href="/dashboard/admin/contact-import"
                      className="inline-flex items-center text-xs font-semibold text-emerald-700 hover:underline"
                    >
                      + Importar un nuevo CSV de contactos
                    </Link>
                  </div>
                )}
              </label>
            </div>

            <label className="block">
              <span className="text-sm font-semibold text-slate-700">
                Mensaje del email
              </span>
              <textarea
                value={message}
                onChange={(event) => setMessage(event.target.value)}
                required
                rows={5}
                placeholder="Hola, completá tu evaluación para recibir el reporte personalizado."
                className="mt-2 w-full rounded-3xl border border-slate-200 bg-slate-50 px-4 py-3 text-slate-950 placeholder:text-slate-400 focus:border-emerald-500 focus:outline-none focus:ring-2 focus:ring-emerald-500/20"
              />
            </label>

            <label className="block">
              <span className="text-sm font-semibold text-slate-700">
                Descripción
              </span>
              <textarea
                value={description}
                onChange={(event) => setDescription(event.target.value)}
                rows={2}
                placeholder="Descripción breve de la campaña"
                className="mt-2 w-full rounded-3xl border border-slate-200 bg-slate-50 px-4 py-3 text-slate-950 placeholder:text-slate-400 focus:border-emerald-500 focus:outline-none focus:ring-2 focus:ring-emerald-500/20"
              />
            </label>

            <div className="grid gap-6 sm:grid-cols-2">
              <label className="block">
                <span className="text-sm font-semibold text-slate-700">
                  Fecha de envío
                </span>
                <input
                  type="datetime-local"
                  value={scheduledAt}
                  onChange={(event) => setScheduledAt(event.target.value)}
                  className="mt-2 w-full rounded-3xl border border-slate-200 bg-slate-50 px-4 py-3 text-slate-950 placeholder:text-slate-400 focus:border-emerald-500 focus:outline-none focus:ring-2 focus:ring-emerald-500/20"
                />
              </label>
              <label className="block">
                <span className="text-sm font-semibold text-slate-700">
                  Zona horaria
                </span>
                <input
                  value={timezone}
                  onChange={(event) => setTimezone(event.target.value)}
                  className="mt-2 w-full rounded-3xl border border-slate-200 bg-slate-50 px-4 py-3 text-slate-950 placeholder:text-slate-400 focus:border-emerald-500 focus:outline-none focus:ring-2 focus:ring-emerald-500/20"
                />
              </label>
            </div>

            <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between pt-2">
              <button
                type="submit"
                disabled={status === "submitting" || contactImports.length === 0}
                className="inline-flex justify-center rounded-3xl bg-emerald-600 px-6 py-3 text-sm font-semibold text-white transition hover:bg-emerald-500 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {status === "submitting" ? "Creando campaña..." : "Crear y enviar campaña"}
              </button>
              <p className="text-xs text-slate-500">
                Debes seleccionar una importación de contactos válida.
              </p>
            </div>

            {status === "success" && (
              <div className="rounded-3xl border border-emerald-500/20 bg-emerald-500/10 p-4 text-emerald-800">
                {responseMessage}
              </div>
            )}
            {status === "error" && (
              <div className="rounded-3xl border border-rose-500/20 bg-rose-500/10 p-4 text-rose-800">
                {responseMessage}
              </div>
            )}
          </form>
        )}

        {/* LIST VIEW */}
        {viewMode === "list" && (
          <div className="mt-8 space-y-6">
            {/* Status Filter Toolbar */}
            <div>
              <p className="text-xs font-semibold uppercase tracking-wider text-slate-500">
                Filtrar por Estado:
              </p>
              <div className="mt-3 flex flex-wrap gap-2">
                {STATUS_FILTERS.map((f) => {
                  const isActive = selectedStatusFilter === f.value;
                  return (
                    <button
                      key={f.value}
                      type="button"
                      onClick={() => setSelectedStatusFilter(f.value)}
                      className={`rounded-2xl border px-3 py-1.5 text-xs font-medium transition ${
                        isActive
                          ? "border-emerald-600 bg-emerald-50 text-emerald-700 shadow-sm font-semibold"
                          : "border-slate-200 bg-white text-slate-600 hover:bg-slate-50"
                      }`}
                    >
                      {f.label}
                    </button>
                  );
                })}
              </div>
            </div>

            {/* Content List / State */}
            {loadingCampaigns ? (
              <div className="py-12 text-center text-slate-500">
                <div className="inline-block h-6 w-6 animate-spin rounded-full border-2 border-emerald-600 border-t-transparent" />
                <p className="mt-3 text-sm font-medium">Cargando campañas...</p>
              </div>
            ) : campaignsError ? (
              <div className="rounded-3xl border border-rose-200 bg-rose-50 p-6 text-center text-rose-800">
                <p className="font-semibold">Ocurrió un error al cargar las campañas</p>
                <p className="mt-1 text-sm">{campaignsError}</p>
                <button
                  type="button"
                  onClick={loadCampaigns}
                  className="mt-4 rounded-2xl bg-rose-600 px-4 py-2 text-xs font-semibold text-white transition hover:bg-rose-500"
                >
                  Reintentar
                </button>
              </div>
            ) : campaignsList.length === 0 ? (
              <div className="rounded-[2rem] border border-slate-200 bg-slate-50 p-10 text-center text-slate-500">
                <p className="text-base font-semibold text-slate-700">
                  No se encontraron campañas
                </p>
                <p className="mt-1 text-sm">
                  {selectedStatusFilter !== "ALL"
                    ? `No hay ninguna campaña con el estado "${selectedStatusFilter}".`
                    : "Aún no se ha creado ninguna campaña."}
                </p>
              </div>
            ) : (
              <div className="grid gap-4 sm:grid-cols-2">
                {campaignsList.map((c) => {
                  const badgeStyle = getStatusBadgeStyle(c.status);
                  const formattedDate = c.createdAt
                    ? new Date(c.createdAt).toLocaleDateString("es-ES", {
                        year: "numeric",
                        month: "short",
                        day: "numeric",
                        hour: "2-digit",
                        minute: "2-digit",
                      })
                    : "Fecha N/A";

                  return (
                    <div
                      key={c.id}
                      className="flex flex-col justify-between rounded-[1.75rem] border border-slate-200 bg-white p-6 shadow-sm shadow-slate-100 transition hover:shadow-md"
                    >
                      <div>
                        <div className="flex items-start justify-between gap-3">
                          <h3 className="text-lg font-semibold text-slate-950 leading-snug">
                            {c.name}
                          </h3>
                          <span
                            className={`shrink-0 rounded-full border px-3 py-1 text-xs font-semibold uppercase tracking-wider ${badgeStyle}`}
                          >
                            {c.status}
                          </span>
                        </div>
                        <p className="mt-3 text-xs text-slate-500">
                          ID: <span className="font-mono text-slate-700">{c.id}</span>
                        </p>
                      </div>

                      <div className="mt-6 flex items-center justify-between border-t border-slate-100 pt-4 text-xs text-slate-600">
                        <div>
                          <span className="font-semibold text-slate-950 text-sm">
                            {c.recipientCount}
                          </span>{" "}
                          destinatarios
                        </div>
                        <div className="text-slate-400">{formattedDate}</div>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        )}
      </div>

      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 px-4 py-6">
          <div className="w-full max-w-md rounded-[2rem] border border-slate-200 bg-white p-6 shadow-sm shadow-slate-300">
            <h2 className="text-xl font-semibold text-slate-950">
              {modalTitle}
            </h2>
            <p
              className={`mt-4 text-sm ${modalIsError ? "text-rose-600" : "text-emerald-600"}`}
            >
              {modalMessage}
            </p>
            <div className="mt-6 flex justify-end">
              <button
                type="button"
                onClick={() => setShowModal(false)}
                className="inline-flex justify-center rounded-3xl bg-emerald-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-emerald-500"
              >
                Cerrar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

