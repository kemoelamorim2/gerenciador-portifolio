import axios from "axios";
import { API_BASE_URL } from "./config";
import { getStoredBasicAuth } from "./auth";

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

apiClient.interceptors.request.use((config) => {
  const basicAuth = getStoredBasicAuth();

  if (basicAuth) {
    config.headers.Authorization = `Basic ${basicAuth}`;
  }

  return config;
});
