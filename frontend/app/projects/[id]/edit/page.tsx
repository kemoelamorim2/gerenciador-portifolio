"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { useProjects } from "@/hooks/use-projects";
import { ProjectForm } from "@/components/projects/ProjectForm";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import type { ProjectResponse } from "@/types/project";
import { ArrowLeft } from "lucide-react";

export default function EditProjectPage() {
  const params = useParams();
  const router = useRouter();
  const { getProjectById } = useProjects();
  const [project, setProject] = useState<ProjectResponse | null>(null);
  const [loading, setLoading] = useState(true);

  const projectId = Number(params.id);

  useEffect(() => {
    async function fetchProject() {
      try {
        const data = await getProjectById(projectId);
        setProject(data);
      } catch (error) {
        console.error("Failed to load project", error);
      } finally {
        setLoading(false);
      }
    }
    fetchProject();
  }, [projectId, getProjectById]);

  if (loading) {
    return <div className="p-8 text-center text-muted-foreground animate-pulse">Carregando projeto...</div>;
  }

  if (!project) {
    return <div className="p-8 text-center text-destructive">Projeto não encontrado.</div>;
  }

  return (
    <div className="container mx-auto p-4 md:p-8 max-w-4xl">
      <div className="flex items-center gap-4 mb-8">
        <Button variant="ghost" size="icon" onClick={() => router.back()}>
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Editar Projeto</h1>
          <p className="text-muted-foreground">{project.name}</p>
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Detalhes do Projeto</CardTitle>
          <CardDescription>
            Atualize os dados, cronograma ou status do projeto.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <ProjectForm initialData={project} isEdit={true} />
        </CardContent>
      </Card>
    </div>
  );
}
