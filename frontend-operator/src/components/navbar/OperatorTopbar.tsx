"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { Lock, Check } from "lucide-react";
import { cn } from "@/lib/utils";
import { useInvitationStore } from "@/store/invitationStore";
import { BrandCube } from "@/components/blocks/BrandCube";
import { useOperatorProgress } from "@/hooks/useOperatorProgress";

export function OperatorTopbar() {
  const pathname = usePathname();
  const invitation = useInvitationStore((s) => s.invitation);
  const steps = useOperatorProgress();

  return (
    <header className="border-b border-graphite-100 bg-white/90 backdrop-blur-sm print:hidden">
      <div className="flex items-center justify-between px-8 py-4">
        <div className="flex items-center gap-2.5">
          <BrandCube variant="grass" size={30} />
          <div>
            <p className="text-[14px] font-semibold leading-tight text-graphite-900">Project Ghost Load</p>
            <p className="text-[11px] leading-tight text-graphite-400">{invitation?.companyName ?? "Operador"}</p>
          </div>
        </div>
      </div>

      {/* Step nav: the Operator role is "completar calculadora -> responder
          benchmark -> consultar resultados", strictly in order. Steps ahead
          of the operator's real progress are shown locked (not a Link) so
          there's no way to skip ahead from the nav itself. */}
      <nav className="flex gap-1.5 px-8 pb-4">
        {steps.map((step, i) => {
          const active = pathname === step.href;

          if (!step.unlocked) {
            return (
              <span
                key={step.href}
                aria-disabled="true"
                title="Completa el paso anterior primero"
                className="flex cursor-not-allowed items-center gap-1.5 rounded-full bg-graphite-50 px-3.5 py-1.5 text-xs font-medium text-graphite-300"
              >
                <Lock className="h-3 w-3" />
                {step.label}
              </span>
            );
          }

          return (
            <Link
              key={step.href}
              href={step.href}
              className={cn(
                "flex items-center gap-1.5 rounded-full px-3.5 py-1.5 text-xs font-medium transition-colors",
                active
                  ? "bg-forest-700 text-white shadow-sm"
                  : "bg-graphite-50 text-graphite-500 hover:bg-graphite-100"
              )}
            >
              {step.complete && !active ? (
                <Check className="h-3 w-3 text-forest-600" />
              ) : (
                <span className="font-tabular">{i + 1}</span>
              )}
              {step.label}
            </Link>
          );
        })}
      </nav>
    </header>
  );
}
