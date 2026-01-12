import { ServerGrid } from "@/components/server-grid"
import { StatsOverview } from "@/components/stats-overview"
import { Header } from "@/components/header"

export default function DashboardPage() {
  return (
    <div className="min-h-screen bg-background">
      <Header />
      <main className="mx-auto max-w-[1400px] px-6 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-semibold tracking-tight text-balance mb-2">MCP Gateway</h1>
          <p className="text-muted-foreground text-pretty">Manage and monitor your Model Context Protocol servers</p>
        </div>

        <StatsOverview />
        <ServerGrid />
      </main>
    </div>
  )
}
