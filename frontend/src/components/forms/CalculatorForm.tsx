"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useRouter } from "next/navigation";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";
import { Button } from "@/components/ui/Button";
import { useCalculatorStore } from "@/store/calculatorStore";
import { useInvitationStore } from "@/store/invitationStore";
import { useMarkCalculatorCompleted } from "@/hooks/useInvitation";

const schema = z.object({
  installedCapacityKw: z.coerce.number().min(1, "Required"),
  currentUtilizationPct: z.coerce.number().min(0).max(100),
  gpuCount: z.coerce.number().min(1, "Required"),
  powerConsumptionKw: z.coerce.number().min(0, "Required"),
  coolingCapacityKw: z.coerce.number().min(0, "Required"),
  growthExpectationPct: z.coerce.number().min(0).max(500),
});

type FormValues = z.infer<typeof schema>;

const FIELDS: {
  name: keyof FormValues;
  label: string;
  suffix: string;
  placeholder: string;
}[] = [
  {
    name: "installedCapacityKw",
    label: "Installed Electrical Capacity",
    suffix: "kW",
    placeholder: "18000",
  },
  {
    name: "currentUtilizationPct",
    label: "Current Utilization",
    suffix: "%",
    placeholder: "72",
  },
  {
    name: "gpuCount",
    label: "GPU Count",
    suffix: "units",
    placeholder: "4200",
  },
  {
    name: "powerConsumptionKw",
    label: "Power Consumption",
    suffix: "kW",
    placeholder: "15400",
  },
  {
    name: "coolingCapacityKw",
    label: "Cooling Capacity",
    suffix: "kW",
    placeholder: "19000",
  },
  {
    name: "growthExpectationPct",
    label: "Growth Expectation (12mo)",
    suffix: "%",
    placeholder: "35",
  },
];

export function CalculatorForm() {
  const router = useRouter();
  const setInputs = useCalculatorStore((s) => s.setInputs);
  const computeKpis = useCalculatorStore((s) => s.computeKpis);
  const storedInputs = useCalculatorStore((s) => s.inputs);
  const evaluationId = useInvitationStore((s) => s.evaluation?.evaluationId);
  const setEvaluationStatus = useInvitationStore((s) => s.setEvaluationStatus);
  const markCalculatorCompleted = useMarkCalculatorCompleted();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      installedCapacityKw: storedInputs.installedCapacityKw ?? undefined,
      currentUtilizationPct: storedInputs.currentUtilizationPct ?? undefined,
      gpuCount: storedInputs.gpuCount ?? undefined,
      powerConsumptionKw: storedInputs.powerConsumptionKw ?? undefined,
      coolingCapacityKw: storedInputs.coolingCapacityKw ?? undefined,
      growthExpectationPct: storedInputs.growthExpectationPct ?? undefined,
    },
  });

  async function onSubmit(values: FormValues) {
    setInputs(values);
    computeKpis();
    if (evaluationId) {
      await markCalculatorCompleted.mutateAsync(evaluationId);
      setEvaluationStatus("CALCULATOR_COMPLETED");
    }
    // Clear any persisted benchmark progress before navigating so the
    // operator always starts a fresh run after completing the calculator.
    try {
      localStorage.removeItem("ghost-load-benchmark");
    } catch (e) {
      // ignore if unavailable
    }
    // small delay to ensure persistence backends settle (safe and negligible)
    await new Promise((r) => setTimeout(r, 120));
    router.push("/operator/benchmark");
  }

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="grid grid-cols-1 gap-5 sm:grid-cols-2"
    >
      {FIELDS.map((field) => (
        <div key={field.name}>
          <Label htmlFor={field.name}>{field.label}</Label>
          <div className="relative">
            <Input
              id={field.name}
              type="number"
              step="any"
              placeholder={field.placeholder}
              className="pr-14"
              {...register(field.name)}
            />
            <span className="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-xs text-graphite-400">
              {field.suffix}
            </span>
          </div>
          {errors[field.name] && (
            <p className="mt-1 text-xs text-red-600">
              {errors[field.name]?.message}
            </p>
          )}
        </div>
      ))}

      <div className="sm:col-span-2">
        <Button
          type="submit"
          className="w-full sm:w-auto"
          loading={isSubmitting || markCalculatorCompleted.isPending}
        >
          Calculate & Continue to Benchmark
        </Button>
      </div>
    </form>
  );
}
