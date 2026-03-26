export const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

export const API_ROUTES = {
  health: "/api/health",
  projects: "/api/projects",
  members: "/api/members",
  reports: "/api/reports",
} as const;
