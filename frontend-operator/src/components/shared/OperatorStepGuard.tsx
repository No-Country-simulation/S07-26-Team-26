"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useOperatorProgress, type OperatorStepId } from "@/hooks/useOperatorProgress";

// Defense in depth for the nav lock in OperatorTopbar: if an operator opens
// a step's URL directly (bookmark, back button, typed URL) before its
// prerequisite is done, send them back to the furthest step they've
// actually unlocked instead of letting the page render.
export function OperatorStepGuard({ step, children }: { step: OperatorStepId; children: React.ReactNode }) {
  const router = useRouter();
  const steps = useOperatorProgress();
  const current = steps.find((s) => s.id === step);
  const isUnlocked = current?.unlocked ?? true;

  useEffect(() => {
    if (!isUnlocked) {
      const furthest = [...steps].reverse().find((s) => s.unlocked) ?? steps[0];
      router.replace(furthest.href);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isUnlocked]);

  if (!isUnlocked) {
    return (
      <div className="flex h-64 items-center justify-center text-sm text-graphite-400">
        Completa el paso anterior primero…
      </div>
    );
  }

  return <>{children}</>;
}
