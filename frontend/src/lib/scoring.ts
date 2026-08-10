// Central scoring engine for the AI Data Center Benchmark.
// Kept isolated from UI so the same logic can move server-side later
// without touching any component.

import benchmarkSchema from "@/mock/benchmark.json";

export type BenchmarkAnswers = Record<string, string | number>;

export type CategoryId = "energy" | "gpu" | "cooling" | "operations" | "capacity";

export type MaturityLevel =
  | "Critical"
  | "Operational Risk"
  | "Growing"
  | "Mature"
  | "Leader";

interface Question {
  id: string;
  category: CategoryId;
  order: number;
  text: string;
  type: "select" | "numeric";
  unit?: string;
  min?: number;
  max?: number;
  options?: { value: string; label: string; score: number }[];
}

const QUESTIONS = benchmarkSchema.questions as Question[];
const CATEGORIES = benchmarkSchema.categories as { id: CategoryId; label: string }[];

/** Normalizes a single answer to a 0-100 score. */
export function scoreQuestion(question: Question, answer: string | number | undefined): number | null {
  if (answer === undefined || answer === null || answer === "") return null;

  if (question.type === "numeric") {
    const min = question.min ?? 0;
    const max = question.max ?? 100;
    const value = Number(answer);
    if (Number.isNaN(value)) return null;
    const clamped = Math.min(Math.max(value, min), max);
    return ((clamped - min) / (max - min)) * 100;
  }

  const option = question.options?.find((o) => o.value === answer);
  return option ? option.score : null;
}

export interface CategoryScoreBreakdown {
  category: CategoryId;
  label: string;
  score: number;
  answeredCount: number;
  totalCount: number;
}

export function computeCategoryScores(answers: BenchmarkAnswers): CategoryScoreBreakdown[] {
  return CATEGORIES.map(({ id, label }) => {
    const categoryQuestions = QUESTIONS.filter((q) => q.category === id);
    const scored = categoryQuestions
      .map((q) => scoreQuestion(q, answers[q.id]))
      .filter((s): s is number => s !== null);

    const score = scored.length ? Math.round(scored.reduce((a, b) => a + b, 0) / scored.length) : 0;

    return {
      category: id,
      label,
      score,
      answeredCount: scored.length,
      totalCount: categoryQuestions.length,
    };
  });
}

export function computeOverallScore(answers: BenchmarkAnswers): number {
  const breakdown = computeCategoryScores(answers);
  const withAnswers = breakdown.filter((b) => b.answeredCount > 0);
  if (!withAnswers.length) return 0;
  return Math.round(withAnswers.reduce((a, b) => a + b.score, 0) / withAnswers.length);
}

export function maturityLevelFromScore(score: number): MaturityLevel {
  if (score <= 20) return "Critical";
  if (score <= 40) return "Operational Risk";
  if (score <= 60) return "Growing";
  if (score <= 80) return "Mature";
  return "Leader";
}

export function maturityColor(level: MaturityLevel | null | undefined): string {
  switch (level) {
    case "Critical":
      return "text-red-700 bg-red-50 border-red-200";
    case "Operational Risk":
      return "text-orange-700 bg-orange-50 border-orange-200";
    case "Growing":
      return "text-gold-700 bg-gold-50 border-gold-100";
    case "Mature":
      return "text-forest-700 bg-forest-50 border-forest-100";
    case "Leader":
      return "text-forest-50 bg-forest-700 border-forest-700";
    default:
      return "text-graphite-600 bg-graphite-100 border-graphite-200";
  }
}

export { QUESTIONS as benchmarkQuestions, CATEGORIES as benchmarkCategories };
