import type { AxiosError } from "axios";
import type { ApiErrorResponse } from "@/types/api";

export function getApiErrorMessage(error: unknown, fallback: string) {
  const apiError = error as AxiosError<ApiErrorResponse>;
  return apiError.response?.data?.message || fallback;
}
