import type { Metadata } from "next";
import "./globals.css";
import { Geist } from "next/font/google";
import { cn } from "@/lib/utils";
import { Toaster } from "@/components/ui/sonner";
import { AuthGate } from "@/components/auth/AuthGate";
import { AuthProvider } from "@/components/auth/AuthProvider";
import { Topbar } from "@/components/layout/Topbar";
import { ThemeProvider } from "@/components/theme/ThemeProvider";

const geist = Geist({subsets:['latin'],variable:'--font-sans'});

export const metadata: Metadata = {
  title: "Gerenciador de Portifolio",
  description: "Frontend do sistema de gerenciamento de portifolio",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="pt-BR" className={cn("font-sans", geist.variable)} suppressHydrationWarning>
      <body className="min-h-screen bg-background font-sans text-foreground antialiased">
        <ThemeProvider>
          <AuthProvider>
            <AuthGate>
              <div className="relative flex min-h-screen flex-col">
                <Topbar />
                <main className="flex-1">{children}</main>
              </div>
              <Toaster richColors position="top-right" />
            </AuthGate>
          </AuthProvider>
        </ThemeProvider>
      </body>
    </html>
  );
}
