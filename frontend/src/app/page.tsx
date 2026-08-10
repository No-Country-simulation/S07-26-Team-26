import Link from "next/link";
import Image from "next/image";
import SiteHeader from "@/components/SiteHeader";
import SiteFooter from "@/components/SiteFooter";

export default function Home() {
  return (
    <div className="min-h-screen text-[#163236] bg-[#f7faf8] font-['Inter',sans-serif]">
      <SiteHeader />

      {/* Main Container - 1440px Max Width with Blueprint Borders */}
      <div className="max-w-[1440px] mx-auto blueprint-container relative">
        {/* 1. El problema */}
        <section className="section-divider py-24 md:py-32 px-6 md:px-16 relative overflow-hidden">
          <div className="grid grid-cols-1 md:grid-cols-12 gap-10 md:gap-12 items-center md:min-h-[640px]">
            {/* Left Content */}
            <div className="md:col-span-7 z-10 space-y-5">
              <h1 className="font-['Sora',sans-serif] text-4xl md:text-[48px] lg:text-[56px] md:leading-[1.08] font-bold text-[#163236] tracking-tight max-w-3xl">
                Detecta la carga fantasma de tu data center
              </h1>

              <p className="font-['Inter',sans-serif] text-lg md:text-[19px] text-[#365452] max-w-xl leading-relaxed">
                Las GPUs hacen los cálculos de un data center de inteligencia
                artificial. Cada una consume electricidad desde que se enciende,
                incluso cuando no procesa datos.
              </p>

              <p className="font-['Inter',sans-serif] text-lg md:text-[19px] font-bold text-[#163236] max-w-xl leading-relaxed">
                En promedio, solo 41 de cada 100 GPUs encendidas están trabajando.
              </p>

              <div className="max-w-xl">
                <div className="flex overflow-hidden rounded-lg border border-[#b1cfd1] font-['JetBrains_Mono',monospace] text-lg font-bold text-[#163236]">
                  <div
                    className="bg-[#d5e7df] px-2 sm:px-3 py-3"
                    style={{ width: "41%" }}
                  >
                    41 trabajan
                  </div>
                  <div
                    className="bg-[#e1ead8] px-2 sm:px-3 py-3"
                    style={{ width: "59%" }}
                  >
                    59 siguen inactivas
                  </div>
                </div>
                <p className="text-[#365452] text-lg pt-2 leading-relaxed">
                  Datos promedio de 214 data centers de IA analizados en 2025.
                </p>
              </div>

            </div>

            {/* Right Illustration */}
            <div className="md:col-span-5 relative h-[320px] sm:h-[400px] md:h-[480px] w-full flex items-center justify-center">
              <Image
                src="/images/hero.jpg"
                alt="Carga fantasma entre servidores de un data center"
                fill
                sizes="(max-width: 767px) calc(100vw - 3rem), (max-width: 1440px) 38vw, 550px"
                className="object-contain animate-float"
                priority
              />
            </div>
          </div>

          <div className="mt-8 flex justify-center">
            <a
              href="#causas"
              className="inline-flex min-h-14 items-center gap-3 rounded-full border-2 border-[#24563c] bg-[#e8f0f2] px-7 py-3.5 font-['JetBrains_Mono',monospace] text-lg font-bold text-[#24563c] shadow-md transition-colors hover:bg-[#24563c] hover:text-white hover:shadow-lg"
            >
              <span>Conoce las causas</span>
              <span
                aria-hidden="true"
                className="material-symbols-outlined text-[22px]"
              >
                arrow_downward
              </span>
            </a>
          </div>
        </section>

        {/* 2. Por qué pasa */}
        <section
          id="causas"
          className="section-divider py-28 md:py-32 px-6 md:px-16 bg-[#e8f0f2] scroll-mt-20"
        >
          <div className="flex flex-col md:flex-row md:items-end md:justify-between gap-3 mb-6">
            <h2 className="font-['Sora',sans-serif] text-3xl md:text-4xl font-bold text-[#163236]">
              Por qué pasa esto
            </h2>
            <p className="text-lg text-[#365452] max-w-md md:text-right leading-relaxed">
              Cuatro problemas operativos concentran la mayor parte de la
              capacidad desperdiciada.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5">
            <div className="border border-[#b1cfd1] p-6 rounded-xl bg-white shadow-sm">
              <h3 className="font-['Sora',sans-serif] text-xl font-bold text-[#163236] mb-2">
                Esperan datos
              </h3>
              <p className="text-lg text-[#365452] leading-relaxed">
                Las GPUs procesan rápido, pero los datos llegan tarde. Durante la
                espera permanecen encendidas y consumen energía.
              </p>
            </div>

            <div className="border border-[#b1cfd1] p-6 rounded-xl bg-white shadow-sm">
              <h3 className="font-['Sora',sans-serif] text-xl font-bold text-[#163236] mb-2">
                Se reservan antes de tiempo
              </h3>
              <p className="text-lg text-[#365452] leading-relaxed">
                Los equipos apartan GPUs para trabajos futuros. Mientras nadie
                las usa, permanecen encendidas.
              </p>
            </div>

            <div className="border border-[#b1cfd1] p-6 rounded-xl bg-white shadow-sm">
              <h3 className="font-['Sora',sans-serif] text-xl font-bold text-[#163236] mb-2">
                El calor limita la capacidad
              </h3>
              <p className="text-lg text-[#365452] leading-relaxed">
                Si el sistema de enfriamiento llega a su límite, no puedes activar
                más GPUs aunque tengas espacio y energía disponibles.
              </p>
            </div>

            <div className="border border-[#b1cfd1] p-6 rounded-xl bg-white shadow-sm">
              <h3 className="font-['Sora',sans-serif] text-xl font-bold text-[#163236] mb-2">
                Los datos están separados
              </h3>
              <p className="text-lg text-[#365452] leading-relaxed">
                Infraestructura y operaciones usan tableros distintos. Sin una
                vista común, la capacidad desperdiciada pasa desapercibida.
              </p>
            </div>
          </div>

          <div className="mt-10 flex justify-center">
            <a
              href="#costo"
              className="inline-flex min-h-14 items-center gap-3 rounded-full border-2 border-[#24563c] bg-[#f7faf8] px-7 py-3.5 font-['JetBrains_Mono',monospace] text-lg font-bold text-[#24563c] shadow-md transition-colors hover:bg-[#24563c] hover:text-white hover:shadow-lg"
            >
              <span>Evalúa el impacto</span>
              <span
                aria-hidden="true"
                className="material-symbols-outlined text-[22px]"
              >
                arrow_downward
              </span>
            </a>
          </div>
        </section>

        {/* 3. Cuánto cuesta */}
        <section id="costo" className="section-divider py-28 md:py-32 px-6 md:px-16 bg-[#f7faf8] scroll-mt-20">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-12 items-center">
            <div className="lg:col-span-7">
              <h2 className="font-['Sora',sans-serif] text-3xl md:text-4xl font-bold text-[#163236] mb-3">
                El costo de la carga fantasma
              </h2>
              <p className="font-['Inter',sans-serif] text-lg text-[#365452] max-w-2xl leading-relaxed mb-7">
                En promedio, el{" "}
                <span className="font-bold text-[#163236]">
                  38% de la factura eléctrica
                </span>{" "}
                se destina a equipos encendidos que no producen. Aun así,{" "}
                <span className="font-bold text-[#163236]">
                  la mitad de los operadores
                </span>{" "}
                rechaza nuevos trabajos por falta de capacidad.
              </p>

              <Link
                href="/assessment"
                className="inline-flex min-h-12 items-center gap-2 bg-[#24563c] text-white font-['JetBrains_Mono',monospace] font-bold text-lg px-7 py-3.5 rounded-full hover:bg-[#163236] transition-colors shadow-md"
              >
                <span>Mide tu eficiencia</span>
                <span
                  aria-hidden="true"
                  className="material-symbols-outlined text-[16px]"
                >
                  arrow_forward
                </span>
              </Link>
            </div>

            <div
              className="lg:col-span-5 grid grid-cols-2 gap-3"
              aria-label="Indicadores de desperdicio"
            >
              <div className="flex min-h-48 flex-col justify-center rounded-2xl border border-[#b1cfd1] bg-white p-6 shadow-sm">
                <p className="font-['JetBrains_Mono',monospace] text-4xl md:text-5xl font-bold text-[#24563c]">
                  38%
                </p>
                <p className="mt-2 text-lg leading-relaxed text-[#365452]">
                  de la factura eléctrica no genera trabajo útil
                </p>
              </div>
              <div className="flex min-h-48 flex-col justify-center rounded-2xl border border-[#24563c] bg-[#24563c] p-6 text-white shadow-sm">
                <p className="font-['JetBrains_Mono',monospace] text-4xl md:text-5xl font-bold">
                  50%
                </p>
                <p className="mt-2 text-lg leading-relaxed text-white">
                  de los operadores rechaza nuevos trabajos
                </p>
              </div>
            </div>
          </div>
        </section>

        <SiteFooter />
      </div>
    </div>
  );
}
