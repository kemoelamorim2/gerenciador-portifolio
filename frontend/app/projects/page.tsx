"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { ChevronLeft, ChevronRight, Filter, Plus } from "lucide-react";
import { toast } from "sonner";
import { getApiErrorMessage } from "@/lib/api/error";
import { getProjectStatusLabel, getRiskLevelLabel } from "@/lib/presentation/project";
import { useProjects } from "@/hooks/use-projects";
import type { ProjectFilterRequest, ProjectResponse, ProjectStatus, RiskLevel } from "@/types/project";
import type { PagedResponse } from "@/types/api";
import { Button, buttonVariants } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";

const riskColors: Record<string, string> = {
  BAIXO: "bg-green-100 text-green-800 hover:bg-green-100",
  MEDIO: "bg-yellow-100 text-yellow-800 hover:bg-yellow-100",
  ALTO: "bg-red-100 text-red-800 hover:bg-red-100",
};

const statusOptions: Array<{ value: ProjectStatus; label: string }> = [
  { value: "EM_ANALISE", label: "Em análise" },
  { value: "ANALISE_REALIZADA", label: "Análise realizada" },
  { value: "ANALISE_APROVADA", label: "Análise aprovada" },
  { value: "INICIADO", label: "Iniciado" },
  { value: "PLANEJADO", label: "Planejado" },
  { value: "EM_ANDAMENTO", label: "Em andamento" },
  { value: "ENCERRADO", label: "Encerrado" },
  { value: "CANCELADO", label: "Cancelado" },
];

const riskOptions: Array<{ value: RiskLevel; label: string }> = [
  { value: "BAIXO", label: "Baixo" },
  { value: "MEDIO", label: "Médio" },
  { value: "ALTO", label: "Alto" },
];

export default function ProjectsPage() {
  const { getProjects } = useProjects();
  const [data, setData] = useState<PagedResponse<ProjectResponse> | null>(null);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState<ProjectFilterRequest>({
    page: 0,
    size: 10,
    name: "",
  });

  useEffect(() => {
    async function fetchProjects() {
      setLoading(true);
      try {
        const result = await getProjects(filters);
        setData(result);
      } catch (error) {
        toast.error(getApiErrorMessage(error, "Não foi possível carregar os projetos."));
      } finally {
        setLoading(false);
      }
    }

    void fetchProjects();
  }, [filters, getProjects]);

  function handleFilterChange<K extends keyof ProjectFilterRequest>(
    key: K,
    value: ProjectFilterRequest[K],
  ) {
    setFilters((prev) => ({
      ...prev,
      [key]: value,
      page: 0,
    }));
  }

  function clearFilters() {
    setFilters({ page: 0, size: 10, name: "" });
  }

  return (
    <div className="mx-auto w-full max-w-7xl px-4 py-6 md:px-6 md:py-8">
      <div className="mb-8 flex flex-col items-start justify-between gap-4 md:flex-row md:items-center">
        <div>
          <h1 className="text-3xl font-semibold tracking-tight">Projetos</h1>
          <p className="text-muted-foreground">Gerencie o portfólio de projetos</p>
        </div>
        <Link href="/projects/new" className={buttonVariants({ variant: "default" })}>
          <Plus className="mr-2 h-4 w-4" />
          Novo Projeto
        </Link>
      </div>

      <Card className="mb-6">
        <CardContent className="pt-5">
          <div className="mb-4 flex items-center gap-2 text-sm font-medium text-muted-foreground">
            <Filter className="h-4 w-4" />
            Filtros
          </div>
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-5">
            <div className="space-y-2 xl:col-span-2">
              <Label htmlFor="name">Nome</Label>
              <Input
                id="name"
                value={filters.name ?? ""}
                onChange={(event) => handleFilterChange("name", event.target.value)}
                placeholder="Buscar por nome"
              />
            </div>

            <div className="space-y-2">
              <Label>Status</Label>
              <Select
                value={filters.status ?? undefined}
                onValueChange={(value) => handleFilterChange("status", (value || undefined) as ProjectStatus | undefined)}
              >
                <SelectTrigger className="h-10 w-full">
                  <SelectValue placeholder="Todos" />
                </SelectTrigger>
                <SelectContent>
                  {statusOptions.map((option) => (
                    <SelectItem key={option.value} value={option.value}>
                      {option.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <Label>Risco</Label>
              <Select
                value={filters.riskLevel ?? undefined}
                onValueChange={(value) => handleFilterChange("riskLevel", (value || undefined) as RiskLevel | undefined)}
              >
                <SelectTrigger className="h-10 w-full">
                  <SelectValue placeholder="Todos" />
                </SelectTrigger>
                <SelectContent>
                  {riskOptions.map((option) => (
                    <SelectItem key={option.value} value={option.value}>
                      {option.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <Label htmlFor="size">Itens por página</Label>
              <Select
                value={String(filters.size ?? 10)}
                onValueChange={(value) => handleFilterChange("size", Number(value))}
              >
                <SelectTrigger className="h-10 w-full">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="5">5</SelectItem>
                  <SelectItem value="10">10</SelectItem>
                  <SelectItem value="20">20</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          <div className="mt-4 flex justify-end">
            <Button variant="outline" onClick={clearFilters}>
              Limpar filtros
            </Button>
          </div>
        </CardContent>
      </Card>

      <div className="rounded-[28px] border border-border/70 bg-card/75 p-2">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Nome</TableHead>
              <TableHead>Gerente</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Risco</TableHead>
              <TableHead className="text-right">Ações</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell colSpan={5} className="h-24 text-center">
                  Carregando projetos...
                </TableCell>
              </TableRow>
            ) : data?.content && data.content.length > 0 ? (
              data.content.map((project) => (
                <TableRow key={project.id}>
                  <TableCell className="font-medium">{project.name}</TableCell>
                  <TableCell>{project.managerName}</TableCell>
                    <TableCell>{getProjectStatusLabel(project.status)}</TableCell>
                    <TableCell>
                      <Badge className={riskColors[project.riskLevel] || ""} variant="outline">
                        {getRiskLevelLabel(project.riskLevel)}
                      </Badge>
                    </TableCell>
                  <TableCell className="text-right">
                    <Link
                      href={`/projects/${project.id}`}
                      className={buttonVariants({ variant: "outline", size: "sm" })}
                    >
                      Detalhes
                    </Link>
                  </TableCell>
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={5} className="h-24 text-center text-muted-foreground">
                  Nenhum projeto encontrado.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>

      <div className="mt-5 flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
        <p className="text-sm text-muted-foreground">
          {data
            ? `Página ${data.page + 1} de ${Math.max(data.totalPages, 1)} • ${data.totalElements} registro(s)`
            : "Sem dados carregados"}
        </p>
        <div className="flex items-center gap-2">
          <Button
            variant="outline"
            onClick={() => setFilters((prev) => ({ ...prev, page: Math.max((prev.page ?? 0) - 1, 0) }))}
            disabled={!data || data.first}
          >
            <ChevronLeft className="mr-1 h-4 w-4" />
            Anterior
          </Button>
          <Button
            onClick={() => setFilters((prev) => ({ ...prev, page: (prev.page ?? 0) + 1 }))}
            disabled={!data || data.last}
          >
            Próxima
            <ChevronRight className="ml-1 h-4 w-4" />
          </Button>
        </div>
      </div>
    </div>
  );
}
