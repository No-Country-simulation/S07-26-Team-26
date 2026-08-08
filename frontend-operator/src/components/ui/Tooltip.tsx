import { ReactNode } from "react";

export function Tooltip({ label, children }: { label: string; children: ReactNode }) {
  return (
    <span className="group/tooltip relative inline-flex">
      {children}
      <span
        role="tooltip"
        className="pointer-events-none absolute -top-8 left-1/2 z-20 -translate-x-1/2 whitespace-nowrap rounded-sm bg-graphite-900 px-2 py-1 text-[11px] font-medium text-white opacity-0 shadow-elevated transition-opacity duration-150 group-hover/tooltip:opacity-100"
      >
        {label}
      </span>
    </span>
  );
}
