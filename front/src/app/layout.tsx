import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google"; // Using standard Next.js fonts
import "./globals.css";
import { Sidebar } from "@/components/layout/sidebar";
import { cn } from "@/lib/utils";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "MCP Gateway",
  description: "Observability Platform for MCP Servers",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className="dark h-full">
      <body
        className={cn(
          geistSans.variable,
          geistMono.variable,
          "font-sans antialiased bg-background text-foreground h-full"
        )}
      >
        <div className="grid min-h-screen w-full md:grid-cols-[240px_1fr] lg:grid-cols-[260px_1fr]">
          <Sidebar />
          <div className="flex flex-col min-h-screen">
            {/* Header could go here for mobile */}
            <main className="flex-1 p-6 lg:p-10 bg-muted/10">
              <div className="max-w-7xl mx-auto w-full">
                {children}
              </div>
            </main>
          </div>
        </div>
      </body>
    </html>
  );
}
