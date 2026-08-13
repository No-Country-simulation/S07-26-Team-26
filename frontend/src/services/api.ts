// ---------------------------------------------------------------------------
// Service layer for Project Ghost Load.
//
// Every data access in the app goes through the functions in this file.
// Today they read from (and write to, in-memory) the local mock/*.json
// files via the *Db arrays below. Once a real backend exists, swap the body
// of each function for a `fetch(...)` call against NEXT_PUBLIC_API_BASE_URL
// -- no component, hook, or store needs to change, because they only ever
// import from here.
//
//   if (USE_MOCKS) { ...current mock implementation... }
//   else           { return fetch(`${API_BASE_URL}/companies`).then(r => r.json()) }
// ---------------------------------------------------------------------------

import companiesData from "@/mock/companies.json";
import campaignsData from "@/mock/campaigns.json";
import weeklyActivityData from "@/mock/weeklyActivity.json";
import maturityDistributionData from "@/mock/maturityDistribution.json";
import pdfsData from "@/mock/pdfs.json";
import evaluationsData from "@/mock/evaluations.json";
import benchmarkSchema from "@/mock/benchmark.json";
import adminsData from "@/mock/admins.json";
import operatorsData from "@/mock/operators.json";

export const USE_MOCKS = process.env.NEXT_PUBLIC_USE_MOCKS !== "false";
export const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "";

// Simulates real network latency so loading states are visible/testable.
function delay<T>(value: T, ms = 350): Promise<T> {
  return new Promise((resolve) => setTimeout(() => resolve(value), ms));
}

export type Company = Omit<(typeof companiesData)[number], "assignedAdminId"> & {
  assignedAdminId: string | null;
};
export type Campaign = (typeof campaignsData)[number];
export type PdfRecord = (typeof pdfsData)[number];
export type Evaluation = (typeof evaluationsData)[number];
export type AdminRole = "ROOT_ADMIN" | "ADMIN";
export type AvatarVariant = "grass" | "gold" | "stone" | "wood";
export type Admin = Omit<(typeof adminsData)[number], "role" | "avatar"> & {
  role: AdminRole;
  avatar: AvatarVariant;
};
export type OperatorAccount = Omit<(typeof operatorsData)[number], "avatar"> & { avatar: AvatarVariant };

// ---------------------------------------------------------------------------
// In-memory "database" for mock mode. Seeded from the JSON fixtures, then
// mutated by the create*/update* functions below so the UI reflects new
// companies/admins/operators for the rest of the session. A real backend
// replaces this whole block -- every function that touches these arrays has
// its `else` branch already sketched out.
// ---------------------------------------------------------------------------
let companiesDb: Company[] = [...(companiesData as Company[])];
let adminsDb: Admin[] = [...(adminsData as Admin[])];
let operatorsDb: OperatorAccount[] = [...(operatorsData as OperatorAccount[])];

// --- Companies --------------------------------------------------------------

export async function fetchCompanies(): Promise<Company[]> {
  if (USE_MOCKS) return delay([...companiesDb]);
  return fetch(`${API_BASE_URL}/companies`).then((r) => r.json());
}

export async function fetchCompanyById(id: string): Promise<Company | undefined> {
  if (USE_MOCKS) return delay(companiesDb.find((c) => c.id === id));
  return fetch(`${API_BASE_URL}/companies/${id}`).then((r) => r.json());
}

export async function fetchCompanyByEmail(email: string): Promise<Company | undefined> {
  if (USE_MOCKS) {
    return delay(companiesDb.find((c) => c.email.toLowerCase() === email.toLowerCase()));
  }
  return fetch(`${API_BASE_URL}/companies?email=${encodeURIComponent(email)}`).then((r) => r.json());
}

export interface NewCompanyInput {
  name: string;
  email: string;
  country: string;
  industry: string;
  employees: string;
  dataCenterTier: string;
  gpuClusterSize: number;
  assignedAdminId: string | null;
}

function toCompanyRecord(input: NewCompanyInput, idSuffix: string): Company {
  return {
    id: `c-${idSuffix}`,
    name: input.name,
    email: input.email,
    country: input.country,
    industry: input.industry,
    employees: input.employees,
    dataCenterTier: input.dataCenterTier,
    gpuClusterSize: input.gpuClusterSize,
    status: "Invited",
    score: null,
    maturityLevel: null,
    pdfAvailable: false,
    joinedAt: new Date().toISOString().slice(0, 10),
    categoryScores: null,
    assignedAdminId: input.assignedAdminId,
  } as Company;
}

export async function createCompany(input: NewCompanyInput): Promise<Company> {
  if (USE_MOCKS) {
    const record = toCompanyRecord(input, Date.now().toString(36));
    companiesDb = [record, ...companiesDb];
    return delay(record, 500);
  }
  return fetch(`${API_BASE_URL}/companies`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(input),
  }).then((r) => r.json());
}

export async function createCompaniesFromCsv(
  inputs: Omit<NewCompanyInput, "assignedAdminId">[],
  assignedAdminId: string | null
): Promise<Company[]> {
  if (USE_MOCKS) {
    const records = inputs.map((input, i) =>
      toCompanyRecord({ ...input, assignedAdminId }, `${Date.now().toString(36)}-${i}`)
    );
    companiesDb = [...records, ...companiesDb];
    return delay(records, 600);
  }
  return fetch(`${API_BASE_URL}/companies/bulk`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ inputs, assignedAdminId }),
  }).then((r) => r.json());
}

// --- Admins (Root Admin only) -------------------------------------------------

export async function fetchAdmins(): Promise<Admin[]> {
  if (USE_MOCKS) return delay([...adminsDb]);
  return fetch(`${API_BASE_URL}/admins`).then((r) => r.json());
}

export async function fetchAdminByEmail(email: string): Promise<Admin | undefined> {
  if (USE_MOCKS) {
    return delay(adminsDb.find((a) => a.email.toLowerCase() === email.toLowerCase()));
  }
  return fetch(`${API_BASE_URL}/admins?email=${encodeURIComponent(email)}`).then((r) => r.json());
}

export async function createAdmin(input: {
  name: string;
  lastName?: string;
  email: string;
  role: AdminRole;
  avatar?: AvatarVariant;
  status?: "Active" | "Invited";
}): Promise<Admin> {
  if (USE_MOCKS) {
    const record: Admin = {
      id: `admin-${Date.now().toString(36)}`,
      name: input.lastName ? `${input.name} ${input.lastName}` : input.name,
      email: input.email,
      role: input.role,
      status: input.status ?? "Invited",
      avatar: input.avatar ?? "grass",
      createdAt: new Date().toISOString().slice(0, 10),
      lastAccess: new Date().toISOString().slice(0, 10),
    };
    adminsDb = [...adminsDb, record];
    return delay(record, 500);
  }
  return fetch(`${API_BASE_URL}/admins`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(input),
  }).then((r) => r.json());
}

// --- Operators (Admin + Root Admin) -------------------------------------------

export async function fetchOperators(): Promise<OperatorAccount[]> {
  if (USE_MOCKS) return delay([...operatorsDb]);
  return fetch(`${API_BASE_URL}/operators`).then((r) => r.json());
}

export async function fetchOperatorByEmail(email: string): Promise<OperatorAccount | undefined> {
  if (USE_MOCKS) {
    return delay(operatorsDb.find((o) => o.email.toLowerCase() === email.toLowerCase()));
  }
  return fetch(`${API_BASE_URL}/operators?email=${encodeURIComponent(email)}`).then((r) => r.json());
}

export async function createOperator(input: {
  name: string;
  lastName?: string;
  email: string;
  companyId: string;
  avatar?: AvatarVariant;
  status?: "Active" | "Invited";
}): Promise<OperatorAccount> {
  if (USE_MOCKS) {
    const record: OperatorAccount = {
      id: `op-${Date.now().toString(36)}`,
      name: input.lastName ? `${input.name} ${input.lastName}` : input.name,
      email: input.email,
      companyId: input.companyId,
      status: input.status ?? "Invited",
      avatar: input.avatar ?? "grass",
      createdAt: new Date().toISOString().slice(0, 10),
      lastAccess: new Date().toISOString().slice(0, 10),
    };
    operatorsDb = [...operatorsDb, record];
    return delay(record, 500);
  }
  return fetch(`${API_BASE_URL}/operators`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(input),
  }).then((r) => r.json());
}

// --- Dashboard KPIs -----------------------------------------------------------

export interface DashboardKpis {
  registeredCompanies: number;
  completedBenchmarks: number;
  averageScore: number;
  generatedPdfs: number;
}

// `scopeCompanyIds` is undefined for Root Admin (unrestricted); passing an
// array restricts every KPI to that set, which is how a scoped Admin's
// dashboard differs from Root Admin's.
export async function fetchDashboardKpis(scopeCompanyIds?: string[]): Promise<DashboardKpis> {
  if (USE_MOCKS) {
    const companies = scopeCompanyIds
      ? companiesDb.filter((c) => scopeCompanyIds.includes(c.id))
      : companiesDb;
    const completed = companies.filter((c) => c.status === "Completed");
    const avg = completed.length
      ? Math.round(completed.reduce((sum, c) => sum + (c.score ?? 0), 0) / completed.length)
      : 0;
    const scopedPdfCount = scopeCompanyIds
      ? (pdfsData as PdfRecord[]).filter((p) => scopeCompanyIds.includes(p.companyId)).length
      : (pdfsData as PdfRecord[]).length;
    return delay({
      registeredCompanies: companies.length,
      completedBenchmarks: completed.length,
      averageScore: avg,
      generatedPdfs: scopedPdfCount,
    });
  }
  const qs = scopeCompanyIds ? `?companyIds=${scopeCompanyIds.join(",")}` : "";
  return fetch(`${API_BASE_URL}/dashboard/kpis${qs}`).then((r) => r.json());
}

export async function fetchInvitationFunnel() {
  if (USE_MOCKS) {
    const campaigns = campaignsData as Campaign[];
    const totals = campaigns.reduce(
      (acc, c) => ({
        registered: acc.registered + c.funnel.registered,
        visited: acc.visited + c.funnel.visited,
        started: acc.started + c.funnel.started,
        completed: acc.completed + c.funnel.completed,
        pdfGenerated: acc.pdfGenerated + c.funnel.pdfGenerated,
      }),
      { registered: 0, visited: 0, started: 0, completed: 0, pdfGenerated: 0 }
    );
    return delay(totals);
  }
  return fetch(`${API_BASE_URL}/dashboard/funnel`).then((r) => r.json());
}

export async function fetchMaturityDistribution() {
  if (USE_MOCKS) return delay(maturityDistributionData);
  return fetch(`${API_BASE_URL}/dashboard/maturity-distribution`).then((r) => r.json());
}

export async function fetchWeeklyActivity() {
  if (USE_MOCKS) return delay(weeklyActivityData);
  return fetch(`${API_BASE_URL}/dashboard/weekly-activity`).then((r) => r.json());
}

// --- Campaigns ----------------------------------------------------------------

export async function fetchCampaigns(): Promise<Campaign[]> {
  if (USE_MOCKS) return delay(campaignsData as Campaign[]);
  return fetch(`${API_BASE_URL}/campaigns`).then((r) => r.json());
}

// --- Results / PDFs -------------------------------------------------------------

export async function fetchPdfs(): Promise<PdfRecord[]> {
  if (USE_MOCKS) return delay(pdfsData as PdfRecord[]);
  return fetch(`${API_BASE_URL}/pdfs`).then((r) => r.json());
}

export async function fetchEvaluationByCompanyId(companyId: string): Promise<Evaluation | undefined> {
  if (USE_MOCKS) {
    return delay((evaluationsData as Evaluation[]).find((e) => e.companyId === companyId));
  }
  return fetch(`${API_BASE_URL}/evaluations?companyId=${companyId}`).then((r) => r.json());
}

// --- Benchmark schema -----------------------------------------------------------

export async function fetchBenchmarkSchema() {
  if (USE_MOCKS) return delay(benchmarkSchema);
  return fetch(`${API_BASE_URL}/benchmark/schema`).then((r) => r.json());
}

// --- Mutations (writes) — structured for a real backend from day one ------------

export async function submitBenchmark(payload: {
  companyId: string;
  answers: Record<string, string | number>;
}): Promise<{ ok: true; submittedAt: string }> {
  if (USE_MOCKS) {
    // In mock mode we simply acknowledge the submission; BenchmarkStore
    // keeps the authoritative in-memory copy for the current session.
    return delay({ ok: true, submittedAt: new Date().toISOString() }, 500);
  }
  return fetch(`${API_BASE_URL}/benchmark/submit`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  }).then((r) => r.json());
}

export async function generatePdfReport(companyId: string): Promise<{ ok: true; url: string }> {
  if (USE_MOCKS) {
    return delay({ ok: true, url: `#mock-report-${companyId}` }, 700);
  }
  return fetch(`${API_BASE_URL}/pdf/generate`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ companyId }),
  }).then((r) => r.json());
}

// --- Operator invitation flow (no login — see docs/11_Backend_API_Flow.md) -----------
import { useInvitationStore } from "@/store/invitationStore";
import { useCalculatorStore } from "@/store/calculatorStore";

export interface InvitationDetails {
  invitationToken: string;
  operatorName: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  companyName: string;
  role: string;
  position?: string;
  campaignName: string;
  estimatedMinutes: number;
}

export async function fetchInvitation(invitationToken: string): Promise<InvitationDetails> {
  if (USE_MOCKS) {
    if (!invitationToken) throw new Error("Invalid invitation token");
    return delay(
      {
        invitationToken,
        operatorName: "Alex Rivera",
        companyName: "Aetheris Cloud",
        role: "Infrastructure Lead",
        campaignName: "Ghost Load Benchmark — Q2 2026",
        estimatedMinutes: 12,
      },
      450
    );
  }
  return fetch(`${API_BASE_URL}/api/v1/invitations/${invitationToken}`).then((r) => {
    if (!r.ok) throw new Error("Invitation not found");
    return r.json();
  }).then((data) => {
    const firstName = data.firstName || "";
    const lastName = data.lastName || "";
    const fullName = `${firstName} ${lastName}`.trim();
    return {
      invitationToken: data.invitationToken || invitationToken,
      operatorName: fullName || data.operatorName || "Operador",
      firstName: data.firstName,
      lastName: data.lastName,
      email: data.email,
      companyName: data.companyName || "Empresa",
      role: data.position || data.role || "Operador TI",
      position: data.position,
      campaignName: data.campaignName || "Benchmark",
      estimatedMinutes: data.estimatedMinutes || 12,
    };
  });
}

export type EvaluationStatus = "STARTED" | "CALCULATOR_COMPLETED" | "BENCHMARK_COMPLETED";

export interface EvaluationSession {
  evaluationId: string;
  evaluationToken: string;
  status: EvaluationStatus;
}

export async function startEvaluation(
  input: string | { invitationToken: string; invitation?: InvitationDetails | null }
): Promise<EvaluationSession> {
  const invitationToken = typeof input === "string" ? input : input.invitationToken;
  const invitation = typeof input === "string" ? null : input.invitation;

  if (USE_MOCKS) {
    return delay(
      {
        evaluationId: `ev-${Date.now().toString(36)}`,
        evaluationToken: `evtok-${Date.now().toString(36)}`,
        status: "STARTED",
      },
      500
    );
  }

  const body = {
    firstName: invitation?.firstName || invitation?.operatorName?.split(" ")[0] || "Operador",
    lastName: invitation?.lastName || invitation?.operatorName?.split(" ").slice(1).join(" ") || "Invitado",
    email: invitation?.email || "operador@empresa.com",
    companyName: invitation?.companyName || "Empresa",
    position: invitation?.position || invitation?.role || "Gerente TI",
    country: "Peru",
    consentAccepted: true,
    marketingConsent: false,
    source: "OUTREACH",
    invitationToken: invitationToken,
  };

  return fetch(`${API_BASE_URL}/api/v1/evaluations`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  }).then((r) => {
    if (!r.ok) throw new Error("Error registrando la evaluación");
    return r.json();
  }).then((data) => ({
    evaluationId: data.evaluationId,
    evaluationToken: data.evaluationToken,
    status: (data.state || data.status || "STARTED") as EvaluationStatus,
  }));
}

export async function markCalculatorCompleted(evaluationId: string): Promise<{ status: EvaluationStatus }> {
  if (USE_MOCKS) return delay({ status: "CALCULATOR_COMPLETED" as const }, 300);

  const evaluationToken = useInvitationStore.getState().evaluation?.evaluationToken ?? "";
  const inputs = useCalculatorStore.getState().inputs;
  const totalKw = inputs.installedCapacityKw || 10000;
  const utilPct = inputs.currentUtilizationPct ?? 65;
  const totalMw = totalKw / 1000.0;
  const prodMw = (totalMw * utilPct) / 100.0;

  const body = {
    totalCapacityMw: totalMw > 0 ? totalMw : 10.0,
    productiveCapacityMw: prodMw >= 0 ? prodMw : 6.5,
    monthlyCostPerKw: 120.0,
    currency: "USD",
  };

  return fetch(`${API_BASE_URL}/api/v1/evaluations/${evaluationId}/calculator`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      "X-Evaluation-Token": evaluationToken,
    },
    body: JSON.stringify(body),
  }).then((r) => {
    if (!r.ok) throw new Error("Error al guardar datos de la calculadora");
    return r.json();
  }).then(() => ({ status: "CALCULATOR_COMPLETED" as const }));
}

export async function submitOperatorBenchmark(payload: {
  evaluationId: string;
  answers: Record<string, string | number> | any[];
}): Promise<{ status: EvaluationStatus }> {
  if (USE_MOCKS) return delay({ status: "BENCHMARK_COMPLETED" as const }, 500);

  const evaluationToken = useInvitationStore.getState().evaluation?.evaluationToken ?? "";

  let answerList: { questionId: string; value: number }[] = [];
  if (Array.isArray(payload.answers)) {
    answerList = payload.answers;
  } else if (typeof payload.answers === "object" && payload.answers !== null) {
    answerList = Object.entries(payload.answers).map(([questionId, val]) => ({
      questionId,
      value: Number(val) || 3,
    }));
  }

  const body = {
    questionnaireVersion: "v1",
    answers: answerList,
  };

  return fetch(`${API_BASE_URL}/api/v1/evaluations/${payload.evaluationId}/benchmark`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      "X-Evaluation-Token": evaluationToken,
    },
    body: JSON.stringify(body),
  }).then((r) => {
    if (!r.ok) throw new Error("Error al enviar el benchmark");
    return r.json();
  }).then(() => ({ status: "BENCHMARK_COMPLETED" as const }));
}

export type ReportStatus = "NOT_REQUESTED" | "REPORT_GENERATING" | "REPORT_COMPLETED" | "REPORT_FAILED";

export interface ReportStatusResponse {
  status: ReportStatus;
  pdfUrl?: string;
  excelUrl?: string;
}

const mockReportJobs = new Map<string, { startedAt: number }>();

export async function fetchReportStatus(evaluationId: string): Promise<ReportStatusResponse> {
  if (USE_MOCKS) {
    const job = mockReportJobs.get(evaluationId);
    if (!job) {
      mockReportJobs.set(evaluationId, { startedAt: Date.now() });
      return delay({ status: "REPORT_GENERATING" }, 300);
    }
    const elapsed = Date.now() - job.startedAt;
    if (elapsed < 2500) {
      return delay({ status: "REPORT_GENERATING" }, 300);
    }
    return delay({ status: "REPORT_COMPLETED", pdfUrl: "#mock-pdf", excelUrl: "#mock-excel" }, 300);
  }

  const evaluationToken = useInvitationStore.getState().evaluation?.evaluationToken ?? "";

  return fetch(`${API_BASE_URL}/api/v1/evaluations/${evaluationId}/report`, {
    headers: {
      "X-Evaluation-Token": evaluationToken,
    },
  }).then((r) => {
    if (!r.ok) throw new Error("Error consultando el estado del reporte");
    return r.json();
  }).then((data) => {
    let status: ReportStatus = "NOT_REQUESTED";
    if (data.status === "REPORT_COMPLETED" || data.status === "GENERATED" || data.status === "COMPLETED") {
      status = "REPORT_COMPLETED";
    } else if (data.status === "REPORT_GENERATING" || data.status === "PROCESSING" || data.status === "GENERATING") {
      status = "REPORT_GENERATING";
    } else if (data.status === "REPORT_FAILED" || data.status === "FAILED") {
      status = "REPORT_FAILED";
    }

    return {
      status,
      pdfUrl: data.downloadUrl || data.pdfUrl || `${API_BASE_URL}/api/v1/evaluations/${evaluationId}/report/download`,
      excelUrl: data.excelUrl,
    };
  });
}

export async function downloadReportPdfBlob(evaluationId: string): Promise<void> {
  const evaluationToken = useInvitationStore.getState().evaluation?.evaluationToken ?? "";
  const response = await fetch(`${API_BASE_URL}/api/v1/evaluations/${evaluationId}/report/download`, {
    headers: {
      "X-Evaluation-Token": evaluationToken,
    },
  });
  if (!response.ok) {
    throw new Error("No se pudo descargar el PDF");
  }
  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = `reporte-ghostload-${evaluationId.slice(0, 8)}.pdf`;
  document.body.appendChild(a);
  a.click();
  a.remove();
  window.URL.revokeObjectURL(url);
}

// --- Contacts / CSV import --------------------------------------------------

export async function prepareCampaignFromContacts(payload: {
  contactIds: string[];
}): Promise<{ ok: true; campaignId: string }> {
  if (USE_MOCKS) {
    // The real endpoint will deduplicate against existing contacts and be
    // idempotent on repeated calls with the same CSV (see UI copy on the
    // Contacts page). Here we just acknowledge the request.
    return delay({ ok: true, campaignId: `camp-${Date.now()}` }, 600);
  }
  return fetch(`${API_BASE_URL}/campaigns/from-contacts`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  }).then((r) => r.json());
}
