import type { ProjectStatus } from "./project";

export type PortfolioStatusSummaryResponse = {
  status: ProjectStatus;
  projectCount: number;
  totalBudget: number;
};

export type PortfolioReportResponse = {
  statusSummary: PortfolioStatusSummaryResponse[];
  averageClosedProjectDurationInDays: number;
  totalUniqueAllocatedMembers: number;
};
