"use client";

import { useAuthStore } from "@/store/authStore";
import { Breadcrumbs } from "@/components/shared/Breadcrumbs";

export function AdminTopbar({
  title,
  description,
  breadcrumbs,
}: {
  title: string;
  description?: string;
  breadcrumbs?: string[];
}) {
  const session = useAuthStore((s) => s.session);
  const roleLabel = session?.role === "ROOT_ADMIN" ? "Root Admin" : "Administrador";

  return (
    <header className="flex items-center justify-between border-b border-graphite-100 bg-white/90 px-8 py-6 backdrop-blur-sm">
      <div>
        {breadcrumbs && <Breadcrumbs items={breadcrumbs} />}
        <h1 className="text-[22px] font-semibold tracking-tight text-graphite-900">{title}</h1>
        {description && <p className="mt-1 text-sm text-graphite-500">{description}</p>}
      </div>
      <div className="flex items-center gap-3">
        <div className="text-right">
          <p className="text-sm font-medium text-graphite-800">{session?.name ?? roleLabel}</p>
          <p className="text-xs text-graphite-400">{session?.email}</p>
        </div>
        <div className="flex h-9 w-9 items-center justify-center rounded-full bg-forest-700 text-sm font-semibold text-gold-400">
          {(session?.name ?? "A").charAt(0).toUpperCase()}
        </div>
      </div>
    </header>
  );
}
