"use client";

import { ProjectForm } from "@/components/projects/ProjectForm";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";

export default function NewProjectPage() {
  return (
    <div className="container mx-auto p-4 md:p-8 max-w-4xl">
      <div className="mb-8">
        <h1 className="text-3xl font-bold tracking-tight">Novo Projeto</h1>
        <p className="text-muted-foreground">Preencha os dados iniciais do projeto.</p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Detalhes do Projeto</CardTitle>
          <CardDescription>
            Defina o gerente, orçamento e cronograma previsto. A classificação de risco será calculada automaticamente.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <ProjectForm isEdit={false} />
        </CardContent>
      </Card>
    </div>
  );
}
