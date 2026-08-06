'use client';

import React, { useCallback, useRef, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { UploadCloud, FileSpreadsheet, X, AlertTriangle, CheckCircle2 } from 'lucide-react';
import { useAdminAuthStore } from '@/store/adminAuthStore';
import { apiClient, ContactImportResponse } from '@/lib/api';

type PreviewRow = {
  firstName: string;
  lastName: string;
  email: string;
  company: string;
  position: string;
};

function parsePreview(text: string): { rows: PreviewRow[]; error: string | null } {
  const lines = text.trim().split(/\r?\n/).filter((line) => line.trim().length > 0);
  if (lines.length === 0) {
    return { rows: [], error: null };
  }
  const header = lines[0].split(',').map((cell) => cell.trim().toLowerCase());
  const index = (name: string) => header.findIndex((h) => h === name || h.includes(name));

  const firstNameIdx = index('first_name') !== -1 ? index('first_name') : index('firstName');
  const lastNameIdx = index('last_name') !== -1 ? index('last_name') : index('lastName');
  const emailIdx = index('email');
  const companyIdx = index('company') !== -1 ? index('company') : index('company_name');
  const positionIdx = index('position');

  if (firstNameIdx === -1 || emailIdx === -1) {
    return {
      rows: [],
      error: 'El archivo debe tener columnas "first_name" y "email" como mínimo.',
    };
  }

  const rows = lines.slice(1).map((line) => {
    const cells = line.split(',');
    const value = (idx: number) => (idx >= 0 && idx < cells.length ? cells[idx].trim() : '');
    return {
      firstName: value(firstNameIdx),
      lastName: value(lastNameIdx),
      email: value(emailIdx),
      company: value(companyIdx),
      position: value(positionIdx),
    };
  });
  return { rows, error: null };
}

export default function OutreachImportPage() {
  const accessToken = useAdminAuthStore((state) => state.accessToken);
  const queryClient = useQueryClient();
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [fileName, setFileName] = useState<string | null>(null);
  const [fileContent, setFileContent] = useState<string | null>(null);
  const [preview, setPreview] = useState<{ rows: PreviewRow[]; error: string | null }>({
    rows: [],
    error: null,
  });
  const [importName, setImportName] = useState('');
  const [dragOver, setDragOver] = useState(false);
  const [lastResult, setLastResult] = useState<ContactImportResponse | null>(null);

  const { data: imports, refetch } = useQuery({
    queryKey: ['contact-imports'],
    queryFn: () => apiClient.listContactImports(accessToken ?? ''),
    enabled: Boolean(accessToken),
  });

  const importMutation = useMutation({
    mutationFn: (file: File) =>
      apiClient.importContacts(accessToken ?? '', importName.trim(), file),
    onSuccess: (result) => {
      setLastResult(result);
      setFileName(null);
      setFileContent(null);
      setPreview({ rows: [], error: null });
      setImportName('');
      queryClient.invalidateQueries({ queryKey: ['contact-imports'] });
      void refetch();
    },
  });

  const handleFile = useCallback((file: File) => {
    if (!file.name.toLowerCase().endsWith('.csv')) {
      setPreview({ rows: [], error: 'Solo se permiten archivos .csv' });
      return;
    }
    setFileName(file.name);
    const reader = new FileReader();
    reader.onload = () => {
      const text = String(reader.result ?? '');
      setFileContent(text);
      setPreview(parsePreview(text));
    };
    reader.readAsText(file);
  }, []);

  function handleSubmit() {
    if (!fileContent || !importName.trim()) {
      return;
    }
    const file = new File([fileContent], fileName ?? 'contactos.csv', {
      type: 'text/csv',
    });
    importMutation.mutate(file);
  }

  const statusBadge = (status: string) => {
    const styles: Record<string, string> = {
      COMPLETED: 'bg-emerald-500/10 border-emerald-500/25 text-emerald-400',
      FAILED: 'bg-red-500/10 border-red-500/25 text-red-400',
      PROCESSING: 'bg-amber-500/10 border-amber-500/25 text-amber-400',
    };
    return styles[status] ?? 'bg-slate-500/10 border-slate-500/25 text-slate-400';
  };

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-extrabold tracking-tight text-white">
          Importar contactos
        </h1>
        <p className="text-sm text-slate-400 mt-1">
          Sube un CSV con contactos para usarlos en campañas de outreach.
        </p>
      </div>

      <section className="rounded-2xl border border-slate-800 bg-slate-900/40 backdrop-blur-xl p-6 space-y-4">
        <input
          ref={fileInputRef}
          type="file"
          accept=".csv"
          className="hidden"
          onChange={(event) => {
            const file = event.target.files?.[0];
            if (file) {
              handleFile(file);
            }
          }}
        />

        <div
          onDragOver={(event) => {
            event.preventDefault();
            setDragOver(true);
          }}
          onDragLeave={() => setDragOver(false)}
          onDrop={(event) => {
            event.preventDefault();
            setDragOver(false);
            const file = event.dataTransfer.files?.[0];
            if (file) {
              handleFile(file);
            }
          }}
          onClick={() => fileInputRef.current?.click()}
          className={`cursor-pointer rounded-xl border-2 border-dashed p-10 text-center transition-all ${
            dragOver
              ? 'border-indigo-500 bg-indigo-500/5'
              : 'border-slate-700 bg-slate-950/40 hover:border-slate-500'
          }`}
        >
          {fileName ? (
            <div className="flex flex-col items-center gap-2">
              <FileSpreadsheet className="w-8 h-8 text-indigo-400" />
              <p className="text-sm font-medium text-slate-200">{fileName}</p>
              <p className="text-xs text-slate-500">
                Haz clic o arrastra otro archivo para reemplazarlo
              </p>
            </div>
          ) : (
            <div className="flex flex-col items-center gap-2">
              <UploadCloud className="w-8 h-8 text-slate-500" />
              <p className="text-sm font-medium text-slate-300">
                Arrastra tu archivo CSV aquí
              </p>
              <p className="text-xs text-slate-500">
                o haz clic para seleccionarlo desde tu equipo
              </p>
            </div>
          )}
        </div>

        {fileName && (
          <button
            onClick={() => {
              setFileName(null);
              setFileContent(null);
              setPreview({ rows: [], error: null });
            }}
            className="flex items-center gap-1 text-xs text-slate-500 hover:text-red-400 transition-colors"
          >
            <X className="w-3 h-3" />
            Quitar archivo
          </button>
        )}

        {preview.error && (
          <div className="flex items-start gap-2 rounded-lg border border-red-500/25 bg-red-500/10 p-3 text-sm text-red-400">
            <AlertTriangle className="w-4 h-4 mt-0.5 shrink-0" />
            {preview.error}
          </div>
        )}

        {preview.rows.length > 0 && (
          <div className="rounded-xl border border-slate-800 overflow-hidden">
            <div className="px-4 py-3 border-b border-slate-800 flex items-center justify-between">
              <p className="text-sm font-semibold text-slate-200">
                Vista previa ({preview.rows.length} filas)
              </p>
              <span className="text-xs text-slate-500">
                Se importarán las primeras filas del archivo
              </span>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-left text-xs text-slate-500 uppercase tracking-wider border-b border-slate-800 bg-slate-950/40">
                    <th className="py-2 px-4">Nombre</th>
                    <th className="py-2 px-4">Apellido</th>
                    <th className="py-2 px-4">Email</th>
                    <th className="py-2 px-4">Empresa</th>
                    <th className="py-2 px-4">Cargo</th>
                  </tr>
                </thead>
                <tbody>
                  {preview.rows.slice(0, 8).map((row, idx) => (
                    <tr key={idx} className="border-b border-slate-800/60 text-slate-300">
                      <td className="py-2 px-4">{row.firstName}</td>
                      <td className="py-2 px-4">{row.lastName}</td>
                      <td className="py-2 px-4">{row.email}</td>
                      <td className="py-2 px-4">{row.company}</td>
                      <td className="py-2 px-4">{row.position}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        <div className="space-y-2">
          <label htmlFor="import-name" className="block text-xs text-slate-400 font-medium">
            Nombre de la importación
          </label>
          <input
            id="import-name"
            value={importName}
            onChange={(event) => setImportName(event.target.value)}
            placeholder="Ej: Prospectos Q3 2026"
            className="w-full rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-sm text-slate-200 placeholder:text-slate-600 focus:outline-none focus:border-indigo-500"
          />
        </div>

        {importMutation.isError && (
          <div className="flex items-start gap-2 rounded-lg border border-red-500/25 bg-red-500/10 p-3 text-sm text-red-400">
            <AlertTriangle className="w-4 h-4 mt-0.5 shrink-0" />
            No se pudo importar el archivo. Verifica el formato e inténtalo nuevamente.
          </div>
        )}

        <button
          onClick={handleSubmit}
          disabled={!fileName || !importName.trim() || importMutation.isPending}
          className="px-4 py-2.5 rounded-lg bg-indigo-500 text-white text-sm font-semibold hover:bg-indigo-400 disabled:opacity-40 disabled:cursor-not-allowed transition-all"
        >
          {importMutation.isPending ? 'Importando...' : 'Importar contactos'}
        </button>

        {lastResult && (
          <div className="rounded-xl border border-emerald-500/25 bg-emerald-500/5 p-4 space-y-2">
            <div className="flex items-center gap-2 text-emerald-400 text-sm font-semibold">
              <CheckCircle2 className="w-4 h-4" />
              Importación completada
            </div>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-sm">
              <div>
                <p className="text-xs text-slate-500">Contactos válidos</p>
                <p className="font-semibold text-slate-200">{lastResult.validContacts}</p>
              </div>
              <div>
                <p className="text-xs text-slate-500">Nuevos</p>
                <p className="font-semibold text-slate-200">{lastResult.newContacts}</p>
              </div>
              <div>
                <p className="text-xs text-slate-500">Duplicados</p>
                <p className="font-semibold text-slate-200">{lastResult.duplicates}</p>
              </div>
              <div>
                <p className="text-xs text-slate-500">Filas inválidas</p>
                <p className="font-semibold text-slate-200">{lastResult.invalidRows}</p>
              </div>
            </div>
            {lastResult.issues.length > 0 && (
              <ul className="space-y-1 text-xs text-slate-400">
                {lastResult.issues.slice(0, 5).map((issue, idx) => (
                  <li key={idx}>
                    Fila {issue.row}: {issue.message}
                  </li>
                ))}
              </ul>
            )}
          </div>
        )}
      </section>

      <section className="rounded-2xl border border-slate-800 bg-slate-900/40 backdrop-blur-xl p-6">
        <h2 className="text-lg font-bold text-white mb-4">Importaciones recientes</h2>
        {imports && imports.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-xs text-slate-500 uppercase tracking-wider border-b border-slate-800">
                  <th className="py-2 pr-4">Nombre</th>
                  <th className="py-2 pr-4">Estado</th>
                  <th className="py-2 pr-4">Válidos</th>
                  <th className="py-2 pr-4">Duplicados</th>
                  <th className="py-2 pr-4">Inválidos</th>
                  <th className="py-2">Fecha</th>
                </tr>
              </thead>
              <tbody>
                {imports.map((item) => (
                  <tr key={item.importId} className="border-b border-slate-800/60 text-slate-300">
                    <td className="py-2.5 pr-4 font-medium text-slate-200">{item.name}</td>
                    <td className="py-2.5 pr-4">
                      <span
                        className={`px-2 py-0.5 rounded border text-xs font-medium ${statusBadge(
                          item.status
                        )}`}
                      >
                        {item.status}
                      </span>
                    </td>
                    <td className="py-2.5 pr-4 font-mono">{item.validContacts}</td>
                    <td className="py-2.5 pr-4 font-mono">{item.duplicates}</td>
                    <td className="py-2.5 pr-4 font-mono">{item.invalidRows}</td>
                    <td className="py-2.5 text-xs text-slate-500">
                      {new Date(item.createdAt).toLocaleDateString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <p className="text-sm text-slate-500">Aún no hay importaciones registradas.</p>
        )}
      </section>
    </div>
  );
}
