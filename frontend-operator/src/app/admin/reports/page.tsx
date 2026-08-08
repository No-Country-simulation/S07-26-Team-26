"use client";

import { FileText, Download } from "lucide-react";
import { AdminTopbar } from "@/components/navbar/AdminTopbar";
import { Card, CardContent } from "@/components/ui/Card";
import { Skeleton } from "@/components/ui/Skeleton";
import { Button } from "@/components/ui/Button";
import { usePdfs } from "@/hooks/useResults";
import { useVisibleCompanies } from "@/hooks/useCompanies";
import { maturityColor } from "@/lib/scoring";
import { formatDate } from "@/lib/utils";
import { cn } from "@/lib/utils";

export default function AdminReportsPage() {
  const { data: pdfs, isLoading } = usePdfs();
  const { data: visibleCompanies } = useVisibleCompanies();
  const visibleIds = visibleCompanies ? new Set(visibleCompanies.map((c) => c.id)) : null;
  const scopedPdfs = pdfs && visibleIds ? pdfs.filter((p) => visibleIds.has(p.companyId)) : pdfs;

  return (
    <>
      <AdminTopbar title="Results & PDFs" description="Institutional reports generated for completed benchmarks" />
      <div className="p-8">
        <Card>
          <CardContent className="divide-y divide-graphite-100 p-0">
            {isLoading || !scopedPdfs ? (
              <div className="p-5">
                <Skeleton className="h-64" />
              </div>
            ) : (
              scopedPdfs.map((pdf) => (
                <div key={pdf.companyId} className="flex items-center justify-between px-5 py-4">
                  <div className="flex items-center gap-3">
                    <div className="flex h-9 w-9 items-center justify-center rounded-md bg-forest-50 text-forest-700">
                      <FileText className="h-4 w-4" strokeWidth={1.75} />
                    </div>
                    <div>
                      <p className="text-sm font-medium text-graphite-900">{pdf.companyName}</p>
                      <p className="text-xs text-graphite-400">Generated {formatDate(pdf.generatedAt)}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-4">
                    <span className="font-tabular text-sm text-graphite-700">{pdf.score}/100</span>
                    <span
                      className={cn(
                        "inline-flex items-center rounded-sm border px-2 py-0.5 text-xs font-medium",
                        maturityColor(pdf.maturityLevel as never)
                      )}
                    >
                      {pdf.maturityLevel}
                    </span>
                    <Button variant="secondary" size="sm">
                      <Download className="h-3.5 w-3.5" />
                      Download
                    </Button>
                  </div>
                </div>
              ))
            )}
          </CardContent>
        </Card>
      </div>
    </>
  );
}
