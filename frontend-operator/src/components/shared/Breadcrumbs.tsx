import { ChevronRight } from "lucide-react";

export function Breadcrumbs({ items }: { items: string[] }) {
  return (
    <nav className="mb-1 flex items-center gap-1.5 text-xs text-graphite-400" aria-label="Breadcrumb">
      {items.map((item, i) => (
        <span key={item} className="flex items-center gap-1.5">
          {i > 0 && <ChevronRight className="h-3 w-3" />}
          <span className={i === items.length - 1 ? "font-medium text-graphite-600" : ""}>{item}</span>
        </span>
      ))}
    </nav>
  );
}
