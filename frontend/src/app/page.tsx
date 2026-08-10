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
        <section className="section-divider py-14 md:py-20 px-6 md:px-16 relative overflow-hidden">
          <div className="grid grid-cols-1 md:grid-cols-12 gap-10 md:gap-12 items-center md:min-h-[520px]">
            {/* Left Content */}
            <div className="md:col-span-7 z-10 space-y-5">
              <h1 className="font-['Sora',sans-serif] text-4xl md:text-[48px] lg:text-[56px] md:leading-[1.08] font-bold text-[#163236] tracking-tight max-w-3xl">
                Detecta la carga fantasma de tu data center
              </h1>

              <p className="font-['Inter',sans-serif] text-lg md:text-[19px] text-[#365452] max-w-xl leading-relaxed">
                Un data center de inteligencia artificial funciona con GPUs: los
                chips que hacen los cálculos. Una GPU consume electricidad desde
                que se enciende, trabaje o no.
              </p>

              <p className="font-['Inter',sans-serif] text-lg md:text-[19px] font-bold text-[#163236] max-w-xl leading-relaxed">
                De cada 100 GPUs encendidas, en promedio solo 41 están
                calculando algo.
              </p>

              <div className="max-w-xl">
                <div className="flex rounded-lg overflow-hidden font-['JetBrains_Mono',monospace] text-base text-white">
                  <div
                    className="bg-[#24563c] px-2 sm:px-3 py-3"
                    style={{ width: "41%" }}
                  >
                    41 trabajan
                  </div>
                  <div
                    className="bg-[#31551f] px-2 sm:px-3 py-3"
                    style={{ width: "59%" }}
                  >
                    59 encendidas sin hacer nada
                  </div>
                </div>
                <p className="text-[#365452] text-base pt-2 leading-relaxed">
                  Promedio de 214 data centers de IA medidos durante 2025.
                </p>
              </div>

              <div className="flex items-center pt-1">
                <a
                  href="#causas"
                  className="inline-flex min-h-12 items-center gap-2 font-['JetBrains_Mono',monospace] text-base text-[#24563c] hover:text-[#163236] transition-colors"
                >
                  <span>Conoce las causas</span>
                  <span
                    aria-hidden="true"
                    className="material-symbols-outlined text-[16px]"
                  >
                    arrow_downward
                  </span>
                </a>
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
        </section>

        {/* 2. Por qué pasa */}
        <section
          id="causas"
          className="section-divider py-16 md:py-20 px-6 md:px-16 bg-[#e8f0f2] scroll-mt-20"
        >
          <div className="flex flex-col md:flex-row md:items-end md:justify-between gap-3 mb-6">
            <h2 className="font-['Sora',sans-serif] text-3xl md:text-4xl font-bold text-[#163236]">
              Por qué pasa esto
            </h2>
            <p className="text-base text-[#365452] max-w-md md:text-right leading-relaxed">
              Cuatro fricciones operativas explican la mayor parte de la
              capacidad desperdiciada.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5">
            <div className="border border-[#b1cfd1] p-6 rounded-xl bg-white shadow-sm">
              <h3 className="font-['Sora',sans-serif] text-xl font-bold text-[#163236] mb-2">
                Esperan Datos
              </h3>
              <p className="text-base text-[#365452] leading-relaxed">
                La GPU calcula rápido, pero la información llega lenta. Mientras
                espera, sigue encendida y sigue consumiendo.
              </p>
            </div>

            <div className="border border-[#b1cfd1] p-6 rounded-xl bg-white shadow-sm">
              <h3 className="font-['Sora',sans-serif] text-xl font-bold text-[#163236] mb-2">
                Están Apartadas
              </h3>
              <p className="text-base text-[#365452] leading-relaxed">
                Un equipo reserva GPUs para un trabajo futuro. Nadie las usa
                mientras tanto, pero ya están prendidas.
              </p>
            </div>

            <div className="border border-[#b1cfd1] p-6 rounded-xl bg-white shadow-sm">
              <h3 className="font-['Sora',sans-serif] text-xl font-bold text-[#163236] mb-2">
                Se Calientan
              </h3>
              <p className="text-base text-[#365452] leading-relaxed">
                Las GPUs generan mucho calor. Si el aire acondicionado no da
                abasto, no se pueden encender más aunque haya espacio y
                electricidad disponibles.
              </p>
            </div>

            <div className="border border-[#b1cfd1] p-6 rounded-xl bg-white shadow-sm">
              <h3 className="font-['Sora',sans-serif] text-xl font-bold text-[#163236] mb-2">
                Nadie lo Mide Junto
              </h3>
              <p className="text-base text-[#365452] leading-relaxed">
                El área que cuida el edificio y el área que reparte los trabajos
                usan tableros distintos. El desperdicio queda en el medio y no
                aparece en ninguno.
              </p>
            </div>
          </div>
        </section>

        {/* 3. Cuánto cuesta */}
        <section className="section-divider py-16 md:py-20 px-6 md:px-16 bg-[#f7faf8]">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-12 items-center">
            <div className="lg:col-span-7">
              <h2 className="font-['Sora',sans-serif] text-3xl md:text-4xl font-bold text-[#163236] mb-3">
                Cuánto cuesta
              </h2>
              <p className="font-['Inter',sans-serif] text-base text-[#365452] max-w-2xl leading-relaxed mb-7">
                Sumando todo, en promedio el{" "}
                <span className="font-bold text-[#163236]">
                  38% de la factura eléctrica
                </span>{" "}
                del data center se va en equipos encendidos que no producen
                nada. Y aun así,{" "}
                <span className="font-bold text-[#163236]">
                  la mitad de los operadores
                </span>{" "}
                rechaza trabajos por falta de espacio.
              </p>

              <Link
                href="/assessment"
                className="inline-flex min-h-12 items-center gap-2 bg-[#24563c] text-white font-['JetBrains_Mono',monospace] font-bold text-base px-7 py-3.5 rounded-full hover:bg-[#163236] transition-colors shadow-md"
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
                <p className="mt-2 text-base leading-relaxed text-[#365452]">
                  de la factura eléctrica se desperdicia
                </p>
              </div>
              <div className="flex min-h-48 flex-col justify-center rounded-2xl border border-[#24563c] bg-[#24563c] p-6 text-white shadow-sm">
                <p className="font-['JetBrains_Mono',monospace] text-4xl md:text-5xl font-bold">
                  50%
                </p>
                <p className="mt-2 text-base leading-relaxed text-white">
                  rechaza trabajos por falta de capacidad
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
