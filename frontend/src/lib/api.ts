const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8081/api/v1';

export class ApiError extends Error {
  readonly status: number;
  readonly code: string | null;

  constructor(status: number, message: string, code: string | null = null) {
    super(message);
    this.status = status;
    this.code = code;
  }
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const isFormData = options.body instanceof FormData;
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      ...(isFormData ? {} : { 'Content-Type': 'application/json' }),
      ...options.headers,
    },
  });

  if (!response.ok) {
    let code: string | null = null;
    try {
      const body = await response.json();
      code = body?.code ?? null;
    } catch {
      // Sin cuerpo JSON; usamos el mensaje de estado.
    }
    throw new ApiError(response.status, `Error ${response.status}`, code);
  }

  return (await response.json()) as T;
}

export const apiClient = {
  login: (email: string, password: string) =>
    request<LoginResponse>('/admin/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    }),
  dashboardSummary: (token: string, params: SummaryParams = {}) =>
    request<DashboardSummary>('/admin/dashboard/summary', {
      headers: { Authorization: `Bearer ${token}` },
      ...toQuery(params),
    }),
  recentResponses: (token: string, params: RecentResponsesParams = {}) =>
    request<RecentResponsesPage>('/admin/dashboard/recent-responses', {
      headers: { Authorization: `Bearer ${token}` },
      ...toQuery(params),
    }),
  listOperators: (token: string, params: OperatorParams = {}) =>
    request<OperatorPage>('/admin/operators', {
      headers: { Authorization: `Bearer ${token}` },
      ...toQuery(params),
    }),
  listContactImports: (token: string) =>
    request<ContactImportSummary[]>('/admin/contact-imports', {
      headers: { Authorization: `Bearer ${token}` },
    }),
  importContacts: (token: string, name: string, file: File) => {
    const form = new FormData();
    form.append('name', name);
    form.append('file', file);
    return request<ContactImportResponse>('/admin/contact-imports', {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
      body: form,
    });
  },
  listCampaigns: (token: string) =>
    request<CampaignSummary[]>('/admin/campaigns', {
      headers: { Authorization: `Bearer ${token}` },
    }),
  createCampaign: (token: string, payload: CreateCampaignPayload) =>
    request<CampaignSummary>('/admin/campaigns', {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
      body: JSON.stringify(payload),
    }),
  sendCampaign: (token: string, campaignId: string) =>
    request<CampaignSummary>(`/admin/campaigns/${campaignId}/send`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
    }),
  getCampaignTracking: (token: string, campaignId: string) =>
    request<CampaignTracking>('/admin/campaigns/' + campaignId, {
      headers: { Authorization: `Bearer ${token}` },
    }),
  listPipeline: (token: string, filters: PipelineFilters = {}) => {
    const params = new URLSearchParams();
    if (filters.status) params.set('status', filters.status);
    if (filters.region) params.set('region', filters.region);
    if (filters.scoreMin !== undefined) params.set('scoreMin', String(filters.scoreMin));
    if (filters.scoreMax !== undefined) params.set('scoreMax', String(filters.scoreMax));
    const query = params.toString();
    return request<PipelineEntry[]>('/admin/crm/pipeline' + (query ? `?${query}` : ''), {
      headers: { Authorization: `Bearer ${token}` },
    });
  },
  createPipelineEntry: (token: string, payload: CreatePipelineEntryPayload) =>
    request<PipelineEntry>('/admin/crm/pipeline', {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
      body: JSON.stringify(payload),
    }),
  getPipelineDetail: (token: string, pipelineId: string) =>
    request<PipelineDetail>('/admin/crm/pipeline/' + pipelineId, {
      headers: { Authorization: `Bearer ${token}` },
    }),
  addPipelineNote: (token: string, pipelineId: string, note: string) =>
    request<PipelineDetail>(`/admin/crm/pipeline/${pipelineId}/notes`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
      body: JSON.stringify({ note }),
    }),
  transitionPipelineStatus: (
    token: string,
    pipelineId: string,
    status: string,
    note?: string
  ) =>
    request<PipelineDetail>(`/admin/crm/pipeline/${pipelineId}/status`, {
      method: 'PATCH',
      headers: { Authorization: `Bearer ${token}` },
      body: JSON.stringify({ status, note }),
    }),
  exportPipelineCsv: async (token: string, filters: PipelineFilters = {}) => {
    const params = new URLSearchParams();
    if (filters.status) params.set('status', filters.status);
    if (filters.region) params.set('region', filters.region);
    if (filters.scoreMin !== undefined) params.set('scoreMin', String(filters.scoreMin));
    if (filters.scoreMax !== undefined) params.set('scoreMax', String(filters.scoreMax));
    const query = params.toString();
    const response = await fetch(
      `${API_BASE_URL}/admin/crm/pipeline/export${query ? `?${query}` : ''}`,
      { headers: { Authorization: `Bearer ${token}` } }
    );
    if (!response.ok) {
      throw new ApiError(response.status, `Error ${response.status}`);
    }
    return response.blob();
  },
  registerEvaluation: (payload: RegisterEvaluationPayload) =>
    request<CreateEvaluationResponse>('/evaluations', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  saveCalculatorResult: (evaluationId: string, token: string, payload: CalculatorPayload) =>
    request<CalculatorResult>('/evaluations/' + evaluationId + '/calculator', {
      method: 'PUT',
      headers: { 'X-Evaluation-Token': token },
      body: JSON.stringify(payload),
    }),
  listBenchmarkQuestions: (version: string = 'v1') =>
    request<BenchmarkQuestion[]>('/benchmark/questions?version=' + version),
  submitBenchmark: (evaluationId: string, token: string, payload: SubmitBenchmarkPayload) =>
    request<BenchmarkResult>('/evaluations/' + evaluationId + '/benchmark', {
      method: 'PUT',
      headers: { 'X-Evaluation-Token': token },
      body: JSON.stringify(payload),
    }),
  saveBenchmarkProgress: (evaluationId: string, token: string, payload: SubmitBenchmarkPayload) =>
    request<BenchmarkProgress>('/evaluations/' + evaluationId + '/benchmark/progress', {
      method: 'PUT',
      headers: { 'X-Evaluation-Token': token },
      body: JSON.stringify(payload),
    }),
  getEvaluationStatus: (evaluationId: string, token: string) =>
    request<EvaluationStatus>('/evaluations/' + evaluationId + '/status', {
      headers: { 'X-Evaluation-Token': token },
    }),
};

function toQuery(params: object) {
  const searchParams = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined) {
      searchParams.set(key, String(value));
    }
  });
  const query = searchParams.toString();
  return query ? { search: `?${query}` } : {};
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresInSeconds: number;
  admin: {
    id: string;
    name: string;
    email: string;
  };
}

export interface SummaryParams {
  from?: string;
  to?: string;
  campaignId?: string;
}

export interface DashboardSummary {
  contactsLoaded: number;
  invitationsSent: number;
  linksVisited: number;
  evaluationsStarted: number;
  evaluationsCompleted: number;
  completionRate: number;
  averageBenchmarkScore: number;
  averageUtilization: number;
  accumulatedNonProductiveCapacityMw: number;
  accumulatedEstimatedAnnualCost: number;
  generatedReports: number;
  maturityDistribution: { level: string; count: number }[];
  categoryAverages: { module: string; score: number }[];
}

export interface RecentResponsesParams {
  page?: number;
  size?: number;
  from?: string;
  to?: string;
}

export interface RecentResponseItem {
  operatorId: string;
  fullName: string;
  email: string;
  companyName: string;
  evaluationId: string;
  score: number;
  percentile: number;
  maturityLevel: string;
  completedAt: string;
}

export interface RecentResponsesPage {
  items: RecentResponseItem[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface OperatorParams {
  page?: number;
  size?: number;
  state?: string;
  search?: string;
}

export interface OperatorSummary {
  operatorId: string;
  fullName: string;
  email: string;
  companyName: string;
  evaluationId: string | null;
  state: string | null;
  benchmarkScore: number | null;
  maturityLevel: string | null;
  completedAt: string | null;
  createdAt: string;
}

export interface OperatorPage {
  items: OperatorSummary[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface ContactImportSummary {
  importId: string;
  name: string;
  status: string;
  totalRows: number;
  validContacts: number;
  duplicates: number;
  invalidRows: number;
  createdAt: string;
}

export interface ImportIssue {
  row: number;
  email: string;
  code: string;
  message: string;
}

export interface ContactImportResponse extends ContactImportSummary {
  newContacts: number;
  existingContacts: number;
  issues: ImportIssue[];
}

export interface CampaignSummary {
  id: string;
  name: string;
  status: string;
  subject: string;
  recipientCount: number;
  scheduledAt: string | null;
  sentAt: string | null;
  createdAt: string;
}

export interface CreateCampaignPayload {
  name: string;
  description?: string;
  subject: string;
  message: string;
  callToActionText: string;
  contactImportId: string;
  scheduledAt?: string | null;
  timezone?: string;
}

export interface InvitationTracking {
  invitationId: string;
  firstName: string;
  lastName: string;
  email: string;
  status: string;
  sentAt: string | null;
  visitedAt: string | null;
  startedAt: string | null;
  completedAt: string | null;
  failedAt: string | null;
  failureReason: string | null;
  createdAt: string;
}

export interface CampaignTracking {
  id: string;
  name: string;
  status: string;
  description: string | null;
  subject: string;
  callToActionText: string;
  recipientCount: number;
  scheduledAt: string | null;
  sentAt: string | null;
  createdAt: string;
  invitations: InvitationTracking[];
}

export type PipelineStatusValue =
  | 'OUTREACH_PENDING'
  | 'OUTREACH_SENT'
  | 'MEETING_SCHEDULED'
  | 'CONVERTED'
  | 'LOST';

export interface PipelineFilters {
  status?: PipelineStatusValue;
  region?: string;
  scoreMin?: number;
  scoreMax?: number;
}

export interface PipelineEntry {
  id: string;
  companyName: string;
  contactName: string | null;
  email: string | null;
  region: string | null;
  benchmarkScore: number | null;
  status: PipelineStatusValue;
  noteCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreatePipelineEntryPayload {
  companyName: string;
  contactName?: string;
  email?: string;
  region?: string;
  benchmarkScore?: number;
}

export interface PipelineNote {
  id: string;
  note: string;
  createdAt: string;
}

export interface PipelineHistory {
  id: string;
  fromStatus: string;
  toStatus: string;
  changedAt: string;
}

export interface PipelineDetail extends PipelineEntry {
  notes: PipelineNote[];
  history: PipelineHistory[];
}

export interface RegisterEvaluationPayload {
  firstName: string;
  lastName: string;
  email: string;
  companyName: string;
  position?: string;
  country?: string;
  consentAccepted: boolean;
  marketingConsent: boolean;
  source: string;
  invitationToken?: string;
}

export interface CreateEvaluationResponse {
  operatorId: string;
  evaluationId: string;
  evaluationToken: string;
  state: string;
  createdAt: string;
}

export interface CalculatorPayload {
  totalCapacityMw: number;
  productiveCapacityMw: number;
  monthlyCostPerKw: number;
  currency: string;
}

export interface CalculatorResult {
  totalCapacityMw: number;
  productiveCapacityMw: number;
  nonProductiveCapacityMw: number;
  utilizationPercentage: number;
  nonProductivePercentage: number;
  monthlyCostPerKw: number;
  estimatedAnnualCost: number;
  currency: string;
  calculatedAt: string;
}

export interface BenchmarkScaleOption {
  value: number;
  label: string;
}

export interface BenchmarkQuestion {
  id: string;
  version: string;
  module: string;
  order: number;
  text: string;
  active: boolean;
  scale: BenchmarkScaleOption[];
}

export interface BenchmarkAnswerPayload {
  questionId: string;
  value: number;
}

export interface SubmitBenchmarkPayload {
  questionnaireVersion: string;
  answers: BenchmarkAnswerPayload[];
}

export interface ModuleScore {
  module: string;
  score: number;
}

export interface BenchmarkResult {
  totalScore: number;
  maturityLevel: string;
  percentile: number;
  percentileDisclaimer: string;
  moduleScores: ModuleScore[];
  completedAt: string;
}

export interface BenchmarkProgress {
  answeredCount: number;
  completionPercentage: number;
}

export interface EvaluationStatus {
  evaluationId: string;
  operatorId: string;
  firstName: string;
  lastName: string;
  email: string;
  companyName: string;
  position: string;
  state: string;
  calculatorResult: CalculatorResult | null;
  answers: BenchmarkAnswerPayload[];
  answeredCount: number;
  completionPercentage: number;
  benchmarkResult: BenchmarkResult | null;
}
