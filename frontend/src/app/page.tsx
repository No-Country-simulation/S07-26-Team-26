'use client';

import React from 'react';
import Link from 'next/link';
import { useQuery } from '@tanstack/react-query';
import { useCounterStore } from '@/store/counterStore';
import { 
  Users, 
  RotateCw, 
  Plus, 
  Minus, 
  RefreshCcw, 
  Search, 
  Cpu, 
  Database, 
  Compass, 
  ExternalLink,
  Mail,
  Briefcase,
  Layers
} from 'lucide-react';

interface User {
  id: number;
  name: string;
  email: string;
  website: string;
  company: {
    name: string;
  };
}

export default function Home() {
  const { count, increment, decrement, reset, userFilter, setUserFilter } = useCounterStore();

  const { 
    data: users, 
    isLoading, 
    isError, 
    error, 
    isFetching, 
    refetch, 
    dataUpdatedAt 
  } = useQuery<User[]>({
    queryKey: ['users'],
    queryFn: async () => {
      // Simulate slow network to demonstrate loader aesthetics
      await new Promise((resolve) => setTimeout(resolve, 1200));
      const res = await fetch('https://jsonplaceholder.typicode.com/users');
      if (!res.ok) throw new Error('Error al obtener los usuarios');
      return res.json();
    },
  });

  // Filter users based on search string from Zustand store
  const filteredUsers = users?.filter(user =>
    user.name.toLowerCase().includes(userFilter.toLowerCase()) ||
    user.email.toLowerCase().includes(userFilter.toLowerCase()) ||
    user.company.name.toLowerCase().includes(userFilter.toLowerCase())
  );

  return (
    <div className="relative min-h-screen overflow-x-hidden">
      {/* Decorative Ambient Background Glows */}
      <div className="absolute top-[-10%] left-[-10%] w-[50%] h-[50%] rounded-full bg-indigo-500/10 blur-[120px] pointer-events-none" />
      <div className="absolute bottom-[-10%] right-[-10%] w-[50%] h-[50%] rounded-full bg-violet-600/10 blur-[120px] pointer-events-none" />

      {/* Main Container */}
      <div className="max-w-7xl mx-auto px-4 py-12 sm:px-6 lg:px-8 relative z-10">
        
        {/* Header */}
        <header className="text-center mb-16 space-y-4">
          <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-indigo-500/10 border border-indigo-500/25 text-indigo-400 text-sm font-medium mb-2 backdrop-blur-md">
            <Layers className="w-4 h-4" /> Next.js Base Project ready
          </div>
          <h1 className="text-4xl sm:text-5xl lg:text-6xl font-extrabold tracking-tight bg-clip-text text-transparent bg-gradient-to-r from-white via-slate-200 to-slate-400">
            Next.js Stack Starter
          </h1>
          <p className="max-w-2xl mx-auto text-base sm:text-lg text-slate-400">
            Un entorno moderno preconfigurado con Tailwind CSS, Zustand para estado global y React Query para estado del servidor.
          </p>

          {/* Badges */}
          <div className="flex flex-wrap items-center justify-center gap-3 pt-4">
            <span className="px-3 py-1 rounded-md text-xs font-semibold bg-slate-900 border border-slate-800 text-slate-300">
              Next.js 15 (App Router)
            </span>
            <span className="px-3 py-1 rounded-md text-xs font-semibold bg-slate-900 border border-slate-800 text-slate-300">
              Tailwind CSS
            </span>
            <span className="px-3 py-1 rounded-md text-xs font-semibold bg-slate-900 border border-slate-800 text-slate-300">
              Zustand v5
            </span>
            <span className="px-3 py-1 rounded-md text-xs font-semibold bg-slate-900 border border-slate-800 text-slate-300">
              React Query v5
            </span>
            <span className="px-3 py-1 rounded-md text-xs font-semibold bg-slate-900 border border-slate-800 text-slate-300">
              TypeScript
            </span>
          </div>

          <div className="mt-8 flex flex-col items-center gap-3 sm:flex-row sm:justify-center">
            <Link
              href="/login"
              className="rounded-2xl bg-indigo-500 px-5 py-3 text-sm font-semibold text-white transition hover:bg-indigo-400"
            >
              Iniciar sesión administrador
            </Link>
            <Link
              href="/dashboard/admin"
              className="rounded-2xl border border-slate-800 bg-slate-900/80 px-5 py-3 text-sm font-semibold text-slate-100 transition hover:border-indigo-500 hover:text-white"
            >
              Ir a dashboard admin
            </Link>
          </div>
        </header>

        {/* Dashboard Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
          
          {/* LEFT: Zustand Controls (col-span-4) */}
          <section className="lg:col-span-4 space-y-6">
            
            {/* Zustand Card */}
            <div className="rounded-2xl border border-slate-800 bg-slate-900/40 backdrop-blur-xl p-6 shadow-2xl relative overflow-hidden group">
              <div className="absolute top-0 right-0 w-24 h-24 bg-indigo-500/5 rounded-full blur-2xl group-hover:bg-indigo-500/10 transition-all duration-300" />
              
              <div className="flex items-center gap-3 mb-6">
                <div className="p-2.5 rounded-lg bg-indigo-500/10 border border-indigo-500/20 text-indigo-400">
                  <Cpu className="w-5 h-5" />
                </div>
                <div>
                  <h2 className="text-xl font-bold text-white">Estado Global</h2>
                  <p className="text-xs text-slate-500">Manejado por Zustand Store</p>
                </div>
              </div>

              {/* Counter Demo */}
              <div className="space-y-4 bg-slate-950/40 border border-slate-800/80 rounded-xl p-4 mb-6">
                <span className="text-xs font-medium text-slate-400 uppercase tracking-wider block">Demo Contador</span>
                <div className="flex items-center justify-between">
                  <span className="text-3xl font-extrabold text-white font-mono">{count}</span>
                  <div className="flex items-center gap-2">
                    <button 
                      onClick={decrement}
                      className="p-2 rounded-lg bg-slate-800 border border-slate-700 text-slate-300 hover:bg-slate-700 hover:text-white transition-all hover:scale-105 active:scale-95"
                      title="Decrementar"
                    >
                      <Minus className="w-4 h-4" />
                    </button>
                    <button 
                      onClick={increment}
                      className="p-2 rounded-lg bg-slate-800 border border-slate-700 text-slate-300 hover:bg-slate-700 hover:text-white transition-all hover:scale-105 active:scale-95"
                      title="Incrementar"
                    >
                      <Plus className="w-4 h-4" />
                    </button>
                    <button 
                      onClick={reset}
                      className="p-2 rounded-lg bg-slate-800/50 border border-slate-800 text-slate-400 hover:bg-slate-800 hover:text-red-400 transition-all hover:scale-105 active:scale-95"
                      title="Resetear"
                    >
                      <RefreshCcw className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              </div>

              {/* Search Filter input */}
              <div className="space-y-2">
                <label className="text-xs font-medium text-slate-400 uppercase tracking-wider block">Filtro de Usuarios</label>
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                  <input
                    type="text"
                    value={userFilter}
                    onChange={(e) => setUserFilter(e.target.value)}
                    placeholder="Filtrar por nombre, email o compañía..."
                    className="w-full pl-10 pr-4 py-2.5 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 placeholder-slate-500 focus:outline-none focus:border-indigo-500/50 focus:ring-1 focus:ring-indigo-500/30 transition-all text-sm"
                  />
                </div>
                {userFilter && (
                  <p className="text-xs text-indigo-400 flex items-center gap-1.5 pt-1">
                    <span className="inline-block w-1.5 h-1.5 rounded-full bg-indigo-400 animate-pulse" />
                    Filtro activo sincronizado globalmente
                  </p>
                )}
              </div>
            </div>

            {/* Structure info Card */}
            <div className="rounded-2xl border border-slate-800 bg-slate-900/20 p-6 space-y-4">
              <h3 className="text-sm font-semibold text-slate-300">Ubicaciones clave del proyecto:</h3>
              <ul className="text-xs text-slate-400 space-y-2 font-mono">
                <li className="flex items-center gap-2">
                  <span className="w-1.5 h-1.5 rounded-full bg-indigo-500" />
                  Store Zustand: <span className="text-slate-300 ml-auto">src/store/counterStore.ts</span>
                </li>
                <li className="flex items-center gap-2">
                  <span className="w-1.5 h-1.5 rounded-full bg-indigo-500" />
                  Provider Query: <span className="text-slate-300 ml-auto">src/providers/QueryProvider.tsx</span>
                </li>
                <li className="flex items-center gap-2">
                  <span className="w-1.5 h-1.5 rounded-full bg-indigo-500" />
                  Página principal: <span className="text-slate-300 ml-auto">src/app/page.tsx</span>
                </li>
              </ul>
            </div>
          </section>

          {/* RIGHT: React Query Data Display (col-span-8) */}
          <section className="lg:col-span-8">
            <div className="rounded-2xl border border-slate-800 bg-slate-900/40 backdrop-blur-xl p-6 shadow-2xl relative overflow-hidden group">
              <div className="absolute top-0 right-0 w-48 h-48 bg-violet-500/5 rounded-full blur-3xl group-hover:bg-violet-500/10 transition-all duration-300" />

              {/* Card Header & Controls */}
              <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6 border-b border-slate-800/80 pb-5">
                <div className="flex items-center gap-3">
                  <div className="p-2.5 rounded-lg bg-violet-500/10 border border-violet-500/20 text-violet-400">
                    <Database className="w-5 h-5" />
                  </div>
                  <div>
                    <h2 className="text-xl font-bold text-white">Estado del Servidor</h2>
                    <p className="text-xs text-slate-500">Manejado por React Query & Fetching Asíncrono</p>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  {dataUpdatedAt > 0 && (
                    <span className="text-[11px] text-slate-500 font-mono hidden sm:inline-block">
                      Act. {new Date(dataUpdatedAt).toLocaleTimeString()}
                    </span>
                  )}
                  <button
                    onClick={() => refetch()}
                    disabled={isFetching}
                    className="inline-flex items-center gap-2 px-3 py-1.5 rounded-lg bg-slate-800 border border-slate-700 text-slate-300 text-xs font-semibold hover:bg-slate-700 hover:text-white transition-all disabled:opacity-50 disabled:cursor-not-allowed hover:scale-[1.02] active:scale-[0.98]"
                  >
                    <RotateCw className={`w-3.5 h-3.5 ${isFetching ? 'animate-spin text-violet-400' : ''}`} />
                    {isFetching ? 'Actualizando...' : 'Refrescar'}
                  </button>
                </div>
              </div>

              {/* React Query State Content */}
              {isLoading ? (
                // Shimmer Skeletons
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  {[...Array(4)].map((_, i) => (
                    <div key={i} className="border border-slate-800/60 rounded-xl p-4 bg-slate-950/20 space-y-3 animate-pulse">
                      <div className="h-4 bg-slate-800 rounded w-3/4" />
                      <div className="h-3 bg-slate-800 rounded w-1/2" />
                      <div className="h-3 bg-slate-800 rounded w-5/6" />
                    </div>
                  ))}
                </div>
              ) : isError ? (
                // Error State
                <div className="p-6 rounded-xl border border-red-500/20 bg-red-500/5 text-center space-y-3">
                  <p className="text-red-400 font-semibold">Error al cargar datos</p>
                  <p className="text-xs text-slate-400">{error?.message || 'Algo salió mal al obtener los usuarios del servidor de pruebas.'}</p>
                  <button 
                    onClick={() => refetch()}
                    className="px-4 py-2 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 text-xs font-semibold hover:bg-red-500/20 transition-all"
                  >
                    Reintentar
                  </button>
                </div>
              ) : filteredUsers && filteredUsers.length > 0 ? (
                // User Grid
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  {filteredUsers.map((user) => (
                    <div 
                      key={user.id} 
                      className="group/item border border-slate-850 hover:border-slate-700/60 rounded-xl p-4 bg-slate-950/30 hover:bg-slate-950/60 transition-all duration-200 shadow-md relative overflow-hidden"
                    >
                      <div className="flex flex-col h-full justify-between gap-3">
                        <div className="space-y-1">
                          <div className="flex items-start justify-between">
                            <h3 className="font-bold text-slate-200 group-hover/item:text-white transition-colors">
                              {user.name}
                            </h3>
                            <span className="text-[10px] px-1.5 py-0.5 rounded bg-slate-900 border border-slate-850 text-slate-500">
                              ID {user.id}
                            </span>
                          </div>

                          {/* Email */}
                          <div className="flex items-center gap-2 text-xs text-slate-400">
                            <Mail className="w-3.5 h-3.5 text-slate-500" />
                            <span className="truncate">{user.email}</span>
                          </div>

                          {/* Company */}
                          <div className="flex items-center gap-2 text-xs text-slate-400">
                            <Briefcase className="w-3.5 h-3.5 text-slate-500" />
                            <span className="truncate">{user.company.name}</span>
                          </div>
                        </div>

                        {/* Website external link */}
                        <div className="pt-2 border-t border-slate-900/50 flex items-center justify-between">
                          <a 
                            href={`https://${user.website}`} 
                            target="_blank" 
                            rel="noopener noreferrer"
                            className="inline-flex items-center gap-1 text-[11px] text-indigo-400 hover:text-indigo-300 font-semibold"
                          >
                            <Compass className="w-3 h-3" />
                            {user.website}
                            <ExternalLink className="w-2.5 h-2.5" />
                          </a>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                // Empty filtered state
                <div className="p-8 text-center border border-dashed border-slate-800 rounded-xl text-slate-500 space-y-2">
                  <Users className="w-8 h-8 mx-auto text-slate-600" />
                  <p className="text-sm font-medium">Ningún usuario coincide con tu filtro</p>
                  <p className="text-xs">Prueba escribiendo otra palabra o limpia el buscador de Zustand.</p>
                  <button 
                    onClick={() => setUserFilter('')}
                    className="text-xs text-indigo-400 font-semibold hover:underline"
                  >
                    Limpiar filtro
                  </button>
                </div>
              )}
            </div>
          </section>

        </div>

        {/* Footer */}
        <footer className="mt-20 border-t border-slate-900 pt-8 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs text-slate-500">
          <p>© {new Date().getFullYear()} Enterprise Stack Boilerplate. Desarrollado con Next.js + Zustand + React Query.</p>
          <div className="flex items-center gap-4">
            <span className="flex items-center gap-1">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse" />
              Tailwind V4 Listo
            </span>
            <span>·</span>
            <span>Control de Estado Activo</span>
          </div>
        </footer>

      </div>
    </div>
  );
}
