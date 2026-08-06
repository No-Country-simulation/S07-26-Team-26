'use client';

import React from 'react';
import { useMutation, useQuery } from '@tanstack/react-query';
import {
  apiClient,
  BenchmarkQuestion,
  BenchmarkResult,
  CalculatorPayload,
  CreateEvaluationResponse,
  RegisterEvaluationPayload,
} from '@/lib/api';
import {
  AssessmentSession,
  AssessmentStep,
  clearSession,
  loadSession,
  saveSession,
  updateSession,
} from '@/lib/assessmentSession';
import {
  ArrowRight,
  BarChart3,
  Calculator,
  Check,
  ClipboardList,
  Flame,
  Sprout,
  Trophy,
  UserPlus,
} from 'lucide-react';

const SOURCE = 'CALCULATOR';
const VERSION = 'v1';

const MODULE_LABELS: Record<string, string> = {
  CAPACITY_VISIBILITY: 'Visibilidad de capacidad',
  OPERATIONAL_COORDINATION: 'Coordinación operativa',
  AUTOMATION: 'Automatización',
  GOVERNANCE: 'Gobernanza',
  CONTINUOUS_IMPROVEMENT: 'Mejora continua',
};

export default function EvaluacionPage() {
  const [session, setSession] = React.useState<AssessmentSession | null>(null);
  const [step, setStep] = React.useState<AssessmentStep>('register');
  const [result, setResult] = React.useState<BenchmarkResult | null>(null);

  React.useEffect(() => {
    const existing = loadSession();
    if (!existing) return;
    const timer = window.setTimeout(() => {
      setSession(existing);
      setStep(existing.step);
    }, 0);
    return () => window.clearTimeout(timer);
  }, []);

  const start = (created: CreateEvaluationResponse) => {
    const next = { evaluationId: created.evaluationId, token: created.evaluationToken, step: 'calculator' as AssessmentStep, answers: {} };
    saveSession(next);
    setSession(next);
    setStep('calculator');
  };

  const onCalculatorDone = () => {
    const updated = updateSession({ step: 'questionnaire' });
    if (updated) setSession(updated);
    setStep('questionnaire');
  };

  const onQuestionnaireDone = (res: BenchmarkResult) => {
    const updated = updateSession({ step: 'results' });
    if (updated) setSession(updated);
    setResult(res);
    setStep('results');
  };

  const restart = () => {
    clearSession();
    setSession(null);
    setResult(null);
    setStep('register');
  };

  return (
    <div className="relative min-h-screen overflow-x-hidden">
      <div className="absolute top-[-10%] left-[-10%] w-[50%] h-[50%] rounded-full bg-indigo-500/10 blur-[120px] pointer-events-none" />
      <div className="absolute bottom-[-10%] right-[-10%] w-[50%] h-[50%] rounded-full bg-violet-600/10 blur-[120px] pointer-events-none" />

      <div className="max-w-3xl mx-auto px-4 py-10 sm:px-6 relative z-10">
        <header className="text-center mb-8 space-y-2">
          <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-indigo-500/10 border border-indigo-500/25 text-indigo-400 text-sm font-medium">
            <Sprout className="w-4 h-4" /> Evaluación de madurez energética
          </div>
          <h1 className="text-3xl sm:text-4xl font-extrabold tracking-tight bg-clip-text text-transparent bg-gradient-to-r from-white via-slate-200 to-slate-400">
            Ghost Load
          </h1>
          <p className="text-sm text-slate-400">Descubre el estado de tu operación y qué tan despejada está tu carga fantasma.</p>
        </header>

        <Stepper active={step} />

        {step === 'register' && <RegisterStep onDone={start} />}
        {step === 'calculator' && session && <CalculatorStep session={session} onDone={onCalculatorDone} />}
        {step === 'questionnaire' && session && (
          <QuestionnaireStep session={session} onDone={onQuestionnaireDone} />
        )}
        {step === 'results' && result && <ResultsStep result={result} onRestart={restart} />}
      </div>
    </div>
  );
}

function Stepper({ active }: { active: AssessmentStep }) {
  const steps: { key: AssessmentStep; label: string }[] = [
    { key: 'register', label: 'Registro' },
    { key: 'calculator', label: 'Calculadora' },
    { key: 'questionnaire', label: 'Cuestionario' },
    { key: 'results', label: 'Resultados' },
  ];
  const activeIndex = steps.findIndex((s) => s.key === active);

  return (
    <div className="flex items-center justify-center gap-2 mb-8">
      {steps.map((s, i) => {
        const done = i < activeIndex;
        const current = i === activeIndex;
        return (
          <React.Fragment key={s.key}>
            {i > 0 && <div className={`h-px w-6 sm:w-10 ${i <= activeIndex ? 'bg-indigo-500' : 'bg-slate-800'}`} />}
            <div
              className={`flex items-center gap-1.5 px-2 py-1 rounded-full text-xs font-semibold border ${
                current
                  ? 'bg-indigo-500/15 border-indigo-500/40 text-indigo-300'
                  : done
                  ? 'bg-slate-900 border-slate-800 text-slate-300'
                  : 'bg-slate-900/50 border-slate-800 text-slate-500'
              }`}
            >
              {done ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <span className="w-3.5 h-3.5 inline-flex items-center justify-center">{i + 1}</span>}
              <span className="hidden sm:inline">{s.label}</span>
            </div>
          </React.Fragment>
        );
      })}
    </div>
  );
}

function Card({ title, subtitle, icon, children }: { title: string; subtitle: string; icon: React.ReactNode; children: React.ReactNode }) {
  return (
    <div className="rounded-2xl border border-slate-800 bg-slate-900/40 backdrop-blur-xl p-6 shadow-2xl">
      <div className="flex items-center gap-3 mb-6">
        <div className="p-2.5 rounded-lg bg-indigo-500/10 border border-indigo-500/20 text-indigo-400">{icon}</div>
        <div>
          <h2 className="text-xl font-bold text-white">{title}</h2>
          <p className="text-xs text-slate-500">{subtitle}</p>
        </div>
      </div>
      {children}
    </div>
  );
}

const inputClass =
  'w-full px-4 py-2.5 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 placeholder-slate-500 focus:outline-none focus:border-indigo-500/50 focus:ring-1 focus:ring-indigo-500/30 transition-all text-sm';
const labelClass = 'text-xs font-medium text-slate-400 uppercase tracking-wider block mb-1.5';
const primaryButton =
  'inline-flex items-center justify-center gap-2 px-5 py-2.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-semibold transition-all disabled:opacity-50 disabled:cursor-not-allowed';
const errorText = 'text-xs text-red-400 mt-1';

function RegisterStep({ onDone }: { onDone: (r: CreateEvaluationResponse) => void }) {
  const [form, setForm] = React.useState({
    firstName: '',
    lastName: '',
    email: '',
    companyName: '',
    position: '',
    country: '',
    consentAccepted: false,
    marketingConsent: false,
  });
  const [error, setError] = React.useState<string | null>(null);

  const set = (key: keyof typeof form, value: string | boolean) => setForm((f) => ({ ...f, [key]: value }));

  const mutation = useMutation({
    mutationFn: (payload: RegisterEvaluationPayload) => apiClient.registerEvaluation(payload),
    onSuccess: onDone,
    onError: (e: Error) => setError(e.message),
  });

  const submit = (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    mutation.mutate({ ...form, source: SOURCE });
  };

  return (
    <Card title="Empecemos" subtitle="Cuéntanos quién es tu operador energético" icon={<UserPlus className="w-5 h-5" />}>
      <form onSubmit={submit} className="space-y-4">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className={labelClass}>Nombre</label>
            <input required className={inputClass} value={form.firstName} onChange={(e) => set('firstName', e.target.value)} placeholder="María" />
          </div>
          <div>
            <label className={labelClass}>Apellido</label>
            <input required className={inputClass} value={form.lastName} onChange={(e) => set('lastName', e.target.value)} placeholder="González" />
          </div>
        </div>
        <div>
          <label className={labelClass}>Email corporativo</label>
          <input required type="email" className={inputClass} value={form.email} onChange={(e) => set('email', e.target.value)} placeholder="maria@empresa.com" />
        </div>
        <div>
          <label className={labelClass}>Empresa</label>
          <input required className={inputClass} value={form.companyName} onChange={(e) => set('companyName', e.target.value)} placeholder="Acme Energía" />
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className={labelClass}>Cargo</label>
            <input className={inputClass} value={form.position} onChange={(e) => set('position', e.target.value)} placeholder="Gerente de operaciones" />
          </div>
          <div>
            <label className={labelClass}>País</label>
            <input className={inputClass} value={form.country} onChange={(e) => set('country', e.target.value)} placeholder="Argentina" />
          </div>
        </div>

        <label className="flex items-start gap-3 pt-1 cursor-pointer">
          <input type="checkbox" checked={form.consentAccepted} onChange={(e) => set('consentAccepted', e.target.checked)} className="mt-0.5 w-4 h-4 accent-indigo-500" />
          <span className="text-xs text-slate-400">
            Acepto que mis datos sean utilizados para calcular la evaluación de madurez y contacto comercial. <span className="text-red-400">*</span>
          </span>
        </label>
        <label className="flex items-start gap-3 cursor-pointer">
          <input type="checkbox" checked={form.marketingConsent} onChange={(e) => set('marketingConsent', e.target.checked)} className="mt-0.5 w-4 h-4 accent-indigo-500" />
          <span className="text-xs text-slate-400">Quiero recibir recomendaciones y novedades por email.</span>
        </label>

        {error && <p className={errorText}>{error}</p>}
        {!form.consentAccepted && <p className="text-xs text-amber-400">Debes aceptar el consentimiento para continuar.</p>}

        <div className="pt-2">
          <button type="submit" disabled={!form.consentAccepted || mutation.isPending} className={primaryButton}>
            {mutation.isPending ? 'Creando...' : 'Continuar'}
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>
      </form>
    </Card>
  );
}

function CalculatorStep({ session, onDone }: { session: AssessmentSession; onDone: () => void }) {
  const [form, setForm] = React.useState<CalculatorPayload>({
    totalCapacityMw: 0,
    productiveCapacityMw: 0,
    monthlyCostPerKw: 0,
    currency: 'USD',
  });
  const [error, setError] = React.useState<string | null>(null);

  const mutation = useMutation({
    mutationFn: (payload: CalculatorPayload) =>
      apiClient.saveCalculatorResult(session.evaluationId, session.token, payload),
    onSuccess: onDone,
    onError: (e: Error) => setError(e.message),
  });

  const submit = (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    if (form.totalCapacityMw <= 0) {
      setError('La capacidad total debe ser mayor a 0.');
      return;
    }
    if (form.productiveCapacityMw > form.totalCapacityMw) {
      setError('La capacidad productiva no puede superar la total.');
      return;
    }
    mutation.mutate(form);
  };

  return (
    <Card title="Tu capacidad energética" subtitle="Estimemos cuánta carga fantasma hay en tu operación" icon={<Calculator className="w-5 h-5" />}>
      <form onSubmit={submit} className="space-y-4">
        <div>
          <label className={labelClass}>Capacidad total instalada (MW)</label>
          <input required type="number" step="any" min="0" className={inputClass} value={form.totalCapacityMw} onChange={(e) => setForm({ ...form, totalCapacityMw: Number(e.target.value) })} placeholder="0" />
        </div>
        <div>
          <label className={labelClass}>Capacidad productiva (MW)</label>
          <input required type="number" step="any" min="0" className={inputClass} value={form.productiveCapacityMw} onChange={(e) => setForm({ ...form, productiveCapacityMw: Number(e.target.value) })} placeholder="0" />
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className={labelClass}>Costo mensual por kW</label>
            <input required type="number" step="any" min="0" className={inputClass} value={form.monthlyCostPerKw} onChange={(e) => setForm({ ...form, monthlyCostPerKw: Number(e.target.value) })} placeholder="0" />
          </div>
          <div>
            <label className={labelClass}>Moneda</label>
            <input required maxLength={3} className={inputClass} value={form.currency} onChange={(e) => setForm({ ...form, currency: e.target.value.toUpperCase() })} placeholder="USD" />
          </div>
        </div>
        {error && <p className={errorText}>{error}</p>}

        <div className="pt-2 flex items-center gap-3">
          <button type="submit" disabled={mutation.isPending} className={primaryButton}>
            {mutation.isPending ? 'Calculando...' : 'Siguiente paso'}
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>
      </form>
    </Card>
  );
}

function QuestionnaireStep({ session, onDone }: { session: AssessmentSession; onDone: (r: BenchmarkResult) => void }) {
  const [answers, setAnswers] = React.useState<Record<string, number>>(session.answers ?? {});
  const [storedCount, setStoredCount] = React.useState(Object.keys(session.answers ?? {}).length);
  const [error, setError] = React.useState<string | null>(null);

  const { data: questions, isLoading, isError } = useQuery({
    queryKey: ['benchmark-questions', VERSION],
    queryFn: () => apiClient.listBenchmarkQuestions(VERSION),
  });

  const saveStatus = useMutation({
    mutationFn: () =>
      apiClient.saveBenchmarkProgress(
        session.evaluationId,
        session.token,
        { questionnaireVersion: VERSION, answers: Object.entries(answers).map(([questionId, value]) => ({ questionId, value })) }
      ),
    onSuccess: (data) => setStoredCount(data.answeredCount),
  });

  const submit = useMutation({
    mutationFn: () =>
      apiClient.submitBenchmark(
        session.evaluationId,
        session.token,
        { questionnaireVersion: VERSION, answers: Object.entries(answers).map(([questionId, value]) => ({ questionId, value })) }
      ),
    onSuccess: onDone,
    onError: (e: Error) => setError(e.message),
  });

  const persist = (next: Record<string, number>) => {
    const updated = updateSession({ answers: next });
    if (updated) setAnswers(next);
    saveStatus.mutate();
  };

  const pick = (q: BenchmarkQuestion, value: number) => {
    const next = { ...answers, [q.id]: value };
    persist(next);
  };

  if (isLoading) {
    return (
      <Card title="Cuestionario" subtitle="Cargando preguntas de madurez..." icon={<ClipboardList className="w-5 h-5" />}>
        <div className="space-y-3 animate-pulse">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="h-16 bg-slate-800 rounded-xl" />
          ))}
        </div>
      </Card>
    );
  }

  if (isError || !questions) {
    return (
      <Card title="Cuestionario" subtitle="No se pudieron cargar las preguntas" icon={<ClipboardList className="w-5 h-5" />}>
        <p className="text-sm text-slate-400">Hubo un problema al cargar el cuestionario. Recargá la página para reintentar.</p>
      </Card>
    );
  }

  const ordered = [...questions].sort((a, b) => a.order - b.order);
  const answered = Object.keys(answers).length;
  const total = questions.length || 20;
  const progress = total === 0 ? 0 : Math.round((answered / total) * 100);

  return (
    <Card
      title="Cuestionario de madurez"
      subtitle={`Respondé según el estado actual de tu operación · ${answered}/${total} respondidas` + (storedCount > 0 ? ` · guardadas: ${storedCount}` : '')}
      icon={<ClipboardList className="w-5 h-5" />}
    >
      <div className="mb-6">
        <div className="flex justify-between text-xs text-slate-400 mb-1.5">
          <span>Progreso guardado en la nube</span>
          <span className="font-mono">{progress}%</span>
        </div>
        <div className="h-2 rounded-full bg-slate-800 overflow-hidden">
          <div className="h-full bg-indigo-500 transition-all duration-300" style={{ width: `${progress}%` }} />
        </div>
      </div>

      <div className="space-y-5">
        {ordered.map((q) => {
          const current = answers[q.id];
          return (
            <div key={q.id} className="rounded-xl border border-slate-800 bg-slate-950/40 p-4">
              <div className="flex items-start justify-between gap-3 mb-3">
                <span className="text-[10px] px-2 py-0.5 rounded bg-slate-900 border border-slate-800 text-slate-500">
                  {MODULE_LABELS[q.module] ?? q.module}
                </span>
                <span className="text-[10px] text-slate-600 font-mono">{q.order}.</span>
              </div>
              <p className="text-sm text-slate-200 mb-3">{q.text}</p>
              <div className="flex flex-wrap gap-2">
                {q.scale.map((opt) => (
                  <button
                    key={opt.value}
                    type="button"
                    onClick={() => pick(q, opt.value)}
                    className={`px-3 py-1.5 rounded-lg text-xs font-semibold border transition-all ${
                      current === opt.value
                        ? 'bg-indigo-500/20 border-indigo-500 text-indigo-200'
                        : 'bg-slate-900 border-slate-800 text-slate-400 hover:border-slate-600'
                    }`}
                  >
                    {opt.value} · {opt.label}
                  </button>
                ))}
              </div>
            </div>
          );
        })}
      </div>

      {error && <p className={errorText}>{error}</p>}

      <div className="pt-6 flex items-center justify-between gap-3">
        <span className="text-xs text-slate-500">
          {answered === total ? 'Completaste todas las preguntas.' : `Respondí las ${total} para calcular tu resultado.`}
        </span>
        <button type="button" disabled={answered < total || submit.isPending} onClick={() => submit.mutate()} className={primaryButton}>
          {submit.isPending ? 'Calculando resultado...' : 'Ver mis resultados'}
          <Trophy className="w-4 h-4" />
        </button>
      </div>
    </Card>
  );
}

function ResultsStep({ result, onRestart }: { result: BenchmarkResult; onRestart: () => void }) {
  const score = Math.round(result.totalScore);
  const bars: { level: string; color: string }[] = [
    { level: 'INEXISTENT', color: 'bg-red-500' },
    { level: 'INITIAL', color: 'bg-orange-500' },
    { level: 'DEFINED', color: 'bg-amber-500' },
    { level: 'MANAGED', color: 'bg-emerald-500' },
    { level: 'OPTIMIZED', color: 'bg-indigo-400' },
  ];
  const levelLabel = result.maturityLevel.charAt(0) + result.maturityLevel.slice(1).toLowerCase();

  return (
    <div className="space-y-6">
      <Card title="Tus resultados" subtitle="Índice de madurez en gestión de carga fantasma" icon={<BarChart3 className="w-5 h-5" />}>
        <div className="flex flex-col sm:flex-row items-center gap-6">
          <div className="flex-1 w-full">
            <div className="flex items-end gap-1 h-28 overflow-hidden">
              {bars.map((b) => {
                const on = result.maturityLevel === b.level;
                return (
                  <div key={b.level} className="flex-1 flex flex-col items-center gap-1">
                    <div className={`w-full h-24 rounded-t-lg ${b.color} ${on ? 'opacity-100' : 'opacity-20'}`} />
                    <span className={`text-[9px] font-semibold ${on ? 'text-white' : 'text-slate-500'}`}>{b.level.slice(0, 4)}</span>
                  </div>
                );
              })}
            </div>
            <div className="text-center mt-4">
              <p className="text-4xl font-extrabold bg-clip-text text-transparent bg-gradient-to-r from-white to-slate-400">{score}/100</p>
              <p className="text-sm font-semibold text-indigo-300 mt-1">Nivel: {levelLabel}</p>
              <p className="text-xs text-slate-500 mt-1">{result.percentileDisclaimer}</p>
            </div>
          </div>
        </div>
      </Card>

      <Card title="Por área" subtitle="Desglose por dimensión de la madurez" icon={<ClipboardList className="w-5 h-5" />}>
        <div className="space-y-4">
          {result.moduleScores.map((m) => (
            <div key={m.module}>
              <div className="flex justify-between text-xs text-slate-300 mb-1">
                <span>{MODULE_LABELS[m.module] ?? m.module}</span>
                <span className="font-mono text-slate-500">{Math.round(m.score)}/100</span>
              </div>
              <div className="h-2 rounded-full bg-slate-800 overflow-hidden">
                <div
                  className="h-full bg-gradient-to-r from-indigo-500 to-violet-400 transition-all duration-500"
                  style={{ width: `${Math.min(100, m.score)}%` }}
                />
              </div>
            </div>
          ))}
        </div>
      </Card>

      <div className="text-center space-y-2">
        <div className="inline-flex items-center gap-2 text-emerald-400 text-sm font-semibold">
          <Check className="w-4 h-4" /> Evaluación completada
        </div>
        <p className="text-xs text-slate-500">
          Próximamente podrás generar tu reporte personalizado con recomendaciones accionables.
        </p>
        <button onClick={onRestart} className="inline-flex items-center gap-2 px-5 py-2.5 rounded-lg bg-slate-900 border border-slate-800 text-slate-300 hover:border-slate-600 text-sm font-semibold transition-all">
          <Flame className="w-4 h-4" /> Nueva evaluación
        </button>
      </div>
    </div>
  );
}