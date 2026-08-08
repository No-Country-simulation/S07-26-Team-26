"use client";

import { Building2, ClipboardCheck, Gauge, FileDown } from "lucide-react";
import { AdminTopbar } from "@/components/navbar/AdminTopbar";
import { KpiCard } from "@/components/dashboard/KpiCard";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/Card";
import { Skeleton } from "@/components/ui/Skeleton";
import { InvitationFunnelChart } from "@/components/charts/InvitationFunnelChart";
import { MaturityDistributionChart } from "@/components/charts/MaturityDistributionChart";
import { WeeklyActivityChart } from "@/components/charts/WeeklyActivityChart";
import { useDashboardKpis, useInvitationFunnel, useMaturityDistribution, useWeeklyActivity } from "@/hooks/useDashboard";
import { formatNumber } from "@/lib/utils";

export default function AdminDashboardPage() {
  const { data: kpis, isLoading: kpisLoading } = useDashboardKpis();
  const { data: funnel, isLoading: funnelLoading } = useInvitationFunnel();
  const { data: maturity, isLoading: maturityLoading } = useMaturityDistribution();
  const { data: weekly, isLoading: weeklyLoading } = useWeeklyActivity();

  return (
    <>
      <AdminTopbar title="Dashboard" description="Fleet-wide benchmark performance and pipeline health" />

      <div className="space-y-6 p-8">
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {kpisLoading || !kpis ? (
            Array.from({ length: 4 }).map((_, i) => <Skeleton key={i} className="h-[104px]" />)
          ) : (
            <>
              <KpiCard label="Registered Companies" value={formatNumber(kpis.registeredCompanies)} icon={Building2} />
              <KpiCard label="Completed Benchmarks" value={formatNumber(kpis.completedBenchmarks)} icon={ClipboardCheck} />
              <KpiCard label="Average Score" value={`${kpis.averageScore}/100`} icon={Gauge} accent="gold" />
              <KpiCard label="Generated PDFs" value={formatNumber(kpis.generatedPdfs)} icon={FileDown} />
            </>
          )}
        </div>

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-5">
          <Card className="lg:col-span-2">
            <CardHeader>
              <CardTitle>Invitation Funnel</CardTitle>
            </CardHeader>
            <CardContent>
              {funnelLoading || !funnel ? <Skeleton className="h-[220px]" /> : <InvitationFunnelChart data={funnel} />}
            </CardContent>
          </Card>

          <Card className="lg:col-span-3">
            <CardHeader>
              <CardTitle>Maturity Distribution</CardTitle>
            </CardHeader>
            <CardContent>
              {maturityLoading || !maturity ? (
                <Skeleton className="h-[260px]" />
              ) : (
                <MaturityDistributionChart data={maturity} />
              )}
            </CardContent>
          </Card>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Weekly Benchmark Activity</CardTitle>
          </CardHeader>
          <CardContent>
            {weeklyLoading || !weekly ? <Skeleton className="h-[260px]" /> : <WeeklyActivityChart data={weekly} />}
          </CardContent>
        </Card>
      </div>
    </>
  );
}
