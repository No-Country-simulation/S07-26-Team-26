import { ProgressBar } from "@/components/ui/ProgressBar";

interface Datum {
  label: string;
  score: number;
}

export function CategoryBarList({ data }: { data: Datum[] }) {
  return (
    <div className="space-y-4">
      {data.map((d) => (
        <div key={d.label}>
          <div className="mb-1 flex items-center justify-between text-xs">
            <span className="font-medium text-graphite-700">{d.label}</span>
            <span className="font-tabular text-graphite-500">{d.score}/100</span>
          </div>
          <ProgressBar value={d.score} />
        </div>
      ))}
    </div>
  );
}
