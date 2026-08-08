"use client";

import Image from "next/image";
import Link from "next/link";
import { ReactNode } from "react";
import { usePathname } from "next/navigation";
import { useAuthStore } from "@/store/authStore";

const navItems = [
  {
    name: "Dashboard",
    href: "/dashboard/admin",
    icon: "/icons/01_dashboard.png",
  },
  {
    name: "Outreach",
    href: "/dashboard/admin/contact-import",
    icon: "/icons/02_reportes.png",
  },
  {
    name: "Campañas",
    href: "/dashboard/admin/campaign",
    icon: "/icons/09_trofeo.png",
  },
];

export default function AdminLayout({ children }: { children: ReactNode }) {
  const pathname = usePathname();
  const logout = useAuthStore((state) => state.logout);

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900">
      <aside className="hidden xl:fixed xl:left-0 xl:top-0 xl:z-20 xl:flex xl:h-full xl:w-80 xl:flex-col xl:border-r xl:border-slate-200 xl:bg-white xl:p-8">
        <div>
          <span className="text-xs uppercase tracking-[0.35em] text-emerald-700">
            Admin
          </span>
          <h1 className="mt-4 text-3xl font-semibold text-slate-950">
            Dashboard
          </h1>
          <p className="mt-2 text-sm text-slate-500">
            Administra campañas y outreach desde un solo lugar.
          </p>
        </div>

        <nav className="mt-10 flex-1 space-y-3">
          {navItems.map((item) => {
            const isActive = pathname === item.href;
            return (
              <Link
                key={item.href}
                href={item.href}
                className={`flex items-center gap-3 rounded-3xl px-4 py-3 text-sm font-semibold transition ${
                  isActive
                    ? "bg-emerald-600 text-white"
                    : "text-slate-700 hover:bg-slate-100 hover:text-slate-950"
                }`}
              >
                <Image
                  src={item.icon}
                  alt={`${item.name} icon`}
                  width={18}
                  height={18}
                />
                {item.name}
              </Link>
            );
          })}
        </nav>

        <button
          type="button"
          onClick={() => logout()}
          className="mt-auto inline-flex w-full items-center justify-center rounded-3xl bg-emerald-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-emerald-500"
        >
          <Image
            src="/icons/06_cerrar_sesion.png"
            alt="Cerrar sesión"
            width={20}
            height={20}
          />
          <span className="ml-3">Cerrar sesión</span>
        </button>
      </aside>

      <main className="min-h-screen xl:pl-80">
        <div className="px-4 py-6 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between gap-4 rounded-3xl border border-slate-200 bg-white p-4 shadow-sm xl:hidden">
            <div>
              <span className="text-xs uppercase tracking-[0.35em] text-emerald-700">
                Admin
              </span>
              <h2 className="mt-2 text-lg font-semibold text-slate-950">
                Dashboard
              </h2>
            </div>
            <button
              type="button"
              onClick={() => logout()}
              className="inline-flex items-center justify-center rounded-3xl bg-emerald-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-emerald-500"
            >
              Salir
            </button>
          </div>

          <div className="mx-auto max-w-7xl mt-6 xl:mt-0">{children}</div>
        </div>
      </main>
    </div>
  );
}
