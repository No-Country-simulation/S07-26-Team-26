"use client";

import { cn } from "@/lib/utils";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";

interface Option {
  value: string;
  label: string;
  score: number;
}

interface ScaleOption {
  value: number; // 1-5
  label: string; // INEXISTENT | INITIAL | DEFINED | MANAGED | OPTIMIZED
}

interface Question {
  id: string;
  category: string;
  order: number;
  text: string;
  type: "select" | "numeric" | "scale";
  unit?: string;
  min?: number;
  max?: number;
  options?: Option[];
  scale?: ScaleOption[];
}

interface Props {
  question: Question;
  value: string | number | undefined;
  onChange: (value: string | number) => void;
}

const CATEGORY_LABELS: Record<string, string> = {
  energy: "Energy",
  gpu: "GPU",
  gpu_utilization: "GPU",
  cooling: "Cooling",
  operations: "Operations",
  capacity: "Capacity",
};

// Human-readable labels for the backend 1-5 maturity scale
const SCALE_LABEL_MAP: Record<string, string> = {
  INEXISTENT: "Inexistente",
  INITIAL: "Inicial",
  DEFINED: "Definido",
  MANAGED: "Gestionado",
  OPTIMIZED: "Optimizado",
};

// Background/border colour per maturity level (1→5)
const SCALE_COLORS: Record<number, string> = {
  1: "border-red-300 bg-red-50 text-red-800 hover:border-red-400 hover:bg-red-100",
  2: "border-orange-300 bg-orange-50 text-orange-800 hover:border-orange-400 hover:bg-orange-100",
  3: "border-gold-300 bg-gold-50 text-gold-800 hover:border-gold-400 hover:bg-gold-100",
  4: "border-forest-300 bg-forest-50 text-forest-800 hover:border-forest-400 hover:bg-forest-100",
  5: "border-forest-600 bg-forest-700 text-forest-50 hover:bg-forest-800",
};

const SCALE_COLORS_SELECTED: Record<number, string> = {
  1: "border-red-600 bg-red-600 text-white",
  2: "border-orange-500 bg-orange-500 text-white",
  3: "border-gold-600 bg-gold-600 text-white",
  4: "border-forest-600 bg-forest-600 text-white",
  5: "border-forest-800 bg-forest-800 text-white",
};

export function BenchmarkQuestionCard({ question, value, onChange }: Props) {
  const categoryLabel = CATEGORY_LABELS[question.category] ?? question.category;

  return (
    <div>
      <p className="mb-2 text-xs font-semibold uppercase tracking-wide text-gold-700">
        {categoryLabel} · Pregunta {question.order} de 20
      </p>
      <h2 className="mb-6 text-xl font-semibold leading-snug text-graphite-900">{question.text}</h2>

      {/* ── 1-5 Maturity scale (backend mode) ── */}
      {question.type === "scale" && question.scale && (
        <div className="flex flex-col gap-2 sm:flex-row sm:flex-wrap">
          {question.scale.map((option) => {
            const selected = value === option.value;
            const baseClass = selected
              ? SCALE_COLORS_SELECTED[option.value]
              : SCALE_COLORS[option.value];
            return (
              <button
                key={option.value}
                type="button"
                onClick={() => onChange(option.value)}
                className={cn(
                  "flex flex-1 flex-col items-center gap-1 rounded-md border px-4 py-3 text-center transition-colors",
                  baseClass,
                  "min-w-[100px]"
                )}
              >
                <span className="text-lg font-bold">{option.value}</span>
                <span className="text-[11px] font-semibold uppercase tracking-wide">
                  {SCALE_LABEL_MAP[option.label] ?? option.label}
                </span>
              </button>
            );
          })}
        </div>
      )}

      {/* ── Select with custom options (mock mode) ── */}
      {question.type === "select" && question.options && (
        <div className="grid grid-cols-1 gap-2.5 sm:grid-cols-2">
          {question.options.map((option) => {
            const selected = value === option.value;
            return (
              <button
                key={option.value}
                type="button"
                onClick={() => onChange(option.value)}
                className={cn(
                  "rounded-md border px-4 py-3 text-left text-sm font-medium transition-colors",
                  selected
                    ? "border-forest-700 bg-forest-50 text-forest-800"
                    : "border-graphite-200 bg-white text-graphite-700 hover:border-forest-700/40 hover:bg-forest-50/40"
                )}
              >
                {option.label}
              </button>
            );
          })}
        </div>
      )}

      {/* ── Numeric input (mock mode) ── */}
      {question.type === "numeric" && (
        <div className="max-w-xs">
          <Label htmlFor={question.id}>
            Value ({question.min}–{question.max}
            {question.unit})
          </Label>
          <div className="relative">
            <Input
              id={question.id}
              type="number"
              min={question.min}
              max={question.max}
              value={value ?? ""}
              onChange={(e) => onChange(Number(e.target.value))}
              className="pr-10"
            />
            {question.unit && (
              <span className="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-xs text-graphite-400">
                {question.unit}
              </span>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
