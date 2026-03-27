"use client";

import { useEffect, useState } from "react";
import { Briefcase, Info, Users } from "lucide-react";
import { toast } from "sonner";
import { useMembers } from "@/hooks/use-members";
import { getApiErrorMessage } from "@/lib/api/error";
import type { MemberResponse } from "@/types/member";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";

export default function MembersPage() {
  const { getMembers } = useMembers();
  const [members, setMembers] = useState<MemberResponse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    void fetchMembers();
  }, []);

  async function fetchMembers() {
    setLoading(true);
    try {
      setMembers(await getMembers());
    } catch (error) {
      toast.error(getApiErrorMessage(error, "Não foi possível carregar os membros."));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="mx-auto w-full max-w-7xl px-4 py-6 md:px-6 md:py-8">
      <div className="mb-8 flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-3xl font-semibold tracking-tight">Membros</h1>
          <p className="text-muted-foreground">
            Consulte os membros disponibilizados pela API mockada externa para gerência e alocação.
          </p>
        </div>
      </div>

      <div className="grid gap-6">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Info className="h-4 w-4 text-primary" />
              Origem dos membros
            </CardTitle>
            <CardDescription>
              O cadastro de membros não é feito diretamente na interface principal.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="rounded-2xl border border-border/70 bg-muted/35 px-4 py-4 text-sm text-muted-foreground">
              Os dados exibidos abaixo vêm da <span className="font-medium text-foreground">API REST mockada de membros</span>.
              Para este projeto, a interface principal apenas consulta esses registros e os reutiliza nos fluxos de
              gerente responsável e alocação de equipe.
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Users className="h-4 w-4 text-primary" />
              Membros disponíveis
            </CardTitle>
            <CardDescription>
              Esses membros foram obtidos da API mockada externa e podem ser usados como gerente de projeto e em alocações.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="rounded-[24px] border border-border/70 bg-background/65">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>ID</TableHead>
                    <TableHead>Nome</TableHead>
                    <TableHead>Atribuição</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {loading ? (
                    <TableRow>
                      <TableCell className="h-20 text-center text-muted-foreground" colSpan={3}>
                        Carregando membros...
                      </TableCell>
                    </TableRow>
                  ) : members.length > 0 ? (
                    members.map((member) => (
                      <TableRow key={member.id}>
                        <TableCell>{member.id}</TableCell>
                        <TableCell className="font-medium">{member.name}</TableCell>
                        <TableCell>
                          <span className="inline-flex items-center gap-2">
                            <Briefcase className="h-3.5 w-3.5 text-primary" />
                            {member.assignment}
                          </span>
                        </TableCell>
                      </TableRow>
                    ))
                  ) : (
                    <TableRow>
                      <TableCell className="h-20 text-center text-muted-foreground" colSpan={3}>
                        Nenhum membro cadastrado.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
