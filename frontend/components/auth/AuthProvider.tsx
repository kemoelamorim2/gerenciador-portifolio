"use client";

import { createContext, useContext, useEffect, useMemo, useState } from "react";
import axios from "axios";
import { API_BASE_URL, API_ROUTES } from "@/lib/api/config";
import {
  clearBasicAuth,
  encodeBasicAuth,
  getStoredBasicAuth,
  saveBasicAuth,
  type BasicAuthCredentials,
} from "@/lib/api/auth";

type AuthContextValue = {
  isAuthenticated: boolean;
  isReady: boolean;
  login: (credentials: BasicAuthCredentials) => Promise<void>;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isReady, setIsReady] = useState(false);

  useEffect(() => {
    setIsAuthenticated(Boolean(getStoredBasicAuth()));
    setIsReady(true);
  }, []);

  async function login(credentials: BasicAuthCredentials) {
    const encoded = encodeBasicAuth(credentials);

    try {
      await axios.get(`${API_BASE_URL}${API_ROUTES.projects}`, {
        params: { page: 0, size: 1 },
        headers: {
          Authorization: `Basic ${encoded}`,
        },
      });

      saveBasicAuth(credentials);
      setIsAuthenticated(true);
    } catch (error) {
      clearBasicAuth();
      setIsAuthenticated(false);
      throw error;
    }
  }

  function logout() {
    clearBasicAuth();
    setIsAuthenticated(false);
  }

  const value = useMemo(
    () => ({
      isAuthenticated,
      isReady,
      login,
      logout,
    }),
    [isAuthenticated, isReady],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }

  return context;
}
