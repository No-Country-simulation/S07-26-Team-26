"use client";

import { AdminTopbar } from "@/components/navbar/AdminTopbar";
import { Card, CardContent } from "@/components/ui/Card";
import { Skeleton } from "@/components/ui/Skeleton";
import { useVisibleCompanies } from "@/hooks/useCompanies";
import { formatDate } from "@/lib/utils";
import { cn } from "@/lib/utils";

const STAGES = ["Invited", "Visited", "Started", "Completed", "PDF Generated"];

function stageIndex(status: string, pdfAvailable: boolean) {
  if (status === "Completed") return pdfAvailable ? 5 : 4;
  if (status === "In Progress") return 3;
  return 1;
}

export default function AdminTrackingPage() {
  const { data: companies, isLoading } = useVisibleCompanies();

  return (
    <>
      <AdminTopbar title="Tracking" description="Where each company sits in the invitation-to-report journey" />
      <div className="p-8">
        <Card>
          <CardContent className="pt-5">
            {isLoading || !companies ? (
              <Skeleton className="h-96" />
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-sm">
                  <thead className="text-xs uppercase tracking-wide text-graphite-500">
                    <tr>
                      <th className="px-4 py-3 font-medium">Company</th>
                      <th className="px-4 py-3 font-medium">Joined</th>
                      <th className="px-4 py-3 font-medium" colSpan={5}>
                        Journey
                      </th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-graphite-100">
                    {companies.map((c) => {
                      const idx = stageIndex(c.status, c.pdfAvailable);
                      return (
                        <tr key={c.id}>
                          <td className="whitespace-nowrap px-4 py-3.5 font-medium text-graphite-900">{c.name}</td>
                          <td className="whitespace-nowrap px-4 py-3.5 text-graphite-500">{formatDate(c.joinedAt)}</td>
                          {STAGES.map((stage, i) => (
                            <td key={stage} className="px-2 py-3.5">
                              <div className="flex flex-col items-center gap-1">
                                <div
                                  className={cn(
                                    "h-2.5 w-2.5 rounded-full",
                                    i < idx ? "bg-forest-700" : "bg-graphite-200"
                                  )}
                                />
                                <span className="text-[10px] text-graphite-400">{stage}</span>
                              </div>
                            </td>
                          ))}
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </>
  );
}
