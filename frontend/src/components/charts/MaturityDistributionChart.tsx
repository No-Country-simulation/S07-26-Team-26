"use client";

import {
  Bar,
  BarChart,
  CartesianGrid,
  ResponsiveContainer,
        <Tooltip
          contentStyle={{ fontSize: 12, borderRadius: 6, borderColor: "#E7ECE9" }}
          // Accept any argument signature to satisfy Recharts' overloaded types;
          // safely read the first argument which is the value.
          formatter={(...args: any[]) => {
            const value = args[0] as number | string | undefined;
            return `${value ?? 0} companies`;
          }}
        />
interface Datum {
  level: string;
  count: number;
  range: string;
}

const LEVEL_COLORS: Record<string, string> = {
  Critical: "#B91C1C",
  "Operational Risk": "#C2703D",
  Growing: "#D4AF37",
  Mature: "#0B6B4F",
  Leader: "#064E3B",
};

export function MaturityDistributionChart({ data }: { data: Datum[] }) {
  return (
    <ResponsiveContainer width="100%" height={260}>
      <BarChart data={data} margin={{ top: 8, right: 8, left: -12, bottom: 0 }}>
        <CartesianGrid
          strokeDasharray="3 3"
          stroke="#E7ECE9"
          vertical={false}
        />
        <XAxis
          dataKey="level"
          tick={{ fontSize: 11, fill: "#6B7A73" }}
          axisLine={{ stroke: "#C7D0CC" }}
          tickLine={false}
        />
        <YAxis
          tick={{ fontSize: 11, fill: "#6B7A73" }}
          axisLine={false}
          tickLine={false}
        />
        <Tooltip
          contentStyle={{
            fontSize: 12,
            borderRadius: 6,
            borderColor: "#E7ECE9",
          }}
          // Recharts' TS types are strict here; return a simple formatted string
          // instead of a tuple to satisfy the expected return types.
          formatter={(value: number | string | undefined) =>
            `${value ?? 0} companies`
          }
        />
        <Bar dataKey="count" radius={[4, 4, 0, 0]} maxBarSize={48}>
          {data.map((d) => (
            <Cell key={d.level} fill={LEVEL_COLORS[d.level] ?? "#6B7A73"} />
          ))}
        </Bar>
      </BarChart>
    </ResponsiveContainer>
  );
}
