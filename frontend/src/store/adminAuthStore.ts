import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface AdminProfile {
  id: string;
  name: string;
  email: string;
}

interface AdminAuthState {
  accessToken: string | null;
  admin: AdminProfile | null;
  setSession: (accessToken: string, admin: AdminProfile) => void;
  clearSession: () => void;
}

export const useAdminAuthStore = create<AdminAuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      admin: null,
      setSession: (accessToken, admin) => set({ accessToken, admin }),
      clearSession: () => set({ accessToken: null, admin: null }),
    }),
    { name: 'ghost-load-admin-session' }
  )
);
