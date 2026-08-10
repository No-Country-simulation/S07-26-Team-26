import Image from 'next/image';

export default function SiteFooter() {
  return (
    <footer className="bg-[#e8f0f2] text-[#163236] font-['Inter',sans-serif] text-lg border-t border-[#b1cfd1]">
      <div className="flex flex-col sm:flex-row items-center justify-between gap-4 px-6 md:px-16 py-8 max-w-[1440px] mx-auto w-full">
        <div className="flex items-center gap-2">
          <Image
            src="/images/favicon.png"
            alt=""
            width={28}
            height={28}
            style={{ imageRendering: 'pixelated' }}
          />
          <span className="font-['JetBrains_Mono',monospace] uppercase tracking-widest font-bold text-lg">
            GHOST LOAD
          </span>
        </div>

        <div className="flex flex-wrap justify-center gap-x-8 gap-y-2 font-['JetBrains_Mono',monospace] text-lg text-[#365452]">
          <a className="inline-flex min-h-11 items-center hover:text-[#163236] transition-colors" href="#">
            Política de privacidad
          </a>
          <a className="inline-flex min-h-11 items-center hover:text-[#163236] transition-colors" href="#">
            Términos del servicio
          </a>
        </div>
      </div>
    </footer>
  );
}
