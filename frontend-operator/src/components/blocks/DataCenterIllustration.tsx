// Stylized isometric "data center built from blocks" -- five voxel server
// towers of varying height, one grass-green "efficient" tower as the hero,
// a couple of gold status-light accents, on a soft radial floor glow.
// Deliberately not a game screenshot: flat-shaded geometric faces only,
// generous negative space, corporate-safe.
export function DataCenterIllustration({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 580 260" className={className} aria-hidden="true">
      <defs>
        <radialGradient id="floorGlow" cx="50%" cy="50%" r="50%">
          <stop offset="0%" stopColor="#7FAE55" stopOpacity="0.16" />
          <stop offset="100%" stopColor="#7FAE55" stopOpacity="0" />
        </radialGradient>
      </defs>

      <ellipse cx="290" cy="238" rx="260" ry="26" fill="url(#floorGlow)" />

      {/* Tower 1 -- stone, height 1 */}
      <g transform="translate(20,142)">
        <polygon points="4,26 50,50 50,98 4,74" fill="#8A8A8A" />
        <polygon points="50,50 96,26 96,74 50,98" fill="#5C5C5C" />
        <polygon points="50,2 96,26 50,50 4,26" fill="#C6C6C6" />
      </g>

      {/* Tower 2 -- stone, height 2, status light */}
      <g transform="translate(130,94)">
        <polygon points="4,26 50,50 50,146 4,122" fill="#8A8A8A" />
        <polygon points="50,50 96,26 96,122 50,146" fill="#5C5C5C" />
        <polygon points="50,2 96,26 50,50 4,26" fill="#C6C6C6" />
        <circle cx="20" cy="40" r="3.5" fill="#E8C34A" />
      </g>

      {/* Tower 3 -- grass (hero), height 3 */}
      <g transform="translate(240,46)">
        <polygon points="4,26 50,50 50,194 4,170" fill="#5D8A3A" />
        <polygon points="50,50 96,26 96,170 50,194" fill="#3F6023" />
        <polygon points="50,2 96,26 50,50 4,26" fill="#7FAE55" />
      </g>

      {/* Tower 4 -- stone, height 2, status light */}
      <g transform="translate(350,94)">
        <polygon points="4,26 50,50 50,146 4,122" fill="#8A8A8A" />
        <polygon points="50,50 96,26 96,122 50,146" fill="#5C5C5C" />
        <polygon points="50,2 96,26 50,50 4,26" fill="#C6C6C6" />
        <circle cx="20" cy="40" r="3.5" fill="#E8C34A" />
      </g>

      {/* Tower 5 -- wood, height 1 */}
      <g transform="translate(460,142)">
        <polygon points="4,26 50,50 50,98 4,74" fill="#8B5A2B" />
        <polygon points="50,50 96,26 96,74 50,98" fill="#5A3A1E" />
        <polygon points="50,2 96,26 50,50 4,26" fill="#A9713A" />
      </g>
    </svg>
  );
}
