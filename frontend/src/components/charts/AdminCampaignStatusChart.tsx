"use client";

import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Cell,
  Tooltip,
  ResponsiveContainer,
} from "recharts";
import { dashboardFakeData } from "@/data/dashboardFakeData";

export function AdminCampaignStatusChart() {
  const {
    activeCampaigns,
    readyCampaigns,
    sendingCampaigns,
    failedCampaigns,
  } = dashboardFakeData.outreach;

  const data = [
    { name: "Activas", count: activeCampaigns, color: "#10b981" },
    { name: "Listas", count: readyCampaigns, color: "#6366f1" },
    { name: "Enviando", count: sendingCampaigns, color: "#f59e0b" },
    { name: "Fallidas", count: failedCampaigns, color: "#f43f5e" },
  ];

  return (
    <div className="h-44 w-full">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart
          layout="vertical"
          data={data}
          margin={{ top: 5, right: 20, left: 10, bottom: 5 }}
        >
          <XAxis type="number" hide />
          <YAxis
            type="category"
            dataKey="name"
            tick={{ fontSize: 12, fill: "#475569", fontWeight: 500 }}
            axisLine={false}
            tickLine={false}
            width={70}
          />
          <Tooltip
            cursor={{ fill: "rgba(241, 245, 249, 0.6)" }}
            contentStyle={{
              backgroundColor: "#ffffff",
              borderRadius: "0.75rem",
              borderColor: "#e2e8f0",
              fontSize: "12px",
            }}
          />
          <Bar dataKey="count" name="Campañas" radius={[0, 8, 8, 0]} barSize={16}>
            {data.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={entry.color} />
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
