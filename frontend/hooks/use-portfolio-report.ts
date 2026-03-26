"use client";

import { getPortfolioSummary } from "@/lib/api/reports";

export function usePortfolioReport() {
  return {
    getPortfolioSummary,
  };
}
