"use client";

import { Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";

interface Datum {
  week: string;
  benchmarksCompleted: number;
  avgScore: number;
}

export function WeeklyActivityChart({ data }: { data: Datum[] }) {
  return (
    <ResponsiveContainer width="100%" height={260}>
      <AreaChart data={data} margin={{ top: 8, right: 8, left: -12, bottom: 0 }}>
        <defs>
          <linearGradient id="benchmarksGradient" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="#064E3B" stopOpacity={0.25} />
            <stop offset="100%" stopColor="#064E3B" stopOpacity={0} />
          </linearGradient>
        </defs>
        <CartesianGrid strokeDasharray="3 3" stroke="#E7ECE9" vertical={false} />
        <XAxis dataKey="week" tick={{ fontSize: 11, fill: "#6B7A73" }} axisLine={{ stroke: "#C7D0CC" }} tickLine={false} />
        <YAxis tick={{ fontSize: 11, fill: "#6B7A73" }} axisLine={false} tickLine={false} />
        <Tooltip contentStyle={{ fontSize: 12, borderRadius: 6, borderColor: "#E7ECE9" }} />
        <Area
          type="monotone"
          dataKey="benchmarksCompleted"
          name="Benchmarks completed"
          stroke="#064E3B"
          strokeWidth={2}
          fill="url(#benchmarksGradient)"
        />
      </AreaChart>
    </ResponsiveContainer>
  );
}
