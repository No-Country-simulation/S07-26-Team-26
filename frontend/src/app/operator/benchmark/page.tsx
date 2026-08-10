"use client";

import React, { useEffect, useState } from "react";
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
  const { currentIndex, answers, setAnswer, next, back, goTo, markSubmitted } =
    useBenchmarkStore();
  const submitMutation = useSubmitOperatorBenchmark();

  const question = questions[currentIndex];
  const answeredCount = Object.keys(answers).length;
  const progressPct = Math.round((answeredCount / questions.length) * 100);
  const isLast = currentIndex === questions.length - 1;
  const currentAnswered =
    answers[question.id] !== undefined && answers[question.id] !== "";

  // Grouping logic: show 5 questions per step (stepper). Maintain local
  // `groupIndex` for UI paging and keep the store's `currentIndex` in sync
  // for compatibility with other helpers.
  const [groupIndex, setGroupIndex] = useState(Math.floor(currentIndex / 5));
  const groupsCount = Math.ceil(questions.length / 5);

  useEffect(() => {
    // If there are unanswered questions, navigate to the first unanswered one.
    const firstUnanswered = questions.findIndex(
      (q) => answers[q.id] === undefined || answers[q.id] === "",
    );
    if (firstUnanswered !== -1) {
      goTo(firstUnanswered);
      setGroupIndex(Math.floor(firstUnanswered / 5));
      // ensure the top of the page is visible when jumping to the first
      // unanswered question
      setTimeout(() => window.scrollTo({ top: 0, behavior: "auto" }), 0);
      return;
    }

    // If there are no answers but currentIndex is non-zero (persisted state),
    // reset to the start of the wizard.
    if (Object.keys(answers).length === 0 && currentIndex !== 0) {
      goTo(0);
      setGroupIndex(0);
      return;
    }

    // Otherwise align the local group index with the store's currentIndex.
    setGroupIndex(Math.floor(currentIndex / 5));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const groupStart = groupIndex * 5;
  const groupQuestions = questions.slice(groupStart, groupStart + 5);
  const groupAnswered = groupQuestions.every(
    (q) => answers[q.id] !== undefined && answers[q.id] !== "",
  );
  const isLastGroup = groupIndex === groupsCount - 1;

  async function handleNextGroup() {
    if (isLastGroup) {
      if (evaluationId) {
        await submitMutation.mutateAsync({ evaluationId, answers });
        setEvaluationStatus("BENCHMARK_COMPLETED");
      }
      markSubmitted();
      router.push("/operator/results");
      return;
    }

    const nextGroupStart = (groupIndex + 1) * 5;
    goTo(nextGroupStart);
    setGroupIndex((g) => g + 1);
    // scroll to top of page when moving between groups
    setTimeout(() => window.scrollTo({ top: 0, behavior: "smooth" }), 0);
  }

  function handlePrevGroup() {
    if (groupIndex === 0) return;
    const prevStart = Math.max(0, (groupIndex - 1) * 5);
    goTo(prevStart);
    setGroupIndex((g) => g - 1);
    // scroll to top of page when moving between groups
    setTimeout(() => window.scrollTo({ top: 0, behavior: "smooth" }), 0);
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
