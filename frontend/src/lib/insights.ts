// ---------------------------------------------------------------------------
// Lightweight, rule-based "Hallazgos" (findings) and "Recomendaciones"
// (recommendations) generator for the results screen. Pure functions over
// the same CategoryScoreBreakdown already computed by lib/scoring.ts -- no
// new backend calls, no change to how scores are computed.
// ---------------------------------------------------------------------------
import type { CategoryScoreBreakdown } from "@/lib/scoring";

const RECOMMENDATION_COPY: Record<string, string> = {
  energy: "Implementa medición de consumo eléctrico por rack o clúster para localizar ineficiencias específicas.",
  gpu: "Automatiza la reasignación de GPUs inactivas — es la fuente más rápida de recuperar capacidad ya pagada.",
  cooling: "Evalúa migrar zonas de alta densidad a refrigeración líquida o híbrida para levantar el techo de expansión.",
  operations: "Unifica los paneles de Facilities y Operaciones para que ambos equipos decidan sobre los mismos datos.",
  capacity: "Adelanta simulaciones de crecimiento trimestrales para anticipar cuellos de botella de capacidad.",
};

export interface Insight {
  category: string;
  label: string;
  score: number;
  text: string;
}

export function computePercentile(score: number, allScores: number[]): number {
  if (allScores.length === 0) return 50;
  const below = allScores.filter((s) => s <= score).length;
  return Math.round((below / allScores.length) * 100);
}

export function generateFindings(breakdown: CategoryScoreBreakdown[]): Insight[] {
  const answered = breakdown.filter((b) => b.answeredCount > 0);
  if (answered.length === 0) return [];

  const sorted = [...answered].sort((a, b) => b.score - a.score);
  const strongest = sorted[0];
  const weakest = sorted[sorted.length - 1];

  const findings: Insight[] = [];
  if (strongest.score >= 60) {
    findings.push({
      category: strongest.category,
      label: strongest.label,
      score: strongest.score,
      text: `${strongest.label} es tu categoría más sólida (${strongest.score}/100) — un punto de apoyo para el resto del roadmap.`,
    });
  }
  if (weakest.category !== strongest.category) {
    findings.push({
      category: weakest.category,
      label: weakest.label,
      score: weakest.score,
      text: `${weakest.label} es el mayor punto de fuga de capacidad (${weakest.score}/100) dentro de tu benchmark.`,
    });
  }
  return findings;
}

export function generateRecommendations(breakdown: CategoryScoreBreakdown[], max = 3): Insight[] {
  const answered = breakdown.filter((b) => b.answeredCount > 0);
  return [...answered]
    .sort((a, b) => a.score - b.score)
    .slice(0, max)
    .map((b) => ({
      category: b.category,
      label: b.label,
      score: b.score,
      text: RECOMMENDATION_COPY[b.category] ?? `Prioriza mejoras en ${b.label.toLowerCase()}.`,
    }));
}
