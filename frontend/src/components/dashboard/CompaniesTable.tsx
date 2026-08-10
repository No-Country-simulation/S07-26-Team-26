"use client";

import { useMemo, useState } from "react";
import { Search, Download } from "lucide-react";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import type { Company } from "@/services/api";
import { maturityColor } from "@/lib/scoring";
import { cn } from "@/lib/utils";

const statusTone: Record<string, "success" | "warning" | "neutral"> = {
  Completed: "success",
  "In Progress": "warning",
  Invited: "neutral",
};

export function CompaniesTable({ companies }: { companies: Company[] }) {
  const [query, setQuery] = useState("");

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase();
    if (!q) return companies;
    return companies.filter(
      (c) =>
        c.name.toLowerCase().includes(q) ||
        c.country.toLowerCase().includes(q) ||
        c.industry.toLowerCase().includes(q)
    );
  }, [companies, query]);

  return (
    <div>
      <div className="relative mb-4 max-w-xs">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-graphite-400" />
        <Input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search companies, country, industry…"
          className="pl-9"
        />
      </div>

      <div className="overflow-x-auto rounded-lg border border-graphite-100">
        <table className="w-full text-left text-sm">
          <thead className="bg-graphite-50 text-xs uppercase tracking-wide text-graphite-500">
            <tr>
              <th className="px-4 py-3 font-medium">Company</th>
              <th className="px-4 py-3 font-medium">Country</th>
              <th className="px-4 py-3 font-medium">Industry</th>
              <th className="px-4 py-3 font-medium">Status</th>
              <th className="px-4 py-3 font-medium">Score</th>
              <th className="px-4 py-3 font-medium">Maturity Level</th>
              <th className="px-4 py-3 font-medium">PDF</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-graphite-100">
            {filtered.map((c) => (
              <tr key={c.id} className="hover:bg-graphite-50/60">
                <td className="px-4 py-3 font-medium text-graphite-900">{c.name}</td>
                <td className="px-4 py-3 text-graphite-600">{c.country}</td>
                <td className="px-4 py-3 text-graphite-600">{c.industry}</td>
                <td className="px-4 py-3">
                  <Badge tone={statusTone[c.status] ?? "neutral"}>{c.status}</Badge>
                </td>
                <td className="px-4 py-3 font-tabular text-graphite-800">{c.score ?? "—"}</td>
                <td className="px-4 py-3">
                  {c.maturityLevel ? (
                    <span
                      className={cn(
                        "inline-flex items-center rounded-sm border px-2 py-0.5 text-xs font-medium",
                        maturityColor(c.maturityLevel as never)
                      )}
                    >
                      {c.maturityLevel}
                    </span>
                  ) : (
                    <span className="text-graphite-300">—</span>
                  )}
                </td>
                <td className="px-4 py-3">
                  {c.pdfAvailable ? (
                    <button className="inline-flex items-center gap-1.5 text-xs font-medium text-forest-700 hover:text-forest-800">
                      <Download className="h-3.5 w-3.5" />
                      Download PDF
                    </button>
                  ) : (
                    <span className="text-graphite-300">—</span>
                  )}
                </td>
              </tr>
            ))}
            {filtered.length === 0 && (
              <tr>
                <td colSpan={7} className="px-4 py-8 text-center text-sm text-graphite-400">
                  No companies match “{query}”.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
