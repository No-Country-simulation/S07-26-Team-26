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
import { PixelKey } from "@/components/blocks/PixelIcons";
import { useCreateAdmin } from "@/hooks/useAdmins";

const schema = z.object({
  name: z.string().min(2, "Requerido"),
  lastName: z.string().min(2, "Requerido"),
  email: z.string().email("Ingresa un correo de trabajo válido"),
  role: z.enum(["ADMIN", "ROOT_ADMIN"]),
  status: z.enum(["Active", "Invited"]),
});

type FormValues = z.infer<typeof schema>;

export function CreateAdminForm({ onCreated }: { onCreated?: () => void }) {
  const createAdmin = useCreateAdmin();
  const [avatar, setAvatar] = useState<AvatarVariant>("grass");
  const [justCreated, setJustCreated] = useState(false);
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { role: "ADMIN", status: "Invited" },
  });

  async function onSubmit(values: FormValues) {
    await createAdmin.mutateAsync({ ...values, avatar });
    reset({ name: "", lastName: "", email: "", role: "ADMIN", status: "Invited" });
    setAvatar("grass");
    setJustCreated(true);
    setTimeout(() => setJustCreated(false), 3000);
    onCreated?.();
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div>
          <Label htmlFor="admin-name">Nombre</Label>
          <Input id="admin-name" placeholder="Jordan" {...register("name")} />
          {errors.name && <p className="mt-1 text-xs text-red-600">{errors.name.message}</p>}
        </div>
        <div>
          <Label htmlFor="admin-lastname">Apellido</Label>
          <Input id="admin-lastname" placeholder="Lee" {...register("lastName")} />
          {errors.lastName && <p className="mt-1 text-xs text-red-600">{errors.lastName.message}</p>}
        </div>
        <div>
          <Label htmlFor="admin-new-email">Correo de trabajo</Label>
          <Input id="admin-new-email" type="email" placeholder="jordan.lee@ghostload.com" {...register("email")} />
          {errors.email && <p className="mt-1 text-xs text-red-600">{errors.email.message}</p>}
        </div>
        <div>
          <Label htmlFor="admin-role" className="flex items-center gap-1.5">
            <PixelKey size={14} />
            Rol
          </Label>
          <select
            id="admin-role"
            className="w-full rounded-md border border-graphite-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-forest-700/40"
            {...register("role")}
          >
            <option value="ADMIN">Admin (con alcance)</option>
            <option value="ROOT_ADMIN">Root Admin (acceso total)</option>
          </select>
        </div>
        <div>
          <Label htmlFor="admin-status">Estado inicial</Label>
          <select
            id="admin-status"
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
        <Button type="submit" loading={createAdmin.isPending}>
          Guardar Administrador
        </Button>
        {justCreated && (
          <span className="flex items-center gap-1.5 text-sm font-medium text-forest-700">
            <CheckCircle2 className="h-4 w-4" />
            Administrador creado
          </span>
        )}
      </div>
    </form>
  );
}
