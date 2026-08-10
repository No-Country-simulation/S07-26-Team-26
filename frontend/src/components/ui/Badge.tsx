import { HTMLAttributes } from "react";
import { cn } from "@/lib/utils";

interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  tone?: "neutral" | "success" | "warning" | "danger" | "gold" | "info";
}

const toneClasses: Record<NonNullable<BadgeProps["tone"]>, string> = {
  neutral: "bg-graphite-100 text-graphite-700 border-graphite-200",
  success: "bg-forest-50 text-forest-700 border-forest-100",
  warning: "bg-orange-50 text-orange-700 border-orange-200",
  danger: "bg-red-50 text-red-700 border-red-200",
  gold: "bg-gold-50 text-gold-700 border-gold-100",
  info: "bg-blue-50 text-blue-700 border-blue-200",
};

export function Badge({ className, tone = "neutral", ...props }: BadgeProps) {
  return (
    <span
      className={cn(
        "inline-flex items-center rounded-sm border px-2 py-0.5 text-xs font-medium",
        toneClasses[tone],
        className
      )}
      {...props}
    />
  );
}
