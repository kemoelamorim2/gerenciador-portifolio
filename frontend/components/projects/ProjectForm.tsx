"use client";

import { useEffect, useState } from "react";
import type { AxiosError } from "axios";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useMembers } from "@/hooks/use-members";
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
import type { ApiErrorResponse } from "@/types/api";
import type { MemberResponse } from "@/types/member";
import type { ProjectResponse, ProjectCreateRequest, ProjectUpdateRequest } from "@/types/project";

interface ProjectFormProps {
  initialData?: ProjectResponse;
  isEdit?: boolean;
}

export function ProjectForm({ initialData, isEdit = false }: ProjectFormProps) {
  const router = useRouter();
  const { getMembers } = useMembers();
  const { createProject, updateProject } = useProjects();
  const [loading, setLoading] = useState(false);
  const [members, setMembers] = useState<MemberResponse[]>([]);
  const [membersLoading, setMembersLoading] = useState(true);

  const formatCurrencyInput = (value: string) => {
    const digits = value.replace(/\D/g, "");
    const normalized = digits === "" ? "0" : digits;
    const amount = Number(normalized) / 100;

    return new Intl.NumberFormat("pt-BR", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(amount);
  };

  const parseCurrencyToNumber = (value: string) => {
    const digits = value.replace(/\D/g, "");
    if (!digits) {
      return 0;
    }

    return Number(digits) / 100;
  };

  const [formData, setFormData] = useState({
    name: initialData?.name || "",
    startDate: initialData?.startDate || "",
    expectedEndDate: initialData?.expectedEndDate || "",
    actualEndDate: initialData?.actualEndDate || "",
    budget:
      initialData?.budget != null
        ? formatCurrencyInput(String(Math.round(Number(initialData.budget) * 100)))
        : "0,00",
    description: initialData?.description || "",
    managerId: initialData?.managerId ?? null,
    status: initialData?.status || "EM_ANALISE",
  });

  useEffect(() => {
    async function fetchMembers() {
      try {
        const data = await getMembers();
        setMembers(data);

        if (!initialData?.managerId && data.length > 0) {
          setFormData((prev) => ({
            ...prev,
            managerId: prev.managerId ?? data[0].id,
          }));
        }
      } catch (error) {
        console.error("Erro ao carregar membros", error);
        toast.error("Não foi possível carregar os membros disponíveis.");
      } finally {
        setMembersLoading(false);
      }
    }

    fetchMembers();
  }, [getMembers, initialData?.managerId]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleBudgetChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const formattedValue = formatCurrencyInput(e.target.value);
    setFormData((prev) => ({
      ...prev,
      budget: formattedValue,
    }));
  };

  const handleSelectChange = (value: string | null) => {
    if (!value) {
      return;
    }

    setFormData((prev) => ({ ...prev, status: value as ProjectUpdateRequest["status"] }));
  };

  const handleManagerChange = (value: string | null) => {
    if (!value) {
      return;
    }

    setFormData((prev) => ({ ...prev, managerId: Number(value) }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.managerId) {
      toast.error("Selecione um gerente responsável antes de salvar o projeto.");
      return;
    }

    setLoading(true);

    try {
      if (isEdit && initialData) {
        const payload: ProjectUpdateRequest = {
          name: formData.name,
          startDate: formData.startDate,
          expectedEndDate: formData.expectedEndDate,
          actualEndDate: formData.actualEndDate || null,
          budget: parseCurrencyToNumber(formData.budget),
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
          actualEndDate: formData.actualEndDate || null,
          budget: parseCurrencyToNumber(formData.budget),
          description: formData.description,
          managerId: formData.managerId,
          status: formData.status as any,
        };
        const created = await createProject(payload);
        toast.success("Projeto criado com sucesso!");
        router.push(`/projects/${created.id}`);
      }
      router.refresh();
    } catch (error) {
      console.error(error);
      const apiError = error as AxiosError<ApiErrorResponse>;
      const message = apiError.response?.data?.message;

      toast.error(message || "Erro ao salvar o projeto. Verifique os dados e tente novamente.");
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
          <Label htmlFor="managerId">Gerente Responsável</Label>
          <Select
            value={formData.managerId ? String(formData.managerId) : ""}
            onValueChange={handleManagerChange}
          >
            <SelectTrigger className="h-11 w-full rounded-2xl border-border/80 bg-background/85 px-4">
              <SelectValue
                placeholder={
                  membersLoading
                    ? "Carregando membros..."
                    : members.length > 0
                      ? "Selecione um gerente"
                      : "Nenhum membro disponível"
                }
              />
            </SelectTrigger>
            <SelectContent>
              {members.map((member) => (
                <SelectItem key={member.id} value={String(member.id)}>
                  {member.name} - {member.assignment}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          {!membersLoading && members.length === 0 && (
            <p className="text-sm text-muted-foreground">
              Cadastre pelo menos um membro antes de criar um projeto.{" "}
              <Link className="font-medium text-primary hover:underline" href="/members">
                Ir para membros
              </Link>
            </p>
          )}
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
            type="text"
            inputMode="numeric"
            value={formData.budget}
            onChange={handleBudgetChange}
            placeholder="0,00"
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
        <Button type="submit" disabled={loading || membersLoading || members.length === 0}>
          {loading ? "Salvando..." : "Salvar Projeto"}
        </Button>
      </div>
    </form>
  );
}
