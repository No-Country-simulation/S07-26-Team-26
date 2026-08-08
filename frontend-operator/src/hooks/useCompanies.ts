import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { fetchCompanies, fetchCompanyById, fetchCompanyByEmail, createCompany, createCompaniesFromCsv, type NewCompanyInput } from "@/services/api";
import { useAuthStore } from "@/store/authStore";

export function useCompanies() {
  return useQuery({
    queryKey: ["companies"],
    queryFn: fetchCompanies,
  });
}

export function useCompany(id: string | undefined) {
  return useQuery({
    queryKey: ["companies", id],
    queryFn: () => fetchCompanyById(id as string),
    enabled: Boolean(id),
  });
}

export function useCompanyByEmail(email: string | undefined) {
  return useQuery({
    queryKey: ["companies", "by-email", email],
    queryFn: () => fetchCompanyByEmail(email as string),
    enabled: Boolean(email),
  });
}

/**
 * Scope-aware company list: ROOT_ADMIN sees every company; a scoped ADMIN
 * sees only companies whose assignedAdminId matches their own admin id.
 */
export function useVisibleCompanies() {
  const session = useAuthStore((s) => s.session);
  const query = useCompanies();

  const companies = query.data;
  const visible =
    !companies || session?.role === "ROOT_ADMIN"
      ? companies
      : companies.filter((c) => c.assignedAdminId === session?.adminId);

  return { ...query, data: visible };
}

export function useCreateCompany() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (input: NewCompanyInput) => createCompany(input),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["companies"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
    },
  });
}

export function useCreateCompaniesFromCsv() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      inputs,
      assignedAdminId,
    }: {
      inputs: Omit<NewCompanyInput, "assignedAdminId">[];
      assignedAdminId: string | null;
    }) => createCompaniesFromCsv(inputs, assignedAdminId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["companies"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
    },
  });
}
