'use client';

export type AssessmentStep = 'register' | 'calculator' | 'questionnaire' | 'results';

export interface AssessmentSession {
  evaluationId: string;
  token: string;
  step: AssessmentStep;
  answers: Record<string, number>;
}

const SESSION_KEY = 'ghostload.assessment.session';

export function loadSession(): AssessmentSession | null {
  if (typeof window === 'undefined') return null;
  try {
    const raw = window.localStorage.getItem(SESSION_KEY);
    if (!raw) return null;
    const parsed = JSON.parse(raw) as AssessmentSession;
    if (!parsed.evaluationId || !parsed.token) return null;
    return parsed;
  } catch {
    return null;
  }
}

export function saveSession(session: AssessmentSession): void {
  if (typeof window === 'undefined') return;
  window.localStorage.setItem(SESSION_KEY, JSON.stringify(session));
}

export function updateSession(partial: Partial<AssessmentSession>): AssessmentSession | null {
  const current = loadSession();
  if (!current) return null;
  const next = { ...current, ...partial };
  saveSession(next);
  return next;
}

export function clearSession(): void {
  if (typeof window === 'undefined') return;
  window.localStorage.removeItem(SESSION_KEY);
}
