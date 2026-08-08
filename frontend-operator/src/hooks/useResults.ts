import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  fetchPdfs,
  fetchEvaluationByCompanyId,
  fetchBenchmarkSchema,
  submitBenchmark,
  generatePdfReport,
} from "@/services/api";

export function usePdfs() {
  return useQuery({ queryKey: ["pdfs"], queryFn: fetchPdfs });
}

export function useEvaluation(companyId: string | undefined) {
  return useQuery({
    queryKey: ["evaluations", companyId],
    queryFn: () => fetchEvaluationByCompanyId(companyId as string),
    enabled: Boolean(companyId),
  });
}

export function useBenchmarkSchema() {
  return useQuery({ queryKey: ["benchmark", "schema"], queryFn: fetchBenchmarkSchema, staleTime: Infinity });
}

export function useSubmitBenchmark() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: submitBenchmark,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
      queryClient.invalidateQueries({ queryKey: ["companies"] });
    },
  });
}

export function useGeneratePdf() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: generatePdfReport,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["pdfs"] });
    },
  });
}
