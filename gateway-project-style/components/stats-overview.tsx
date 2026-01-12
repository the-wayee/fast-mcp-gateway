import { Card } from "@/components/ui/card"
import { Activity, Server, CheckCircle2, AlertCircle } from "lucide-react"

const stats = [
  {
    label: "Total Servers",
    value: "12",
    icon: Server,
    description: "8 active, 4 idle",
  },
  {
    label: "Healthy",
    value: "10",
    icon: CheckCircle2,
    description: "83% uptime",
    trend: "up",
  },
  {
    label: "Active Connections",
    value: "47",
    icon: Activity,
    description: "+12 from last hour",
  },
  {
    label: "Errors",
    value: "2",
    icon: AlertCircle,
    description: "Requires attention",
    trend: "warning",
  },
]

export function StatsOverview() {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
      {stats.map((stat) => {
        const Icon = stat.icon
        return (
          <Card key={stat.label} className="p-6 hover:bg-accent/50 transition-colors">
            <div className="flex items-start justify-between mb-4">
              <div className="p-2 bg-secondary rounded-md">
                <Icon className="w-5 h-5 text-secondary-foreground" />
              </div>
            </div>
            <div className="space-y-1">
              <p className="text-sm text-muted-foreground">{stat.label}</p>
              <p className="text-3xl font-semibold tracking-tight">{stat.value}</p>
              <p className="text-xs text-muted-foreground">{stat.description}</p>
            </div>
          </Card>
        )
      })}
    </div>
  )
}
