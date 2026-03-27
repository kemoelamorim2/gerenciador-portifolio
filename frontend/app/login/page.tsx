"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { FolderKanban } from "lucide-react";
import { toast } from "sonner";
import { useAuth } from "@/components/auth/AuthProvider";
import { ThemeToggle } from "@/components/theme/ThemeToggle";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

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
    <div className="relative min-h-screen overflow-hidden px-4 py-8 md:px-6">
      <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_top_left,rgba(95,124,255,0.16),transparent_22%),radial-gradient(circle_at_bottom_right,rgba(162,188,255,0.22),transparent_26%)] dark:bg-[radial-gradient(circle_at_top_left,rgba(95,124,255,0.22),transparent_22%),radial-gradient(circle_at_bottom_right,rgba(95,124,255,0.18),transparent_26%)]" />

      <div className="absolute right-4 top-4 md:right-6 md:top-6">
        <ThemeToggle />
      </div>

      <div className="relative flex min-h-[calc(100vh-4rem)] items-center justify-center">
        <Card className="w-full max-w-[26rem] bg-card/92 px-3 py-3 shadow-[0_32px_90px_rgba(15,23,42,0.14)]">
          <CardContent className="px-5 py-5 md:px-7 md:py-7">
            <div className="flex flex-col items-center text-center">
              <div className="flex h-[72px] w-[72px] items-center justify-center rounded-[24px] bg-primary/10 text-primary ring-1 ring-primary/15">
                <FolderKanban className="h-8 w-8" />
              </div>
              <h1 className="mt-6 text-[2.1rem] font-semibold tracking-tight text-foreground">
                Portfólio Manager
              </h1>
              <p className="mt-2 text-base text-muted-foreground">
                Entre com suas credenciais
              </p>
            </div>

            <form className="mt-8 space-y-5" onSubmit={handleSubmit}>
              <div className="space-y-2.5">
                <Label className="text-base font-semibold text-foreground" htmlFor="username">
                  Usuário
                </Label>
                <Input
                  id="username"
                  name="username"
                  autoComplete="username"
                  value={username}
                  onChange={(event) => setUsername(event.target.value)}
                  required
                  className="h-14 rounded-2xl border-border/80 bg-muted/55 px-4 text-lg shadow-inner shadow-primary/5"
                />
              </div>

              <div className="space-y-2.5">
                <Label className="text-base font-semibold text-foreground" htmlFor="password">
                  Senha
                </Label>
                <Input
                  id="password"
                  name="password"
                  type="password"
                  autoComplete="current-password"
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                  required
                  className="h-14 rounded-2xl border-border/80 bg-muted/55 px-4 text-lg shadow-inner shadow-primary/5"
                />
              </div>

              <Button className="mt-2 h-14 w-full rounded-2xl text-lg" type="submit" disabled={loading}>
                {loading ? "Validando acesso..." : "Entrar"}
              </Button>
            </form>

            <p className="mt-6 text-center text-sm text-muted-foreground">
              admin / admin123
            </p>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
