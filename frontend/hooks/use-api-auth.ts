"use client";

import {
  clearBasicAuth,
  getStoredBasicAuth,
  saveBasicAuth,
  type BasicAuthCredentials,
} from "@/lib/api/auth";

export function useApiAuth() {
  return {
    saveCredentials: (credentials: BasicAuthCredentials) => saveBasicAuth(credentials),
    clearCredentials: () => clearBasicAuth(),
    hasCredentials: () => Boolean(getStoredBasicAuth()),
  };
}
