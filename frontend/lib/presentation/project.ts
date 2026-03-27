import type { ProjectStatus, RiskLevel } from "@/types/project";

const statusLabels: Record<ProjectStatus, string> = {
  EM_ANALISE: "Em análise",
  ANALISE_REALIZADA: "Análise realizada",
  ANALISE_APROVADA: "Análise aprovada",
  INICIADO: "Iniciado",
  PLANEJADO: "Planejado",
  EM_ANDAMENTO: "Em andamento",
  ENCERRADO: "Encerrado",
  CANCELADO: "Cancelado",
};

const riskLabels: Record<RiskLevel, string> = {
  BAIXO: "Baixo",
  MEDIO: "Médio",
  ALTO: "Alto",
};

export function getProjectStatusLabel(status: ProjectStatus) {
  return statusLabels[status];
}

export function getRiskLevelLabel(riskLevel: RiskLevel) {
  return riskLabels[riskLevel];
}
