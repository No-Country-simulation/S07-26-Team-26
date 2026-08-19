"use client";

import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

const sampleData = [
  { week: "Sem 1", sent: 120, visited: 65, completed: 22 },
  { week: "Sem 2", sent: 180, visited: 95, completed: 35 },
  { week: "Sem 3", sent: 240, visited: 130, completed: 48 },
  { week: "Sem 4", sent: 310, visited: 180, completed: 68 },
  { week: "Sem 5", sent: 450, visited: 250, completed: 88 },
  { week: "Sem 6", sent: 580, visited: 310, completed: 105 },
];

export function AdminOutreachAreaChart() {
  return (
    <div className="h-64 w-full">
      <ResponsiveContainer width="100%" height="100%">
        <AreaChart
          data={sampleData}
          margin={{ top: 10, right: 10, left: -20, bottom: 0 }}
        >
          <defs>
            <linearGradient id="sentGradient" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#059669" stopOpacity={0.4} />
              <stop offset="95%" stopColor="#059669" stopOpacity={0.0} />
            </linearGradient>
            <linearGradient id="visitedGradient" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#0284c7" stopOpacity={0.4} />
              <stop offset="95%" stopColor="#0284c7" stopOpacity={0.0} />
            </linearGradient>
            <linearGradient id="completedGradient" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#8b5cf6" stopOpacity={0.4} />
              <stop offset="95%" stopColor="#8b5cf6" stopOpacity={0.0} />
            </linearGradient>
          </defs>
          <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" vertical={false} />
          <XAxis
            dataKey="week"
            tick={{ fontSize: 12, fill: "#64748b" }}
            axisLine={{ stroke: "#cbd5e1" }}
            tickLine={false}
          />
          <YAxis
            tick={{ fontSize: 12, fill: "#64748b" }}
            axisLine={false}
            tickLine={false}
          />
          <Tooltip
            contentStyle={{
              backgroundColor: "#ffffff",
              borderRadius: "1rem",
              borderColor: "#e2e8f0",
              boxShadow: "0 4px 12px rgba(0, 0, 0, 0.05)",
              fontSize: "12px",
            }}
          />
          <Area
            type="monotone"
            dataKey="sent"
            name="Emails Enviados"
            stroke="#059669"
            strokeWidth={2}
            fillOpacity={1}
            fill="url(#sentGradient)"
          />
          <Area
            type="monotone"
            dataKey="visited"
            name="Links Visitados"
            stroke="#0284c7"
            strokeWidth={2}
            fillOpacity={1}
            fill="url(#visitedGradient)"
          />
          <Area
            type="monotone"
            dataKey="completed"
            name="Evaluaciones Completadas"
            stroke="#8b5cf6"
            strokeWidth={2}
            fillOpacity={1}
            fill="url(#completedGradient)"
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
}
