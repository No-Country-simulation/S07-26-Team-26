import { HTMLAttributes } from "react";
import { cn } from "@/lib/utils";

interface CardProps extends HTMLAttributes<HTMLDivElement> {
  /** Adds a hover lift + shadow transition for clickable cards. */
  interactive?: boolean;
  /** Very light stone-speckle texture -- use sparingly (hero panels, sidebars), never on dense data cards. */
  textured?: boolean;
}

export function Card({ className, interactive, textured, ...props }: CardProps) {
  return (
    <div
      className={cn(
        "relative rounded-lg border border-graphite-100 bg-white shadow-card transition-shadow duration-200",
        interactive && "cursor-pointer hover:shadow-elevated hover:-translate-y-0.5 transition-transform",
        textured && "stone-texture",
        className
      )}
      {...props}
    />
  );
}

export function CardHeader({ className, ...props }: HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={cn("flex items-center justify-between px-6 py-5 border-b border-graphite-100/80", className)}
      {...props}
    />
  );
}

export function CardTitle({ className, ...props }: HTMLAttributes<HTMLHeadingElement>) {
  return <h3 className={cn("text-[15px] font-semibold text-graphite-900 tracking-tight", className)} {...props} />;
}

export function CardContent({ className, ...props }: HTMLAttributes<HTMLDivElement>) {
  return <div className={cn("px-6 py-5", className)} {...props} />;
}
