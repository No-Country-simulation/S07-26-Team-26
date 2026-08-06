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
  hasHydrated: boolean;
  setSession: (accessToken: string, admin: AdminProfile) => void;
  clearSession: () => void;
  onHydrated: () => void;
}

export const useAdminAuthStore = create<AdminAuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      admin: null,
      hasHydrated: false,
      setSession: (accessToken, admin) => set({ accessToken, admin }),
      clearSession: () => set({ accessToken: null, admin: null }),
      onHydrated: () => set({ hasHydrated: true }),
    }),
    {
      name: 'ghost-load-admin-session',
      skipHydration: true,
      onRehydrateStorage: () => (state) => {
        state?.onHydrated();
      },
    }
  )
);
