import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface AuthState {
  token: string | null;
  tenantId: string | null;
  setToken: (token: string, tenantId: string) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      tenantId: null,
      setToken: (token, tenantId) => set({ token, tenantId }),
      logout: () => set({ token: null, tenantId: null }),
    }),
    {
      name: 'auth-storage',
    }
  )
);
