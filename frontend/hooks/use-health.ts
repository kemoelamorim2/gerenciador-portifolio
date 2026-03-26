"use client";

import { getHealth } from "@/lib/api/health";

export function useHealth() {
  return {
    getHealth,
  };
}
