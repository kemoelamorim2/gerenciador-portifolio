export type ProjectStatus =
  | "EM_ANALISE"
  | "ANALISE_REALIZADA"
  | "ANALISE_APROVADA"
  | "INICIADO"
  | "PLANEJADO"
  | "EM_ANDAMENTO"
  | "ENCERRADO"
  | "CANCELADO";

export type RiskLevel = "BAIXO" | "MEDIO" | "ALTO";

export type ProjectResponse = {
  id: number;
  name: string;
  startDate: string;
  expectedEndDate: string;
  actualEndDate: string | null;
  budget: number;
  description: string;
  managerId: number;
  managerName: string;
  status: ProjectStatus;
  riskLevel: RiskLevel;
};

export type ProjectCreateRequest = {
  name: string;
  startDate: string;
  expectedEndDate: string;
  budget: number;
  description: string;
  managerId: number;
};

export type ProjectUpdateRequest = {
  name: string;
  startDate: string;
  expectedEndDate: string;
  actualEndDate: string | null;
  budget: number;
  description: string;
  managerId: number;
  status: ProjectStatus;
};

export type ProjectStatusUpdateRequest = {
  status: ProjectStatus;
};

export type ProjectFilterRequest = {
  page?: number;
  size?: number;
  name?: string;
  status?: ProjectStatus;
  riskLevel?: RiskLevel;
  managerId?: number;
  budgetMin?: number;
  budgetMax?: number;
  startDateFrom?: string;
  startDateTo?: string;
  expectedEndDateFrom?: string;
  expectedEndDateTo?: string;
};
