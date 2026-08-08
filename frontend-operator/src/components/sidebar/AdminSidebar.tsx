"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { Megaphone, Radar, Users, LogOut } from "lucide-react";
import { cn } from "@/lib/utils";
import { useAuthStore } from "@/store/authStore";
import { BrandCube } from "@/components/blocks/BrandCube";
import {
  PixelBuilding,
  PixelPeople,
  PixelDocument,
  PixelGear,
  PixelShieldCheck,
} from "@/components/blocks/PixelIcons";

// Nav items mix Lucide outline icons (default) with the original pixel-art
// set for the handful of spots specified in the design brief -- Dashboard,
// Empresas, Operadores, Reportes, Configuración, Administradores. Not every
// item got a pixel icon: the brief says not to force it, and Contacts/
// Campaigns/Tracking don't have a pixel equivalent that reads better than
// their current Lucide icon.
const NAV_ITEMS = [
  { href: "/admin/dashboard", label: "Dashboard", pixel: "grass" as const, rootOnly: false },
  { href: "/admin/companies", label: "Empresas", pixel: "building" as const, rootOnly: false },
  { href: "/admin/operators", label: "Operadores", pixel: "people" as const, rootOnly: false },
  { href: "/admin/contacts", label: "Contactos", icon: Users, rootOnly: false },
  { href: "/admin/campaigns", label: "Campañas", icon: Megaphone, rootOnly: false },
  { href: "/admin/tracking", label: "Seguimiento", icon: Radar, rootOnly: false },
  { href: "/admin/reports", label: "Reportes", pixel: "document" as const, rootOnly: false },
  { href: "/admin/admins", label: "Administradores", pixel: "shield" as const, rootOnly: true },
  { href: "/admin/settings", label: "Configuración", pixel: "gear" as const, rootOnly: true },
];

function NavIcon({ item }: { item: (typeof NAV_ITEMS)[number] }) {
  if ("pixel" in item) {
    switch (item.pixel) {
      case "grass":
        return <BrandCube variant="grass" size={18} />;
      case "building":
        return <PixelBuilding size={18} />;
      case "people":
        return <PixelPeople size={18} />;
      case "document":
        return <PixelDocument size={18} />;
      case "gear":
        return <PixelGear size={18} />;
      case "shield":
        return <PixelShieldCheck size={18} />;
    }
  }
  const Icon = item.icon;
  return <Icon className="h-4 w-4" strokeWidth={1.75} />;
}

// Light/white sidebar with a thin gold left-bar on the active item --
// matches the reference palette (white surface, near-black green text,
// sparing gold accent) instead of the previous dark-green sidebar.
export function AdminSidebar() {
  const pathname = usePathname();
  const router = useRouter();
  const session = useAuthStore((s) => s.session);
  const clearSession = useAuthStore((s) => s.clearSession);
  const isRoot = session?.role === "ROOT_ADMIN";

  const items = NAV_ITEMS.filter((item) => !item.rootOnly || isRoot);

  return (
    <aside className="flex h-screen w-64 shrink-0 flex-col border-r border-graphite-100 bg-white">
      <div className="flex items-center gap-2.5 px-6 py-7">
        <BrandCube variant="grass" size={32} />
        <div>
          <p className="text-[14px] font-semibold leading-tight tracking-tight text-graphite-900">Admin Central</p>
          <p className="text-[11px] leading-tight text-graphite-400">
            {isRoot ? "Root Admin" : "Panel administrativo"}
          </p>
        </div>
      </div>

      <nav className="flex-1 space-y-0.5 px-3">
        {items.map((item) => {
          const active = pathname?.startsWith(item.href);
          return (
            <Link
              key={item.href}
              href={item.href}
              className={cn(
                "relative flex items-center gap-2.5 rounded-md px-3.5 py-2.5 text-[13.5px] font-medium transition-colors",
                active ? "bg-forest-50/70 text-forest-800" : "text-graphite-500 hover:bg-graphite-50 hover:text-graphite-900"
              )}
            >
              {active && <span className="absolute left-0 top-1.5 bottom-1.5 w-[3px] rounded-full bg-gold-500" />}
              <NavIcon item={item} />
              {item.label}
            </Link>
          );
        })}
      </nav>

      <div className="border-t border-graphite-100 px-3 py-4">
        <button
          onClick={() => {
            clearSession();
            router.push("/login");
          }}
          className="flex w-full items-center gap-2.5 rounded-md px-3.5 py-2.5 text-[13.5px] font-medium text-graphite-500 transition-colors hover:bg-graphite-50 hover:text-graphite-900"
        >
          <LogOut className="h-4 w-4" strokeWidth={1.75} />
          Cerrar sesión
        </button>
      </div>
    </aside>
  );
}
