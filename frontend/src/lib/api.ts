const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api/v1';

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
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
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
  listOperators: (token: string, params: OperatorParams = {}) =>
    request<OperatorPage>('/admin/operators', {
      headers: { Authorization: `Bearer ${token}` },
      ...toQuery(params),
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
