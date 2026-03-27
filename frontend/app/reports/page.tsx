"use client";

export const dynamic = "force-dynamic";

import { useEffect, useState } from "react";
import { BarChart3, Clock3, Users2, Wallet } from "lucide-react";
import { toast } from "sonner";
import { usePortfolioReport } from "@/hooks/use-portfolio-report";
import { getApiErrorMessage } from "@/lib/api/error";
import { getProjectStatusLabel } from "@/lib/presentation/project";
import type { PortfolioReportResponse } from "@/types/report";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

const currencyFormatter = new Intl.NumberFormat("pt-BR", {
  style: "currency",
  currency: "BRL",
});

export default function ReportsPage() {
  const { getPortfolioSummary } = usePortfolioReport();
  const [report, setReport] = useState<PortfolioReportResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchReport() {
      try {
        setReport(await getPortfolioSummary());
      } catch (error) {
        toast.error(getApiErrorMessage(error, "Não foi possível carregar o relatório."));
      } finally {
        setLoading(false);
      }
    }

    void fetchReport();
  }, [getPortfolioSummary]);

  return (
    <div className="mx-auto w-full max-w-7xl px-4 py-6 md:px-6 md:py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-semibold tracking-tight">Relatórios</h1>
        <p className="text-muted-foreground">
          Leitura resumida do portfólio por status, orçamento, duração e pessoas alocadas.
        </p>
      </div>

      {loading ? (
        <div className="grid gap-4 md:grid-cols-3">
          {[1, 2, 3].map((item) => (
            <Card key={item}>
              <CardHeader className="h-24 animate-pulse bg-muted/50" />
              <CardContent className="h-24 animate-pulse bg-muted/30" />
            </Card>
          ))}
        </div>
      ) : report ? (
        <>
          <div className="grid gap-4 md:grid-cols-3">
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm">Membros únicos</CardTitle>
                <Users2 className="h-4 w-4 text-primary" />
              </CardHeader>
              <CardContent>
                <div className="text-3xl font-semibold tracking-tight">
                  {report.totalUniqueAllocatedMembers}
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm">Duração média</CardTitle>
                <Clock3 className="h-4 w-4 text-primary" />
              </CardHeader>
              <CardContent>
                <div className="text-3xl font-semibold tracking-tight">
                  {report.averageClosedProjectDurationInDays
                    ? `${Math.round(report.averageClosedProjectDurationInDays)} dias`
                    : "-"}
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm">Status monitorados</CardTitle>
                <BarChart3 className="h-4 w-4 text-primary" />
              </CardHeader>
              <CardContent>
                <div className="text-3xl font-semibold tracking-tight">
                  {report.statusSummary.length}
                </div>
              </CardContent>
            </Card>
          </div>

          <div className="mt-6 grid gap-4 lg:grid-cols-2">
            {report.statusSummary.map((item) => (
              <Card key={item.status}>
                <CardHeader className="flex flex-row items-center justify-between">
                  <div>
                    <CardTitle className="text-base">{getProjectStatusLabel(item.status)}</CardTitle>
                    <p className="mt-1 text-sm text-muted-foreground">
                      {item.projectCount} projeto(s)
                    </p>
                  </div>
                  <Badge variant="secondary">{item.projectCount}</Badge>
                </CardHeader>
                <CardContent className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">Total orçado</span>
                  <span className="inline-flex items-center gap-2 text-sm font-semibold">
                    <Wallet className="h-4 w-4 text-primary" />
                    {currencyFormatter.format(item.totalBudget)}
                  </span>
                </CardContent>
              </Card>
            ))}
          </div>
        </>
      ) : (
        <Card>
          <CardContent className="py-12 text-center text-muted-foreground">
            Nenhum dado de relatório disponível no momento.
          </CardContent>
        </Card>
      )}
    </div>
  );
}
