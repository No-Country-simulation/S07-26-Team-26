import type { Metadata } from 'next';
import Link from 'next/link';
import SiteHeader from '@/components/SiteHeader';
import SiteFooter from '@/components/SiteFooter';

export const metadata: Metadata = {
  title: 'Evaluación de eficiencia | Ghost Load',
  description:
    'Responde 20 preguntas y descubre cuánto desperdicia tu data center, qué puedes mejorar y cómo se compara tu operación con la industria.',
};

const NIVELES = [
  { nombre: 'Líder', rango: '90 – 100' },
  { nombre: 'Maduro', rango: '75 – 89' },
  { nombre: 'En crecimiento', rango: '60 – 74' },
  { nombre: 'Riesgo operativo', rango: '40 – 59' },
  { nombre: 'Crítico', rango: '0 – 39' },
];

export default function Assessment() {
  return (
    <div className="min-h-screen text-[#163236] bg-[#f7faf8] font-['Inter',sans-serif]">
      <SiteHeader />

      {/* Main Container - 1440px Max Width with Blueprint Borders */}
      <div className="max-w-[1440px] mx-auto blueprint-container relative">

        {/* Qué es la evaluación */}
        <section className="section-divider py-24 md:py-32 px-6 md:px-16">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-12 items-end mb-10 md:mb-12">
            <div className="lg:col-span-7">
              <p className="font-['JetBrains_Mono',monospace] text-lg font-bold uppercase tracking-[0.1em] text-[#24563c] mb-3">
                Completa el diagnóstico en 10 minutos
              </p>
              <h1 className="font-['Sora',sans-serif] text-3xl md:text-4xl font-bold text-[#163236] mb-3">
                Evaluación de eficiencia
              </h1>
              <p className="text-[#365452] text-lg leading-relaxed max-w-2xl">
                Responde 20 preguntas para medir el desperdicio de tu data center
                y comparar tu operación con la industria.
              </p>
            </div>

            <dl className="lg:col-span-5 grid grid-cols-3 overflow-hidden rounded-2xl border border-[#b1cfd1] bg-white text-center shadow-sm">
              <div className="p-2 sm:p-5 border-r border-[#d9e6e7]">
                <dt className="text-lg text-[#365452]">Duración</dt>
                <dd className="font-['JetBrains_Mono',monospace] text-xl font-bold text-[#163236]">10 min</dd>
              </div>
              <div className="p-2 sm:p-5 border-r border-[#d9e6e7]">
                <dt className="text-lg text-[#365452]">Preguntas</dt>
                <dd className="font-['JetBrains_Mono',monospace] text-xl font-bold text-[#163236]">20</dd>
              </div>
              <div className="p-2 sm:p-5">
                <dt className="text-lg text-[#365452]">Resultado</dt>
                <dd className="font-['JetBrains_Mono',monospace] text-xl font-bold text-[#163236]">PDF</dd>
              </div>
            </dl>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="rounded-2xl border border-[#b1cfd1] bg-white p-7 md:p-8 shadow-sm">
              <h2 className="font-['Sora',sans-serif] text-xl font-bold text-[#163236] mb-3">
                Qué necesitamos saber
              </h2>
              <ul className="list-disc pl-5 space-y-2.5 text-lg leading-relaxed text-[#365452] marker:text-[#24563c]">
                <li>La potencia que tienes contratada y tu consumo real.</li>
                <li>Cuántas GPUs tienes y cuánto tiempo trabajan.</li>
                <li>Cómo mides, distribuyes y enfrías tus equipos.</li>
                <li>Cómo se coordinan tus equipos de infraestructura y operaciones.</li>
                <li>Tu correo para enviarte el resultado.</li>
              </ul>
            </div>

            <div className="rounded-2xl border border-[#b1cfd1] bg-white p-7 md:p-8 shadow-sm">
              <h2 className="font-['Sora',sans-serif] text-xl font-bold text-[#163236] mb-3">
                Qué obtienes al terminar
              </h2>
              <ul className="list-disc pl-5 space-y-2.5 text-lg leading-relaxed text-[#365452] marker:text-[#24563c]">
                <li>El consumo y el costo anual de tus equipos inactivos.</li>
                <li>Las horas diarias en las que tus GPUs no trabajan.</li>
                <li>La capacidad adicional que puedes aprovechar sin comprar equipos.</li>
                <li>El principal límite de tu operación y qué mejorar primero.</li>
                <li>Un informe en PDF listo para compartir con tu equipo.</li>
              </ul>
            </div>
          </div>

          <div className="mt-10 flex justify-center">
            <a
              href="#nivel-eficiencia"
              className="inline-flex min-h-14 items-center gap-3 rounded-full border-2 border-[#24563c] bg-[#e8f0f2] px-7 py-3.5 font-['JetBrains_Mono',monospace] text-lg font-bold text-[#24563c] shadow-md transition-colors hover:bg-[#24563c] hover:text-white hover:shadow-lg"
            >
              <span>Conoce los niveles de eficiencia</span>
              <span
                aria-hidden="true"
                className="material-symbols-outlined text-[22px]"
              >
                arrow_downward
              </span>
            </a>
          </div>
        </section>

        {/* Niveles de eficiencia */}
        <section id="nivel-eficiencia" className="section-divider py-28 md:py-32 px-6 md:px-16 bg-[#e8f0f2] scroll-mt-20">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-10 lg:gap-14 items-center">
            <div className="lg:col-span-5">
              <h2 className="font-['Sora',sans-serif] text-3xl md:text-4xl font-bold text-[#163236] mb-3">
                Conoce los niveles de eficiencia
              </h2>
              <p className="text-[#365452] text-lg leading-relaxed mb-6">
                Tu resultado mostrará el nivel de tu data center y las mejoras que
                deberías priorizar.
              </p>

              <Link
                href="/login"
                className="inline-flex min-h-12 items-center gap-2 bg-[#24563c] text-white font-['JetBrains_Mono',monospace] font-bold text-lg px-7 py-3.5 rounded-full hover:bg-[#163236] transition-colors shadow-md"
              >
                <span>Empieza la evaluación</span>
                <span aria-hidden="true" className="material-symbols-outlined text-[16px]">arrow_forward</span>
              </Link>
              <p className="text-[#365452] text-lg mt-3 leading-relaxed">
                Solo usaremos tu correo para enviarte el resultado.
              </p>
            </div>

            <ul className="lg:col-span-7 space-y-2 font-['JetBrains_Mono',monospace] text-lg">
              {NIVELES.map((nivel, index) => (
                <li
                  key={nivel.nombre}
                  className="grid grid-cols-[2.25rem_1fr_auto] items-center gap-4 px-5 py-4 bg-white border border-[#b1cfd1] rounded-xl shadow-sm"
                >
                  <span className="flex size-10 items-center justify-center rounded-full bg-[#e8f0f2] text-lg text-[#24563c]">
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
