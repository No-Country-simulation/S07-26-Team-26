"use client";

import { useMemo, useState } from "react";
import { Search, Users2 } from "lucide-react";
import { PixelShieldCheck } from "@/components/blocks/PixelIcons";
import { AdminTopbar } from "@/components/navbar/AdminTopbar";
import { RouteGuard } from "@/components/shared/RouteGuard";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Skeleton } from "@/components/ui/Skeleton";
import { Input } from "@/components/ui/Input";
import { Pagination } from "@/components/ui/Pagination";
import { BrandCube } from "@/components/blocks/BrandCube";
import { CreateAdminForm } from "@/components/forms/CreateAdminForm";
import { useAdmins } from "@/hooks/useAdmins";
import { useCompanies } from "@/hooks/useCompanies";
import { formatDate } from "@/lib/utils";

const PAGE_SIZE = 5;

export default function AdminsPage() {
  return (
    <RouteGuard role="ROOT_ADMIN">
      <AdminsPageContent />
    </RouteGuard>
  );
}

function AdminsPageContent() {
  const { data: admins, isLoading } = useAdmins();
  const { data: companies } = useCompanies();
  const [query, setQuery] = useState("");
  const [roleFilter, setRoleFilter] = useState<"all" | "ROOT_ADMIN" | "ADMIN">("all");
  const [statusFilter, setStatusFilter] = useState<"all" | "Active" | "Invited">("all");
  const [page, setPage] = useState(1);

  const filtered = useMemo(() => {
    if (!admins) return [];
    const q = query.trim().toLowerCase();
    return admins.filter((a) => {
      const matchesQuery = !q || a.name.toLowerCase().includes(q) || a.email.toLowerCase().includes(q);
      const matchesRole = roleFilter === "all" || a.role === roleFilter;
      const matchesStatus = statusFilter === "all" || a.status === statusFilter;
      return matchesQuery && matchesRole && matchesStatus;
    });
  }, [admins, query, roleFilter, statusFilter]);

  const pageCount = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
  const pageItems = filtered.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);

  return (
    <>
      <AdminTopbar
        title="Administradores"
        description="Root-only: crea cuentas de Admin y revisa qué empresas gestiona cada uno"
        breadcrumbs={["Admin", "Administradores"]}
      />
      <div className="space-y-6 p-8">
        <Card>
          <CardHeader>
            <CardTitle>Alta de Administrador</CardTitle>
          </CardHeader>
          <CardContent>
            <CreateAdminForm onCreated={() => setPage(1)} />
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex-col items-stretch gap-4 sm:flex-row sm:items-center">
            <CardTitle>Todos los Administradores</CardTitle>
            <div className="flex flex-wrap items-center gap-2">
              <div className="relative">
                <Search className="pointer-events-none absolute left-3 top-1/2 h-3.5 w-3.5 -translate-y-1/2 text-graphite-400" />
                <Input
                  value={query}
                  onChange={(e) => {
                    setQuery(e.target.value);
                    setPage(1);
                  }}
                  placeholder="Buscar por nombre o correo…"
                  className="w-56 pl-8 text-xs"
                />
              </div>
              <select
                value={roleFilter}
                onChange={(e) => {
                  setRoleFilter(e.target.value as typeof roleFilter);
                  setPage(1);
                }}
                className="rounded-md border border-graphite-200 bg-white px-2.5 py-2 text-xs focus:outline-none focus:ring-2 focus:ring-forest-700/40"
              >
                <option value="all">Todos los roles</option>
                <option value="ROOT_ADMIN">Root Admin</option>
                <option value="ADMIN">Admin</option>
              </select>
              <select
                value={statusFilter}
                onChange={(e) => {
                  setStatusFilter(e.target.value as typeof statusFilter);
                  setPage(1);
                }}
                className="rounded-md border border-graphite-200 bg-white px-2.5 py-2 text-xs focus:outline-none focus:ring-2 focus:ring-forest-700/40"
              >
                <option value="all">Todos los estados</option>
                <option value="Active">Activo</option>
                <option value="Invited">Invitado</option>
              </select>
            </div>
          </CardHeader>
          <CardContent className="p-0">
            {isLoading || !admins ? (
              <div className="p-5">
                <Skeleton className="h-56" />
              </div>
            ) : pageItems.length === 0 ? (
              <div className="flex flex-col items-center gap-2 py-16 text-center">
                <Users2 className="h-8 w-8 text-graphite-300" />
                <p className="text-sm text-graphite-400">Ningún administrador coincide con tu búsqueda.</p>
              </div>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-sm">
                  <thead className="bg-graphite-50 text-xs uppercase tracking-wide text-graphite-500">
                    <tr>
                      <th className="px-5 py-3 font-medium">Avatar</th>
                      <th className="px-5 py-3 font-medium">Nombre</th>
                      <th className="px-5 py-3 font-medium">Correo</th>
                      <th className="px-5 py-3 font-medium">Rol</th>
                      <th className="px-5 py-3 font-medium">Empresas asignadas</th>
                      <th className="px-5 py-3 font-medium">Estado</th>
                      <th className="px-5 py-3 font-medium">Último acceso</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-graphite-100">
                    {pageItems.map((admin) => {
                      const assignedCount =
                        admin.role === "ROOT_ADMIN"
                          ? companies?.length ?? 0
                          : companies?.filter((c) => c.assignedAdminId === admin.id).length ?? 0;
                      return (
                        <tr key={admin.id} className="transition-colors hover:bg-graphite-50/60">
                          <td className="px-5 py-3.5">
                            <BrandCube variant={admin.avatar} size={28} />
                          </td>
                          <td className="px-5 py-3.5 font-medium text-graphite-900">{admin.name}</td>
                          <td className="px-5 py-3.5 text-graphite-600">{admin.email}</td>
                          <td className="px-5 py-3.5">
                            {admin.role === "ROOT_ADMIN" ? (
                              <span className="inline-flex items-center gap-1 text-xs font-semibold text-gold-700">
                                <PixelShieldCheck size={16} />
                                Root Admin
                              </span>
                            ) : (
                              <span className="text-xs font-medium text-graphite-600">Admin</span>
                            )}
                          </td>
                          <td className="px-5 py-3.5 font-tabular text-graphite-700">
                            {admin.role === "ROOT_ADMIN" ? "Todas" : assignedCount}
                          </td>
                          <td className="px-5 py-3.5">
                            <Badge tone={admin.status === "Active" ? "success" : "neutral"}>
                              {admin.status === "Active" ? "Activo" : "Invitado"}
                            </Badge>
                          </td>
                          <td className="px-5 py-3.5 text-graphite-500">{formatDate(admin.lastAccess ?? admin.createdAt)}</td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
                <Pagination
                  page={page}
                  pageCount={pageCount}
                  onPageChange={setPage}
                  totalItems={filtered.length}
                  pageSize={PAGE_SIZE}
                />
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </>
  );
}
