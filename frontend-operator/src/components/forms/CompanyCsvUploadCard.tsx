"use client";

import { useRef, useState } from "react";
import { UploadCloud, AlertTriangle } from "lucide-react";
import { cn } from "@/lib/utils";
import { parseCompaniesCsv, validateCompanyRow } from "@/lib/csv";
import { useCreateCompaniesFromCsv } from "@/hooks/useCompanies";
import { useAuthStore } from "@/store/authStore";

export function CompanyCsvUploadCard() {
  const [dragging, setDragging] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [result, setResult] = useState<{ imported: number; skipped: number } | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);
  const session = useAuthStore((s) => s.session);
  const createFromCsv = useCreateCompaniesFromCsv();

  async function handleFiles(files: FileList | null) {
    const file = files?.[0];
    if (!file) return;
    setError(null);
    setResult(null);

    if (!file.name.toLowerCase().endsWith(".csv")) {
      setError("Please upload a .csv file.");
      return;
    }

    const text = await file.text();
    const { rows, error: parseError } = parseCompaniesCsv(text);
    if (parseError) {
      setError(parseError);
      return;
    }

    const valid = rows.filter((row) => validateCompanyRow(row).valid);
    const skipped = rows.length - valid.length;

    if (valid.length === 0) {
      setError("No valid rows found. Check that every row has name, email, country, industry, and a GPU cluster size.");
      return;
    }

    await createFromCsv.mutateAsync({
      inputs: valid,
      assignedAdminId: session?.role === "ADMIN" ? session.adminId ?? null : null,
    });
    setResult({ imported: valid.length, skipped });
  }

  return (
    <div>
      <p className="mb-1 flex items-center gap-2 text-xs font-semibold uppercase tracking-wide text-gold-700">
        <span className="h-px w-4 bg-gold-500" />
        Bulk Import
      </p>
      <h3 className="mb-1.5 text-base font-semibold text-graphite-900">Register companies via CSV</h3>
      <p className="mb-4 text-sm text-graphite-500">
        Expected columns: name, email, country, industry, employees, dataCenterTier, gpuClusterSize.
      </p>

      <div
        onDragOver={(e) => {
          e.preventDefault();
          setDragging(true);
        }}
        onDragLeave={() => setDragging(false)}
        onDrop={(e) => {
          e.preventDefault();
          setDragging(false);
          handleFiles(e.dataTransfer.files);
        }}
        onClick={() => inputRef.current?.click()}
        className={cn(
          "flex cursor-pointer flex-col items-center justify-center gap-2 rounded-md border-2 border-dashed py-10 text-center transition-colors",
          dragging ? "border-forest-700 bg-forest-50" : "border-graphite-200 bg-forest-50/30 hover:bg-forest-50/50"
        )}
      >
        <UploadCloud className="h-5 w-5 text-forest-700" strokeWidth={1.75} />
        <p className="text-sm font-medium text-graphite-800">Drag & drop your CSV, or click to browse</p>
        <input ref={inputRef} type="file" accept=".csv" className="hidden" onChange={(e) => handleFiles(e.target.files)} />
      </div>

      {error && (
        <p className="mt-3 flex items-center gap-1.5 text-xs font-medium text-red-600">
          <AlertTriangle className="h-3.5 w-3.5" />
          {error}
        </p>
      )}
      {result && !error && (
        <p className="mt-3 text-xs font-medium text-forest-700">
          Registered {result.imported} compan{result.imported === 1 ? "y" : "ies"}
          {result.skipped > 0 ? ` — skipped ${result.skipped} invalid row${result.skipped === 1 ? "" : "s"}.` : "."}
        </p>
      )}
    </div>
  );
}
