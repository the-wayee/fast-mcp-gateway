import { Card } from "@/components/ui/card"
import { Activity, Clock, TrendingUp, Zap } from "lucide-react"

interface ServerMetricsProps {
  serverId: string
}

export function ServerMetrics({ serverId }: ServerMetricsProps) {
  const metrics = [
    { label: "Total Requests", value: "1,247", icon: Activity, change: "+12.5%" },
    { label: "Avg Latency", value: "12ms", icon: Clock, change: "-2.1ms" },
    { label: "Success Rate", value: "99.9%", icon: TrendingUp, change: "+0.1%" },
    { label: "Active Connections", value: "8", icon: Zap, change: "+3" },
  ]

  return (
    <Card className="p-6">
      <h2 className="text-lg font-semibold mb-4">Metrics</h2>
      <div className="grid grid-cols-2 gap-4">
        {metrics.map((metric) => {
          const Icon = metric.icon
          return (
            <div key={metric.label} className="p-4 bg-muted rounded-lg">
              <div className="flex items-center gap-2 mb-2">
                <Icon className="w-4 h-4 text-muted-foreground" />
                <span className="text-xs text-muted-foreground">{metric.label}</span>
              </div>
              <div className="flex items-baseline gap-2">
                <p className="text-2xl font-semibold">{metric.value}</p>
                <span className="text-xs text-green-500">{metric.change}</span>
              </div>
            </div>
          )
        })}
      </div>
    </Card>
  )
}
