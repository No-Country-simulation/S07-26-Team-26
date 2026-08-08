interface IsoCubeProps {
  size?: number;
  top: string;
  left: string;
  right: string;
  className?: string;
}

// A small isometric cube built from three flat-shaded rhombus faces.
// This is the app's signature "voxel" motif -- used for the logo mark,
// KPI icon slots, and step indicators -- standing in for literal pixel-art
// or game assets while staying legible and professional at small sizes.
export function IsoCube({ size = 32, top, left, right, className }: IsoCubeProps) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 100 100"
      className={className}
      aria-hidden="true"
    >
      <polygon points="50,2 96,26 50,50 4,26" fill={top} />
      <polygon points="4,26 50,50 50,98 4,74" fill={left} />
      <polygon points="50,50 96,26 96,74 50,98" fill={right} />
    </svg>
  );
}
