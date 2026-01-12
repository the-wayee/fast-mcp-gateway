"use client"

import { Button } from "@/components/ui/button"
import { Settings, Activity } from "lucide-react"
import { AddServerDialog } from "@/components/add-server-dialog"
import Link from "next/link"
import { usePathname } from "next/navigation"

export function Header() {
  const pathname = usePathname()

  return (
    <header className="sticky top-0 z-50 w-full border-b border-border bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="mx-auto max-w-[1400px] px-6 h-16 flex items-center justify-between">
        <div className="flex items-center gap-6">
          <Link href="/" className="flex items-center gap-2">
            <div className="w-8 h-8 bg-primary rounded-md flex items-center justify-center">
              <span className="text-primary-foreground font-mono text-sm font-semibold">M</span>
            </div>
            <span className="font-semibold text-lg">MCP Gateway</span>
          </Link>

          <nav className="hidden md:flex items-center gap-1">
            <Link href="/">
              <Button variant={pathname === "/" ? "secondary" : "ghost"} size="sm">
                Dashboard
              </Button>
            </Link>
            <Link href="/monitoring">
              <Button variant={pathname === "/monitoring" ? "secondary" : "ghost"} size="sm">
                <Activity className="w-4 h-4 mr-2" />
                Monitoring
              </Button>
            </Link>
          </nav>
        </div>

        <div className="flex items-center gap-3">
          <Button variant="outline" size="sm">
            <Settings className="w-4 h-4 mr-2" />
            Settings
          </Button>
          <AddServerDialog />
        </div>
      </div>
    </header>
  )
}
