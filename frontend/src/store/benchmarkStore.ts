// ---------------------------------------------------------------------------
// benchmarkStore
//
// Drives the 20-question wizard: current question, all answers so far, and
// submission status. Scoring logic lives in lib/scoring.ts so it can be
// reused server-side later.
//
// IMPORTANT: derived values (score, maturity level, category breakdown) are
// NOT store fields or store methods. A Zustand selector must return the
// same reference when nothing relevant changed, or React's
// useSyncExternalStore throws "getSnapshot should be cached" / risks an
// infinite loop. A method like `categoryBreakdown: () => computeCategoryScores(...)`
// builds a brand-new array on every call, so `useBenchmarkStore(s => s.categoryBreakdown())`
// is unsafe. Instead, select the raw `answers` state (a stable reference)
// and derive everything else with useMemo -- see useBenchmarkResults() below.
// ---------------------------------------------------------------------------
import { useMemo } from "react";
import { create } from "zustand";
import { persist } from "zustand/middleware";
import { benchmarkQuestions, computeCategoryScores, computeOverallScore, maturityLevelFromScore } from "@/lib/scoring";
import type { BackendQuestion, BackendBenchmarkResult } from "@/services/api";

// Module code → frontend category id mapping
const MODULE_TO_CATEGORY: Record<string, string> = {
  ENERGY: "energy",
  GPU_UTILIZATION: "gpu",
  COOLING: "cooling",
  OPERATIONS: "operations",
  CAPACITY: "capacity",
};

const MODULE_LABELS: Record<string, string> = {
  ENERGY: "Energy",
  GPU_UTILIZATION: "GPU Utilization",
  COOLING: "Cooling",
  OPERATIONS: "Operations",
  CAPACITY: "Capacity",
};

interface BenchmarkState {
  currentIndex: number;
  answers: Record<string, string | number>;
  submitted: boolean;
  submittedAt: string | null;

  // Real backend questions loaded from GET /api/v1/benchmark/questions.
  // Null means not loaded yet (or mock mode — falls back to local JSON).
  backendQuestions: BackendQuestion[] | null;

  // Result returned by the backend after a successful submit.
  // When present, the results page uses this instead of local scoring.
  backendResult: BackendBenchmarkResult | null;

  setAnswer: (questionId: string, value: string | number) => void;
  goTo: (index: number) => void;
  next: () => void;
  back: () => void;
  markSubmitted: () => void;
  reset: () => void;
  setBackendQuestions: (questions: BackendQuestion[]) => void;
  setBackendResult: (result: BackendBenchmarkResult) => void;
}

export const useBenchmarkStore = create<BenchmarkState>()(
  persist(
    (set, get) => ({
      currentIndex: 0,
      answers: {},
      submitted: false,
      submittedAt: null,
      backendQuestions: null,
      backendResult: null,

      setAnswer: (questionId, value) =>
        set((state) => ({ answers: { ...state.answers, [questionId]: value } })),
      goTo: (index) => {
        const total = get().backendQuestions?.length ?? benchmarkQuestions.length;
        set({ currentIndex: Math.min(Math.max(index, 0), total - 1) });
      },
      next: () =>
        set((state) => {
          const total = state.backendQuestions?.length ?? benchmarkQuestions.length;
          return { currentIndex: Math.min(state.currentIndex + 1, total - 1) };
        }),
      back: () => set((state) => ({ currentIndex: Math.max(state.currentIndex - 1, 0) })),
      markSubmitted: () => set({ submitted: true, submittedAt: new Date().toISOString() }),
      reset: () =>
        set({
          currentIndex: 0,
          answers: {},
          submitted: false,
          submittedAt: null,
          backendQuestions: null,
          backendResult: null,
        }),
      setBackendQuestions: (questions) => set({ backendQuestions: questions }),
      setBackendResult: (result) => set({ backendResult: result }),
    }),
    { 
      name: "ghost-load-benchmark",
      // Don't persist backend questions or result — they are re-fetched each session.
      // Only persist navigation/answer state so the wizard can be resumed.
      partialize: (state) => ({
        currentIndex: state.currentIndex,
        answers: state.answers,
        submitted: state.submitted,
        submittedAt: state.submittedAt,
      }),
    }
  )
);

// ---------------------------------------------------------------------------
// Helpers to convert backend questions to a format the existing UI can use.
// ---------------------------------------------------------------------------

/** Returns the active question list: real backend questions when loaded, local mock otherwise. */
export function useActiveQuestions() {
  const backendQuestions = useBenchmarkStore((s) => s.backendQuestions);
  return useMemo(() => {
    if (backendQuestions && backendQuestions.length > 0) {
      return backendQuestions
        .filter((q) => q.active)
        .sort((a, b) => a.order - b.order)
        .map((q) => ({
          id: q.id, // UUID — this is what the backend expects on submit
          category: MODULE_TO_CATEGORY[q.module] ?? q.module.toLowerCase(),
          order: q.order,
          text: q.text,
          // Backend questions always use the 1-5 maturity scale
          type: "scale" as const,
          scale: q.scale,
          // These keep the existing Question interface happy:
          options: undefined as undefined,
          unit: undefined as undefined,
          min: undefined as undefined,
          max: undefined as undefined,
        }));
    }
    // Fall back to the local mock questions (used in mock mode or if the fetch fails)
    return [...benchmarkQuestions].sort((a, b) => a.order - b.order);
  }, [backendQuestions]);
}

export const MODULE_LABEL = (module: string) => MODULE_LABELS[module] ?? module;

// ---------------------------------------------------------------------------
// Derived result helpers
// ---------------------------------------------------------------------------

/**
 * Safe way to read derived benchmark results.
 *
 * - When backendResult is available (real backend submit was successful), use it.
 * - Otherwise fall back to local scoring (mock mode or result not yet set).
 */
export function useBenchmarkResults() {
  const answers = useBenchmarkStore((s) => s.answers);
  const backendResult = useBenchmarkStore((s) => s.backendResult);
  const backendQuestions = useBenchmarkStore((s) => s.backendQuestions);

  return useMemo(() => {
    if (backendResult) {
      // Map backend module scores to the CategoryScoreBreakdown shape the UI expects
      const categoryBreakdown = backendResult.moduleScores.map((ms) => ({
        category: (MODULE_TO_CATEGORY[ms.module] ?? ms.module.toLowerCase()) as any,
        label: MODULE_LABELS[ms.module] ?? ms.module,
        // Backend scores are already 0-100
        score: Math.round(ms.score),
        answeredCount: 4, // 4 questions per module (approximate; not critical for display)
        totalCount: 4,
      }));

      return {
        overallScore: Math.round(backendResult.totalScore),
        maturityLevel: mapBackendMaturity(backendResult.maturityLevel),
        categoryBreakdown,
      };
    }

    // Local scoring (mock mode)
    const overallScore = computeOverallScore(answers);
    const maturityLevel = maturityLevelFromScore(overallScore);
    const categoryBreakdown = computeCategoryScores(answers);
    return { overallScore, maturityLevel, categoryBreakdown };
  }, [answers, backendResult, backendQuestions]);
}

/** Converts backend MaturityLevel enum strings to the frontend MaturityLevel type. */
function mapBackendMaturity(level: string): import("@/lib/scoring").MaturityLevel {
  const MAP: Record<string, import("@/lib/scoring").MaturityLevel> = {
    CRITICAL: "Critical",
    OPERATIONAL_RISK: "Operational Risk",
    GROWING: "Growing",
    MATURE: "Mature",
    LEADER: "Leader",
    // Fallbacks in case the backend uses the same casing already
    Critical: "Critical",
    "Operational Risk": "Operational Risk",
    Growing: "Growing",
    Mature: "Mature",
    Leader: "Leader",
  };
  return MAP[level] ?? "Growing";
}

/** Same idea for wizard progress -- derived from `answers`, not a store method. */
export function useBenchmarkProgress() {
  const answers = useBenchmarkStore((s) => s.answers);
  const backendQuestions = useBenchmarkStore((s) => s.backendQuestions);
  return useMemo(() => {
    const total = backendQuestions && backendQuestions.length > 0
      ? backendQuestions.length
      : benchmarkQuestions.length;
    const answeredCount = Object.keys(answers).length;
    return {
      answeredCount,
      progressPct: Math.round((answeredCount / total) * 100),
    };
  }, [answers, backendQuestions]);
}
