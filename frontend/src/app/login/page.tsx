"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/authStore";
import Link from "next/link";

const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

export default function AdminLoginPage() {
  const router = useRouter();
  const login = useAuthStore((state) => state.login);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError("");

    try {
      const response = await fetch(`${API_URL}/api/v1/admin/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: email.trim(), password }),
      });

      const body = await response.json().catch(() => null);
      if (!response.ok) {
        const message =
          body?.message || body?.error || "Email o contraseña incorrectos.";
        setError(String(message));
        return;
      }

      const accessToken = body?.accessToken;
      if (!accessToken) {
        setError("No se recibió token de autenticación.");
        return;
      }

      login(accessToken);
      router.push("/dashboard/admin");
    } catch (exception) {
      setError("Error de conexión con el backend.");
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 px-4 py-12 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-md rounded-[2.5rem] border border-slate-200 bg-white p-8 shadow-sm shadow-slate-300">
        <div className="mb-8 text-center">
          <p className="text-sm uppercase tracking-[0.3em] text-emerald-700">
            Acceso administrador
          </p>
          <h1 className="mt-4 text-3xl font-semibold text-slate-950">
            Iniciar sesión
          </h1>
          <p className="mt-2 text-sm text-slate-600">
            Usa el email y contraseña de administrador para continuar.
          </p>
        </div>

        <form className="space-y-6" onSubmit={handleSubmit}>
          <div>
            <label className="block text-sm font-medium text-slate-700">
              Email
            </label>
            <input
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
              placeholder="admin@ghostload.local"
              className="mt-2 w-full rounded-3xl border border-slate-200 bg-slate-50 px-4 py-3 text-slate-950 placeholder:text-slate-400 focus:border-emerald-500 focus:outline-none focus:ring-2 focus:ring-emerald-500/20"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700">
              Contraseña
            </label>
            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
              placeholder="GhostLoad2026!"
              className="mt-2 w-full rounded-3xl border border-slate-200 bg-slate-50 px-4 py-3 text-slate-950 placeholder:text-slate-400 focus:border-emerald-500 focus:outline-none focus:ring-2 focus:ring-emerald-500/20"
            />
          </div>

          {error && <p className="text-sm text-rose-500">{error}</p>}

          <button
            type="submit"
            className="w-full rounded-3xl bg-emerald-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-emerald-500"
          >
            Entrar al panel
          </button>
        </form>

        <div className="mt-8 text-center text-sm text-slate-600">
          <p>
            Usuario demo:{" "}
            <span className="font-semibold text-slate-950">
              admin@ghostload.local
            </span>
          </p>
          <p>
            Contraseña:{" "}
            <span className="font-semibold text-slate-950">GhostLoad2026!</span>
          </p>
          <p className="mt-4">
            <Link href="/" className="text-emerald-600 hover:text-emerald-500">
              Volver a la página principal
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
