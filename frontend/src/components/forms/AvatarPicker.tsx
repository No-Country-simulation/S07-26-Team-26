"use client";

import { BrandCube } from "@/components/blocks/BrandCube";
import { cn } from "@/lib/utils";

export type AvatarVariant = "grass" | "gold" | "stone" | "wood";

const OPTIONS: AvatarVariant[] = ["grass", "gold", "stone", "wood"];

export function AvatarPicker({
  value,
  onChange,
  label = "Avatar",
}: {
  value: AvatarVariant;
  onChange: (value: AvatarVariant) => void;
  label?: string;
}) {
  return (
    <div>
      <p className="mb-1.5 block text-xs font-medium text-graphite-600">{label}</p>
      <div className="flex gap-2">
        {OPTIONS.map((variant) => (
          <button
            key={variant}
            type="button"
            onClick={() => onChange(variant)}
            aria-label={`Avatar ${variant}`}
            className={cn(
              "flex h-11 w-11 items-center justify-center rounded-md border-2 bg-white transition-all",
              value === variant
                ? "border-forest-700 shadow-bevel-sm"
                : "border-graphite-200 hover:border-graphite-400"
            )}
          >
            <BrandCube variant={variant} size={26} />
          </button>
        ))}
      </div>
    </div>
  );
}
