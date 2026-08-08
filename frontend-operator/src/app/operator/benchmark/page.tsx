"use client";

import { useRouter } from "next/navigation";
import { ArrowLeft, ArrowRight } from "lucide-react";
import { PixelMail } from "@/components/blocks/PixelIcons";
import { Card, CardContent } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { ProgressBar } from "@/components/ui/ProgressBar";
import { BenchmarkQuestionCard } from "@/components/forms/BenchmarkQuestionCard";
import { useBenchmarkStore } from "@/store/benchmarkStore";
import { useSubmitOperatorBenchmark } from "@/hooks/useInvitation";
import { useInvitationStore } from "@/store/invitationStore";
import { benchmarkQuestions } from "@/lib/scoring";
import { OperatorStepGuard } from "@/components/shared/OperatorStepGuard";

const questions = [...benchmarkQuestions].sort((a, b) => a.order - b.order);

export default function OperatorBenchmarkPage() {
  const router = useRouter();
  const evaluationId = useInvitationStore((s) => s.evaluation?.evaluationId);
  const setEvaluationStatus = useInvitationStore((s) => s.setEvaluationStatus);
  const { currentIndex, answers, setAnswer, next, back, goTo, markSubmitted } = useBenchmarkStore();
  const submitMutation = useSubmitOperatorBenchmark();

  const question = questions[currentIndex];
  const answeredCount = Object.keys(answers).length;
  const progressPct = Math.round((answeredCount / questions.length) * 100);
  const isLast = currentIndex === questions.length - 1;
  const currentAnswered = answers[question.id] !== undefined && answers[question.id] !== "";

  async function handleNext() {
    if (isLast) {
      if (evaluationId) {
        await submitMutation.mutateAsync({ evaluationId, answers });
        setEvaluationStatus("BENCHMARK_COMPLETED");
      }
      markSubmitted();
      router.push("/operator/results");
      return;
    }
    next();
  }

  return (
    <OperatorStepGuard step="benchmark">
    <div>
      <div className="mb-6">
        <div className="mb-2 flex items-center justify-between text-xs text-graphite-500">
          <span>
            {answeredCount} of {questions.length} answered
          </span>
          <span className="font-tabular">{progressPct}%</span>
        </div>
        <ProgressBar value={progressPct} />
      </div>

      <Card>
        <CardContent className="py-8">
          <BenchmarkQuestionCard
            question={question}
            value={answers[question.id]}
            onChange={(value) => setAnswer(question.id, value)}
          />
        </CardContent>
      </Card>

      <div className="mt-6 flex items-center justify-between">
        <Button variant="ghost" onClick={back} disabled={currentIndex === 0}>
          <ArrowLeft className="h-4 w-4" />
          Back
        </Button>

        <div className="hidden gap-1.5 sm:flex">
          {questions.map((q, i) => (
            <button
              key={q.id}
              onClick={() => goTo(i)}
              aria-label={`Go to question ${i + 1}`}
              className={`h-1.5 w-4 rounded-full transition-colors ${
                i === currentIndex
                  ? "bg-forest-700"
                  : answers[q.id] !== undefined
                  ? "bg-forest-300"
                  : "bg-graphite-200"
              }`}
            />
          ))}
        </div>

        <Button onClick={handleNext} disabled={!currentAnswered} loading={submitMutation.isPending}>
          {isLast ? (
            <>
              <PixelMail size={16} />
              Submit Benchmark
            </>
          ) : (
            <>
              Next
              <ArrowRight className="h-4 w-4" />
            </>
          )}
        </Button>
      </div>
    </div>
    </OperatorStepGuard>
  );
}
