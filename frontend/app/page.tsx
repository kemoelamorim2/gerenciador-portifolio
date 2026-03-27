"use client";

import { useEffect, useState } from "react";
import { usePortfolioReport } from "@/hooks/use-portfolio-report";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { FolderKanban, Users, Clock, DollarSign } from "lucide-react";
import type { PortfolioReportResponse } from "@/types/report";

export default function DashboardPage() {
  const { getPortfolioSummary } = usePortfolioReport();
  const [summary, setSummary] = useState<PortfolioReportResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchData() {
      try {
        const data = await getPortfolioSummary();
        setSummary(data);
      } catch (error) {
        console.error("Failed to load summary", error);
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, [getPortfolioSummary]);

  const totalProjects = summary
    ? summary.statusSummary.reduce((sum, s) => sum + s.projectCount, 0)
    : 0;
  const totalBudget = summary
    ? summary.statusSummary.reduce((sum, s) => sum + s.totalBudget, 0)
    : 0;

  return (
    <div className="container mx-auto p-4 md:p-8">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
          <p className="text-muted-foreground">
            Visão geral do seu portfólio de projetos
          </p>
        </div>
      </div>

      {loading ? (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4 animate-pulse">
          {[1, 2, 3, 4].map((i) => (
            <Card key={i}>
              <CardHeader className="h-[88px] bg-muted rounded-t-xl" />
              <CardContent className="h-[48px] bg-muted/50 rounded-b-xl" />
            </Card>
          ))}
        </div>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">
                Total de Projetos
              </CardTitle>
              <FolderKanban className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{totalProjects}</div>
              <p className="text-xs text-muted-foreground">
                Projetos cadastrados
              </p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Orçamento Total</CardTitle>
              <DollarSign className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">
                {new Intl.NumberFormat("pt-BR", {
                  style: "currency",
                  currency: "BRL",
                }).format(totalBudget)}
              </div>
              <p className="text-xs text-muted-foreground">
                Soma de todos os orçamentos
              </p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Membros Únicos</CardTitle>
              <Users className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">
                {summary?.totalUniqueAllocatedMembers ?? 0}
              </div>
              <p className="text-xs text-muted-foreground">
                Pessoas alocadas
              </p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Duração Média</CardTitle>
              <Clock className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">
                {summary?.averageClosedProjectDurationInDays
                  ? `${Math.round(summary.averageClosedProjectDurationInDays)} dias`
                  : "-"}
              </div>
              <p className="text-xs text-muted-foreground">
                Projetos encerrados
              </p>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}
