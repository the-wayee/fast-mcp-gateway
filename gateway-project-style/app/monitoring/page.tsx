import { Header } from "@/components/header"
import { HealthOverview } from "@/components/health-overview"
import { HealthChart } from "@/components/health-chart"
import { IncidentsList } from "@/components/incidents-list"
import { UptimeGrid } from "@/components/uptime-grid"

export default function MonitoringPage() {
  return (
    <div className="min-h-screen bg-background">
      <Header />
      <main className="mx-auto max-w-[1400px] px-6 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-semibold tracking-tight text-balance mb-2">Health Monitoring</h1>
          <p className="text-muted-foreground text-pretty">
            Real-time health status and performance metrics for all servers
          </p>
        </div>

        <div className="space-y-6">
          <HealthOverview />
          <HealthChart />
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <IncidentsList />
            <UptimeGrid />
          </div>
        </div>
      </main>
    </div>
  )
}
