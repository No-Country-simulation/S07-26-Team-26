import { create } from 'zustand';

export type Role = "ROOT_ADMIN" | "ADMIN" | "OPERATOR";

export interface Session {
  role: Role;
  email: string;
  name?: string;
  adminId?: string;
  organization?: string;
  companyId?: string;
  [key: string]: any;
}

export interface AuthState {
  isAuthenticated: boolean;
  accessToken: string | null;
  session: Session | null;
  login: (token: string) => void;
  logout: () => void;
  setSession: (session: Session | null) => void;
  setProfile: (profile: Record<string, any>) => void;
}

const getInitialToken = () => {
  if (typeof window === 'undefined') {
    return null;
  }
  return localStorage.getItem('admin_token');
};

const getInitialSession = (): Session | null => {
  if (typeof window === 'undefined') {
    return null;
  }
  const raw = localStorage.getItem('admin_session');
  if (!raw) return null;
  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
};

const initialToken = getInitialToken();
const initialSession = getInitialSession();

export const useAuthStore = create<AuthState>((set) => ({
  isAuthenticated: Boolean(initialToken || initialSession),
  accessToken: initialToken,
  session: initialSession,

  login: (token: string) => {
    const defaultSession: Session = {
      role: 'ADMIN',
      email: 'admin@ghostload.local',
      name: 'Admin',
    };
    if (typeof window !== 'undefined') {
      localStorage.setItem('admin_token', token);
      localStorage.setItem('admin_session', JSON.stringify(defaultSession));
    }
    set({ isAuthenticated: true, accessToken: token, session: defaultSession });
  },

  logout: () => {
    if (typeof window !== 'undefined') {
      localStorage.removeItem('admin_token');
      localStorage.removeItem('admin_session');
    }
    set({ isAuthenticated: false, accessToken: null, session: null });
  },

  setSession: (session: Session | null) => {
    if (typeof window !== 'undefined') {
      if (session) {
        localStorage.setItem('admin_session', JSON.stringify(session));
      } else {
        localStorage.removeItem('admin_session');
      }
    }
    set((state) => ({
      session,
      isAuthenticated: Boolean(session || state.accessToken),
    }));
  },

  setProfile: (profile: Record<string, any>) => {
    set((state) => {
      const updatedSession = state.session
        ? {
            ...state.session,
            ...profile,
            organization: profile.companyName ?? state.session.organization,
            email: profile.corporateEmail ?? state.session.email,
          }
        : null;
      if (typeof window !== 'undefined' && updatedSession) {
        localStorage.setItem('admin_session', JSON.stringify(updatedSession));
      }
      return { session: updatedSession };
    });
  },
}));

