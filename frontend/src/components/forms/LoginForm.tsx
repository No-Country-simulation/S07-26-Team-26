"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { Mail, Lock, ArrowRight } from "lucide-react";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";
import { Button } from "@/components/ui/Button";
import { useAuthStore } from "@/store/authStore";
import { fetchOperatorByEmail, fetchCompanyById } from "@/services/api";

const schema = z.object({
  email: z.string().email("Ingresa un correo válido"),
  password: z.string().min(6, "La contraseña debe tener al menos 6 caracteres"),
});

type FormValues = z.infer<typeof schema>;

function organizationFromEmail(email: string) {
  const domain = email.split("@")[1] ?? "";
  const base = domain.split(".")[0] ?? "Organización";
  return base.charAt(0).toUpperCase() + base.slice(1);
}

// --------------------------------------------------------------------------
// Operator-only login. Looks the email up against mock/operators.json; if
// it's not there yet, treats it as a brand-new operator (same fallback the
// old OperatorLoginForm used) so the demo flow keeps working for any email.
// No admin/root-admin lookup or routing here -- this screen is scoped to
// the operator flow only.
//
// Future Clerk integration: replace fetchOperatorByEmail with
// `useSignIn().signIn.create({ identifier, password })`.
// --------------------------------------------------------------------------
export function LoginForm() {
  const router = useRouter();
  const setSession = useAuthStore((s) => s.setSession);
  const [formError, setFormError] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormValues>({ resolver: zodResolver(schema) });

  async function onSubmit(values: FormValues) {
    setFormError(null);
    try {
      const operator = await fetchOperatorByEmail(values.email);
      if (operator) {
        const company = await fetchCompanyById(operator.companyId);
        setSession({
          role: "OPERATOR",
          email: values.email,
          name: operator.name,
          organization: company?.name ?? organizationFromEmail(values.email),
          companyId: company?.id,
        });
        router.push("/operator/profile");
        return;
      }

      setSession({
        role: "OPERATOR",
        email: values.email,
        name: values.email.split("@")[0].replace(/\W+/g, " "),
        organization: organizationFromEmail(values.email),
      });
      router.push("/operator/profile");
    } catch {
      setFormError("No pudimos iniciar sesión con esas credenciales.");
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
      <div>
        <Label htmlFor="login-email">Correo electrónico</Label>
        <div className="relative">
          <Mail className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-graphite-400" />
          <Input
            id="login-email"
            type="email"
            placeholder="tu@empresa.com"
            className="pl-9"
            {...register("email")}
          />
        </div>
        {errors.email && <p className="mt-1 text-xs text-red-600">{errors.email.message}</p>}
      </div>

      <div>
        <div className="mb-1.5 flex items-center justify-between">
          <Label htmlFor="login-password" className="mb-0">
            Contraseña
          </Label>
          <a href="#" className="text-xs font-medium text-forest-700 hover:text-forest-800">
            ¿Olvidaste tu contraseña?
          </a>
        </div>
        <div className="relative">
          <Lock className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-graphite-400" />
          <Input
            id="login-password"
            type="password"
            placeholder="••••••••"
            className="pl-9"
            {...register("password")}
          />
        </div>
        {errors.password && <p className="mt-1 text-xs text-red-600">{errors.password.message}</p>}
      </div>

      {formError && <p className="text-xs text-red-600">{formError}</p>}

      <Button type="submit" className="w-full" size="lg" loading={isSubmitting}>
        Iniciar sesión
        <ArrowRight className="h-4 w-4" />
      </Button>

      <p className="text-center text-[11px] leading-relaxed text-graphite-400">
        Demo: <span className="font-medium text-graphite-500">operator@aetheris.com</span>. Cualquier contraseña
        funciona.
      </p>
    </form>
  );
}
