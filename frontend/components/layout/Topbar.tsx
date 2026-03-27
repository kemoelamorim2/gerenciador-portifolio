"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { FolderKanban } from "lucide-react";
import { useAuth } from "@/components/auth/AuthProvider";
import { ThemeToggle } from "@/components/theme/ThemeToggle";
import { Button } from "@/components/ui/button";

export function Topbar() {
  const pathname = usePathname();
  const router = useRouter();
  const { isAuthenticated, logout } = useAuth();
  const navItems = [
    { href: "/", label: "Dashboard" },
    { href: "/projects", label: "Projetos" },
    { href: "/members", label: "Membros" },
    { href: "/reports", label: "Relatórios" },
  ];

  if (pathname === "/login" || !isAuthenticated) {
    return null;
  }

  function handleLogout() {
    logout();
    router.replace("/login");
  }

  return (
    <header className="sticky top-0 z-50 px-3 pt-3 md:px-6 md:pt-5">
      <div className="mx-auto flex h-16 w-full max-w-7xl items-center gap-4 rounded-[28px] border border-border/70 bg-background/72 px-4 shadow-[0_20px_60px_rgba(15,23,42,0.08)] backdrop-blur-xl md:px-6">
        <Link href="/" className="flex items-center gap-3">
          <span className="flex h-11 w-11 items-center justify-center rounded-2xl bg-primary/10 text-primary ring-1 ring-primary/15">
            <FolderKanban className="h-5 w-5" />
          </span>
          <span className="hidden sm:block">
            <strong className="block text-sm font-semibold tracking-tight">Portfólio Manager</strong>
            <span className="block text-xs text-muted-foreground">Gestão executiva de projetos</span>
          </span>
        </Link>

        <div className="flex flex-1 items-center justify-end gap-3">
          <nav className="hidden items-center gap-2 md:flex">
            {navItems.map((item) => {
              const isActive = pathname === item.href;

              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className={[
                    "rounded-full px-4 py-2 text-sm font-medium transition-all",
                    isActive
                      ? "bg-primary text-primary-foreground shadow-sm"
                      : "text-muted-foreground hover:bg-accent hover:text-accent-foreground",
                  ].join(" ")}
                >
                  {item.label}
                </Link>
              );
            })}
          </nav>

          <ThemeToggle />

          <Button variant="ghost" size="sm" className="rounded-full px-4" onClick={handleLogout}>
            Sair
          </Button>
        </div>
      </div>
    </header>
  );
}
