import { InputHTMLAttributes, forwardRef } from "react";
import { cn } from "@/lib/utils";

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  error?: string;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(({ className, error, ...props }, ref) => {
  return (
    <input
      ref={ref}
      className={cn(
        "w-full rounded-md border bg-white px-3 py-2 text-sm text-graphite-900 placeholder:text-graphite-400",
        "focus:outline-none focus:ring-2 focus:ring-forest-700/40 focus:border-forest-700",
        error ? "border-red-400" : "border-graphite-200",
        className
      )}
      {...props}
    />
  );
});
Input.displayName = "Input";
