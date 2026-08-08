"use client";

import { Megaphone } from "lucide-react";
import { AdminTopbar } from "@/components/navbar/AdminTopbar";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/Card";
import { Skeleton } from "@/components/ui/Skeleton";
import { InvitationFunnelChart } from "@/components/charts/InvitationFunnelChart";
import { Badge } from "@/components/ui/Badge";
import { useCampaigns } from "@/hooks/useCampaigns";
import { formatDate, formatNumber } from "@/lib/utils";

export default function AdminCampaignsPage() {
  const { data: campaigns, isLoading } = useCampaigns();

  return (
    <>
      <AdminTopbar title="Campaigns" description="Lead-generation campaigns feeding the benchmark funnel" />
      <div className="space-y-5 p-8">
        {isLoading || !campaigns ? (
          Array.from({ length: 2 }).map((_, i) => <Skeleton key={i} className="h-52" />)
        ) : (
          campaigns.map((campaign) => (
            <Card key={campaign.id}>
              <CardHeader>
                <div className="flex items-center gap-2.5">
                  <div className="flex h-8 w-8 items-center justify-center rounded-md bg-forest-50 text-forest-700">
                    <Megaphone className="h-4 w-4" strokeWidth={1.75} />
                  </div>
                  <div>
                    <CardTitle>{campaign.name}</CardTitle>
                    <p className="text-xs text-graphite-400">Launched {formatDate(campaign.createdAt)}</p>
                  </div>
                </div>
                <Badge tone="neutral">{campaign.channel}</Badge>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 gap-8 md:grid-cols-2">
                  <InvitationFunnelChart data={campaign.funnel} />
                  <div className="grid grid-cols-2 gap-4 content-start">
                    <Stat label="Registered" value={campaign.funnel.registered} />
                    <Stat label="Completed" value={campaign.funnel.completed} />
                    <Stat
                      label="Completion Rate"
                      value={Math.round((campaign.funnel.completed / campaign.funnel.registered) * 100)}
                      suffix="%"
                    />
                    <Stat label="PDFs Generated" value={campaign.funnel.pdfGenerated} />
                  </div>
                </div>
              </CardContent>
            </Card>
          ))
        )}
      </div>
    </>
  );
}

function Stat({ label, value, suffix }: { label: string; value: number; suffix?: string }) {
  return (
    <div className="rounded-md border border-graphite-100 bg-graphite-50/50 px-4 py-3">
      <p className="text-xs text-graphite-500">{label}</p>
      <p className="font-tabular text-lg font-semibold text-graphite-900">
        {formatNumber(value)}
        {suffix}
      </p>
    </div>
  );
}
