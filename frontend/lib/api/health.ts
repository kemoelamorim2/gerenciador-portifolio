import { apiClient } from "./client";
import { API_ROUTES } from "./config";

export type HealthResponse = {
  status?: string;
  service?: string;
};

export async function getHealth() {
  const response = await apiClient.get<HealthResponse>(API_ROUTES.health);
  return response.data;
}
