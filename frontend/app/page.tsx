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
    <div className="mx-auto w-full max-w-7xl px-4 py-6 md:px-6 md:py-8">
      <section className="relative overflow-hidden rounded-[36px] border border-border/70 bg-card/78 px-6 py-8 shadow-[0_30px_80px_rgba(15,23,42,0.08)] backdrop-blur md:px-8">
        <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_top_left,rgba(80,114,255,0.16),transparent_32%),radial-gradient(circle_at_bottom_right,rgba(127,153,255,0.12),transparent_28%)] dark:bg-[radial-gradient(circle_at_top_left,rgba(80,114,255,0.22),transparent_32%),radial-gradient(circle_at_bottom_right,rgba(127,153,255,0.16),transparent_28%)]" />
        <div className="relative flex flex-col gap-6 lg:flex-row lg:items-end lg:justify-between">
          <div className="max-w-2xl">
            <span className="inline-flex rounded-full border border-primary/15 bg-primary/10 px-3 py-1 text-xs font-semibold uppercase tracking-[0.24em] text-primary">
              Visão executiva
            </span>
            <h1 className="mt-4 text-3xl font-semibold tracking-tight md:text-5xl">
              Portfólio com leitura clara, metas visíveis e operação em ritmo constante.
            </h1>
            <p className="mt-4 max-w-xl text-sm leading-7 text-muted-foreground md:text-base">
              Acompanhe projetos, orçamento, equipe e duração média em uma interface mais limpa, fluida e pronta para apresentação.
            </p>
          </div>

          <div className="grid gap-3 sm:grid-cols-2">
            <div className="rounded-3xl border border-border/70 bg-background/80 px-5 py-4 shadow-sm">
              <p className="text-xs font-medium uppercase tracking-[0.22em] text-muted-foreground">
                Projetos
              </p>
              <strong className="mt-3 block text-3xl font-semibold tracking-tight">{totalProjects}</strong>
            </div>
            <div className="rounded-3xl border border-border/70 bg-background/80 px-5 py-4 shadow-sm">
              <p className="text-xs font-medium uppercase tracking-[0.22em] text-muted-foreground">
                Membros únicos
              </p>
              <strong className="mt-3 block text-3xl font-semibold tracking-tight">
                {summary?.totalUniqueAllocatedMembers ?? 0}
              </strong>
            </div>
          </div>
        </div>
      </section>

      {loading ? (
        <div className="mt-8 grid animate-pulse gap-4 md:grid-cols-2 lg:grid-cols-4">
          {[1, 2, 3, 4].map((i) => (
            <Card key={i}>
              <CardHeader className="h-[88px] rounded-t-[28px] bg-muted" />
              <CardContent className="h-[48px] rounded-b-[28px] bg-muted/50" />
            </Card>
          ))}
        </div>
      ) : (
        <div className="mt-8 grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">
                Total de Projetos
              </CardTitle>
              <span className="flex h-10 w-10 items-center justify-center rounded-2xl bg-primary/10 text-primary">
                <FolderKanban className="h-4 w-4" />
              </span>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-semibold tracking-tight">{totalProjects}</div>
              <p className="text-xs text-muted-foreground">
                Projetos cadastrados
              </p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Orçamento Total</CardTitle>
              <span className="flex h-10 w-10 items-center justify-center rounded-2xl bg-primary/10 text-primary">
                <DollarSign className="h-4 w-4" />
              </span>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-semibold tracking-tight">
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
              <span className="flex h-10 w-10 items-center justify-center rounded-2xl bg-primary/10 text-primary">
                <Users className="h-4 w-4" />
              </span>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-semibold tracking-tight">
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
              <span className="flex h-10 w-10 items-center justify-center rounded-2xl bg-primary/10 text-primary">
                <Clock className="h-4 w-4" />
              </span>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-semibold tracking-tight">
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
