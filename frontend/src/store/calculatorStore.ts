// ---------------------------------------------------------------------------
// calculatorStore
//
// Holds the operator's raw infrastructure inputs and the KPIs derived from
// them. Kept separate from benchmarkStore because the calculator can be
// revisited independently (e.g. updated quarterly) while a benchmark run is
// a discrete, submitted event.
// ---------------------------------------------------------------------------
import { create } from "zustand";
import { persist } from "zustand/middleware";

export interface CalculatorInputs {
  installedCapacityKw: number | null;
  currentUtilizationPct: number | null;
  gpuCount: number | null;
  powerConsumptionKw: number | null;
  coolingCapacityKw: number | null;
  growthExpectationPct: number | null;
}

export interface CalculatorKpis {
  idleCapacityKw: number;
  idleCapacityPct: number;
  powerPerGpuKw: number;
  coolingHeadroomKw: number;
  projectedCapacityNeedKw: number;
}

interface CalculatorState {
  inputs: CalculatorInputs;
  kpis: CalculatorKpis | null;
  setInputs: (inputs: Partial<CalculatorInputs>) => void;
  computeKpis: () => void;
  reset: () => void;
}

const emptyInputs: CalculatorInputs = {
  installedCapacityKw: null,
  currentUtilizationPct: null,
  gpuCount: null,
  powerConsumptionKw: null,
  coolingCapacityKw: null,
  growthExpectationPct: null,
};

export const useCalculatorStore = create<CalculatorState>()(
  persist(
    (set, get) => ({
      inputs: emptyInputs,
      kpis: null,
      setInputs: (partial) => set((state) => ({ inputs: { ...state.inputs, ...partial } })),
      computeKpis: () => {
        const { installedCapacityKw, currentUtilizationPct, gpuCount, powerConsumptionKw, coolingCapacityKw, growthExpectationPct } =
          get().inputs;

        if (
          installedCapacityKw == null ||
          currentUtilizationPct == null ||
          gpuCount == null ||
          powerConsumptionKw == null ||
          coolingCapacityKw == null ||
          growthExpectationPct == null
        ) {
          set({ kpis: null });
          return;
        }

        const usedKw = installedCapacityKw * (currentUtilizationPct / 100);
        const idleCapacityKw = Math.max(installedCapacityKw - usedKw, 0);
        const idleCapacityPct = installedCapacityKw > 0 ? (idleCapacityKw / installedCapacityKw) * 100 : 0;
        const powerPerGpuKw = gpuCount > 0 ? powerConsumptionKw / gpuCount : 0;
        const coolingHeadroomKw = coolingCapacityKw - powerConsumptionKw;
        const projectedCapacityNeedKw = installedCapacityKw * (1 + growthExpectationPct / 100);

        set({
          kpis: {
            idleCapacityKw: Math.round(idleCapacityKw),
            idleCapacityPct: Math.round(idleCapacityPct * 10) / 10,
            powerPerGpuKw: Math.round(powerPerGpuKw * 100) / 100,
            coolingHeadroomKw: Math.round(coolingHeadroomKw),
            projectedCapacityNeedKw: Math.round(projectedCapacityNeedKw),
          },
        });
      },
      reset: () => set({ inputs: emptyInputs, kpis: null }),
    }),
    { name: "ghost-load-calculator" }
  )
);
