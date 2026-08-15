"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { ArrowLeft, ArrowRight, Loader2 } from "lucide-react";
import { PixelMail } from "@/components/blocks/PixelIcons";
import { Card, CardContent } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { ProgressBar } from "@/components/ui/ProgressBar";
import { BenchmarkQuestionCard } from "@/components/forms/BenchmarkQuestionCard";
import { useBenchmarkStore, useActiveQuestions } from "@/store/benchmarkStore";
import { useSubmitOperatorBenchmark } from "@/hooks/useInvitation";
import { useInvitationStore } from "@/store/invitationStore";
import { OperatorStepGuard } from "@/components/shared/OperatorStepGuard";
import { fetchBenchmarkQuestions, USE_MOCKS } from "@/services/api";

export default function OperatorBenchmarkPage() {
  const router = useRouter();
  const evaluationId = useInvitationStore((s) => s.evaluation?.evaluationId);
  const setEvaluationStatus = useInvitationStore((s) => s.setEvaluationStatus);

  const {
    currentIndex,
    answers,
    setAnswer,
    goTo,
    markSubmitted,
    setBackendQuestions,
    setBackendResult,
  } = useBenchmarkStore();

  const submitMutation = useSubmitOperatorBenchmark();

  // Load backend questions on mount (no-op in mock mode)
  const [questionsLoading, setQuestionsLoading] = useState(!USE_MOCKS);
  const [questionsError, setQuestionsError] = useState<string | null>(null);

  useEffect(() => {
    if (USE_MOCKS) return; // mock mode uses local JSON via useActiveQuestions()

    let cancelled = false;
    setQuestionsLoading(true);
    fetchBenchmarkQuestions("v1")
      .then((qs) => {
        if (!cancelled && qs.length > 0) {
          setBackendQuestions(qs);
        }
      })
      .catch((err) => {
        if (!cancelled) setQuestionsError(err.message ?? "Error cargando preguntas");
      })
      .finally(() => {
        if (!cancelled) setQuestionsLoading(false);
      });

    return () => {
      cancelled = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Active questions: real backend questions (UUIDs) OR local mock questions
  const questions = useActiveQuestions();

  const answeredCount = Object.keys(answers).length;
  const progressPct = Math.round((answeredCount / questions.length) * 100);

  // Grouping logic: show 5 questions per step
  const [groupIndex, setGroupIndex] = useState(Math.floor(currentIndex / 5));
  const groupsCount = Math.ceil(questions.length / 5);

  useEffect(() => {
    // If there are unanswered questions, navigate to the first unanswered one
    const firstUnanswered = questions.findIndex(
      (q) => answers[q.id] === undefined || answers[q.id] === ""
    );
    if (firstUnanswered !== -1) {
      goTo(firstUnanswered);
      setGroupIndex(Math.floor(firstUnanswered / 5));
      setTimeout(() => window.scrollTo({ top: 0, behavior: "auto" }), 0);
      return;
    }

    // Reset to start if answers are empty but index is non-zero
    if (Object.keys(answers).length === 0 && currentIndex !== 0) {
      goTo(0);
      setGroupIndex(0);
      return;
    }

    setGroupIndex(Math.floor(currentIndex / 5));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [questions.length]); // re-run when questions load so index aligns

  const groupStart = groupIndex * 5;
  const groupQuestions = questions.slice(groupStart, groupStart + 5);
  const groupAnswered = groupQuestions.every(
    (q) => answers[q.id] !== undefined && answers[q.id] !== ""
  );
  const isLastGroup = groupIndex === groupsCount - 1;

  async function handleNextGroup() {
    if (isLastGroup) {
      if (evaluationId) {
        const result = await submitMutation.mutateAsync({ evaluationId, answers });
        // Store the backend result so the results page can display real server scores
        if (result.backendResult) {
          setBackendResult(result.backendResult);
        }
        setEvaluationStatus("BENCHMARK_COMPLETED");
      }
      markSubmitted();
      router.push("/operator/results");
      return;
    }

    const nextGroupStart = (groupIndex + 1) * 5;
    goTo(nextGroupStart);
    setGroupIndex((g) => g + 1);
    setTimeout(() => window.scrollTo({ top: 0, behavior: "smooth" }), 0);
  }

  function handlePrevGroup() {
    if (groupIndex === 0) return;
    const prevStart = Math.max(0, (groupIndex - 1) * 5);
    goTo(prevStart);
    setGroupIndex((g) => g - 1);
    setTimeout(() => window.scrollTo({ top: 0, behavior: "smooth" }), 0);
  }

  // Loading state while fetching questions
  if (questionsLoading) {
    return (
      <OperatorStepGuard step="benchmark">
        <div className="flex flex-col items-center justify-center gap-4 py-20 text-graphite-500">
          <Loader2 className="h-8 w-8 animate-spin" />
          <p className="text-sm">Cargando preguntas del benchmark…</p>
        </div>
      </OperatorStepGuard>
    );
  }

  if (questionsError) {
    return (
      <OperatorStepGuard step="benchmark">
        <div className="rounded-md border border-red-200 bg-red-50 p-6 text-center text-sm text-red-700">
          No se pudieron cargar las preguntas: {questionsError}
        </div>
      </OperatorStepGuard>
    );
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

        <div className="space-y-4">
          {groupQuestions.map((q) => (
            <Card key={q.id}>
              <CardContent className="py-6">
                <BenchmarkQuestionCard
                  question={q}
                  value={answers[q.id]}
                  onChange={(value) => setAnswer(q.id, value)}
                />
              </CardContent>
            </Card>
          ))}

          <div className="mt-4 flex items-center justify-between">
            <Button
              variant="ghost"
              onClick={handlePrevGroup}
              disabled={groupIndex === 0}
            >
              <ArrowLeft className="h-4 w-4" />
              Prev
            </Button>

            <div className="text-sm text-graphite-500">
              Step {groupIndex + 1} of {groupsCount}
            </div>

            <Button
              onClick={handleNextGroup}
              disabled={!groupAnswered}
              loading={submitMutation.isPending}
            >
              {isLastGroup ? (
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
      </div>
    </OperatorStepGuard>
  );
}
