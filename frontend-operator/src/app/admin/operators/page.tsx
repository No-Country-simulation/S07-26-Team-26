"use client";

import { useMemo, useState } from "react";
import { Search, Users2 } from "lucide-react";
import { AdminTopbar } from "@/components/navbar/AdminTopbar";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Skeleton } from "@/components/ui/Skeleton";
import { Input } from "@/components/ui/Input";
import { Pagination } from "@/components/ui/Pagination";
import { BrandCube } from "@/components/blocks/BrandCube";
import { CreateOperatorForm } from "@/components/forms/CreateOperatorForm";
import { useOperators } from "@/hooks/useOperators";
import { useVisibleCompanies } from "@/hooks/useCompanies";
import { formatDate } from "@/lib/utils";

const PAGE_SIZE = 5;

export default function AdminOperatorsPage() {
  const { data: operators, isLoading: operatorsLoading } = useOperators();
  const { data: companies, isLoading: companiesLoading } = useVisibleCompanies();
  const [query, setQuery] = useState("");
  const [companyFilter, setCompanyFilter] = useState<string>("all");
  const [statusFilter, setStatusFilter] = useState<"all" | "Active" | "Invited">("all");
  const [page, setPage] = useState(1);

  const visibleCompanyIds = new Set((companies ?? []).map((c) => c.id));
  const companyName = (companyId: string) => companies?.find((c) => c.id === companyId)?.name ?? "—";

  const filtered = useMemo(() => {
    if (!operators) return [];
    const q = query.trim().toLowerCase();
    return operators
      .filter((o) => visibleCompanyIds.has(o.companyId))
      .filter((o) => {
        const matchesQuery =
          !q || o.name.toLowerCase().includes(q) || o.email.toLowerCase().includes(q);
        const matchesCompany = companyFilter === "all" || o.companyId === companyFilter;
        const matchesStatus = statusFilter === "all" || o.status === statusFilter;
        return matchesQuery && matchesCompany && matchesStatus;
      });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [operators, query, companyFilter, statusFilter, companies]);

  const pageCount = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
  const pageItems = filtered.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);

  return (
    <>
      <AdminTopbar
        title="Operadores"
        description="Crea cuentas de operador para las empresas que gestionas"
        breadcrumbs={["Admin", "Operadores"]}
      />
      <div className="space-y-6 p-8">
        <Card>
          <CardHeader>
            <CardTitle>Alta de Operador</CardTitle>
          </CardHeader>
          <CardContent>
            {companiesLoading || !companies ? (
              <Skeleton className="h-16" />
            ) : (
              <CreateOperatorForm companies={companies} />
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex-col items-stretch gap-4 sm:flex-row sm:items-center">
            <CardTitle>Tus Operadores</CardTitle>
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
                value={companyFilter}
                onChange={(e) => {
                  setCompanyFilter(e.target.value);
                  setPage(1);
                }}
                className="rounded-md border border-graphite-200 bg-white px-2.5 py-2 text-xs focus:outline-none focus:ring-2 focus:ring-forest-700/40"
              >
                <option value="all">Todas las empresas</option>
                {companies?.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
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
            {operatorsLoading || !companies ? (
              <div className="p-5">
                <Skeleton className="h-56" />
              </div>
            ) : pageItems.length === 0 ? (
              <div className="flex flex-col items-center gap-2 py-16 text-center">
                <Users2 className="h-8 w-8 text-graphite-300" />
                <p className="text-sm text-graphite-400">Ningún operador todavía.</p>
              </div>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-sm">
                  <thead className="bg-graphite-50 text-xs uppercase tracking-wide text-graphite-500">
                    <tr>
                      <th className="px-5 py-3 font-medium">Avatar</th>
                      <th className="px-5 py-3 font-medium">Nombre</th>
                      <th className="px-5 py-3 font-medium">Correo</th>
                      <th className="px-5 py-3 font-medium">Empresa</th>
                      <th className="px-5 py-3 font-medium">Estado</th>
                      <th className="px-5 py-3 font-medium">Último acceso</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-graphite-100">
                    {pageItems.map((op) => (
                      <tr key={op.id} className="transition-colors hover:bg-graphite-50/60">
                        <td className="px-5 py-3.5">
                          <BrandCube variant={op.avatar} size={28} />
                        </td>
                        <td className="px-5 py-3.5 font-medium text-graphite-900">{op.name}</td>
                        <td className="px-5 py-3.5 text-graphite-600">{op.email}</td>
                        <td className="px-5 py-3.5 text-graphite-600">{companyName(op.companyId)}</td>
                        <td className="px-5 py-3.5">
                          <Badge tone={op.status === "Active" ? "success" : "neutral"}>
                            {op.status === "Active" ? "Activo" : "Invitado"}
                          </Badge>
                        </td>
                        <td className="px-5 py-3.5 text-graphite-500">{formatDate(op.lastAccess ?? op.createdAt)}</td>
                      </tr>
                    ))}
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
