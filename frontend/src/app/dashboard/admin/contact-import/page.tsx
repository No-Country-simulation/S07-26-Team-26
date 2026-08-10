"use client";

import { FormEvent, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { useAuthStore } from "@/store/authStore";

const API_URL = "https://ghost-load-api.trinitylabs.app";

interface ImportStats {
  totalRows: number;
  validContacts: number;
  newContacts: number;
  existingContacts: number;
  duplicates: number;
  invalidRows: number;
}

export default function AdminContactImportPage() {
  const router = useRouter();
  const { isAuthenticated, accessToken } = useAuthStore();
  const [hasMounted, setHasMounted] = useState(false);
  const [name, setName] = useState("Importación de contactos");
  const [file, setFile] = useState<File | null>(null);
  const [status, setStatus] = useState<
    "idle" | "submitting" | "success" | "error"
  >("idle");
  const [message, setMessage] = useState("");
  const [importId, setImportId] = useState("");
  const [stats, setStats] = useState<ImportStats | null>(null);

  useEffect(() => {
    setHasMounted(true);
  }, []);

  useEffect(() => {
    if (!isAuthenticated) {
      router.replace("/login");
    }
  }, [isAuthenticated, router]);

  if (!hasMounted || !isAuthenticated) {
    return null;
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!file) {
      setMessage("Selecciona un archivo CSV antes de continuar.");
      setStatus("error");
      return;
    }

    if (!accessToken) {
      setStatus("error");
      setMessage("No se encontró token de sesión. Inicia sesión de nuevo.");
      return;
    }

    setStatus("submitting");
    setMessage("");
    setImportId("");
    setStats(null);

    const formData = new FormData();
    formData.append("name", name);
    formData.append("file", file);

    try {
      const result = await fetch(`${API_URL}/api/v1/admin/contact-imports`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
        body: formData,
      });

      if (!result.ok) {
        const payload = await result.json().catch(() => null);
        const errorDetail =
          payload?.message || payload?.error || result.statusText;
        throw new Error(String(errorDetail || "Error al importar el archivo."));
      }

      if (!result.ok) {
        if (result.status === 401 || result.status === 403) {
          throw new Error(
            "Se requiere autenticación administrativa. Por favor inicia sesión de nuevo.",
          );
        }
        const payload = await result.json().catch(() => null);
        const errorDetail =
          payload?.message || payload?.error || result.statusText;
        throw new Error(String(errorDetail || "Error al importar el archivo."));
      }

      const payload = await result.json();
      setImportId(payload.importId || payload.importId);
      setStats({
        totalRows: payload.totalRows,
        validContacts: payload.validContacts,
        newContacts: payload.newContacts,
        existingContacts: payload.existingContacts,
        duplicates: payload.duplicates,
        invalidRows: payload.invalidRows,
      });
      setStatus("success");
      setMessage(
        "Importación completada correctamente. Usa el importId para crear la campaña.",
      );
    } catch (error) {
      setStatus("error");
      setMessage(
        error instanceof Error
          ? error.message
          : "Error desconocido al importar.",
      );
    }
  };

  return (
    <div className="min-h-[calc(100vh-2rem)] bg-slate-50 px-4 py-6 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-5xl rounded-[2.5rem] border border-slate-200 bg-white p-8 shadow-sm shadow-slate-300">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <p className="text-sm uppercase tracking-[0.3em] text-emerald-700">
              Importación CSV
            </p>
            <h1 className="mt-3 text-4xl font-semibold text-slate-950">
              Importar contactos
            </h1>
            <p className="mt-2 text-slate-600">
              Sube un CSV con tus contactos para generar el `contactImportId`
              que usará la campaña.
            </p>
          </div>
          <Link
            href="/dashboard/admin"
            className="inline-flex items-center justify-center rounded-3xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm font-semibold text-emerald-700 transition hover:bg-emerald-100"
          >
            Volver al dashboard
          </Link>
        </div>

        <div className="mt-10 grid gap-6 lg:grid-cols-[1fr_0.9fr]">
          <div className="rounded-[2rem] border border-slate-200 bg-slate-50 p-6">
            <h2 className="text-xl font-semibold text-slate-950">
              Formato esperado
            </h2>
            <p className="mt-3 text-slate-600">
              El backend espera un archivo CSV con estas columnas exactas:
            </p>
            <div className="mt-4 rounded-[1.5rem] bg-white p-4 text-sm text-slate-700 shadow-sm shadow-slate-200">
              <pre className="whitespace-pre-wrap">
                first_name,last_name,email,company,position
                Juan,Pérez,juan.perez@empresa.com,Acme,Gerente
                Ana,Gómez,ana.gomez@empresa.com,Beta S.A.,Directora
              </pre>
            </div>
            <p className="mt-4 text-slate-600">Reglas principales:</p>
            <ul className="mt-3 list-disc space-y-2 pl-5 text-slate-600">
              <li>
                Encabezados exactos: <code>first_name</code>,{" "}
                <code>last_name</code>, <code>email</code>, <code>company</code>
                , <code>position</code>.
              </li>
              <li>El email debe ser válido.</li>
              <li>
                <code>first_name</code> y <code>last_name</code> son
                obligatorios.
              </li>
              <li>
                <code>company</code> es obligatorio.
              </li>
              <li>
                <code>position</code> es opcional.
              </li>
              <li>
                El archivo debe tener extensión <code>.csv</code> y estar en
                UTF-8.
              </li>
            </ul>
          </div>

          <div className="rounded-[2rem] border border-slate-200 bg-white p-6 shadow-sm shadow-slate-200">
            <form className="space-y-6" onSubmit={handleSubmit}>
              <label className="block">
                <span className="text-sm font-semibold text-slate-700">
                  Nombre de la importación
                </span>
                <input
                  value={name}
                  onChange={(event) => setName(event.target.value)}
                  required
                  className="mt-2 w-full rounded-3xl border border-slate-200 bg-slate-50 px-4 py-3 text-slate-950 placeholder:text-slate-400 focus:border-emerald-500 focus:outline-none focus:ring-2 focus:ring-emerald-500/20"
                />
              </label>

              <label className="block">
                <span className="text-sm font-semibold text-slate-700">
                  Archivo CSV
                </span>
                <input
                  type="file"
                  accept=".csv"
                  onChange={(event) => setFile(event.target.files?.[0] ?? null)}
                  className="mt-2 w-full text-sm text-slate-700 file:mr-4 file:rounded-full file:border-0 file:bg-emerald-600 file:px-4 file:py-2 file:text-sm file:font-semibold file:text-white"
                />
              </label>

              <button
                type="submit"
                disabled={status === "submitting"}
                className="inline-flex w-full justify-center rounded-3xl bg-emerald-600 px-6 py-3 text-sm font-semibold text-white transition hover:bg-emerald-500 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {status === "submitting" ? "Subiendo CSV..." : "Importar CSV"}
              </button>
            </form>

            {message && (
              <div
                className={`mt-6 rounded-3xl border p-4 text-sm ${status === "success" ? "border-emerald-500/20 bg-emerald-500/10 text-emerald-800" : "border-rose-500/20 bg-rose-500/10 text-rose-800"}`}
              >
                {message}
              </div>
            )}

            {status === "success" && importId && (
              <div className="mt-6 space-y-4 rounded-[1.75rem] border border-slate-200 bg-slate-50 p-6 shadow-sm shadow-slate-200">
                <div>
                  <p className="text-sm font-semibold text-slate-700">
                    Import ID
                  </p>
                  <p className="mt-2 break-all text-slate-950">{importId}</p>
                </div>
                {stats && (
                  <div className="grid gap-3 sm:grid-cols-2">
                    <div className="rounded-3xl bg-white p-4 shadow-sm shadow-slate-200">
                      <p className="text-xs uppercase tracking-[0.3em] text-slate-500">
                        Total filas
                      </p>
                      <p className="mt-2 text-2xl font-semibold text-slate-950">
                        {stats.totalRows}
                      </p>
                    </div>
                    <div className="rounded-3xl bg-white p-4 shadow-sm shadow-slate-200">
                      <p className="text-xs uppercase tracking-[0.3em] text-slate-500">
                        Contactos válidos
                      </p>
                      <p className="mt-2 text-2xl font-semibold text-slate-950">
                        {stats.validContacts}
                      </p>
                    </div>
                    <div className="rounded-3xl bg-white p-4 shadow-sm shadow-slate-200">
                      <p className="text-xs uppercase tracking-[0.3em] text-slate-500">
                        Nuevos
                      </p>
                      <p className="mt-2 text-2xl font-semibold text-slate-950">
                        {stats.newContacts}
                      </p>
                    </div>
                    <div className="rounded-3xl bg-white p-4 shadow-sm shadow-slate-200">
                      <p className="text-xs uppercase tracking-[0.3em] text-slate-500">
                        Duplicados/invalidos
                      </p>
                      <p className="mt-2 text-2xl font-semibold text-slate-950">
                        {stats.duplicates + stats.invalidRows}
                      </p>
                    </div>
                  </div>
                )}
                <Link
                  href={`/dashboard/admin/campaign?contactImportId=${encodeURIComponent(importId)}`}
                  className="inline-flex w-full justify-center rounded-3xl bg-emerald-500 px-6 py-3 text-sm font-semibold text-white transition hover:bg-emerald-400"
                >
                  Ir a crear campaña con este ID
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
