import Image from "next/image";

export default function SiteHeader() {
  return (
    <header
      className="sticky top-0 z-40 h-14 flex items-center px-4 border-b theme-surface"
      style={{
        backgroundColor: 'var(--gh-slate-950)',
        borderBottomColor: 'var(--gh-slate-800)',
      }}
    >
      <Image
        src="/Logo%20Estudio.png"
        alt="Estudio"
        unoptimized
        priority
        width={300}
        height={70}
        className="h-8 sm:h-9 w-auto object-contain"
        style={{ objectFit: 'contain' }}
      />
      <span
        className="text-lg sm:text-xl font-extrabold tracking-tight"
        style={{ color: 'var(--gh-brand-text)' }}
      >
        Ghost Load
      </span>
    </header>
  );
}