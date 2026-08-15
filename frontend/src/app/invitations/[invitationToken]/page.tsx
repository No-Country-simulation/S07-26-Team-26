"use client";

import { useParams, useRouter } from "next/navigation";
import {
  Clock3,
  Building2,
  Briefcase,
  Megaphone,
  ArrowRight,
} from "lucide-react";
import { PixelKey } from "@/components/blocks/PixelIcons";
import { BrandCube } from "@/components/blocks/BrandCube";
import { Card, CardContent } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Skeleton";
import { useInvitationQuery, useStartEvaluation } from "@/hooks/useInvitation";
import { useInvitationStore } from "@/store/invitationStore";

// Entry point for every Operator: GET /api/v1/invitations/{invitationToken}
// resolves who they are and what they're being asked to evaluate, then
// "Comenzar evaluacion" calls POST /api/v1/evaluations to open the
// evaluation and hands off to the Calculator. No login, no account -- the
// link from the invitation email is the only credential.
export default function InvitationPage() {
  const params = useParams<{ invitationToken: string }>();
  const invitationToken = params.invitationToken;
  const router = useRouter();

  const {
    data: invitation,
    isLoading,
    isError,
  } = useInvitationQuery(invitationToken);
  const startEvaluation = useStartEvaluation();
  const setInvitation = useInvitationStore((s) => s.setInvitation);
  const setEvaluation = useInvitationStore((s) => s.setEvaluation);

  async function handleStart() {
    if (!invitation) return;
    setInvitation(invitation);
    const evaluation = await startEvaluation.mutateAsync({
      invitationToken,
      invitation,
    });
    setEvaluation(evaluation);
    router.push("/operator/calculator");
  }

  return (
    <main className="flex min-h-screen items-center justify-center bg-graphite-50/60 px-6 py-16">
      <div className="w-full max-w-md">
        <div className="mb-10 flex flex-col items-center text-center">
          <BrandCube variant="grass" size={40} className="mb-4" />
          <span className="text-sm font-semibold tracking-wide text-graphite-900">
            PROJECT GHOST LOAD
          </span>
        </div>

        <Card className="p-8">
          <CardContent className="p-0">
            {isLoading && (
              <div className="space-y-4">
                <Skeleton className="mx-auto h-11 w-11 rounded-full" />
                <Skeleton className="mx-auto h-5 w-2/3" />
                <Skeleton className="h-24 w-full" />
                <Skeleton className="h-11 w-full" />
              </div>
            )}

            {isError && (
              <div className="flex flex-col items-center gap-3 text-center">
                <h1 className="text-lg font-semibold text-graphite-900">
                  Invitación no encontrada
                </h1>
                <p className="text-sm text-graphite-500">
                  Este enlace ya no es válido o expiró. Solicita una nueva
                  invitación por correo electrónico.
                </p>
              </div>
            )}

            {invitation && (
              <>
                <div className="mb-6 flex flex-col items-center text-center">
                  <div className="mb-4 flex h-11 w-11 items-center justify-center rounded-full bg-forest-50 text-forest-700">
                    <PixelKey size={24} />
                  </div>
                  <p className="mb-1.5 text-xs font-semibold uppercase tracking-wide text-gold-700">
                    Invitación de Evaluación
                  </p>
                  <h1 className="text-2xl font-semibold tracking-tight text-graphite-900">
                    Hola, {invitation.operatorName}
                  </h1>
                  <p className="mt-2 text-sm text-graphite-500">
                    Has sido invitado a completar el benchmark de
                    infraestructura de Project Ghost Load.
                  </p>
                </div>

                <div className="space-y-3 rounded-md border border-graphite-100 bg-graphite-50/50 px-5 py-4">
                  <DetailRow
                    icon={Building2}
                    label="Empresa"
                    value={invitation.companyName}
                  />
                  <DetailRow
                    icon={Briefcase}
                    label="Cargo"
                    value={invitation.role}
                  />
                  <DetailRow
                    icon={Megaphone}
                    label="Campaña"
                    value={invitation.campaignName}
                  />
                  <DetailRow
                    icon={Clock3}
                    label="Tiempo estimado"
                    value={`${invitation.estimatedMinutes} minutos`}
                  />
                </div>

                <Button
                  className="mt-6 w-full"
                  size="lg"
                  onClick={handleStart}
                  loading={startEvaluation.isPending}
                >
                  Comenzar evaluación
                  <ArrowRight className="h-4 w-4" />
                </Button>
              </>
            )}
          </CardContent>
        </Card>
      </div>
    </main>
  );
}

function DetailRow({
  icon: Icon,
  label,
  value,
}: {
  icon: React.ComponentType<{ className?: string }>;
  label: string;
  value: string;
}) {
  return (
    <div className="flex items-center justify-between gap-3">
      <span className="flex items-center gap-1.5 text-xs text-graphite-500">
        <Icon className="h-3.5 w-3.5" />
        {label}
      </span>
      <span className="text-sm font-medium text-graphite-900">{value}</span>
    </div>
  );
}
