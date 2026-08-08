// ---------------------------------------------------------------------------
// authStore
//
// Holds the app's notion of "who is signed in" so components never need to
// know whether that identity came from Clerk or from local mock state.
//
// Real integration path: a small bridge component (see components/shared/
// ClerkAuthBridge.tsx) reads Clerk's useUser()/useOrganization() once Clerk
// keys are configured, and calls setSession() here. Until then, the sign-in
// screens call setSession() directly with mock values so the whole UI can
// be reviewed with zero backend/Clerk setup.
// ---------------------------------------------------------------------------
import { create } from "zustand";
import { persist } from "zustand/middleware";

export type Role = "ROOT_ADMIN" | "ADMIN" | "OPERATOR";

export interface OperatorProfile {
  companyName: string;
  corporateEmail: string;
  country: string;
  industry: string;
  employees: string;
  dataCenterTier: string;
  gpuClusterSize: number | null;
}

export interface Session {
  role: Role;
  email: string;
  name: string;
  organization?: string;
  companyId?: string;
  /** Present for ROOT_ADMIN / ADMIN — the id of their record in mock/admins.json. */
  adminId?: string;
  profile?: OperatorProfile;
}

interface AuthState {
  session: Session | null;
  setSession: (session: Session) => void;
  setProfile: (profile: OperatorProfile) => void;
  clearSession: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      session: null,
      setSession: (session) => set({ session }),
      setProfile: (profile) =>
        set((state) => (state.session ? { session: { ...state.session, profile } } : state)),
      clearSession: () => set({ session: null }),
    }),
    { name: "ghost-load-auth" }
  )
);
