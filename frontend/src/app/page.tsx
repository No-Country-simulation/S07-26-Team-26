'use client';

import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import Image from 'next/image';

const NAV_ITEMS = [
  { id: 'hero', label: 'Producto' },
  { id: 'modulos', label: 'Módulos' },
  { id: 'madurez', label: 'Niveles de Madurez' },
  { id: 'metodologia', label: 'Metodología' },
];

export default function Home() {
  const [activeSection, setActiveSection] = useState<string>('hero');

  useEffect(() => {
    const handleScroll = () => {
      const scrollPosition = window.scrollY + 200;

      // Check if user is near the bottom of the page
      if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 50) {
        setActiveSection(NAV_ITEMS[NAV_ITEMS.length - 1].id);
        return;
      }

      for (let i = NAV_ITEMS.length - 1; i >= 0; i--) {
        const section = document.getElementById(NAV_ITEMS[i].id);
        if (section) {
          const top = section.offsetTop;
          if (scrollPosition >= top) {
            setActiveSection(NAV_ITEMS[i].id);
            break;
          }
        }
      }
    };

    window.addEventListener('scroll', handleScroll, { passive: true });
    handleScroll();

    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const handleNavClick = (e: React.MouseEvent<HTMLAnchorElement>, id: string) => {
    e.preventDefault();
    setActiveSection(id);
    const element = document.getElementById(id);
    if (element) {
      const headerOffset = 80;
      const elementPosition = element.getBoundingClientRect().top;
      const offsetPosition = elementPosition + window.pageYOffset - headerOffset;

      window.scrollTo({
        top: offsetPosition,
        behavior: 'smooth'
      });
    }
  };

  return (
    <div className="min-h-screen text-[#1b1c1c] bg-[#fbf9f8] font-['Inter',sans-serif]">
      {/* Top Announcement Bar */}
      <div className="bg-[#000000] text-white py-2.5 px-4 text-center font-['JetBrains_Mono',monospace] text-xs flex justify-center items-center gap-2 border-b border-[#303031] relative z-40">
        <span className="w-2 h-2 rounded-full bg-[#00fabe] animate-pulse"></span>
        <span>GHOST LOAD levanta $15M en Serie A para la optimización de data centers.</span>
        <a className="underline hover:text-[#00e1ab] transition-colors ml-1" href="#metrics">
          Leer informe →
        </a>
      </div>

      {/* Sticky Header / Navigation */}
      <header className="bg-[#fbf9f8]/90 backdrop-blur-md text-[#000000] font-['JetBrains_Mono',monospace] text-xs border-b border-[#cfc4c5] sticky top-0 z-50 transition-all duration-300 shadow-xs">
        <div className="max-w-[1440px] mx-auto flex justify-between items-center h-16 px-6 md:px-16 w-full border-x border-[#e4e2e2]">
          {/* Brand */}
          <Link href="/" className="flex items-center gap-2 group">
            <span className="material-symbols-outlined text-[26px] text-[#006c50] group-hover:scale-110 transition-transform">
              dns
            </span>
            <span className="font-['Sora',sans-serif] text-xl font-bold tracking-tighter text-[#000000]">
              GHOST LOAD
            </span>
          </Link>

          {/* Navigation Links */}
          <nav className="hidden md:flex gap-6 items-center">
            {NAV_ITEMS.map((item) => {
              const isActive = activeSection === item.id;
              return (
                <a
                  key={item.id}
                  href={`#${item.id}`}
                  onClick={(e) => handleNavClick(e, item.id)}
                  className={`px-2 py-1 border-b-2 transition-all rounded-t ${
                    isActive
                      ? 'text-[#000000] font-bold border-[#000000]'
                      : 'text-[#4c4546] border-transparent hover:text-[#000000] hover:bg-[#f5f3f3]/80'
                  }`}
                >
                  {item.label}
                </a>
              );
            })}
          </nav>

          {/* CTA Actions */}
          <div className="flex items-center gap-3">
            <Link
              href="/login"
              className="text-[#000000] border border-[#000000] font-['JetBrains_Mono',monospace] text-xs px-5 py-2.5 rounded-full hover:bg-[#000000] hover:text-white transition-all shadow-xs active:scale-95"
            >
              Iniciar Sesión
            </Link>
          </div>
        </div>
      </header>

      {/* Main Container - 1440px Max Width with Blueprint Borders */}
      <div className="max-w-[1440px] mx-auto blueprint-container relative">

        {/* Hero Section */}
        <section id="hero" className="section-divider py-16 md:py-24 px-6 md:px-16 relative overflow-hidden scroll-mt-20">
          <div className="grid grid-cols-1 md:grid-cols-12 gap-8 items-center">
            {/* Left Content */}
            <div className="md:col-span-6 z-10 space-y-6">
              <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#efeded] border border-[#cfc4c5] text-xs font-['JetBrains_Mono',monospace] text-[#4c4546]">
                <span className="w-2 h-2 rounded-full bg-[#006c50] animate-pulse"></span>
                PLATAFORMA SAAS DE BENCHMARKING DE INFRAESTRUCTURA
              </div>

              <h1 className="font-['Sora',sans-serif] text-4xl md:text-[52px] lg:text-[60px] md:leading-[1.1] font-bold text-[#000000] tracking-tight">
                Inteligencia de Infraestructura para Data Centers
              </h1>
              
              <p className="font-['Inter',sans-serif] text-lg md:text-[19px] text-[#4c4546] max-w-xl leading-relaxed">
                Identifique capacidad ociosa, evalúe la eficiencia operativa de su centro de datos y obtenga reportes ejecutivos institucionales respaldados por métricas de alta precisión.
              </p>
            </div>

            {/* Right Illustration */}
            <div className="md:col-span-6 relative h-[380px] md:h-[520px] w-full flex items-center justify-center overflow-hidden">
              <Image
                src="/images/Asset-Tracking-Data-Center.svg"
                alt="Asset Tracking Data Center"
                fill
                className="object-contain animate-float"
                priority
              />
            </div>
          </div>
        </section>

        {/* Core Metrics Highlight */}
        <section id="metrics" className="section-divider py-16 px-6 md:px-16 bg-[#f5f3f3]">
          <div className="mb-8">
            <span className="font-['JetBrains_Mono',monospace] text-xs text-[#006c50] uppercase tracking-wider font-bold block mb-1">
              /// Indicadores Clave de Desempeño (KPIs)
            </span>
            <h2 className="font-['Sora',sans-serif] text-2xl md:text-3xl font-bold text-[#000000]">
              Visibilidad Total sobre la Capacidad Desperdiciada
            </h2>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="border border-[#cfc4c5] p-6 rounded-lg bg-white space-y-3 shadow-xs">
              <div className="font-['JetBrains_Mono',monospace] text-xs text-[#006c50] uppercase tracking-wider font-bold">
                Carga Fantasma Reducida
              </div>
              <div className="font-['JetBrains_Mono',monospace] text-4xl font-extrabold text-[#000000]">
                -34.8%
              </div>
              <p className="text-xs text-[#4c4546] leading-relaxed">
                Detección automática de servidores zombies, hilos inactivos y capacidad de rack subutilizada.
              </p>
            </div>

            <div className="border border-[#cfc4c5] p-6 rounded-lg bg-white space-y-3 shadow-xs">
              <div className="font-['JetBrains_Mono',monospace] text-xs text-[#006c50] uppercase tracking-wider font-bold">
                Eficacia de Uso Energético (PUE)
              </div>
              <div className="font-['JetBrains_Mono',monospace] text-4xl font-extrabold text-[#000000]">
                1.12 avg
              </div>
              <p className="text-xs text-[#4c4546] leading-relaxed">
                Optimización de la relación energía total/TI mediante análisis térmico y distribución de carga.
              </p>
            </div>

            <div className="border border-[#cfc4c5] p-6 rounded-lg bg-white space-y-3 shadow-xs">
              <div className="font-['JetBrains_Mono',monospace] text-xs text-[#006c50] uppercase tracking-wider font-bold">
                Retorno de Inversión (ROI)
              </div>
              <div className="font-['JetBrains_Mono',monospace] text-4xl font-extrabold text-[#000000]">
                4.2x ROI
              </div>
              <p className="text-xs text-[#4c4546] leading-relaxed">
                Recuperación proyectada del CAPEX desperdiciado mediante acciones de optimización en 90 días.
              </p>
            </div>
          </div>
        </section>

        {/* Core Modules Grid */}
        <section id="modulos" className="section-divider py-20 px-6 md:px-16 bg-[#fbf9f8] scroll-mt-20">
          <div className="max-w-3xl mb-12">
            <span className="font-['JetBrains_Mono',monospace] text-xs text-[#006c50] uppercase tracking-wider font-bold block mb-1">
              /// Arquitectura de la Solución
            </span>
            <h2 className="font-['Sora',sans-serif] text-3xl md:text-4xl font-bold text-[#000000] mb-4">
              Módulos Especializados para Operadores de Data Centers
            </h2>
            <p className="text-[#4c4546] text-base leading-relaxed">
              Ghost Load integra herramientas cuantitativas y modelos analíticos diseñados para transformar datos operativos en decisiones ejecutivas de alto impacto.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {/* Card 1 */}
            <div className="border border-[#cfc4c5] p-6 rounded-xl bg-white space-y-4 hover:border-[#000000] transition-all">
              <div className="w-10 h-10 rounded-lg bg-[#efeded] flex items-center justify-center border border-[#cfc4c5]">
                <span className="material-symbols-outlined text-[#006c50]">calculate</span>
              </div>
              <h3 className="font-['Sora',sans-serif] text-lg font-bold text-[#000000]">
                Capacity Calculator
              </h3>
              <p className="text-xs text-[#4c4546] leading-relaxed">
                Algoritmo cuantitativo que procesa métricas de carga eléctrica, densidad de rack, enfriamiento y servidores activos para determinar la capacidad inutilizada exactas.
              </p>
              <div className="pt-2 text-[11px] font-['JetBrains_Mono',monospace] text-[#006c50] font-semibold">
                → Muestreo & Scoring /100
              </div>
            </div>

            {/* Card 2 */}
            <div className="border border-[#cfc4c5] p-6 rounded-xl bg-white space-y-4 hover:border-[#000000] transition-all">
              <div className="w-10 h-10 rounded-lg bg-[#efeded] flex items-center justify-center border border-[#cfc4c5]">
                <span className="material-symbols-outlined text-[#006c50]">leaderboard</span>
              </div>
              <h3 className="font-['Sora',sans-serif] text-lg font-bold text-[#000000]">
                Benchmark Engine
              </h3>
              <p className="text-xs text-[#4c4546] leading-relaxed">
                Motor de comparación continua contra estándares globales de la industria. Clasifica la posición competitiva de su infraestructura por percentil global y regional.
              </p>
              <div className="pt-2 text-[11px] font-['JetBrains_Mono',monospace] text-[#006c50] font-semibold">
                → Ranking por Percentil
              </div>
            </div>

            {/* Card 3 */}
            <div className="border border-[#cfc4c5] p-6 rounded-xl bg-white space-y-4 hover:border-[#000000] transition-all">
              <div className="w-10 h-10 rounded-lg bg-[#efeded] flex items-center justify-center border border-[#cfc4c5]">
                <span className="material-symbols-outlined text-[#006c50]">picture_as_pdf</span>
              </div>
              <h3 className="font-['Sora',sans-serif] text-lg font-bold text-[#000000]">
                PDF Institucional
              </h3>
              <p className="text-xs text-[#4c4546] leading-relaxed">
                Generación automática de un informe técnico y financiero de alta fidelidad listo para ser presentado al directorio y comités de inversión.
              </p>
              <div className="pt-2 text-[11px] font-['JetBrains_Mono',monospace] text-[#006c50] font-semibold">
                → Reporte Descargable en PDF
              </div>
            </div>

            {/* Card 4 */}
            <div className="border border-[#cfc4c5] p-6 rounded-xl bg-white space-y-4 hover:border-[#000000] transition-all">
              <div className="w-10 h-10 rounded-lg bg-[#efeded] flex items-center justify-center border border-[#cfc4c5]">
                <span className="material-symbols-outlined text-[#006c50]">analytics</span>
              </div>
              <h3 className="font-['Sora',sans-serif] text-lg font-bold text-[#000000]">
                Optimization Engine
              </h3>
              <p className="text-xs text-[#4c4546] leading-relaxed">
                Análisis algorítmico avanzado para proporcionar recomendaciones personalizadas y hojas de ruta de optimización paso a paso.
              </p>
              <div className="pt-2 text-[11px] font-['JetBrains_Mono',monospace] text-[#006c50] font-semibold">
                → Hojas de Ruta de Optimización
              </div>
            </div>
          </div>
        </section>

        {/* Maturity Index Section */}
        <section id="madurez" className="section-divider py-20 px-6 md:px-16 bg-[#f5f3f3] scroll-mt-20">
          <div className="max-w-3xl mb-12">
            <span className="font-['JetBrains_Mono',monospace] text-xs text-[#006c50] uppercase tracking-wider font-bold block mb-1">
              /// Clasificación de Infraestructura
            </span>
            <h2 className="font-['Sora',sans-serif] text-3xl md:text-4xl font-bold text-[#000000] mb-4">
              Índice de Madurez Operativa
            </h2>
            <p className="text-[#4c4546] text-base leading-relaxed">
              Basado en el puntaje consolidado (0 a 100), Ghost Load clasifica cada instalación en uno de cuatro niveles estándar de madurez técnica.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 font-['JetBrains_Mono',monospace]">
            {/* Level 1 */}
            <div className="border-t-4 border-[#006c50] border-x border-b border-[#cfc4c5] p-6 rounded-b-xl bg-white space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-xs font-bold text-[#006c50]">Rango 75 - 100</span>
                <span className="px-2 py-0.5 text-[10px] rounded bg-[#00fabe]/20 text-[#006f52] font-bold">ÓPTIMO</span>
              </div>
              <h4 className="font-['Sora',sans-serif] text-xl font-bold text-[#000000]">Orquestado</h4>
              <p className="font-['Inter',sans-serif] text-xs text-[#4c4546] leading-relaxed">
                Data centers totalmente automatizados con balanceo dinámico de carga, PUE optimizado y nula capacidad fantasma en rack.
              </p>
            </div>

            {/* Level 2 */}
            <div className="border-t-4 border-[#0284c7] border-x border-b border-[#cfc4c5] p-6 rounded-b-xl bg-white space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-xs font-bold text-[#0284c7]">Rango 50 - 74</span>
                <span className="px-2 py-0.5 text-[10px] rounded bg-sky-100 text-sky-800 font-bold">ESTÁNDAR</span>
              </div>
              <h4 className="font-['Sora',sans-serif] text-xl font-bold text-[#000000]">Coordinado</h4>
              <p className="font-['Inter',sans-serif] text-xs text-[#4c4546] leading-relaxed">
                Supervisión centralizada efectiva pero con oportunidades identificadas en redistribución térmica y cargas zombie menores.
              </p>
            </div>

            {/* Level 3 */}
            <div className="border-t-4 border-[#d97706] border-x border-b border-[#cfc4c5] p-6 rounded-b-xl bg-white space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-xs font-bold text-[#d97706]">Rango 25 - 49</span>
                <span className="px-2 py-0.5 text-[10px] rounded bg-amber-100 text-amber-800 font-bold">ALERTA</span>
              </div>
              <h4 className="font-['Sora',sans-serif] text-xl font-bold text-[#000000]">Reactivo</h4>
              <p className="font-['Inter',sans-serif] text-xs text-[#4c4546] leading-relaxed">
                Operaciones dependientes de mantenimientos manuales. Elevados márgenes de energía no aprovechables.
              </p>
            </div>

            {/* Level 4 */}
            <div className="border-t-4 border-[#ba1a1a] border-x border-b border-[#cfc4c5] p-6 rounded-b-xl bg-white space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-xs font-bold text-[#ba1a1a]">Rango 0 - 24</span>
                <span className="px-2 py-0.5 text-[10px] rounded bg-red-100 text-red-800 font-bold">CRÍTICO</span>
              </div>
              <h4 className="font-['Sora',sans-serif] text-xl font-bold text-[#000000]">Fragmentado</h4>
              <p className="font-['Inter',sans-serif] text-xs text-[#4c4546] leading-relaxed">
                Deficiencias severas en visibilidad de recursos, altos costos ineficientes de enfriamiento y elevado Ghost Load.
              </p>
            </div>
          </div>
        </section>

        {/* User Journey / Step Methodology */}
        <section id="metodologia" className="section-divider py-20 px-6 md:px-16 bg-[#fbf9f8] scroll-mt-20">
          <div className="text-center max-w-2xl mx-auto mb-16">
            <span className="font-['JetBrains_Mono',monospace] text-xs text-[#006c50] uppercase tracking-wider font-bold block mb-1">
              /// Proceso de Evaluación
            </span>
            <h2 className="font-['Sora',sans-serif] text-3xl md:text-4xl font-bold text-[#000000] mb-4">
              El Embudo de Diagnóstico en 4 Pasos
            </h2>
            <p className="text-[#4c4546] text-sm leading-relaxed">
              Un flujo guiado sin fricciones diseñado para obtener métricas comparables en menos de 10 minutos.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-4 gap-8 relative">
            <div className="space-y-3 border-l-2 border-[#000000] pl-4">
              <span className="font-['JetBrains_Mono',monospace] text-xs font-bold text-[#006c50]">PASO 01</span>
              <h3 className="font-['Sora',sans-serif] font-bold text-base text-[#000000]">Registro de Datos</h3>
              <p className="text-xs text-[#4c4546] leading-relaxed">
                Ingreso de parámetros operativos de la instalación (capacidad contratada, PUE, cantidad de racks, densidad kW).
              </p>
            </div>

            <div className="space-y-3 border-l-2 border-[#000000] pl-4">
              <span className="font-['JetBrains_Mono',monospace] text-xs font-bold text-[#006c50]">PASO 02</span>
              <h3 className="font-['Sora',sans-serif] font-bold text-base text-[#000000]">Cálculo de KPIs</h3>
              <p className="text-xs text-[#4c4546] leading-relaxed">
                El motor cuantifica el índice de Ghost Load, el score consolidado sobre 100 y el nivel de madurez técnica.
              </p>
            </div>

            <div className="space-y-3 border-l-2 border-[#000000] pl-4">
              <span className="font-['JetBrains_Mono',monospace] text-xs font-bold text-[#006c50]">PASO 03</span>
              <h3 className="font-['Sora',sans-serif] font-bold text-base text-[#000000]">Benchmark Global</h3>
              <p className="text-xs text-[#4c4546] leading-relaxed">
                Posicionamiento en el ranking de la industria en comparación con data centers del mismo segmento y región.
              </p>
            </div>

            <div className="space-y-3 border-l-2 border-[#000000] pl-4">
              <span className="font-['JetBrains_Mono',monospace] text-xs font-bold text-[#006c50]">PASO 04</span>
              <h3 className="font-['Sora',sans-serif] font-bold text-base text-[#000000]">Reporte Institucional</h3>
              <p className="text-xs text-[#4c4546] leading-relaxed">
                Generación inmediata del PDF institucional con recomendaciones técnicas y financieras para la toma de decisiones.
              </p>
            </div>
          </div>

          <div className="mt-16 text-center">
            <Link
              href="/login"
              className="inline-flex items-center gap-2 bg-[#000000] text-white font-['JetBrains_Mono',monospace] text-xs px-8 py-4 rounded-full hover:bg-[#1b1b1b] transition-all shadow-md"
            >
              <span>Comenzar Evaluación de Infraestructura</span>
              <span className="material-symbols-outlined text-[16px]">arrow_forward</span>
            </Link>
          </div>
        </section>

        {/* Trust / Partners Section */}
        <section className="section-divider py-16 px-6 md:px-16 bg-[#f5f3f3]">
          <div className="text-center mb-10">
            <span className="font-['JetBrains_Mono',monospace] text-xs text-[#4c4546] uppercase tracking-widest font-semibold">
              Potenciando las instalaciones más eficientes del mundo
            </span>
          </div>
          <div className="flex flex-wrap justify-center items-center gap-x-12 gap-y-6 opacity-75 hover:opacity-100 transition-opacity">
            <span className="font-['Sora',sans-serif] text-xl font-bold tracking-tighter text-[#1b1c1c]">
              EQUINIX
            </span>
            <span className="font-['Sora',sans-serif] text-xl font-bold tracking-tighter text-[#1b1c1c]">
              DIGITAL REALTY
            </span>
            <span className="font-['Sora',sans-serif] text-xl font-bold tracking-tighter text-[#1b1c1c]">
              AWS
            </span>
            <span className="font-['Sora',sans-serif] text-xl font-bold tracking-tighter text-[#1b1c1c]">
              GOOGLE CLOUD
            </span>
            <span className="font-['Sora',sans-serif] text-xl font-bold tracking-tighter text-[#1b1c1c]">
              CYXTERA
            </span>
          </div>
        </section>

        {/* Footer Component */}
        <footer id="reports" className="bg-[#f5f3f3] text-[#1b1c1c] font-['Inter',sans-serif] text-xs border-t border-[#cfc4c5]">
          <div className="grid grid-cols-1 md:grid-cols-12 gap-8 px-6 md:px-16 py-12 max-w-[1440px] mx-auto w-full">
            <div className="md:col-span-5 space-y-3">
              <div className="flex items-center gap-2">
                <span className="material-symbols-outlined text-[20px] text-[#006c50]">
                  dns
                </span>
                <span className="font-['JetBrains_Mono',monospace] uppercase tracking-widest font-bold text-sm">
                  GHOST LOAD
                </span>
              </div>
              <p className="text-[#4c4546] text-xs max-w-sm leading-relaxed">
                Plataforma SaaS para operadores de Data Centers que identifica capacidad desperdiciada mediante benchmarking, cálculo de KPIs y análisis cuantitativo.
              </p>
              <p className="text-[#4c4546] text-[11px] pt-2">
                © {new Date().getFullYear()} GHOST LOAD. Infrastructure Intelligence. Todos los derechos reservados.
              </p>
            </div>
            
            <div className="md:col-span-7 flex flex-wrap justify-start md:justify-end gap-x-8 gap-y-4 font-['JetBrains_Mono',monospace] text-xs text-[#4c4546]">
              <Link className="hover:text-[#000000] transition-colors" href="/login">
                Acceso Operadores
              </Link>
              <Link className="hover:text-[#000000] transition-colors" href="/dashboard/admin">
                Dashboard Admin
              </Link>
              <a className="hover:text-[#000000] transition-colors" href="#">
                Política de Privacidad
              </a>
              <a className="hover:text-[#000000] transition-colors" href="#">
                Términos de Servicio
              </a>
              <a className="hover:text-[#000000] transition-colors" href="#">
                ISO 27001
              </a>
              <a className="hover:text-[#000000] transition-colors" href="#">
                SOC2 Type II
              </a>
            </div>
          </div>
        </footer>
      </div>
    </div>
  );
}


