"use client";

import { PixelMail } from "@/components/blocks/PixelIcons";
import { useInvitationStore } from "@/store/invitationStore";

// Operator equivalent of RouteGuard, but there's nothing to log in to: an
// Operator is "authenticated" simply by holding a started evaluation
// (evaluationId/evaluationToken), which only exists after they've come
// through /invitation/{invitationToken} and pressed "Comenzar evaluación".
// If someone opens an /operator/* URL directly without that, there's no
// login screen to bounce them to -- we just explain they need their
// invitation link.
export function InvitationGuard({ children }: { children: React.ReactNode }) {
  const evaluation = useInvitationStore((s) => s.evaluation);

  if (!evaluation) {
    return (
      <div className="flex min-h-[60vh] flex-col items-center justify-center gap-3 px-6 text-center">
        <PixelMail size={32} />
        <h1 className="text-lg font-semibold text-graphite-900">Se necesita una invitación válida</h1>
        <p className="max-w-sm text-sm text-graphite-500">
          Para comenzar tu evaluación, abre el enlace único que recibiste por correo electrónico.
        </p>
      </div>
    );
  }

  return <>{children}</>;
}
