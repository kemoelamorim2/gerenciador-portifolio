import type { PortfolioReportResponse } from "@/types/report";
import { apiClient } from "./client";
import { API_ROUTES } from "./config";

export async function getPortfolioSummary() {
  const response = await apiClient.get<PortfolioReportResponse>(
    `${API_ROUTES.reports}/portfolio-summary`,
  );
  return response.data;
}
