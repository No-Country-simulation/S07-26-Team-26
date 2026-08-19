"use client";

import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from "recharts";
import { dashboardFakeData } from "@/data/dashboardFakeData";

export function AdminEmailDeliveryDonut() {
  const { sentEmails, failedEmails, pendingEmails, processingEmails } =
    dashboardFakeData.emailDelivery;

  const data = [
    { name: "Enviados", value: sentEmails, color: "#10b981" },
    { name: "Fallidos", value: failedEmails, color: "#f43f5e" },
    { name: "Pendientes", value: pendingEmails + processingEmails, color: "#f59e0b" },
  ];

  const total = data.reduce((sum, item) => sum + item.value, 0);

  return (
    <div className="flex flex-col items-center sm:flex-row sm:justify-between gap-4">
      <div className="relative h-44 w-44">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie
              data={data}
              cx="50%"
              cy="50%"
              innerRadius={52}
              outerRadius={72}
              paddingAngle={4}
              dataKey="value"
            >
              {data.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={entry.color} stroke="none" />
              ))}
            </Pie>
            <Tooltip
              contentStyle={{
                backgroundColor: "#ffffff",
                borderRadius: "0.75rem",
                borderColor: "#e2e8f0",
                fontSize: "12px",
              }}
            />
          </PieChart>
        </ResponsiveContainer>
        <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
          <span className="text-xl font-bold text-slate-950">
            {dashboardFakeData.emailDelivery.deliveryRate}%
          </span>
          <span className="text-[0.68rem] font-semibold text-slate-500 uppercase tracking-wider">
            Entrega
          </span>
        </div>
      </div>

      <div className="w-full sm:w-auto space-y-2.5 text-xs text-slate-600">
        {data.map((item) => {
          const pct = Math.round((item.value / total) * 100);
          return (
            <div key={item.name} className="flex items-center justify-between gap-6">
              <div className="flex items-center gap-2">
                <span
                  className="h-2.5 w-2.5 rounded-full"
                  style={{ backgroundColor: item.color }}
                />
                <span className="font-medium text-slate-700">{item.name}</span>
              </div>
              <div className="flex items-center gap-2 font-semibold text-slate-950">
                <span>{item.value}</span>
                <span className="text-slate-400 font-normal">({pct}%)</span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
