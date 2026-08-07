'use client';

export type Theme = 'dark' | 'light';

const THEME_KEY = 'ghostload.theme';

export function getStoredTheme(): Theme | null {
  if (typeof window === 'undefined') return null;
  try {
    const value = window.localStorage.getItem(THEME_KEY);
    return value === 'light' || value === 'dark' ? value : null;
  } catch {
    return null;
  }
}

export function applyTheme(theme: Theme): void {
  if (typeof document === 'undefined') return;
  const root = document.documentElement;
  root.classList.toggle('light', theme === 'light');
}

export function setTheme(theme: Theme): void {
  applyTheme(theme);
  try {
    window.localStorage.setItem(THEME_KEY, theme);
  } catch {
    // localStorage no disponible (modo incógnito estricto); el tema sigue en memoria.
  }
}

export function toggleTheme(): Theme {
  const next: Theme = document.documentElement.classList.contains('light') ? 'dark' : 'light';
  setTheme(next);
  return next;
}
