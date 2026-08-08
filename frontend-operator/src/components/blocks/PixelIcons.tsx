// ---------------------------------------------------------------------------
// Pixel icon library -- original, hand-authored 12x12 pixel-grid artwork in
// the same spirit as the reference Minecraft-style icon set the team
// shared, but NOT traced or copied from any Mojang/Microsoft asset. Each
// icon is a small array of characters (one per pixel) mapped to a limited
// earth-tone palette, rendered as crisp, non-antialiased <rect> tiles.
//
// Used sparingly (per design brief: "no colocar imagenes por colocar") --
// only where a pixel icon genuinely reads better than the Lucide outline
// icon it stands in for.
// ---------------------------------------------------------------------------
import { cn } from "@/lib/utils";

const PALETTE: Record<string, string> = {
  g: "#5D8A3A", d: "#3F6023",
  k: "#D4A017", y: "#E8C34A",
  s: "#8A8A8A", S: "#5C5C5C",
  w: "#8B5A2B", b: "#5A3A1E",
  r: "#DC2626", u: "#2563EB",
  h: "#E8B888", n: "#1C1C1C", x: "#FFFFFF",
};

const GRIDS: Record<string, string[]> = {
  chest: ["............", "............", "..bbbbbbbb..", ".bwwwwwwwwb.", ".bwwwwwwwwb.", ".bwwkkkkwwb.", "bwwwwkkwwwwb", "bwwwwwwwwwwb", "bwwwwwwwwwwb", "bwwwwwwwwwwb", "bbbbbbbbbbbb", "............"],
  key: ["....kkkk....", "...k....k...", "...k....k...", "...k....k...", "....kkkk....", ".....kk.....", ".....kk.....", ".....kk.....", ".....kkkk...", ".....kk.k...", ".....kkkk...", "............"],
  shield_check: ["..dddddddd..", ".d........d.", "d..........d", "d..x....x..d", "d..xx..xx..d", "d...xxxx...d", "d....xx....d", ".d...xx...d.", ".d........d.", "..d......d..", "...d....d...", "....dddd...."],
  no_entry: ["...rrrrrr...", ".rr......rr.", "r..........r", "r..........r", "r.xxxxxxxx.r", "r.xxxxxxxx.r", "r..........r", "r..........r", ".rr......rr.", "...rrrrrr...", "............", "............"],
  mail: ["............", ".nnnnnnnnnn.", ".nxxxxxxxxn.", ".nxnx..xnxn.", ".nx.nxxn.xn.", ".nx..nn..xn.", ".nxxxxxxxxn.", ".nxxxxxxxxn.", ".nxxxxxxxxn.", ".nnnnnnnnnn.", "............", "............"],
  anvil: ["............", "...ssssss...", "...ssssss...", "....ssss....", "....ssss....", "...ssssss...", ".ssssssssss.", ".ssssssssss.", "..ss....ss..", "..ss....ss..", ".ssssssssss.", "............"],
  people: ["............", ".gg..kk..ww.", ".gg..kk..ww.", "ggggkkkkwwww", "ggggkkkkwwww", "ggggkkkkwwww", "............", "............", "............", "............", "............", "............"],
  bar_chart: ["............", "............", "..........kk", "..........kk", ".......gg.kk", ".......gg.kk", "....gg.gg.kk", "....gg.gg.kk", ".ss.gg.gg.kk", ".ss.gg.gg.kk", "SSSSSSSSSSSS", "............"],
  excel_badge: ["............", ".dddddddddd.", ".dddddddddd.", ".dd.x....dd.", ".dd..x..x.d.", ".dd...xx..d.", ".dd..x..x.d.", ".dd.x....dd.", ".dddddddddd.", ".dddddddddd.", "............", "............"],
  pdf_badge: ["............", ".rrrrrrrrr..", ".rrrrrrrrrr.", ".rrxxxxxrrr.", ".rrx.x.xrrr.", ".rrx.x.xrrr.", ".rrx.x.xrrr.", ".rrxxxxxrrr.", ".rrrrrrrrrr.", ".rrrrrrrrr..", "............", "............"],
  clipboard: ["....SSSS....", "...Sssssss..", "..wwwwwwwww.", ".wxxxxxxxxw.", ".wxSS..SSxw.", ".wxSSSSSSxw.", ".wx......xw.", ".wxSS.SSSxw.", ".wx......xw.", ".wxSSS...xw.", ".wxxxxxxxxw.", "..wwwwwwwww."],
  document: ["............", ".xxxxxxxx...", ".xxxxxxxxn..", ".xSSSSSx.n..", ".x......x...", ".xSSSSSx.x..", ".x......x...", ".xSSSSx..x..", ".x......x...", ".xSSSSSx.x..", ".xxxxxxxx...", "............"],
  document_open: ["............", "..xxxx.xxxx.", ".xSSxx.xxSSx", ".xSSxx.xxSSx", ".x..xx.xx..x", ".xSSxx.xxSSx", ".x..xx.xx..x", ".xSSxx.xxSSx", ".x..xx.xx..x", ".xxxxxx xxxx", "............", "............"],
  building: ["............", "...SSSSSS...", "...SxxSxxS..", "...SSSSSS...", "...SxxSxxS..", "...SSSSSS...", "..SSSSSSSS..", "..SkxxSxxk..", "..SSSSSSSS..", "..SkxxSxxk..", "..SSSSSSSS..", "............"],
  gear: ["....s..s....", "..sssssss...", ".s..ssss.s..", "s.sssSSsss.s", "s.sSSSSSs.s.", ".s sSSSSs s.", ".s sSSSSs s.", "s.sSSSSSs.s.", "s.sssSSsss.s", ".s..ssss.s..", "..sssssss...", "....s..s...."],
  book: ["............", "..wwwwwwww..", ".wwkkkkkkww.", ".wxxxxxxxw..", ".wx.SSSS.xw.", ".wx.SSSS.xw.", ".wx.SSSS.xw.", ".wx.SSSS.xw.", ".wxxxxxxxw..", ".wwwwwwwww..", "............", "............"],
  two_charts: ["............", "............", "............", "............", ".........gg.", ".........gg.", "...gg....gg.", "...gg..kkgg.", ".ssgg..kkgg.", ".ssgg..kkgg.", ".SSSS..SSSS.", "............"],
};

interface PixelIconProps {
  size?: number;
  className?: string;
}

function PixelIcon({ grid, size, className }: { grid: string[]; size: number; className?: string }) {
  return (
    <svg
      viewBox="0 0 12 12"
      width={size}
      height={size}
      shapeRendering="crispEdges"
      className={cn("shrink-0", className)}
      aria-hidden="true"
    >
      {grid.flatMap((row, y) =>
        row.split("").map((ch, x) => {
          const color = PALETTE[ch];
          if (!color) return null;
          return <rect key={`${x}-${y}`} x={x} y={y} width={1} height={1} fill={color} />;
        })
      )}
    </svg>
  );
}

export function PixelChest({ size = 24, className }: PixelIconProps) {
  return <PixelIcon grid={GRIDS.chest} size={size} className={className} />;
}

export function PixelKey({ size = 24, className }: PixelIconProps) {
  return <PixelIcon grid={GRIDS.key} size={size} className={className} />;
}

export function PixelShieldCheck({ size = 24, className }: PixelIconProps) {
  return <PixelIcon grid={GRIDS.shield_check} size={size} className={className} />;
}

export function PixelNoEntry({ size = 24, className }: PixelIconProps) {
  return <PixelIcon grid={GRIDS.no_entry} size={size} className={className} />;
}

export function PixelMail({ size = 24, className }: PixelIconProps) {
  return <PixelIcon grid={GRIDS.mail} size={size} className={className} />;
}

export function PixelAnvil({ size = 24, className }: PixelIconProps) {
  return <PixelIcon grid={GRIDS.anvil} size={size} className={className} />;
}

export function PixelPeople({ size = 24, className }: PixelIconProps) {
  return <PixelIcon grid={GRIDS.people} size={size} className={className} />;
}

export function PixelBarChart({ size = 24, className }: PixelIconProps) {
  return <PixelIcon grid={GRIDS.bar_chart} size={size} className={className} />;
}

export function PixelExcelBadge({ size = 24, className }: PixelIconProps) {
  return <PixelIcon grid={GRIDS.excel_badge} size={size} className={className} />;
}

export function PixelPdfBadge({ size = 24, className }: PixelIconProps) {
  return <PixelIcon grid={GRIDS.pdf_badge} size={size} className={className} />;
}

export function PixelClipboard({ size = 24, className }: PixelIconProps) {
  return <PixelIcon grid={GRIDS.clipboard} size={size} className={className} />;
}

export function PixelDocument({ size = 24, className }: PixelIconProps) {
  return <PixelIcon grid={GRIDS.document} size={size} className={className} />;
}

export function PixelDocumentOpen({ size = 24, className }: PixelIconProps) {
  return <PixelIcon grid={GRIDS.document_open} size={size} className={className} />;
}

export function PixelBuilding({ size = 24, className }: PixelIconProps) {
  return <PixelIcon grid={GRIDS.building} size={size} className={className} />;
}

export function PixelGear({ size = 24, className }: PixelIconProps) {
  return <PixelIcon grid={GRIDS.gear} size={size} className={className} />;
}

export function PixelBook({ size = 24, className }: PixelIconProps) {
  return <PixelIcon grid={GRIDS.book} size={size} className={className} />;
}

export function PixelTwoCharts({ size = 24, className }: PixelIconProps) {
  return <PixelIcon grid={GRIDS.two_charts} size={size} className={className} />;
}
