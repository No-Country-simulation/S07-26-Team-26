import type { Metadata } from 'next';
import Link from 'next/link';
import SiteHeader from '@/components/SiteHeader';
import SiteFooter from '@/components/SiteFooter';

export const metadata: Metadata = {
  title: 'Evaluación de Eficiencia - GHOST LOAD',
  description:
    'Un cuestionario de unos 10 minutos que calcula cuánto desperdicia tu data center y te da un puntaje de 0 a 100 comparable con la industria.',
};

const NIVELES = [
  { nombre: 'Líder', rango: '90 – 100' },
  { nombre: 'Maduro', rango: '75 – 89' },
  { nombre: 'En Crecimiento', rango: '60 – 74' },
  { nombre: 'Riesgo Operativo', rango: '40 – 59' },
  { nombre: 'Crítico', rango: '0 – 39' },
];

export default function Assessment() {
  return (
    <div className="min-h-screen text-[#163236] bg-[#f7faf8] font-['Inter',sans-serif]">
      <SiteHeader />

      {/* Main Container - 1440px Max Width with Blueprint Borders */}
      <div className="max-w-[1440px] mx-auto blueprint-container relative">

        {/* Qué es la evaluación */}
        <section className="section-divider py-14 md:py-20 px-6 md:px-16">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-12 items-end mb-10 md:mb-12">
            <div className="lg:col-span-7">
              <p className="font-['JetBrains_Mono',monospace] text-base font-bold uppercase tracking-[0.1em] text-[#24563c] mb-3">
                Diagnóstico en 10 minutos
              </p>
              <h1 className="font-['Sora',sans-serif] text-3xl md:text-4xl font-bold text-[#163236] mb-3">
                Evaluación de Eficiencia
              </h1>
              <p className="text-[#365452] text-base leading-relaxed max-w-2xl">
                Calculamos cuánto desperdicia tu data center y te damos un puntaje de 0 a 100 comparable con la industria.
              </p>
            </div>

            <dl className="lg:col-span-5 grid grid-cols-3 overflow-hidden rounded-2xl border border-[#b1cfd1] bg-white text-center shadow-sm">
              <div className="p-5 border-r border-[#d9e6e7]">
                <dt className="text-base text-[#365452]">Duración</dt>
                <dd className="font-['JetBrains_Mono',monospace] text-xl font-bold text-[#163236]">10 min</dd>
              </div>
              <div className="p-5 border-r border-[#d9e6e7]">
                <dt className="text-base text-[#365452]">Preguntas</dt>
                <dd className="font-['JetBrains_Mono',monospace] text-xl font-bold text-[#163236]">20</dd>
              </div>
              <div className="p-5">
                <dt className="text-base text-[#365452]">Resultado</dt>
                <dd className="font-['JetBrains_Mono',monospace] text-xl font-bold text-[#163236]">PDF</dd>
              </div>
            </dl>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="rounded-2xl border border-[#b1cfd1] bg-white p-7 md:p-8 shadow-sm">
              <h2 className="font-['Sora',sans-serif] text-xl font-bold text-[#163236] mb-3">
                Qué te vamos a preguntar
              </h2>
              <ul className="list-disc pl-5 space-y-2.5 text-base leading-relaxed text-[#365452] marker:text-[#24563c]">
                <li>Cuánta electricidad tienes contratada y cuánta usas.</li>
                <li>Cuántas GPUs tienes instaladas y qué parte del tiempo están calculando.</li>
                <li>20 preguntas sobre tus prácticas: medición eléctrica, reparto de GPUs, enfriamiento y coordinación entre equipos.</li>
                <li>Tu correo, para enviarte el resultado.</li>
              </ul>
            </div>

            <div className="rounded-2xl border border-[#b1cfd1] bg-white p-7 md:p-8 shadow-sm">
              <h2 className="font-['Sora',sans-serif] text-xl font-bold text-[#163236] mb-3">
                Qué recibes al terminar
              </h2>
              <ul className="list-disc pl-5 space-y-2.5 text-base leading-relaxed text-[#365452] marker:text-[#24563c]">
                <li>Cuánta electricidad pagas sin que produzca nada y cuánto dinero representa al año.</li>
                <li>Cuántas horas al día tus GPUs están encendidas sin trabajar.</li>
                <li>Cuánto más podrías producir sin comprar un solo equipo nuevo.</li>
                <li>Qué está frenando tu data center y qué conviene arreglar primero.</li>
                <li>Un documento en PDF con todo eso, para mostrarlo dentro de tu empresa.</li>
              </ul>
            </div>
          </div>
        </section>

        {/* Tu puntaje */}
        <section className="section-divider py-16 md:py-20 px-6 md:px-16 bg-[#e8f0f2]">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-10 lg:gap-14 items-center">
            <div className="lg:col-span-5">
              <h2 className="font-['Sora',sans-serif] text-3xl md:text-4xl font-bold text-[#163236] mb-3">
                Tu puntaje
              </h2>
              <p className="text-[#365452] text-base leading-relaxed mb-6">
                El resultado ubica tu data center en uno de cinco niveles y señala qué conviene mejorar primero.
              </p>

              <Link
                href="/login"
                className="inline-flex min-h-12 items-center gap-2 bg-[#24563c] text-white font-['JetBrains_Mono',monospace] font-bold text-base px-7 py-3.5 rounded-full hover:bg-[#163236] transition-colors shadow-md"
              >
                <span>Empieza la evaluación</span>
                <span aria-hidden="true" className="material-symbols-outlined text-[16px]">arrow_forward</span>
              </Link>
              <p className="text-[#365452] text-base mt-3 leading-relaxed">
                Tu correo se usa únicamente para enviarte el resultado.
              </p>
            </div>

            <ul className="lg:col-span-7 space-y-2 font-['JetBrains_Mono',monospace] text-base">
              {NIVELES.map((nivel, index) => (
                <li
                  key={nivel.nombre}
                  className="grid grid-cols-[2.25rem_1fr_auto] items-center gap-4 px-5 py-4 bg-white border border-[#b1cfd1] rounded-xl shadow-sm"
                >
                  <span className="flex size-10 items-center justify-center rounded-full bg-[#e8f0f2] text-base text-[#24563c]">
                    {index + 1}
                  </span>
                  <span className="font-bold text-[#163236]">{nivel.nombre}</span>
                  <span className="text-[#365452]">{nivel.rango}</span>
                </li>
              ))}
            </ul>
          </div>
        </section>

        <SiteFooter />
      </div>
    </div>
  );
}
