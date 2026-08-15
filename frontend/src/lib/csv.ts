// ---------------------------------------------------------------------------
// Minimal client-side CSV parser for the Contacts importer.
//
// Deliberately dependency-free: the required stack for this project is
// fixed (Next.js, TS, Tailwind, Zustand, React Query, Clerk, Recharts,
// React Hook Form + Zod, Lucide), so this stays a small hand-rolled parser
// rather than pulling in a CSV library. Handles quoted fields and commas
// inside quotes, which covers real-world exports from Excel/Sheets.
// ---------------------------------------------------------------------------

export interface ParsedCsvRow {
  name: string;
  email: string;
  company: string;
}

export interface CsvParseResult {
  rows: ParsedCsvRow[];
  error: string | null;
}

function parseLine(line: string): string[] {
  const cells: string[] = [];
  let current = "";
  let inQuotes = false;

  for (let i = 0; i < line.length; i++) {
    const char = line[i];
    if (char === '"') {
      if (inQuotes && line[i + 1] === '"') {
        current += '"';
        i++;
      } else {
        inQuotes = !inQuotes;
      }
    } else if (char === "," && !inQuotes) {
      cells.push(current.trim());
      current = "";
    } else {
      current += char;
    }
  }
  cells.push(current.trim());
  return cells;
}

const EXPECTED_HEADERS = ["name", "email", "company"];
// Accepts the Spanish header names from the source spec too.
const HEADER_ALIASES: Record<string, string> = {
  nombre: "name",
  name: "name",
  correo: "email",
  email: "email",
  empresa: "company",
  company: "company",
};

export function parseContactsCsv(text: string): CsvParseResult {
  const lines = text.split(/\r?\n/).filter((l) => l.trim().length > 0);
  if (lines.length < 2) {
    return { rows: [], error: "The file needs a header row plus at least one contact." };
  }

  const headerCells = parseLine(lines[0]).map((h) => h.toLowerCase().trim());
  const normalizedHeaders = headerCells.map((h) => HEADER_ALIASES[h] ?? h);

  const missing = EXPECTED_HEADERS.filter((h) => !normalizedHeaders.includes(h));
  if (missing.length > 0) {
    return {
      rows: [],
      error: `Missing expected column(s): ${missing.join(", ")}. Expected: name, email, company.`,
    };
  }

  const nameIdx = normalizedHeaders.indexOf("name");
  const emailIdx = normalizedHeaders.indexOf("email");
  const companyIdx = normalizedHeaders.indexOf("company");

  const rows: ParsedCsvRow[] = lines.slice(1).map((line) => {
    const cells = parseLine(line);
    return {
      name: cells[nameIdx] ?? "",
      email: cells[emailIdx] ?? "",
      company: cells[companyIdx] ?? "",
    };
  });

  return { rows, error: null };
}

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export function validateContactRow(row: ParsedCsvRow): { valid: boolean; issue?: string } {
  const problems: string[] = [];
  if (!row.name.trim()) problems.push("missing name");
  if (!EMAIL_RE.test(row.email.trim())) problems.push("invalid email format");
  if (!row.company.trim()) problems.push("missing company name");

  if (problems.length === 0) return { valid: true };
  return { valid: false, issue: problems.join(", ") };
}

// --- Companies bulk import ---------------------------------------------------

export interface ParsedCompanyRow {
  name: string;
  email: string;
  country: string;
  industry: string;
  employees: string;
  dataCenterTier: string;
  gpuClusterSize: number;
}

export interface CompanyCsvParseResult {
  rows: ParsedCompanyRow[];
  error: string | null;
}

const COMPANY_HEADERS = ["name", "email", "country", "industry", "employees", "dataCenterTier", "gpuClusterSize"];
const COMPANY_HEADER_ALIASES: Record<string, string> = {
  nombre: "name",
  name: "name",
  correo: "email",
  email: "email",
  pais: "country",
  país: "country",
  country: "country",
  industria: "industry",
  industry: "industry",
  empleados: "employees",
  employees: "employees",
  tier: "dataCenterTier",
  datacentertier: "dataCenterTier",
  gpuclustersize: "gpuClusterSize",
  gpus: "gpuClusterSize",
};

export function parseCompaniesCsv(text: string): CompanyCsvParseResult {
  const lines = text.split(/\r?\n/).filter((l) => l.trim().length > 0);
  if (lines.length < 2) {
    return { rows: [], error: "The file needs a header row plus at least one company." };
  }

  const headerCells = parseLine(lines[0]).map((h) => h.toLowerCase().replace(/\s+/g, ""));
  const normalizedHeaders = headerCells.map((h) => COMPANY_HEADER_ALIASES[h] ?? h);

  const missing = COMPANY_HEADERS.filter((h) => !normalizedHeaders.includes(h));
  if (missing.length > 0) {
    return {
      rows: [],
      error: `Missing expected column(s): ${missing.join(", ")}.`,
    };
  }

  const idx = (key: string) => normalizedHeaders.indexOf(key);

  const rows: ParsedCompanyRow[] = lines.slice(1).map((line) => {
    const cells = parseLine(line);
    return {
      name: cells[idx("name")] ?? "",
      email: cells[idx("email")] ?? "",
      country: cells[idx("country")] ?? "",
      industry: cells[idx("industry")] ?? "",
      employees: cells[idx("employees")] ?? "",
      dataCenterTier: cells[idx("dataCenterTier")] ?? "",
      gpuClusterSize: Number(cells[idx("gpuClusterSize")] ?? 0) || 0,
    };
  });

  return { rows, error: null };
}

export function validateCompanyRow(row: ParsedCompanyRow): { valid: boolean; issue?: string } {
  const problems: string[] = [];
  if (!row.name.trim()) problems.push("missing name");
  if (!EMAIL_RE.test(row.email.trim())) problems.push("invalid email format");
  if (!row.country.trim()) problems.push("missing country");
  if (!row.industry.trim()) problems.push("missing industry");
  if (!row.gpuClusterSize || row.gpuClusterSize <= 0) problems.push("invalid GPU cluster size");

  if (problems.length === 0) return { valid: true };
  return { valid: false, issue: problems.join(", ") };
}
