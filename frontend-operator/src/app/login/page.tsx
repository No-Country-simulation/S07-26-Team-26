import { LoginForm } from "@/components/forms/LoginForm";
import { BrandCube } from "@/components/blocks/BrandCube";
import { DataCenterIcon } from "@/components/blocks/DataCenterIcons";

// Single centered column -- no split hero panel. Operators arrive here by
// invitation (not by browsing a marketing site first), so there's no
// public landing page anymore either. Scoped to the operator flow only --
// no admin/root-admin path on this screen.
export default function LoginPage() {
  return (
    <main className="flex min-h-screen items-center justify-center bg-graphite-50/60 px-6 py-16">
      <div className="w-full max-w-sm">
        <div className="mb-10 flex flex-col items-center text-center">
          <BrandCube variant="grass" size={40} className="mb-4" />
          <span className="text-sm font-semibold tracking-wide text-graphite-900">PROJECT GHOST LOAD</span>
        </div>

        <div className="rounded-lg border border-graphite-100 bg-white p-8 shadow-card">
          <div className="mb-4 flex justify-center">
            <DataCenterIcon name="candado" size={44} />
          </div>
          <p className="mb-1.5 text-center text-xs font-semibold uppercase tracking-wide text-gold-700">Acceso</p>
          <h1 className="mb-2 text-center text-2xl font-semibold tracking-tight text-graphite-900">
            Inicia sesión
          </h1>
          <p className="mb-8 text-center text-sm text-graphite-500">
            Inicia sesión con tu correo y contraseña de operador para continuar con tu evaluación.
          </p>

          <LoginForm />
        </div>
      </div>
    </main>
  );
}
