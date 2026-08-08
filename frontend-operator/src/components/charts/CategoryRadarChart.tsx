"use client";

import {
  PolarAngleAxis,
  PolarGrid,
  Radar,
  RadarChart,
  ResponsiveContainer,
} from "recharts";

interface Datum {
  label: string;
  score: number;
}

export function CategoryRadarChart({ data }: { data: Datum[] }) {
  return (
    <ResponsiveContainer width="100%" height={280}>
      <RadarChart data={data} outerRadius="75%">
        <PolarGrid stroke="#E7ECE9" />
        <PolarAngleAxis dataKey="label" tick={{ fontSize: 11, fill: "#3C4A43" }} />
        <Radar
          name="Score"
          dataKey="score"
          stroke="#064E3B"
          fill="#064E3B"
          fillOpacity={0.25}
          strokeWidth={2}
        />
      </RadarChart>
    </ResponsiveContainer>
  );
}
