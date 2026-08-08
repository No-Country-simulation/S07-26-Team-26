import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  fetchInvitation,
  startEvaluation,
  markCalculatorCompleted,
  submitOperatorBenchmark,
  fetchReportStatus,
} from "@/services/api";
import { useInvitationStore } from "@/store/invitationStore";

export function useInvitationQuery(invitationToken: string) {
  return useQuery({
    queryKey: ["invitation", invitationToken],
    queryFn: () => fetchInvitation(invitationToken),
    enabled: Boolean(invitationToken),
    retry: false,
  });
}

export function useStartEvaluation() {
  return useMutation({ mutationFn: startEvaluation });
}

export function useMarkCalculatorCompleted() {
  return useMutation({ mutationFn: markCalculatorCompleted });
}

export function useSubmitOperatorBenchmark() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: submitOperatorBenchmark,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
      queryClient.invalidateQueries({ queryKey: ["companies"] });
    },
  });
}

// Polls GET /api/v1/evaluations/{evaluationId}/report until it leaves
// REPORT_GENERATING. Mirrors the status straight into invitationStore so
// every screen (the loading gate and the topbar/step nav) reads from one
// place.
export function useReportStatusPolling(evaluationId: string | undefined) {
  const setReportStatus = useInvitationStore((s) => s.setReportStatus);

  return useQuery({
    queryKey: ["evaluations", evaluationId, "report"],
    queryFn: async () => {
      const result = await fetchReportStatus(evaluationId as string);
      setReportStatus(result.status);
      return result;
    },
    enabled: Boolean(evaluationId),
    refetchInterval: (query) => {
      const status = query.state.data?.status;
      return status === "REPORT_GENERATING" || status === undefined ? 1500 : false;
    },
  });
}
