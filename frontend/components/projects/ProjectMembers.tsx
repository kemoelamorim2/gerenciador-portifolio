"use client";

import { useEffect, useState } from "react";
import { Plus, Trash2 } from "lucide-react";
import { useProjectMembers } from "@/hooks/use-project-members";
import { useMembers } from "@/hooks/use-members";
import { getApiErrorMessage } from "@/lib/api/error";
import type { ProjectMemberAllocationResponse } from "@/types/allocation";
import type { MemberResponse } from "@/types/member";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { toast } from "sonner";

export function ProjectMembers({ projectId }: { projectId: number }) {
  const { getProjectMembers, allocateProjectMember, removeProjectMember } = useProjectMembers();
  const { getMembers } = useMembers();

  const [members, setMembers] = useState<ProjectMemberAllocationResponse[]>([]);
  const [availableMembers, setAvailableMembers] = useState<MemberResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [selectedMemberId, setSelectedMemberId] = useState<string>("");
  const [allocating, setAllocating] = useState(false);

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [projectId]);

  async function fetchData() {
    setLoading(true);
    try {
      const allocated = await getProjectMembers(projectId);
      setMembers(allocated);

      // In real scenario, API might filter this or mock data comes paginated.
      // Assuming getMembers() returns the mock array or uses a wrapper.
      // Wait, getMembers takes params? Let's check. Default might just return MemberResponse[]
      const allMembers = await getMembers();
      
      // Filter out already allocated members
      const allocatedIds = allocated.map((m) => m.memberId);
      const funcMembers = (allMembers as any).content || allMembers; // If paginated
      const allocatable = (Array.isArray(funcMembers) ? funcMembers : []).filter(
        (m: MemberResponse) =>
          !allocatedIds.includes(m.id)
          && m.assignment.normalize("NFD").replace(/\p{Diacritic}/gu, "").toLowerCase() === "funcionario"
      );
      setAvailableMembers(allocatable);
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Não foi possível carregar a equipe do projeto."));
    } finally {
      setLoading(false);
    }
  }

  const handleAllocate = async () => {
    if (!selectedMemberId) return;
    setAllocating(true);
    try {
      await allocateProjectMember(projectId, { memberId: Number(selectedMemberId) });
      toast.success("Membro associado ao projeto com sucesso!");
      setDialogOpen(false);
      setSelectedMemberId("");
      fetchData();
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Erro ao associar membro. Verifique as regras de negócio."));
    } finally {
      setAllocating(false);
    }
  };

  const handleRemove = async (memberId: number) => {
    if (confirm("Tem certeza que deseja remover este membro do projeto?")) {
      try {
        await removeProjectMember(projectId, memberId);
        toast.success("Membro removido com sucesso.");
        fetchData();
      } catch (error) {
        toast.error(getApiErrorMessage(error, "Erro ao remover membro."));
      }
    }
  };

  const handleMemberSelect = (value: string | null) => {
    setSelectedMemberId(value ?? "");
  };

  if (loading) {
    return <div className="text-sm text-muted-foreground animate-pulse pb-4">Carregando equipe...</div>;
  }

  return (
    <div className="space-y-4">
      {members.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-border/80 bg-muted/30 px-4 py-5 text-center text-sm text-muted-foreground">
          Nenhum membro associado ao projeto ainda.
        </div>
      ) : (
        <ul className="space-y-2">
          {members.map((member) => (
            <li key={member.allocationId} className="flex items-center justify-between p-2 border rounded-md text-sm">
              <div>
                <span className="font-medium">{member.memberName}</span>
                <span className="text-muted-foreground block text-xs">{member.memberAssignment}</span>
              </div>
              <Button variant="ghost" size="icon" onClick={() => handleRemove(member.memberId)}>
                <Trash2 className="h-4 w-4 text-destructive" />
              </Button>
            </li>
          ))}
        </ul>
      )}

      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <Button variant="outline" size="sm" className="mt-2 w-full" onClick={() => setDialogOpen(true)}>
          <Plus className="mr-2 h-4 w-4" />
          Adicionar Membro
        </Button>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Vincular Membro</DialogTitle>
            <DialogDescription>
              Selecione um funcionário para associar a este projeto.
            </DialogDescription>
          </DialogHeader>
          <div className="py-4 space-y-4">
            <Select value={selectedMemberId} onValueChange={handleMemberSelect}>
              <SelectTrigger className="w-full">
                <SelectValue placeholder="Selecione um membro" />
              </SelectTrigger>
              <SelectContent>
                {availableMembers.length === 0 ? (
                  <SelectItem value="empty" disabled>Nenhum membro elegível encontrado.</SelectItem>
                ) : (
                  availableMembers.map((m) => (
                    <SelectItem key={m.id} value={m.id.toString()}>
                      {m.name}
                    </SelectItem>
                  ))
                )}
              </SelectContent>
            </Select>
            <Button onClick={handleAllocate} disabled={!selectedMemberId || allocating || selectedMemberId === "empty"} className="w-full">
              {allocating ? "Salvando..." : "Confirmar Associação"}
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
