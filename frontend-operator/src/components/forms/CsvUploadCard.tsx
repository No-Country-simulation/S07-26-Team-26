"use client";

import { useRef, useState } from "react";
import { UploadCloud, AlertTriangle } from "lucide-react";
import { Card, CardContent } from "@/components/ui/Card";
import { cn } from "@/lib/utils";
import { parseContactsCsv, validateContactRow } from "@/lib/csv";
import { useContactsStore } from "@/store/contactsStore";

export function CsvUploadCard() {
  const [dragging, setDragging] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [lastImportCount, setLastImportCount] = useState<number | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);
  const addContacts = useContactsStore((s) => s.addContacts);

  async function handleFiles(files: FileList | null) {
    const file = files?.[0];
    if (!file) return;
    setError(null);
    setLastImportCount(null);

    if (!file.name.toLowerCase().endsWith(".csv")) {
      setError("Please upload a .csv file.");
      return;
    }

    const text = await file.text();
    const { rows, error: parseError } = parseContactsCsv(text);
    if (parseError) {
      setError(parseError);
      return;
    }

    const validated = rows.map((row) => {
      const { valid, issue } = validateContactRow(row);
      return { ...row, valid, issue };
    });

    addContacts(validated);
    setLastImportCount(validated.length);
  }

  return (
    <Card>
      <CardContent className="p-6">
        <p className="mb-1 flex items-center gap-2 text-xs font-semibold uppercase tracking-wide text-gold-700">
          <span className="h-px w-4 bg-gold-500" />
          Import Contacts
        </p>
        <h2 className="mb-1.5 text-lg font-semibold text-graphite-900">Upload a CSV list</h2>
        <p className="mb-5 text-sm text-graphite-500">
          Expected columns: name, email, company. The file is processed locally in this demo.
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
            "flex cursor-pointer flex-col items-center justify-center gap-2 rounded-md border-2 border-dashed py-14 text-center transition-colors",
            dragging ? "border-forest-700 bg-forest-50" : "border-graphite-200 bg-forest-50/30 hover:bg-forest-50/50"
          )}
        >
          <UploadCloud className="h-6 w-6 text-forest-700" strokeWidth={1.75} />
          <p className="text-sm font-semibold text-graphite-800">Drag & drop your CSV, or click to browse</p>
          <p className="text-xs text-graphite-400">Recommended max: 5 MB</p>
          <input
            ref={inputRef}
            type="file"
            accept=".csv"
            className="hidden"
            onChange={(e) => handleFiles(e.target.files)}
          />
        </div>

        {error && (
          <p className="mt-3 flex items-center gap-1.5 text-xs font-medium text-red-600">
            <AlertTriangle className="h-3.5 w-3.5" />
            {error}
          </p>
        )}
        {lastImportCount !== null && !error && (
          <p className="mt-3 text-xs font-medium text-forest-700">
            Imported {lastImportCount} row{lastImportCount === 1 ? "" : "s"} — see the preview below.
          </p>
        )}

        <div className="mt-5 rounded-md border border-gold-100 bg-gold-50/60 px-4 py-3 text-xs text-gold-800">
          The real API will apply deduplication and idempotency checks before creating the campaign.
        </div>
      </CardContent>
    </Card>
  );
}
