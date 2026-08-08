"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { useAuthStore } from "@/store/authStore";
import { dashboardFakeData } from "@/data/dashboardFakeData";

export default function AdminDashboardPage() {
  const router = useRouter();
  const { isAuthenticated, logout } = useAuthStore();
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
      <div className="mx-auto max-w-6xl rounded-[2.5rem] border border-slate-200 bg-white p-8 shadow-sm shadow-slate-200">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <p className="text-sm uppercase tracking-[0.3em] text-emerald-700">
              Panel administrativo
            </p>
            <h1 className="mt-3 text-4xl font-semibold text-slate-950">
              Dashboard Admin
            </h1>
            <p className="mt-2 text-slate-600">
              Bienvenido. Este espacio está protegido y solo es accesible tras
              iniciar sesión.
            </p>
          </div>
          <div className="inline-flex items-center rounded-3xl bg-emerald-600 px-4 py-3 text-sm font-semibold text-white shadow-sm shadow-emerald-100">
            <img
              src="/icons/07_notificacion.png"
              alt="Notificaciones"
              className="h-5 w-5"
            />
            <span className="ml-3">Actualizaciones disponibles</span>
          </div>
        </div>

        <div className="mt-8 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
          <div className="rounded-[2rem] border border-slate-200 bg-slate-50 p-6">
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
                <p className="mt-1 text-2xl font-semibold text-slate-950 tracking-normal">
                  {dashboardFakeData.outreach.totalContactImports}
                </p>
              </div>
            </div>
            <p className="mt-3 text-sm text-slate-500">Total imports</p>
          </div>
          <div className="rounded-[2rem] border border-slate-200 bg-slate-50 p-6">
            <div className="flex items-center gap-3">
              <img
                src="/icons/09_trofeo.png"
                alt="Campañas"
                className="h-8 w-8"
              />
              <div>
                <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                  Campañas
                </p>
                <p className="mt-1 text-2xl font-semibold text-slate-950 tracking-normal">
                  {dashboardFakeData.outreach.totalCampaigns}
                </p>
              </div>
            </div>
            <p className="mt-3 text-sm text-slate-500">Campañas totales</p>
          </div>
          <div className="rounded-[2rem] border border-slate-200 bg-slate-50 p-6">
            <div className="flex items-center gap-3">
              <img
                src="/icons/07_notificacion.png"
                alt="Emails enviados"
                className="h-8 w-8"
              />
              <div>
                <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                  Emails enviados
                </p>
                <p className="mt-1 text-2xl font-semibold text-slate-950 tracking-normal">
                  {dashboardFakeData.emailDelivery.sentEmails}
                </p>
              </div>
            </div>
            <p className="mt-3 text-sm text-slate-500">Emails enviados</p>
          </div>
          <div className="rounded-[2rem] border border-slate-200 bg-slate-50 p-6">
            <div className="flex items-center gap-3">
              <img
                src="/icons/11_porcentaje.png"
                alt="Completion rate"
                className="h-8 w-8"
              />
              <div>
                <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                  Completion rate
                </p>
                <p className="mt-1 text-2xl font-semibold text-slate-950 tracking-normal">
                  {dashboardFakeData.completionTracking.completionRate}%
                </p>
              </div>
            </div>
            <p className="mt-3 text-sm text-slate-500">Tasa de completado</p>
          </div>
        </div>

        <div className="mt-10 grid gap-6 xl:grid-cols-[1.2fr_0.8fr]">
          <section className="rounded-[2rem] border border-slate-200 bg-white p-6 shadow-sm shadow-slate-100">
            <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
              <div>
                <p className="text-sm uppercase tracking-[0.3em] text-emerald-700">
                  Outreach
                </p>
                <h2 className="mt-2 text-3xl font-semibold text-slate-950">
                  Estadísticas generales
                </h2>
              </div>
              <span className="rounded-full bg-emerald-100 px-3 py-1 text-xs uppercase tracking-[0.3em] text-emerald-700">
                Próximo
              </span>
            </div>
            <div className="mt-6 h-52 rounded-[2rem] bg-emerald-50 p-4 text-slate-500">
              <p className="mt-16 text-center text-sm">
                Aquí irá el chart de envíos / apertura.
              </p>
            </div>
          </section>
        </div>

        <div className="mt-10 grid gap-6 xl:grid-cols-[1.2fr_0.8fr]">
          <section className="rounded-[2rem] border border-slate-200 bg-white p-6 shadow-sm shadow-slate-100">
            <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
              <div>
                <p className="text-sm uppercase tracking-[0.3em] text-emerald-700">
                  Outreach
                </p>
                <h2 className="mt-2 text-3xl font-semibold text-slate-950">
                  Estadísticas generales
                </h2>
              </div>
              <div className="inline-flex items-center gap-2 rounded-3xl bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
                <img
                  src="/icons/11_porcentaje.png"
                  alt="Últimos datos"
                  className="h-4 w-4"
                />
                <span>Últimos datos fake</span>
              </div>
            </div>

            <div className="mt-8 grid gap-4 sm:grid-cols-2">
              <div className="rounded-[1.75rem] border border-slate-200 bg-slate-50 p-5">
                <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                  Importaciones totales
                </p>
                <p className="mt-3 text-2xl font-semibold text-slate-950 tracking-normal">
                  {dashboardFakeData.outreach.totalContactImports}
                </p>
              </div>
              <div className="rounded-[1.75rem] border border-slate-200 bg-slate-50 p-5">
                <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                  Contactos únicos
                </p>
                <p className="mt-3 text-2xl font-semibold text-slate-950 tracking-normal">
                  {dashboardFakeData.outreach.uniqueContacts}
                </p>
              </div>
              <div className="rounded-[1.75rem] border border-slate-200 bg-slate-50 p-5">
                <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                  Campañas totales
                </p>
                <p className="mt-3 text-2xl font-semibold text-slate-950 tracking-normal">
                  {dashboardFakeData.outreach.totalCampaigns}
                </p>
              </div>
              <div className="rounded-[1.75rem] border border-slate-200 bg-slate-50 p-5">
                <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                  Tasa de entrega
                </p>
                <p className="mt-3 text-2xl font-semibold text-slate-950 tracking-normal">
                  {dashboardFakeData.emailDelivery.deliveryRate}%
                </p>
              </div>
            </div>

            <div className="mt-8 grid gap-4 sm:grid-cols-3">
              <div className="rounded-[1.75rem] border border-slate-200 bg-slate-50 p-5">
                <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                  Importaciones en proceso
                </p>
                <p className="mt-3 text-2xl font-semibold text-slate-950 tracking-normal">
                  {dashboardFakeData.outreach.processingImports}
                </p>
              </div>
              <div className="rounded-[1.75rem] border border-slate-200 bg-slate-50 p-5">
                <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                  Campañas listas
                </p>
                <p className="mt-3 text-2xl font-semibold text-slate-950 tracking-normal">
                  {dashboardFakeData.outreach.readyCampaigns}
                </p>
              </div>
              <div className="rounded-[1.75rem] border border-slate-200 bg-slate-50 p-5">
                <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                  Campañas activas
                </p>
                <p className="mt-3 text-2xl font-semibold text-slate-950 tracking-normal">
                  {dashboardFakeData.outreach.activeCampaigns}
                </p>
              </div>
            </div>
          </section>

          <aside className="space-y-6">
            <div className="rounded-[2rem] border border-slate-200 bg-white p-6 shadow-sm shadow-slate-100">
              <h3 className="text-xl font-semibold text-slate-950">
                Estado de importaciones
              </h3>
              <div className="mt-5 grid gap-4 sm:grid-cols-3">
                <div className="rounded-[1.75rem] bg-slate-50 p-4 text-center">
                  <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                    Completadas
                  </p>
                  <p className="mt-3 text-xl font-semibold text-slate-950 tracking-normal">
                    {dashboardFakeData.outreach.completedImports}
                  </p>
                </div>
                <div className="rounded-[1.75rem] bg-slate-50 p-4 text-center">
                  <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                    Fallidas
                  </p>
                  <p className="mt-3 text-xl font-semibold text-slate-950 tracking-normal">
                    {dashboardFakeData.outreach.failedImports}
                  </p>
                </div>
                <div className="rounded-[1.75rem] bg-slate-50 p-4 text-center">
                  <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                    Envíos
                  </p>
                  <p className="mt-3 text-xl font-semibold text-slate-950 tracking-normal">
                    {dashboardFakeData.emailDelivery.sentEmails}
                  </p>
                </div>
              </div>
            </div>

            <div className="rounded-[2rem] border border-slate-200 bg-white p-6 shadow-sm shadow-slate-100">
              <h3 className="text-xl font-semibold text-slate-950">
                Seguimiento
              </h3>
              <div className="mt-5 space-y-4 text-sm text-slate-600">
                <div className="rounded-[1.75rem] bg-slate-50 p-4">
                  <p className="text-slate-500">Invitaciones enviadas</p>
                  <p className="mt-2 text-2xl font-semibold text-slate-950">
                    {dashboardFakeData.completionTracking.invitationsSent}
                  </p>
                </div>
                <div className="rounded-[1.75rem] bg-slate-50 p-4">
                  <p className="text-slate-500">Links visitados</p>
                  <p className="mt-2 text-2xl font-semibold text-slate-950">
                    {dashboardFakeData.completionTracking.linksVisited}
                  </p>
                </div>
                <div className="rounded-[1.75rem] bg-slate-50 p-4">
                  <p className="text-slate-500">Evaluaciones completadas</p>
                  <p className="mt-2 text-2xl font-semibold text-slate-950">
                    {dashboardFakeData.completionTracking.evaluationsCompleted}
                  </p>
                </div>
              </div>
            </div>
          </aside>
        </div>

        <div className="mt-10 grid gap-6 lg:grid-cols-2">
          <div className="rounded-[2rem] border border-slate-200 bg-white p-6 shadow-sm shadow-slate-100">
            <h3 className="text-xl font-semibold text-slate-950">
              Email delivery
            </h3>
            <div className="mt-6 grid gap-4 sm:grid-cols-2">
              <div className="rounded-[1.75rem] bg-slate-50 p-5">
                <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                  Pendientes
                </p>
                <p className="mt-3 text-2xl font-semibold text-slate-950 tracking-normal">
                  {dashboardFakeData.emailDelivery.pendingEmails}
                </p>
              </div>
              <div className="rounded-[1.75rem] bg-slate-50 p-5">
                <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                  Fallidos
                </p>
                <p className="mt-3 text-2xl font-semibold text-slate-950 tracking-normal">
                  {dashboardFakeData.emailDelivery.failedEmails}
                </p>
              </div>
              <div className="rounded-[1.75rem] bg-slate-50 p-5">
                <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                  Procesando
                </p>
                <p className="mt-3 text-2xl font-semibold text-slate-950 tracking-normal">
                  {dashboardFakeData.emailDelivery.processingEmails}
                </p>
              </div>
              <div className="rounded-[1.75rem] bg-slate-50 p-5">
                <p className="text-[0.72rem] uppercase tracking-[0.06em] text-slate-500 font-semibold">
                  Tasa de entrega
                </p>
                <p className="mt-3 text-2xl font-semibold text-slate-950 tracking-normal">
                  {dashboardFakeData.emailDelivery.deliveryRate}%
                </p>
              </div>
            </div>
          </div>

          <div className="rounded-[2rem] border border-slate-200 bg-white p-6 shadow-sm shadow-slate-100">
            <h3 className="text-xl font-semibold text-slate-950">Resultados</h3>
            <div className="mt-6 grid gap-4 sm:grid-cols-2">
              <div className="rounded-[1.75rem] bg-slate-50 p-5">
                <p className="text-xs uppercase tracking-[0.3em] text-slate-500">
                  Puntaje benchmark
                </p>
                <p className="mt-3 text-3xl font-semibold text-slate-950">
                  {dashboardFakeData.results.averageBenchmarkScore}%
                </p>
              </div>
              <div className="rounded-[1.75rem] bg-slate-50 p-5">
                <p className="text-xs uppercase tracking-[0.3em] text-slate-500">
                  PDFs generados
                </p>
                <p className="mt-3 text-3xl font-semibold text-slate-950">
                  {dashboardFakeData.results.generatedPdfs}
                </p>
              </div>
            </div>
          </div>
        </div>

        <div className="mt-10">
          <Link
            href="/dashboard/admin/campaign"
            className="inline-flex items-center justify-center rounded-3xl bg-emerald-600 px-6 py-3 text-sm font-semibold text-white transition hover:bg-emerald-500"
          >
            Crear campaña de outreach
          </Link>
        </div>
      </div>
    </div>
  );
}
