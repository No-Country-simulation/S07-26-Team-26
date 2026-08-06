import { create } from 'zustand';

interface AuthState {
  isAuthenticated: boolean;
  accessToken: string | null;
  login: (token: string) => void;
  logout: () => void;
}

const getInitialToken = () => {
  if (typeof window === 'undefined') {
    return null;
  }
  return localStorage.getItem('admin_token');
};

export const useAuthStore = create<AuthState>((set) => ({
  isAuthenticated: Boolean(getInitialToken()),
  accessToken: getInitialToken(),
  login: (token: string) => {
    if (typeof window !== 'undefined') {
      localStorage.setItem('admin_token', token);
    }
    set({ isAuthenticated: true, accessToken: token });
  },
  logout: () => {
    if (typeof window !== 'undefined') {
      localStorage.removeItem('admin_token');
    }
    set({ isAuthenticated: false, accessToken: null });
  },
}));
