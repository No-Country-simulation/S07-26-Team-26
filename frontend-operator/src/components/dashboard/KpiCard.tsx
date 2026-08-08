import { LucideIcon } from "lucide-react";
import { cn } from "@/lib/utils";
import { BrandCube } from "@/components/blocks/BrandCube";
import { DataCenterIcon, DataCenterIconName } from "@/components/blocks/DataCenterIcons";

interface KpiCardProps {
  label: string;
  value: string;
  /** Either a Lucide glyph (renders on the voxel cube, existing look) or the
   *  name of a "datacenter verde" badge (renders standalone at full size --
   *  those icons already read as a self-contained elevated block, so they
   *  replace the cube+glyph combo rather than sitting on top of it). */
  icon?: LucideIcon;
  dataCenterIcon?: DataCenterIconName;
  hint?: string;
  accent?: "forest" | "gold";
}

// Left accent-bar KPI card with an isometric-cube icon slot -- the app's
// signature "voxel" touch, standing in for a plain icon circle. KPIs that
// map onto a "datacenter verde" badge (trofeo, empresas, etc.) use that
// badge directly instead, at a size where its own shading reads cleanly.
export function KpiCard({ label, value, icon: Icon, dataCenterIcon, hint, accent = "forest" }: KpiCardProps) {
  return (
    <div
      className={cn(
        "group relative overflow-hidden rounded-lg border border-graphite-100 bg-white p-5 shadow-card",
        "border-l-[3px] transition-shadow duration-200 hover:shadow-elevated",
        accent === "forest" ? "border-l-forest-700" : "border-l-gold-500"
      )}
    >
      <div className="flex items-start justify-between">
        <div>
          <p className="text-[11px] font-semibold uppercase tracking-wider text-graphite-400">{label}</p>
          <p className="mt-2.5 font-tabular text-[28px] font-semibold leading-none text-graphite-900">{value}</p>
          {hint && <p className="mt-2 text-xs text-graphite-400">{hint}</p>}
        </div>
        <div className="relative flex h-10 w-10 shrink-0 items-start justify-center pt-1.5 transition-transform duration-200 group-hover:-translate-y-0.5">
          {dataCenterIcon ? (
            <DataCenterIcon name={dataCenterIcon} size={40} className="absolute inset-0" />
          ) : (
            <>
              <BrandCube variant={accent === "forest" ? "grass" : "gold"} size={36} className="absolute inset-0" />
              {Icon && <Icon className="relative h-3.5 w-3.5 text-forest-950/80" strokeWidth={2.25} />}
            </>
          )}
        </div>
      </div>
    </div>
  );
}
