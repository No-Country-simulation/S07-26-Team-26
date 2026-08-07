import Link from 'next/link';
import {
  ArrowRight,
  Calculator,
  ClipboardList,
  FileText,
  Gauge,
  Leaf,
  Server,
  Sparkles,
  Target,
  TrendingUp,
  Users,
} from 'lucide-react';

export const metadata = {
  title: 'Ghost Load — Benchmark de madurez energética',
  description:
    'Detecta la carga fantasma de tu data center: calcula capacidad desperdiciada, compara tu operación con la industria y genera tu reporte institucional.',
};

const KPI_ITEMS = [
  {
    icon: Gauge,
    title: 'Capacity Calculator',
    description:
      'Estima tu capacidad no productiva en MW y el porcentaje real de utilización de tu infraestructura.',
  },
  {
    icon: ClipboardList,
    title: 'Benchmark Engine',
    description:
      'Compara tu operación contra estándares de la industria y obtén un nivel de madurez por módulo.',
  },
  {
    icon: FileText,
    title: 'Reporte institucional',
    description:
      'Genera un reporte en PDF con tus KPIs clave para transformar tu operación en un lead calificado.',
  },
];

const STEPS = [
  {
    number: '01',
    icon: Calculator,
    title: 'Calcula',
    description: 'Ingresá tu capacidad total y productiva para conocer cuánta carga se desperdicia.',
  },
  {
    number: '02',
    icon: ClipboardList,
    title: 'Benchmark',
    description: 'Respondé el cuestionario de madurez y comparate según tu nivel de automatización y gobernanza.',
  },
  {
    number: '03',
    icon: FileText,
    title: 'Reporte',
    description: 'Descargá el informe institucional con tus resultados de madurez y capacidad.',
  },
];

const FAQ_ITEMS = [
  {
    question: '¿Qué es la carga fantasma?',
    answer: 'Es la capacidad de tu data center que consume energía sin generar valor productivo real: recursos infrautilizados o desperdiciados.',
  },
  {
    question: '¿Cuánto tarda la evaluación?',
    answer: 'Unos pocos minutos: completás la calculadora y el benchmark, y tu informe se genera automáticamente.',
  },
  {
    question: '¿Es gratuita?',
    answer: 'Sí. La evaluación es una herramienta de alto valor para conocer tu operación sin costo alguno.',
  },
];

export default function Home() {
  return (
    <div className="relative overflow-hidden">
      {/* Decorative ambient glows */}
      <div className="pointer-events-none absolute top-[-10%] left-[-10%] h-[50%] w-[50%] rounded-full bg-green-500/10 blur-[120px]" />
      <div className="pointer-events-none absolute bottom-[-10%] right-[-10%] h-[50%] w-[50%] rounded-full bg-indigo-500/10 blur-[120px]" />

      <div className="relative z-10">
        {/* Hero */}
        <section className="mx-auto max-w-7xl px-4 pt-16 pb-20 sm:px-6 lg:px-8">
          <div className="mx-auto max-w-3xl text-center">
            <span className="mb-6 inline-flex items-center gap-2 rounded-full border border-green-500/25 bg-green-500/10 px-3 py-1.5 text-sm font-medium text-green-500">
              <Leaf className="h-4 w-4" /> Benchmark de energía para Data Centers
            </span>
            <h1 className="text-4xl font-extrabold tracking-tight bg-clip-text text-transparent bg-gradient-to-r from-green-300 via-slate-200 to-indigo-300 sm:text-5xl lg:text-6xl">
              Detectá la carga fantasma de tu data center
            </h1>
            <p className="mx-auto mt-6 max-w-2xl text-base text-slate-400 sm:text-lg">
              Ghost Load convierte a operadores de DC en leads calificados mediante herramientas
              gratuitas de alto valor: una calculadora de capacidad y un benchmark de madurez.
            </p>
            <div className="mt-10 flex flex-col items-center justify-center gap-4 sm:flex-row">
              <Link
                href="/evaluacion"
                className="inline-flex items-center gap-2 rounded-lg bg-green-500 px-6 py-3 text-sm font-semibold text-white transition-colors hover:bg-green-400"
              >
                Empezar evaluación <ArrowRight className="h-4 w-4" />
              </Link>
              <span className="text-sm text-slate-500">
                100% gratuito · sin cargo · informe automático
              </span>
            </div>
          </div>

          {/* Trust / insight strip */}
          <div className="mt-20 grid grid-cols-1 gap-6 sm:grid-cols-3">
            {KPI_ITEMS.map((item) => {
              const Icon = item.icon;
              return (
                <div
                  key={item.label}
                  className="pixel-card theme-surface p-6"
                >
                  <div className="mb-4 inline-flex rounded-lg bg-green-500/10 p-2.5 text-green-400">
                    <Icon className="h-5 w-5" />
                  </div>
                  <h3 className="text-lg font-bold text-slate-200">{item.title}</h3>
                  <p className="mt-2 text-sm text-slate-400">{item.description}</p>
                </div>
              );
            })}
          </div>
        </section>

        {/* Value metrics */}
        <section className="border-y border-slate-800 bg-slate-900/20 theme-surface py-14">
          <div className="mx-auto grid max-w-6xl grid-cols-2 gap-8 px-4 text-center sm:px-6 lg:grid-cols-4 lg:px-8">
            <div>
              <p className="flex items-center justify-center gap-1.5 text-2xl font-extrabold text-slate-200">
                <TrendingUp className="h-5 w-5 text-green-500" /> % de utilización
              </p>
              <p className="mt-1 text-xs text-slate-500">Capacidad realmente productiva</p>
            </div>
            <div>
              <p className="flex items-center justify-center gap-1.5 text-2xl font-extrabold text-slate-200">
                <Server className="h-5 w-5 text-green-500" /> MW desperdíciados
              </p>
              <p className="mt-1 text-xs text-slate-500">Capacidad no productiva identificada</p>
            </div>
            <div>
              <p className="flex items-center justify-center gap-1.5 text-2xl font-extrabold text-slate-200">
                <Target className="h-5 w-5 text-green-500" /> Nivel de madurez
              </p>
              <p className="mt-1 text-xs text-slate-500">Por módulo de operación</p>
            </div>
            <div>
              <p className="flex items-center justify-center gap-1.5 text-2xl font-extrabold text-slate-200">
                <Users className="h-5 w-5 text-green-500" /> Lead calificado
              </p>
              <p className="mt-1 text-xs text-slate-500">Con reporte institucional</p>
            </div>
          </div>
        </section>

        {/* How it works */}
        <section className="mx-auto max-w-7xl px-4 py-20 sm:px-6 lg:px-8">
          <div className="mx-auto max-w-2xl text-center">
            <span className="inline-flex items-center gap-2 rounded-full border border-indigo-500/25 bg-indigo-500/10 px-3 py-1.5 text-sm font-medium text-indigo-400">
              <Sparkles className="h-4 w-4" /> ¿Cómo funciona?
            </span>
            <h2 className="mt-6 text-3xl font-extrabold text-slate-200 sm:text-4xl">
              Tres pasos para conocer tu operación
            </h2>
          </div>

          <div className="mt-14 grid grid-cols-1 gap-8 sm:grid-cols-3">
            {STEPS.map((step) => {
              const Icon = step.icon;
              return (
                <div key={step.number} className="pixel-card theme-surface relative p-6">
                  <span className="absolute right-6 top-6 text-5xl font-extrabold text-slate-800">
                    {step.number}
                  </span>
                  <div className="mb-4 inline-flex rounded-lg bg-green-500/10 p-2.5 text-green-400">
                    <Icon className="h-5 w-5" />
                  </div>
                  <h3 className="text-lg font-bold text-slate-200">{step.title}</h3>
                  <p className="mt-2 text-sm text-slate-400">{step.description}</p>
                </div>
              );
            })}
          </div>

          <div className="mt-14 text-center">
            <Link
              href="/evaluacion"
              className="inline-flex items-center gap-2 rounded-lg bg-green-500 px-6 py-3 text-sm font-semibold text-white transition-colors hover:bg-green-400"
            >
              Comenzar mi benchmark <ArrowRight className="h-4 w-4" />
            </Link>
          </div>
        </section>

        {/* FAQ */}
        <section className="border-t border-slate-800 bg-slate-900/20 theme-surface py-20">
          <div className="mx-auto max-w-3xl px-4 sm:px-6 lg:px-8">
            <h2 className="text-center text-3xl font-extrabold text-slate-200 sm:text-4xl">
              Preguntas frecuentes
            </h2>
            <div className="mt-12 space-y-4">
              {FAQ_ITEMS.map((item) => (
                <div key={item.question} className="pixel-card theme-surface p-6">
                  <h3 className="font-semibold text-slate-200">{item.question}</h3>
                  <p className="mt-2 text-sm text-slate-400">{item.answer}</p>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* Final CTA */}
        <section className="mx-auto max-w-7xl px-4 py-20 text-center sm:px-6 lg:px-8">
          <h2 className="text-3xl font-extrabold text-slate-200 sm:text-4xl">
            ¿Despejada tu carga fantasma?
          </h2>
          <p className="mx-auto mt-4 max-w-xl text-slate-400">
            Calculá tu capacidad, compará tu madurez y generá tu reporte institucional en minutos.
          </p>
          <Link
            href="/evaluacion"
            className="mt-8 inline-flex items-center gap-2 rounded-lg bg-green-500 px-6 py-3 text-sm font-semibold text-white transition-colors hover:bg-green-400"
          >
            Comenzar ahora <ArrowRight className="h-4 w-4" />
          </Link>
        </section>

        {/* Footer */}
        <footer className="border-t border-slate-800 py-8">
          <div className="mx-auto flex max-w-7xl flex-col items-center justify-between gap-4 px-4 text-xs text-slate-500 sm:flex-row sm:px-6 lg:px-8">
            <p>© {new Date().getFullYear()} Ghost Load — DataCenter Benchmark.</p>
            <div className="flex items-center gap-4">
              <Link href="/evaluacion" className="transition-colors hover:text-green-400">
                Evaluación
              </Link>
              <Link href="/admin/login" className="transition-colors hover:text-green-400">
                Acceso admin
              </Link>
            </div>
          </div>
        </footer>
      </div>
    </div>
  );
}