'use client';

import React from 'react';
import { Moon, Sun } from 'lucide-react';
import { applyTheme, getStoredTheme, toggleTheme } from '@/lib/theme';

export default function ThemeToggle() {
  const [isLight, setIsLight] = React.useState<boolean>(false);

  React.useEffect(() => {
    const stored = getStoredTheme();
    if (!stored) return;
    applyTheme(stored);
    const timer = window.setTimeout(() => setIsLight(stored === 'light'), 0);
    return () => window.clearTimeout(timer);
  }, []);

  const onClick = () => {
    const next = toggleTheme();
    setIsLight(next === 'light');
  };

  return (
    <button
      type="button"
      onClick={onClick}
      aria-label="Cambiar tema claro/oscuro"
      title={isLight ? 'Cambiar a tema oscuro' : 'Cambiar a tema claro'}
      className="fixed top-4 right-4 z-50 inline-flex items-center justify-center w-10 h-10 rounded-lg border pixel-card transition-colors"
      style={{
        borderColor: 'var(--gh-slate-700)',
        backgroundColor: 'var(--gh-slate-900)',
        color: 'var(--gh-slate-300)',
      }}
      onMouseEnter={(e) => {
        (e.currentTarget as HTMLButtonElement).style.borderColor = 'var(--gh-green-500)';
        (e.currentTarget as HTMLButtonElement).style.color = 'var(--gh-green-400)';
      }}
      onMouseLeave={(e) => {
        (e.currentTarget as HTMLButtonElement).style.borderColor = 'var(--gh-slate-700)';
        (e.currentTarget as HTMLButtonElement).style.color = 'var(--gh-slate-300)';
      }}
    >
      {isLight ? <Moon className="w-5 h-5" /> : <Sun className="w-5 h-5" />}
    </button>
  );
}