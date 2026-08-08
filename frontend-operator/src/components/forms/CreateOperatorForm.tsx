"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useState } from "react";
import { CheckCircle2 } from "lucide-react";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";
import { Button } from "@/components/ui/Button";
import { AvatarPicker, type AvatarVariant } from "@/components/forms/AvatarPicker";
import { useCreateOperator } from "@/hooks/useOperators";
import type { Company } from "@/services/api";

const schema = z.object({
  name: z.string().min(2, "Requerido"),
  lastName: z.string().min(2, "Requerido"),
  email: z.string().email("Ingresa un correo corporativo válido"),
  companyId: z.string().min(1, "Selecciona una empresa"),
  status: z.enum(["Active", "Invited"]),
});

type FormValues = z.infer<typeof schema>;

export function CreateOperatorForm({ companies }: { companies: Company[] }) {
  const createOperator = useCreateOperator();
  const [avatar, setAvatar] = useState<AvatarVariant>("grass");
  const [justCreated, setJustCreated] = useState(false);
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormValues>({ resolver: zodResolver(schema), defaultValues: { status: "Invited" } });

  async function onSubmit(values: FormValues) {
    await createOperator.mutateAsync({ ...values, avatar });
    reset({ name: "", lastName: "", email: "", companyId: "", status: "Invited" });
    setAvatar("grass");
    setJustCreated(true);
    setTimeout(() => setJustCreated(false), 3000);
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div>
          <Label htmlFor="op-name">Nombre</Label>
          <Input id="op-name" placeholder="Sam" {...register("name")} />
          {errors.name && <p className="mt-1 text-xs text-red-600">{errors.name.message}</p>}
        </div>
        <div>
          <Label htmlFor="op-lastname">Apellido</Label>
          <Input id="op-lastname" placeholder="Rivera" {...register("lastName")} />
          {errors.lastName && <p className="mt-1 text-xs text-red-600">{errors.lastName.message}</p>}
        </div>
        <div>
          <Label htmlFor="op-email">Correo corporativo</Label>
          <Input id="op-email" type="email" placeholder="sam.rivera@empresa.com" {...register("email")} />
          {errors.email && <p className="mt-1 text-xs text-red-600">{errors.email.message}</p>}
        </div>
        <div>
          <Label htmlFor="op-company">Empresa</Label>
          <select
            id="op-company"
            className="w-full rounded-md border border-graphite-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-forest-700/40"
            {...register("companyId")}
          >
            <option value="">Selecciona una empresa</option>
            {companies.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </select>
          {errors.companyId && <p className="mt-1 text-xs text-red-600">{errors.companyId.message}</p>}
        </div>
        <div>
          <Label htmlFor="op-status">Estado inicial</Label>
          <select
            id="op-status"
            className="w-full rounded-md border border-graphite-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-forest-700/40"
            {...register("status")}
          >
            <option value="Invited">Invitado</option>
            <option value="Active">Activo</option>
          </select>
        </div>
      </div>

      <AvatarPicker value={avatar} onChange={setAvatar} />

      <div className="flex items-center gap-3">
        <Button type="submit" loading={createOperator.isPending} disabled={companies.length === 0}>
          Guardar Operador
        </Button>
        {justCreated && (
          <span className="flex items-center gap-1.5 text-sm font-medium text-forest-700">
            <CheckCircle2 className="h-4 w-4" />
            Operador creado
          </span>
        )}
      </div>
    </form>
  );
}
