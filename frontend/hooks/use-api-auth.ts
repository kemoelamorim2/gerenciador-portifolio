"use client";

import { useAuth } from "@/components/auth/AuthProvider";
import type { BasicAuthCredentials } from "@/lib/api/auth";

export function useApiAuth() {
  const { isAuthenticated, isReady, login, logout } = useAuth();

  return {
    saveCredentials: (credentials: BasicAuthCredentials) => login(credentials),
    clearCredentials: () => logout(),
    hasCredentials: () => isAuthenticated,
    isReady: () => isReady,
  };
}
