"use client";

import { Monitor, Moon, Sun } from "lucide-react";
import { useTheme } from "next-themes";
import { Button } from "@/components/ui/button";

const themes = [
  { value: "light", label: "Claro", icon: Sun },
  { value: "dark", label: "Escuro", icon: Moon },
  { value: "system", label: "Sistema", icon: Monitor },
] as const;

export function ThemeToggle() {
  const { theme, setTheme } = useTheme();

  return (
    <div className="inline-flex items-center rounded-full border border-border/70 bg-background/75 p-1 shadow-sm backdrop-blur">
      {themes.map((item) => {
        const Icon = item.icon;
        const isActive = theme === item.value;

        return (
          <Button
            key={item.value}
            type="button"
            size="icon-sm"
            variant={isActive ? "secondary" : "ghost"}
            className="rounded-full"
            onClick={() => setTheme(item.value)}
            aria-label={`Alternar para tema ${item.label.toLowerCase()}`}
          >
            <Icon className="size-4" />
          </Button>
        );
      })}
    </div>
  );
}
