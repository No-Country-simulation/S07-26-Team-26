"use client";

import { cn } from "@/lib/utils";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";

interface Option {
  value: string;
  label: string;
  score: number;
}

interface Question {
  id: string;
  category: string;
  order: number;
  text: string;
  type: "select" | "numeric";
  unit?: string;
  min?: number;
  max?: number;
  options?: Option[];
}

interface Props {
  question: Question;
  value: string | number | undefined;
  onChange: (value: string | number) => void;
}

const CATEGORY_LABELS: Record<string, string> = {
  energy: "Energy",
  gpu: "GPU",
  cooling: "Cooling",
  operations: "Operations",
  capacity: "Capacity",
};

export function BenchmarkQuestionCard({ question, value, onChange }: Props) {
  return (
    <div>
      <p className="mb-2 text-xs font-semibold uppercase tracking-wide text-gold-700">
        {CATEGORY_LABELS[question.category] ?? question.category} · Question {question.order} of 20
      </p>
      <h2 className="mb-6 text-xl font-semibold leading-snug text-graphite-900">{question.text}</h2>

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
