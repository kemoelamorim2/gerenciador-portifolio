const AUTH_STORAGE_KEY = "portfolio_basic_auth";

export type BasicAuthCredentials = {
  username: string;
  password: string;
};

export function encodeBasicAuth(credentials: BasicAuthCredentials) {
  if (typeof window === "undefined") {
    return "";
  }

  return window.btoa(`${credentials.username}:${credentials.password}`);
}

export function saveBasicAuth(credentials: BasicAuthCredentials) {
  if (typeof window === "undefined") {
    return;
  }

  window.sessionStorage.setItem(AUTH_STORAGE_KEY, encodeBasicAuth(credentials));
}

export function getStoredBasicAuth() {
  if (typeof window === "undefined") {
    return null;
  }

  return window.sessionStorage.getItem(AUTH_STORAGE_KEY);
}

export function clearBasicAuth() {
  if (typeof window === "undefined") {
    return;
  }

  window.sessionStorage.removeItem(AUTH_STORAGE_KEY);
}
