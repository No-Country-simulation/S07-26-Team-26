'use client';

import Link from 'next/link';
import Image from 'next/image';
import { usePathname } from 'next/navigation';

const NAV_ITEMS = [
  { href: '/', label: 'Inicio', primary: false },
  { href: '/assessment', label: 'Mide tu eficiencia', primary: true },
];

export default function SiteHeader() {
  const pathname = usePathname();

  return (
    <header className="bg-[#f7faf8]/90 backdrop-blur-md text-[#163236] font-['JetBrains_Mono',monospace] text-base border-b border-[#b1cfd1] sticky top-0 z-50 transition-all duration-300 shadow-xs">
      <div className="max-w-[1440px] mx-auto flex justify-between items-center h-16 px-4 sm:px-6 md:px-16 w-full border-x border-[#d9e6e7]">
        {/* Brand */}
        <Link href="/" className="flex min-h-11 items-center gap-2 group">
          <Image
            src="/images/favicon.png"
            alt=""
            width={28}
            height={28}
            className="transition-transform group-hover:scale-110"
            style={{ imageRendering: 'pixelated' }}
          />
          <span className="hidden sm:inline font-['Sora',sans-serif] text-xl font-bold tracking-tighter text-[#163236]">
            GHOST LOAD
          </span>
        </Link>

        {/* Navigation Links */}
        <nav className="flex items-center gap-2">
          {NAV_ITEMS.map((item) => {
            const isActive = pathname === item.href;
            return (
              <Link
                key={item.href}
                href={item.href}
                aria-current={isActive ? 'page' : undefined}
                className={`flex min-h-11 items-center rounded-full px-3 sm:px-5 py-2 font-bold transition-colors ${
                  item.primary
                    ? isActive
                      ? 'bg-[#163236] text-white'
                      : 'bg-[#24563c] text-white hover:bg-[#163236]'
                    : isActive
                      ? 'bg-[#e8f0f2] text-[#163236]'
                      : 'border border-[#24563c] text-[#24563c] hover:bg-[#e8f0f2] hover:text-[#163236]'
                }`}
              >
                {item.label}
              </Link>
            );
          })}
        </nav>
      </div>
    </header>
  );
}
