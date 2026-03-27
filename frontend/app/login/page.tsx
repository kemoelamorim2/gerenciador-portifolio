"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { FolderKanban, LockKeyhole, ShieldCheck } from "lucide-react";
import { useAuth } from "@/components/auth/AuthProvider";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { toast } from "sonner";

export default function LoginPage() {
  const router = useRouter();
  const { login } = useAuth();
  const [username, setUsername] = useState("admin");
  const [password, setPassword] = useState("admin123");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);

    try {
      await login({ username, password });
      toast.success("Acesso liberado com sucesso.");
      router.replace("/");
    } catch {
      toast.error("Não foi possível autenticar. Verifique usuário, senha e backend.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="relative overflow-hidden">
      <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_top_left,rgba(194,120,72,0.18),transparent_28%),radial-gradient(circle_at_bottom_right,rgba(42,96,122,0.16),transparent_30%)]" />
      <div className="container relative mx-auto flex min-h-screen items-center px-4 py-10 md:px-8">
        <div className="grid w-full gap-8 lg:grid-cols-[1.1fr_0.9fr]">
          <section className="flex flex-col justify-center">
            <div className="mb-6 inline-flex w-fit items-center gap-3 rounded-full border border-border/70 bg-background/80 px-4 py-2 text-sm text-muted-foreground shadow-sm backdrop-blur">
              <FolderKanban className="h-4 w-4 text-primary" />
              Plataforma de gestão de projetos
            </div>

            <h1 className="max-w-2xl text-4xl font-semibold tracking-tight text-foreground md:text-6xl">
              Controle o portfólio com clareza, ritmo e visão executiva.
            </h1>
            <p className="mt-5 max-w-xl text-base leading-7 text-muted-foreground md:text-lg">
              Acesse o painel para acompanhar projetos, equipes, risco, orçamento e o resumo do portfólio em uma única interface.
            </p>

            <div className="mt-8 grid gap-4 sm:grid-cols-2">
              <div className="rounded-2xl border border-border/70 bg-card/70 p-5 shadow-sm backdrop-blur">
                <ShieldCheck className="mb-3 h-5 w-5 text-primary" />
                <p className="font-medium">Acesso simples</p>
                <p className="mt-1 text-sm text-muted-foreground">
                  Autenticação básica para desenvolvimento e apresentação técnica.
                </p>
              </div>
              <div className="rounded-2xl border border-border/70 bg-card/70 p-5 shadow-sm backdrop-blur">
                <LockKeyhole className="mb-3 h-5 w-5 text-primary" />
                <p className="font-medium">Integração imediata</p>
                <p className="mt-1 text-sm text-muted-foreground">
                  O frontend já consome os endpoints reais do backend.
                </p>
              </div>
            </div>
          </section>

          <section className="flex items-center justify-center">
            <Card className="w-full max-w-md border-border/80 bg-background/85 shadow-2xl backdrop-blur">
              <CardHeader className="space-y-2">
                <CardTitle className="text-2xl">Entrar</CardTitle>
                <CardDescription>
                  Use as credenciais do ambiente local para acessar o painel.
                </CardDescription>
              </CardHeader>
              <CardContent>
                <form className="space-y-5" onSubmit={handleSubmit}>
                  <div className="space-y-2">
                    <Label htmlFor="username">Usuário</Label>
                    <Input
                      id="username"
                      name="username"
                      autoComplete="username"
                      value={username}
                      onChange={(event) => setUsername(event.target.value)}
                      required
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="password">Senha</Label>
                    <Input
                      id="password"
                      name="password"
                      type="password"
                      autoComplete="current-password"
                      value={password}
                      onChange={(event) => setPassword(event.target.value)}
                      required
                    />
                  </div>

                  <div className="rounded-xl border border-border/70 bg-muted/50 px-4 py-3 text-sm text-muted-foreground">
                    Credenciais locais padrão: <strong className="text-foreground">admin / admin123</strong>
                  </div>

                  <Button className="w-full" type="submit" disabled={loading}>
                    {loading ? "Validando acesso..." : "Acessar painel"}
                  </Button>
                </form>
              </CardContent>
            </Card>
          </section>
        </div>
      </div>
    </div>
  );
}
