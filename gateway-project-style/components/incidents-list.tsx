import { Card } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { ScrollArea } from "@/components/ui/scroll-area"
import { AlertCircle, XCircle, CheckCircle2, Clock } from "lucide-react"

const incidents = [
  {
    id: "1",
    server: "analytics",
    type: "error",
    message: "Connection timeout to upstream service",
    timestamp: "2 minutes ago",
    status: "active",
  },
  {
    id: "2",
    server: "github",
    type: "warning",
    message: "High latency detected (234ms)",
    timestamp: "15 minutes ago",
    status: "active",
  },
  {
    id: "3",
    server: "database",
    type: "info",
    message: "Routine maintenance completed",
    timestamp: "1 hour ago",
    status: "resolved",
  },
  {
    id: "4",
    server: "filesystem",
    type: "warning",
    message: "Memory usage at 85%",
    timestamp: "2 hours ago",
    status: "resolved",
  },
  {
    id: "5",
    server: "slack",
    type: "error",
    message: "API rate limit exceeded",
    timestamp: "3 hours ago",
    status: "resolved",
  },
]

const typeConfig = {
  error: {
    icon: XCircle,
    color: "text-red-500",
    bg: "bg-red-500/10",
    border: "border-red-500/20",
  },
  warning: {
    icon: AlertCircle,
    color: "text-yellow-500",
    bg: "bg-yellow-500/10",
    border: "border-yellow-500/20",
  },
  info: {
    icon: CheckCircle2,
    color: "text-blue-500",
    bg: "bg-blue-500/10",
    border: "border-blue-500/20",
  },
}

export function IncidentsList() {
  return (
    <Card className="p-6">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-xl font-semibold">Recent Incidents</h2>
        <Badge variant="outline">{incidents.filter((i) => i.status === "active").length} Active</Badge>
      </div>

      <ScrollArea className="h-[400px] pr-4">
        <div className="space-y-3">
          {incidents.map((incident) => {
            const config = typeConfig[incident.type as keyof typeof typeConfig]
            const Icon = config.icon

            return (
              <div key={incident.id} className={`p-4 rounded-lg border ${config.bg} ${config.border}`}>
                <div className="flex items-start gap-3">
                  <Icon className={`w-5 h-5 shrink-0 mt-0.5 ${config.color}`} />
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-1">
                      <span className="font-mono text-sm font-semibold">{incident.server}</span>
                      <Badge variant="outline" className={incident.status === "active" ? "bg-background" : "bg-muted"}>
                        {incident.status}
                      </Badge>
                    </div>
                    <p className="text-sm mb-2">{incident.message}</p>
                    <div className="flex items-center gap-1 text-xs text-muted-foreground">
                      <Clock className="w-3 h-3" />
                      {incident.timestamp}
                    </div>
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      </ScrollArea>
    </Card>
  )
}
