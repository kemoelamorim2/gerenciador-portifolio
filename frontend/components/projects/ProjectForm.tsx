"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useProjects } from "@/hooks/use-projects";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { toast } from "sonner";
import type { ProjectResponse, ProjectCreateRequest, ProjectUpdateRequest } from "@/types/project";

interface ProjectFormProps {
  initialData?: ProjectResponse;
  isEdit?: boolean;
}

export function ProjectForm({ initialData, isEdit = false }: ProjectFormProps) {
  const router = useRouter();
  const { createProject, updateProject } = useProjects();
  const [loading, setLoading] = useState(false);

  const [formData, setFormData] = useState({
    name: initialData?.name || "",
    startDate: initialData?.startDate || "",
    expectedEndDate: initialData?.expectedEndDate || "",
    actualEndDate: initialData?.actualEndDate || "",
    budget: initialData?.budget || 0,
    description: initialData?.description || "",
    managerId: initialData?.managerId || 1, // Default info
    status: initialData?.status || "EM_ANALISE",
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === "budget" || name === "managerId" ? Number(value) : value,
    }));
  };

  const handleSelectChange = (value: string | null) => {
    if (!value) {
      return;
    }

    setFormData((prev) => ({ ...prev, status: value as ProjectUpdateRequest["status"] }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      if (isEdit && initialData) {
        const payload: ProjectUpdateRequest = {
          name: formData.name,
          startDate: formData.startDate,
          expectedEndDate: formData.expectedEndDate,
          actualEndDate: formData.actualEndDate || null,
          budget: formData.budget,
          description: formData.description,
          managerId: formData.managerId,
          status: formData.status as any,
        };
        await updateProject(initialData.id, payload);
        toast.success("Projeto atualizado com sucesso!");
        router.push(`/projects/${initialData.id}`);
      } else {
        const payload: ProjectCreateRequest = {
          name: formData.name,
          startDate: formData.startDate,
          expectedEndDate: formData.expectedEndDate,
          budget: formData.budget,
          description: formData.description,
          managerId: formData.managerId,
        };
        const created = await createProject(payload);
        toast.success("Projeto criado com sucesso!");
        router.push(`/projects/${created.id}`);
      }
      router.refresh();
    } catch (error) {
      console.error(error);
      toast.error("Erro ao salvar o projeto. Verifique os dados e tente novamente.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
        <div className="space-y-2">
          <Label htmlFor="name">Nome do Projeto</Label>
          <Input
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            placeholder="Ex: Novo ERP"
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="managerId">ID do Gerente (Mock)</Label>
          <Input
            id="managerId"
            name="managerId"
            type="number"
            value={formData.managerId}
            onChange={handleChange}
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="startDate">Data de Início</Label>
          <Input
            id="startDate"
            name="startDate"
            type="date"
            value={formData.startDate}
            onChange={handleChange}
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="expectedEndDate">Previsão de Término</Label>
          <Input
            id="expectedEndDate"
            name="expectedEndDate"
            type="date"
            value={formData.expectedEndDate}
            onChange={handleChange}
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="budget">Orçamento Total (R$)</Label>
          <Input
            id="budget"
            name="budget"
            type="number"
            min="0"
            step="0.01"
            value={formData.budget}
            onChange={handleChange}
            required
          />
        </div>

        {isEdit && (
          <>
            <div className="space-y-2">
              <Label htmlFor="status">Status</Label>
              <Select value={formData.status} onValueChange={handleSelectChange}>
                <SelectTrigger>
                  <SelectValue placeholder="Selecione o status" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="EM_ANALISE">Em Análise</SelectItem>
                  <SelectItem value="ANALISE_REALIZADA">Análise Realizada</SelectItem>
                  <SelectItem value="ANALISE_APROVADA">Análise Aprovada</SelectItem>
                  <SelectItem value="INICIADO">Iniciado</SelectItem>
                  <SelectItem value="PLANEJADO">Planejado</SelectItem>
                  <SelectItem value="EM_ANDAMENTO">Em Andamento</SelectItem>
                  <SelectItem value="ENCERRADO">Encerrado</SelectItem>
                  <SelectItem value="CANCELADO">Cancelado</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <Label htmlFor="actualEndDate">Data Real de Término</Label>
              <Input
                id="actualEndDate"
                name="actualEndDate"
                type="date"
                value={formData.actualEndDate}
                onChange={handleChange}
              />
            </div>
          </>
        )}

        <div className="space-y-2 md:col-span-2">
          <Label htmlFor="description">Descrição</Label>
          <textarea
            id="description"
            name="description"
            className="flex min-h-[80px] w-full rounded-md border border-input bg-transparent px-3 py-2 text-sm shadow-sm placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50"
            value={formData.description}
            onChange={handleChange}
            required
            rows={4}
          />
        </div>
      </div>

      <div className="flex justify-end space-x-4">
        <Button variant="outline" type="button" onClick={() => router.back()} disabled={loading}>
          Cancelar
        </Button>
        <Button type="submit" disabled={loading}>
          {loading ? "Salvando..." : "Salvar Projeto"}
        </Button>
      </div>
    </form>
  );
}
