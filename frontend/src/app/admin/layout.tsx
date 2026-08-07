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
      <aside
        className="fixed top-14 bottom-0 left-0 w-60 border-r hidden md:flex flex-col theme-surface sidebar-root"
        style={{ borderRightColor: 'var(--gh-sidebar-border)' }}
      >
        {/* Logo / título */}
        <div
          className="px-5 py-5 border-b"
          style={{ borderBottomColor: 'var(--gh-sidebar-border)' }}
        >
          <p className="text-lg font-extrabold tracking-tight text-white">
            DataCenter
          </p>
          <p
            className="text-xs font-semibold tracking-widest uppercase"
            style={{ color: 'var(--gh-green-400)' }}
          >
            Benchmark
          </p>
        </div>

        {/* Navegación */}
        <nav className="flex-1 px-3 py-4 space-y-0.5">
          {navItems.map((item) => {
            const Icon = item.icon;
            const active = pathname.startsWith(item.href);
            return (
              <Link
                key={item.href}
                href={item.href}
                className={`sidebar-nav-item flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all ${
                  active ? 'active' : ''
                }`}
              >
                <Icon className="w-4 h-4 flex-shrink-0" />
                {item.label}
              </Link>
            );
          })}
        </nav>

        {/* Perfil + logout */}
        <div
          className="px-4 py-4 border-t space-y-3"
          style={{ borderTopColor: 'var(--gh-sidebar-border)' }}
        >
          <div className="flex items-center gap-3">
            <div
              className="w-8 h-8 rounded-full flex items-center justify-center text-white text-sm font-bold flex-shrink-0"
              style={{ backgroundColor: 'var(--gh-green-600)' }}
            >
              {(admin?.name ?? 'A').charAt(0).toUpperCase()}
            </div>
            <div className="min-w-0">
              <p
                className="text-sm font-semibold truncate"
                style={{ color: 'var(--gh-sidebar-active)' }}
              >
                {admin?.name ?? 'Administrador'}
              </p>
              <p className="text-xs truncate" style={{ color: 'var(--gh-sidebar-text)' }}>
                {admin?.email}
              </p>
            </div>
          </div>
          <button
            onClick={handleLogout}
            className="w-full flex items-center justify-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition-all"
            style={{
              backgroundColor: 'rgba(0,0,0,0.2)',
              color: 'var(--gh-sidebar-text)',
              border: '1px solid var(--gh-sidebar-border)',
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.backgroundColor = 'rgba(0,0,0,0.35)';
              e.currentTarget.style.color = '#ffffff';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.backgroundColor = 'rgba(0,0,0,0.2)';
              e.currentTarget.style.color = 'var(--gh-sidebar-text)';
            }}
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
