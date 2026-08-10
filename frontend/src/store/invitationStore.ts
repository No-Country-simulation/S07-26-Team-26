// ---------------------------------------------------------------------------
// invitationStore
//
// Replaces authStore/RouteGuard as the source of truth for the Operator
// role. Per the official backend flow, the Operator never logs in: they
// arrive via GET /api/v1/invitations/{invitationToken}, and starting the
// evaluation (POST /api/v1/evaluations) returns an evaluationId +
// evaluationToken that identifies them for every subsequent call.
//
// `status` mirrors the backend evaluation state machine:
//   STARTED -> CALCULATOR_COMPLETED -> BENCHMARK_COMPLETED
// It's the single source of truth for step gating (see
// hooks/useOperatorProgress.ts), replacing the old calculatorStore.kpis /
// benchmarkStore.submitted booleans for that purpose (those stores still
// hold the actual form data).
// ---------------------------------------------------------------------------
import { create } from "zustand";
import { persist } from "zustand/middleware";

export type EvaluationStatus = "STARTED" | "CALCULATOR_COMPLETED" | "BENCHMARK_COMPLETED";
export type ReportStatus = "NOT_REQUESTED" | "REPORT_GENERATING" | "REPORT_COMPLETED" | "REPORT_FAILED";

export interface InvitationInfo {
  invitationToken: string;
  operatorName: string;
  companyName: string;
  role: string;
  campaignName: string;
  estimatedMinutes: number;
}

export interface EvaluationInfo {
  evaluationId: string;
  evaluationToken: string;
  status: EvaluationStatus;
}

interface InvitationState {
  invitation: InvitationInfo | null;
  evaluation: EvaluationInfo | null;
  reportStatus: ReportStatus;
  setInvitation: (invitation: InvitationInfo) => void;
  setEvaluation: (evaluation: EvaluationInfo) => void;
  setEvaluationStatus: (status: EvaluationStatus) => void;
  setReportStatus: (status: ReportStatus) => void;
  reset: () => void;
}

export const useInvitationStore = create<InvitationState>()(
  persist(
    (set) => ({
      invitation: null,
      evaluation: null,
      reportStatus: "NOT_REQUESTED",
      setInvitation: (invitation) => set({ invitation }),
      setEvaluation: (evaluation) => set({ evaluation }),
      setEvaluationStatus: (status) =>
        set((state) => (state.evaluation ? { evaluation: { ...state.evaluation, status } } : state)),
      setReportStatus: (reportStatus) => set({ reportStatus }),
      reset: () => set({ invitation: null, evaluation: null, reportStatus: "NOT_REQUESTED" }),
    }),
    { name: "ghost-load-invitation" }
  )
);
