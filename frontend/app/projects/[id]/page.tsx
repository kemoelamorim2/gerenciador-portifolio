"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";
import { ArrowLeft, Calendar, FileText, User, DollarSign, Activity, Users } from "lucide-react";
import { useProjects } from "@/hooks/use-projects";
import { useProjectMembers } from "@/hooks/use-project-members";
import { useMembers } from "@/hooks/use-members";
import type { ProjectResponse, ProjectStatus } from "@/types/project";
import { Button, buttonVariants } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { toast } from "sonner";
import { ProjectMembers } from "@/components/projects/ProjectMembers";

export default function ProjectDetailsPage() {
  const params = useParams();
  const router = useRouter();
  const { getProjectById, deleteProject, updateProjectStatus } = useProjects();
  const { getProjectMembers } = useProjectMembers();
  const { getMembers } = useMembers();

  const [project, setProject] = useState<ProjectResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [statusLoading, setStatusLoading] = useState(false);

  const projectId = Number(params.id);

  useEffect(() => {
    async function fetchProject() {
      try {
        const data = await getProjectById(projectId);
        setProject(data);
      } catch (error) {
        console.error("Failed to load project details", error);
      } finally {
        setLoading(false);
      }
    }
    fetchProject();
  }, [projectId, getProjectById]);

  const handleDelete = async () => {
    if (!project) return;
    
    // Regra: Não pode excluir se INICIADO, EM_ANDAMENTO, ENCERRADO
    const unallowedStatuses = ["INICIADO", "EM_ANDAMENTO", "ENCERRADO"];
    if (unallowedStatuses.includes(project.status)) {
      toast.error(`Projetos com status ${project.status} não podem ser excluídos.`);
      return;
    }

    if (confirm("Tem certeza que deseja excluir este projeto?")) {
      try {
        await deleteProject(projectId);
        toast.success("Projeto excluído com sucesso.");
        router.push("/projects");
      } catch (error) {
        toast.error("Erro ao excluir o projeto.");
      }
    }
  };

  const handleStatusChange = async (newStatus: ProjectStatus) => {
    try {
      setStatusLoading(true);
      const updated = await updateProjectStatus(projectId, { status: newStatus });
      setProject(updated);
      toast.success("Status atualizado com sucesso!");
    } catch (error) {
      toast.error("Erro ao atualizar o status. Verifique o fluxo permitido.");
    } finally {
      setStatusLoading(false);
    }
  };

  if (loading) {
    return <div className="p-8 text-center text-muted-foreground animate-pulse">Carregando detalhes...</div>;
  }

  if (!project) {
    return <div className="p-8 text-center text-destructive">Projeto não encontrado.</div>;
  }

  return (
    <div className="container mx-auto p-4 md:p-8">
      <div className="flex items-center gap-4 mb-8">
        <Button variant="ghost" size="icon" onClick={() => router.push("/projects")}>
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <div className="flex-1">
          <div className="flex items-center gap-3">
            <h1 className="text-3xl font-bold tracking-tight">{project.name}</h1>
            <Badge variant="outline">{project.status}</Badge>
            <Badge variant={project.riskLevel === "ALTO" ? "destructive" : "secondary"}>
              Risco {project.riskLevel}
            </Badge>
          </div>
          <p className="text-muted-foreground">ID do Projeto: {project.id}</p>
        </div>
        <div className="flex gap-2">
          <Link href={`/projects/${project.id}/edit`} className={buttonVariants({ variant: "outline" })}>
            Editar
          </Link>
          <Button variant="destructive" onClick={handleDelete}>Excluir</Button>
        </div>
      </div>

      <div className="grid gap-6 md:grid-cols-3">
        <div className="md:col-span-2 space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg flex items-center gap-2">
                <FileText className="h-5 w-5" />
                Descrição
              </CardTitle>
            </CardHeader>
            <CardContent>
              <p className="whitespace-pre-wrap text-sm text-foreground/80">{project.description}</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="text-lg flex items-center gap-2">
                <Activity className="h-5 w-5" />
                Avançar Status
              </CardTitle>
            </CardHeader>
            <CardContent className="flex flex-wrap gap-2">
              <span className="text-sm text-muted-foreground w-full mb-2">Fluxo: EM_ANALISE &rarr; ANALISE_REALIZADA &rarr; ANALISE_APROVADA &rarr; INICIADO &rarr; PLANEJADO &rarr; EM_ANDAMENTO &rarr; ENCERRADO</span>
              <Button size="sm" variant="secondary" onClick={() => handleStatusChange("EM_ANALISE")} disabled={statusLoading}>Em Análise</Button>
              <Button size="sm" variant="secondary" onClick={() => handleStatusChange("ANALISE_REALIZADA")} disabled={statusLoading}>Análise Realizada</Button>
              <Button size="sm" variant="secondary" onClick={() => handleStatusChange("ANALISE_APROVADA")} disabled={statusLoading}>Análise Aprovada</Button>
              <Button size="sm" variant="secondary" onClick={() => handleStatusChange("INICIADO")} disabled={statusLoading}>Iniciado</Button>
              <Button size="sm" variant="secondary" onClick={() => handleStatusChange("PLANEJADO")} disabled={statusLoading}>Planejado</Button>
              <Button size="sm" variant="secondary" onClick={() => handleStatusChange("EM_ANDAMENTO")} disabled={statusLoading}>Em Andamento</Button>
              <Button size="sm" variant="secondary" onClick={() => handleStatusChange("ENCERRADO")} disabled={statusLoading}>Encerrado</Button>
              <Button size="sm" variant="destructive" onClick={() => handleStatusChange("CANCELADO")} disabled={statusLoading}>Cancelar Projeto</Button>
            </CardContent>
          </Card>
        </div>

        <div className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Informações</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4 text-sm">
              <div className="flex justify-between border-b pb-2">
                <span className="text-muted-foreground flex items-center gap-2"><User className="h-4 w-4"/> Gerente</span>
                <span className="font-medium">{project.managerName}</span>
              </div>
              <div className="flex justify-between border-b pb-2">
                <span className="text-muted-foreground flex items-center gap-2"><DollarSign className="h-4 w-4"/> Orçamento</span>
                <span className="font-medium">
                  {new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(project.budget)}
                </span>
              </div>
              <div className="flex justify-between border-b pb-2">
                <span className="text-muted-foreground flex items-center gap-2"><Calendar className="h-4 w-4"/> Início</span>
                <span className="font-medium">{new Date(project.startDate).toLocaleDateString("pt-BR")}</span>
              </div>
              <div className="flex justify-between border-b pb-2">
                <span className="text-muted-foreground flex items-center gap-2"><Calendar className="h-4 w-4"/> Previsão Término</span>
                <span className="font-medium">{new Date(project.expectedEndDate).toLocaleDateString("pt-BR")}</span>
              </div>
              {project.actualEndDate && (
                <div className="flex justify-between border-b pb-2">
                  <span className="text-muted-foreground flex items-center gap-2"><Calendar className="h-4 w-4"/> Término Real</span>
                  <span className="font-medium">{new Date(project.actualEndDate).toLocaleDateString("pt-BR")}</span>
                </div>
              )}
            </CardContent>
          </Card>
          
          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-lg flex items-center gap-2"><Users className="h-4 w-4"/> Equipe</CardTitle>
            </CardHeader>
            <CardContent>
              <ProjectMembers projectId={project.id} />
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
