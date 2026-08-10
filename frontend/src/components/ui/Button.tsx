import { ButtonHTMLAttributes, forwardRef } from "react";
import { cn } from "@/lib/utils";
import { Loader2 } from "lucide-react";

type Variant = "primary" | "secondary" | "ghost" | "danger" | "gold" | "info";
type Size = "sm" | "md" | "lg";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant;
  size?: Size;
  loading?: boolean;
}

const variantClasses: Record<Variant, string> = {
  primary:
    "bg-forest-700 text-white hover:bg-forest-800 focus-visible:ring-forest-700 disabled:bg-graphite-200 shadow-sm",
  secondary:
    "bg-white text-forest-800 border border-graphite-200 hover:border-forest-700/40 hover:bg-forest-50/60 focus-visible:ring-forest-700 shadow-sm",
  ghost: "bg-transparent text-graphite-600 hover:bg-graphite-100",
  danger: "bg-red-700 text-white hover:bg-red-800 shadow-sm",
  gold: "bg-gold-500 text-forest-950 hover:bg-gold-600 shadow-sm font-semibold",
  info: "bg-blue-600 text-white hover:bg-blue-700 shadow-sm",
};

const sizeClasses: Record<Size, string> = {
  sm: "text-xs px-3 py-1.5 gap-1.5",
  md: "text-sm px-4 py-2.5 gap-2",
  lg: "text-[15px] px-6 py-3 gap-2",
};

export const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  ({ className, variant = "primary", size = "md", loading, children, disabled, ...props }, ref) => {
    return (
      <button
        ref={ref}
        disabled={disabled || loading}
        className={cn(
          "inline-flex items-center justify-center rounded-md font-medium transition-all duration-150",
          "active:translate-y-px active:shadow-none",
          "disabled:cursor-not-allowed disabled:opacity-60 disabled:shadow-none disabled:active:translate-y-0",
          "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2",
          variantClasses[variant],
          sizeClasses[size],
          className
        )}
        {...props}
      >
        {loading && <Loader2 className="h-3.5 w-3.5 animate-spin" />}
        {children}
      </button>
    );
  }
);
Button.displayName = "Button";
