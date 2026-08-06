'use client';

import React from 'react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { useEffect } from 'react';
import { LayoutDashboard, LogOut, Users, Upload, Megaphone, TrendingUp } from 'lucide-react';
import { useAdminAuthStore } from '@/store/adminAuthStore';

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const pathname = usePathname();
  const accessToken = useAdminAuthStore((state) => state.accessToken);
  const admin = useAdminAuthStore((state) => state.admin);
  const hasHydrated = useAdminAuthStore((state) => state.hasHydrated);
  const clearSession = useAdminAuthStore((state) => state.clearSession);

  useEffect(() => {
    void useAdminAuthStore.persist.rehydrate();
  }, []);

  const isLoginPage = pathname === '/admin/login';

  useEffect(() => {
    if (hasHydrated && !accessToken && !isLoginPage) {
      router.replace('/admin/login');
    }
  }, [hasHydrated, accessToken, router, isLoginPage]);

  if (!hasHydrated) {
    return null;
  }

  if (!accessToken) {
    return isLoginPage ? children : null;
  }

  const navItems = [
    { href: '/admin/dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { href: '/admin/operators', label: 'Operadores', icon: Users },
    { href: '/admin/outreach/import', label: 'Importar contactos', icon: Upload },
    { href: '/admin/outreach/campaigns', label: 'Campañas', icon: Megaphone },
    { href: '/admin/crm/pipeline', label: 'Pipeline comercial', icon: TrendingUp },
  ];

  function handleLogout() {
    clearSession();
    router.replace('/admin/login');
  }

  return (
    <div className="min-h-screen">
      <aside className="fixed inset-y-0 left-0 w-60 border-r border-slate-800 bg-slate-950/80 backdrop-blur-xl hidden md:flex flex-col">
        <div className="px-5 py-5 border-b border-slate-800">
          <p className="text-lg font-extrabold tracking-tight text-white">
            Ghost Load
          </p>
          <p className="text-xs text-slate-500">Panel de administración</p>
        </div>

        <nav className="flex-1 px-3 py-4 space-y-1">
          {navItems.map((item) => {
            const Icon = item.icon;
            const active = pathname.startsWith(item.href);
            return (
              <Link
                key={item.href}
                href={item.href}
                className={`flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all ${
                  active
                    ? 'bg-indigo-500/10 border border-indigo-500/25 text-indigo-400'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900'
                }`}
              >
                <Icon className="w-4 h-4" />
                {item.label}
              </Link>
            );
          })}
        </nav>

        <div className="px-5 py-4 border-t border-slate-800 space-y-3">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-full bg-indigo-500/20 border border-indigo-500/30 flex items-center justify-center text-indigo-400 text-sm font-bold">
              {(admin?.name ?? 'A').charAt(0).toUpperCase()}
            </div>
            <div className="min-w-0">
              <p className="text-sm font-semibold text-slate-200 truncate">
                {admin?.name ?? 'Administrador'}
              </p>
              <p className="text-xs text-slate-500 truncate">{admin?.email}</p>
            </div>
          </div>
          <button
            onClick={handleLogout}
            className="w-full flex items-center justify-center gap-2 px-3 py-2 rounded-lg bg-slate-900 border border-slate-800 text-slate-400 text-sm font-medium hover:bg-slate-800 hover:text-slate-200 transition-all"
          >
            <LogOut className="w-4 h-4" />
            Cerrar sesión
          </button>
        </div>
      </aside>

      <main className="md:pl-60">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">{children}</div>
      </main>
    </div>
  );
}
