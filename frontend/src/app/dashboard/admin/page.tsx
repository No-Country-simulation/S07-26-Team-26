'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useAuthStore } from '@/store/authStore';

export default function AdminDashboardPage() {
  const router = useRouter();
  const { isAuthenticated, logout } = useAuthStore();
  const [hasMounted, setHasMounted] = useState(false);

  useEffect(() => {
    setHasMounted(true);
  }, []);

  useEffect(() => {
    if (!isAuthenticated) {
      router.replace('/login');
    }
  }, [isAuthenticated, router]);

  if (!hasMounted || !isAuthenticated) {
    return null;
  }

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 px-4 py-12 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-5xl rounded-3xl border border-slate-800/80 bg-slate-900/95 p-8 shadow-2xl shadow-black/20">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <p className="text-sm uppercase tracking-[0.3em] text-indigo-400">Panel administrativo</p>
            <h1 className="mt-3 text-4xl font-semibold text-white">Dashboard Admin</h1>
            <p className="mt-2 text-slate-400">Bienvenido. Este espacio está protegido y solo es accesible tras iniciar sesión.</p>
          </div>
          <button
            type="button"
            onClick={() => {
              logout();
              router.push('/login');
            }}
            className="inline-flex items-center justify-center rounded-2xl bg-rose-500 px-4 py-3 text-sm font-semibold text-white transition hover:bg-rose-400"
          >
            Cerrar sesión
          </button>
        </div>

        <div className="mt-10 grid gap-6 md:grid-cols-2">
          <article className="rounded-3xl border border-slate-800/80 bg-slate-950/90 p-6">
            <h2 className="text-xl font-semibold text-white">Resumen</h2>
            <p className="mt-3 text-slate-400">Aquí puedes construir el tablero administrativo real, conectando con los endpoints protegidos del backend cuando esté listo.</p>
          </article>
          <article className="rounded-3xl border border-slate-800/80 bg-slate-950/90 p-6">
            <h2 className="text-xl font-semibold text-white">Outreach</h2>
            <p className="mt-3 text-slate-400">Primero importa contactos en CSV y luego crea la campaña usando el ID de importación generado.</p>
            <Link
              href="/dashboard/admin/contact-import"
              className="mt-4 inline-flex items-center justify-center rounded-3xl bg-indigo-500 px-5 py-3 text-sm font-semibold text-white transition hover:bg-indigo-400"
            >
              Importar contactos CSV
            </Link>
          </article>
        </div>

        <div className="mt-10">
          <Link
            href="/dashboard/admin/campaign"
            className="inline-flex items-center justify-center rounded-3xl bg-indigo-500 px-6 py-3 text-sm font-semibold text-white transition hover:bg-indigo-400"
          >
            Crear campaña de outreach
          </Link>
        </div>
      </div>
    </div>
  );
}
