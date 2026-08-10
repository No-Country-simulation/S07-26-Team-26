import { useQuery } from "@tanstack/react-query";
import {
  fetchDashboardKpis,
  fetchInvitationFunnel,
  fetchMaturityDistribution,
  fetchWeeklyActivity,
} from "@/services/api";
import { useAuthStore } from "@/store/authStore";
import { useCompanies } from "@/hooks/useCompanies";

/**
 * ROOT_ADMIN gets unrestricted fleet-wide KPIs. A scoped ADMIN gets KPIs
 * computed only from the companies assigned to them.
 */
export function useDashboardKpis() {
  const session = useAuthStore((s) => s.session);
  const { data: companies } = useCompanies();

  const scopeCompanyIds =
    session?.role === "ADMIN" && companies
      ? companies.filter((c) => c.assignedAdminId === session.adminId).map((c) => c.id)
      : undefined;

  return useQuery({
    queryKey: ["dashboard", "kpis", scopeCompanyIds ?? "all"],
    queryFn: () => fetchDashboardKpis(scopeCompanyIds),
    enabled: session?.role !== "ADMIN" || Boolean(companies),
  });
}

export function useInvitationFunnel() {
  return useQuery({ queryKey: ["dashboard", "funnel"], queryFn: fetchInvitationFunnel });
}

export function useMaturityDistribution() {
  return useQuery({ queryKey: ["dashboard", "maturity"], queryFn: fetchMaturityDistribution });
}

export function useWeeklyActivity() {
  return useQuery({ queryKey: ["dashboard", "weekly-activity"], queryFn: fetchWeeklyActivity });
}
