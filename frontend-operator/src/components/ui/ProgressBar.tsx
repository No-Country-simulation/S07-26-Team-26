import { cn } from "@/lib/utils";

export function ProgressBar({ value, className }: { value: number; className?: string }) {
  const clamped = Math.min(Math.max(value, 0), 100);
  return (
    <div className={cn("h-1.5 w-full overflow-hidden rounded-full bg-graphite-100", className)}>
      <div
        className="h-full rounded-full bg-forest-700 transition-all duration-300"
        style={{ width: `${clamped}%` }}
      />
    </div>
  );
}
