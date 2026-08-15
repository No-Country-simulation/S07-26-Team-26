import { useInvitationStore } from "@/store/invitationStore";

export type OperatorStepId = "calculator" | "benchmark" | "results" | "report";

export interface OperatorStepStatus {
  id: OperatorStepId;
  href: string;
  label: string;
  /** True once the operator has actually completed this step. */
  complete: boolean;
  /** True if the operator is allowed to navigate here right now. */
  unlocked: boolean;
}

// ---------------------------------------------------------------------------
// Official Operator flow: Invitacion -> Calculadora -> Benchmark ->
// Resultados -> Generacion del Reporte -> Descarga del PDF. Strictly
// linear, gated by the evaluation's backend status (STARTED ->
// CALCULATOR_COMPLETED -> BENCHMARK_COMPLETED) rather than local form
// state, so the nav lock always matches what the backend actually
// recorded. Used by both the step nav (to lock future steps) and by each
// page (to redirect back if someone lands on a step directly, e.g. via a
// bookmarked URL, before its prerequisite is done).
// ---------------------------------------------------------------------------
export function useOperatorProgress(): OperatorStepStatus[] {
  const status = useInvitationStore((s) => s.evaluation?.status);

  const calculatorComplete = status === "CALCULATOR_COMPLETED" || status === "BENCHMARK_COMPLETED";
  const benchmarkComplete = status === "BENCHMARK_COMPLETED";

  return [
    { id: "calculator", href: "/operator/calculator", label: "Calculadora", complete: calculatorComplete, unlocked: true },
    { id: "benchmark", href: "/operator/benchmark", label: "Benchmark", complete: benchmarkComplete, unlocked: calculatorComplete },
    { id: "results", href: "/operator/results", label: "Resultados", complete: benchmarkComplete, unlocked: benchmarkComplete },
    { id: "report", href: "/operator/pdf", label: "Mi Reporte", complete: benchmarkComplete, unlocked: benchmarkComplete },
  ];
}

/** The furthest step the operator is currently allowed to be on. */
export function useFurthestUnlockedStep(): OperatorStepStatus {
  const steps = useOperatorProgress();
  return [...steps].reverse().find((s) => s.unlocked) ?? steps[0];
}
