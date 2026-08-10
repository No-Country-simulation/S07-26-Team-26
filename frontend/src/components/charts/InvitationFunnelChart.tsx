"use client";

import { formatNumber } from "@/lib/utils";

interface FunnelDatum {
  registered: number;
  visited: number;
  started: number;
  completed: number;
  pdfGenerated: number;
}

const STAGES: { key: keyof FunnelDatum; label: string }[] = [
  { key: "registered", label: "Registered" },
  { key: "visited", label: "Visited" },
  { key: "started", label: "Started" },
  { key: "completed", label: "Completed" },
  { key: "pdfGenerated", label: "PDF Generated" },
];

// A deliberate horizontal-bar funnel rather than recharts' wedge-shaped
// FunnelChart -- it reads more like an audit trail of a pipeline, which
// suits an institutional benchmarking product better than a "marketing
// funnel" visual.
export function InvitationFunnelChart({ data }: { data: FunnelDatum }) {
  const max = data.registered || 1;

  return (
    <div className="space-y-3">
      {STAGES.map((stage, i) => {
        const value = data[stage.key];
        const pct = (value / max) * 100;
        const prevValue = i > 0 ? data[STAGES[i - 1].key] : null;
        const dropoff = prevValue ? Math.round(((prevValue - value) / prevValue) * 100) : null;

        return (
          <div key={stage.key}>
            <div className="mb-1 flex items-baseline justify-between text-xs">
              <span className="font-medium text-graphite-700">{stage.label}</span>
              <span className="font-tabular text-graphite-500">
                {formatNumber(value)}
                {dropoff !== null && dropoff > 0 && (
                  <span className="ml-2 text-graphite-400">-{dropoff}%</span>
                )}
              </span>
            </div>
            <div className="h-2.5 w-full rounded-full bg-graphite-100">
              <div
                className="h-full rounded-full bg-gradient-to-r from-forest-700 to-forest-500"
                style={{ width: `${pct}%` }}
              />
            </div>
          </div>
        );
      })}
    </div>
  );
}
