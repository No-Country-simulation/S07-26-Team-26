"use client";

import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/Card";
import { CalculatorForm } from "@/components/forms/CalculatorForm";
import { useCalculatorStore } from "@/store/calculatorStore";
import { formatNumber } from "@/lib/utils";
import { OperatorStepGuard } from "@/components/shared/OperatorStepGuard";

export default function OperatorCalculatorPage() {
  const kpis = useCalculatorStore((s) => s.kpis);

  return (
    <OperatorStepGuard step="calculator">
      <div>
        <div className="mb-6">
          <h1 className="text-xl font-semibold tracking-tight text-graphite-900">
            Infrastructure Calculator
          </h1>
          <p className="mt-1 text-sm text-graphite-500">
            Enter your current power, GPU, and cooling figures. These feed
            directly into your benchmark.
          </p>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Infrastructure Inputs</CardTitle>
          </CardHeader>
          <CardContent>
            <CalculatorForm />
          </CardContent>
        </Card>

        {kpis && (
          <Card className="mt-6">
            <CardHeader>
              <CardTitle>Calculated KPIs</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-2 gap-4 sm:grid-cols-3">
                <Kpi
                  label="Idle Capacity"
                  value={`${formatNumber(kpis.idleCapacityKw)} kW`}
                />
                <Kpi
                  label="Idle Capacity %"
                  value={`${kpis.idleCapacityPct}%`}
                />
                <Kpi label="Power / GPU" value={`${kpis.powerPerGpuKw} kW`} />
                <Kpi
                  label="Cooling Headroom"
                  value={`${formatNumber(kpis.coolingHeadroomKw)} kW`}
                />
                <Kpi
                  label="Projected Need (12mo)"
                  value={`${formatNumber(kpis.projectedCapacityNeedKw)} kW`}
                />
              </div>
            </CardContent>
          </Card>
        )}
      </div>
    </OperatorStepGuard>
  );
}

function Kpi({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-md border border-graphite-100 bg-graphite-50/50 px-4 py-3">
      <p className="text-xs text-graphite-500">{label}</p>
      <p className="font-tabular text-base font-semibold text-graphite-900">
        {value}
      </p>
    </div>
  );
}
