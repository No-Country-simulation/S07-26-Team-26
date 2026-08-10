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

interface BenchmarkState {
  currentIndex: number;
  answers: Record<string, string | number>;
  submitted: boolean;
  submittedAt: string | null;
  setAnswer: (questionId: string, value: string | number) => void;
  goTo: (index: number) => void;
  next: () => void;
  back: () => void;
  markSubmitted: () => void;
  reset: () => void;
}

export const useBenchmarkStore = create<BenchmarkState>()(
  persist(
    (set) => ({
      currentIndex: 0,
      answers: {},
      submitted: false,
      submittedAt: null,
      setAnswer: (questionId, value) =>
        set((state) => ({ answers: { ...state.answers, [questionId]: value } })),
      goTo: (index) =>
        set({ currentIndex: Math.min(Math.max(index, 0), benchmarkQuestions.length - 1) }),
      next: () =>
        set((state) => ({
          currentIndex: Math.min(state.currentIndex + 1, benchmarkQuestions.length - 1),
        })),
      back: () => set((state) => ({ currentIndex: Math.max(state.currentIndex - 1, 0) })),
      markSubmitted: () => set({ submitted: true, submittedAt: new Date().toISOString() }),
      reset: () => set({ currentIndex: 0, answers: {}, submitted: false, submittedAt: null }),
    }),
    { name: "ghost-load-benchmark" }
  )
);

/**
 * Safe way to read derived benchmark results. Selects the stable `answers`
 * reference from the store, then memoizes the score/maturity/category
 * computation so it only re-runs when `answers` actually changes.
 */
export function useBenchmarkResults() {
  const answers = useBenchmarkStore((s) => s.answers);

  return useMemo(() => {
    const overallScore = computeOverallScore(answers);
    const maturityLevel = maturityLevelFromScore(overallScore);
    const categoryBreakdown = computeCategoryScores(answers);
    return { overallScore, maturityLevel, categoryBreakdown };
  }, [answers]);
}

/** Same idea for wizard progress -- derived from `answers`, not a store method. */
export function useBenchmarkProgress() {
  const answers = useBenchmarkStore((s) => s.answers);
  return useMemo(() => {
    const answeredCount = Object.keys(answers).length;
    return {
      answeredCount,
      progressPct: Math.round((answeredCount / benchmarkQuestions.length) * 100),
    };
  }, [answers]);
}
