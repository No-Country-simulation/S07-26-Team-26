import { IsoCube } from "@/components/blocks/IsoCube";

type CubeVariant = "grass" | "gold" | "stone" | "wood";

const VARIANTS: Record<CubeVariant, { top: string; left: string; right: string }> = {
  grass: { top: "#7FAE55", left: "#5D8A3A", right: "#3F6023" },
  gold: { top: "#E8C34A", left: "#D4A017", right: "#8B6914" },
  stone: { top: "#C6C6C6", left: "#8A8A8A", right: "#5C5C5C" },
  wood: { top: "#A9713A", left: "#8B5A2B", right: "#5A3A1E" },
};

export function BrandCube({
  variant = "grass",
  size = 32,
  className,
}: {
  variant?: CubeVariant;
  size?: number;
  className?: string;
}) {
  const colors = VARIANTS[variant];
  return <IsoCube size={size} className={className} {...colors} />;
}
