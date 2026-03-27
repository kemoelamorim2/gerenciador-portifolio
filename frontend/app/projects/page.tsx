"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Plus } from "lucide-react";
import { useProjects } from "@/hooks/use-projects";
import type { ProjectResponse } from "@/types/project";
import type { PagedResponse } from "@/types/api";
import { Button, buttonVariants } from "@/components/ui/button";
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

export default function ProjectsPage() {
  const { getProjects } = useProjects();
  const [data, setData] = useState<PagedResponse<ProjectResponse> | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchProjects() {
      try {
        const result = await getProjects({ page: 0, size: 20 });
        setData(result);
      } catch (error) {
        console.error("Failed to load projects", error);
      } finally {
        setLoading(false);
      }
    }
    fetchProjects();
  }, [getProjects]);

  return (
    <div className="container mx-auto p-4 md:p-8">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Projetos</h1>
          <p className="text-muted-foreground">Gerencie o portfólio de projetos</p>
        </div>
        <Link href="/projects/new" className={buttonVariants({ variant: "default" })}>
          <Plus className="mr-2 h-4 w-4" />
          Novo Projeto
        </Link>
      </div>

      <div className="rounded-md border bg-card">
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
                <TableCell colSpan={5} className="text-center h-24">
                  Carregando projetos...
                </TableCell>
              </TableRow>
            ) : data?.content && data.content.length > 0 ? (
              data.content.map((project) => (
                <TableRow key={project.id}>
                  <TableCell className="font-medium">{project.name}</TableCell>
                  <TableCell>{project.managerName}</TableCell>
                  <TableCell>{project.status}</TableCell>
                  <TableCell>
                    <Badge className={riskColors[project.riskLevel] || ""} variant="outline">
                      {project.riskLevel}
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
                <TableCell colSpan={5} className="text-center h-24 text-muted-foreground">
                  Nenhum projeto encontrado.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
    </div>
  );
}
