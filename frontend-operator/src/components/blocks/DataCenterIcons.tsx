// ---------------------------------------------------------------------------
// "Datacenter verde" icon set -- 18 hand-illustrated flat/pixel badges
// (bevel outline + drop shadow) commissioned for this product, in the same
// forest-green family as the --color-forest-* tokens in globals.css.
//
// Source assets came in at inconsistent canvases (176x208..208x224) with
// uneven padding, which made them align poorly next to each other. Before
// landing here, every icon was re-cropped to its content box and re-centered
// on a shared 256x256 canvas at a common content scale, so the whole set
// now reads at the same visual size and optical weight -- see
// public/icons/datacenter/*.png.
//
// These are raster (not the hand-authored 12x12 SVG grids in PixelIcons.tsx)
// and carry real shading + a soft drop shadow, so detail gets muddy below
// ~32px -- that's the floor. Use PixelIcons for anything under that (nav
// rails, inline badges); reach for these from 32px up: KPI badges, section
// headers, empty states, feature callouts.
// ---------------------------------------------------------------------------
import Image from "next/image";
import { cn } from "@/lib/utils";

export const DATACENTER_ICON_NAMES = [
  "dashboard",
  "reportes",
  "usuarios",
  "empresas",
  "perfil",
  "cerrar-sesion",
  "notificacion",
  "busqueda",
  "trofeo",
  "check",
  "porcentaje",
  "servidor",
  "radar-chart",
  "comparativo",
  "editar",
  "eliminar",
  "agregar",
  "candado",
] as const;

export type DataCenterIconName = (typeof DATACENTER_ICON_NAMES)[number];

interface DataCenterIconProps {
  name: DataCenterIconName;
  size?: number;
  className?: string;
}

/** Minimum size these render legibly at -- shading/shadow turn to mush below it. */
export const DATACENTER_ICON_MIN_SIZE = 32;

export function DataCenterIcon({ name, size = 40, className }: DataCenterIconProps) {
  return (
    <Image
      src={`/icons/datacenter/${name}.png`}
      alt=""
      width={256}
      height={256}
      unoptimized
      className={cn("shrink-0 select-none", className)}
      style={{ width: size, height: size }}
      aria-hidden="true"
    />
  );
}

// Convenience wrappers, same call shape as the PixelIcons exports.
export const IconDashboard = (p: Omit<DataCenterIconProps, "name">) => <DataCenterIcon name="dashboard" {...p} />;
export const IconReportes = (p: Omit<DataCenterIconProps, "name">) => <DataCenterIcon name="reportes" {...p} />;
export const IconUsuarios = (p: Omit<DataCenterIconProps, "name">) => <DataCenterIcon name="usuarios" {...p} />;
export const IconEmpresas = (p: Omit<DataCenterIconProps, "name">) => <DataCenterIcon name="empresas" {...p} />;
export const IconPerfil = (p: Omit<DataCenterIconProps, "name">) => <DataCenterIcon name="perfil" {...p} />;
export const IconCerrarSesion = (p: Omit<DataCenterIconProps, "name">) => <DataCenterIcon name="cerrar-sesion" {...p} />;
export const IconNotificacion = (p: Omit<DataCenterIconProps, "name">) => <DataCenterIcon name="notificacion" {...p} />;
export const IconBusqueda = (p: Omit<DataCenterIconProps, "name">) => <DataCenterIcon name="busqueda" {...p} />;
export const IconTrofeo = (p: Omit<DataCenterIconProps, "name">) => <DataCenterIcon name="trofeo" {...p} />;
export const IconCheck = (p: Omit<DataCenterIconProps, "name">) => <DataCenterIcon name="check" {...p} />;
export const IconPorcentaje = (p: Omit<DataCenterIconProps, "name">) => <DataCenterIcon name="porcentaje" {...p} />;
export const IconServidor = (p: Omit<DataCenterIconProps, "name">) => <DataCenterIcon name="servidor" {...p} />;
export const IconRadarChart = (p: Omit<DataCenterIconProps, "name">) => <DataCenterIcon name="radar-chart" {...p} />;
export const IconComparativo = (p: Omit<DataCenterIconProps, "name">) => <DataCenterIcon name="comparativo" {...p} />;
export const IconEditar = (p: Omit<DataCenterIconProps, "name">) => <DataCenterIcon name="editar" {...p} />;
export const IconEliminar = (p: Omit<DataCenterIconProps, "name">) => <DataCenterIcon name="eliminar" {...p} />;
export const IconAgregar = (p: Omit<DataCenterIconProps, "name">) => <DataCenterIcon name="agregar" {...p} />;
export const IconCandado = (p: Omit<DataCenterIconProps, "name">) => <DataCenterIcon name="candado" {...p} />;
