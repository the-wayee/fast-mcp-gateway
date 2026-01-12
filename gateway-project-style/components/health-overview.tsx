import { Card } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { CheckCircle2, AlertTriangle, XCircle, Activity } from "lucide-react"

const healthData = {
  overall: "operational",
  healthy: 10,
  degraded: 1,
  down: 1,
  avgResponseTime: 45,
}

export function HealthOverview() {
  return (
    <Card className="p-6">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-semibold mb-1">System Status</h2>
          <div className="flex items-center gap-2">
            <Badge variant="outline" className="bg-green-500/10 text-green-500 border-green-500/20">
              <CheckCircle2 className="w-3 h-3 mr-1" />
              All Systems Operational
            </Badge>
            <span className="text-sm text-muted-foreground">Last updated: 2 minutes ago</span>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div className="p-4 bg-green-500/5 rounded-lg border border-green-500/20">
          <div className="flex items-center gap-2 mb-2">
            <CheckCircle2 className="w-5 h-5 text-green-500" />
            <span className="text-sm text-muted-foreground">Healthy</span>
          </div>
          <p className="text-3xl font-semibold">{healthData.healthy}</p>
        </div>

        <div className="p-4 bg-yellow-500/5 rounded-lg border border-yellow-500/20">
          <div className="flex items-center gap-2 mb-2">
            <AlertTriangle className="w-5 h-5 text-yellow-500" />
            <span className="text-sm text-muted-foreground">Degraded</span>
          </div>
          <p className="text-3xl font-semibold">{healthData.degraded}</p>
        </div>

        <div className="p-4 bg-red-500/5 rounded-lg border border-red-500/20">
          <div className="flex items-center gap-2 mb-2">
            <XCircle className="w-5 h-5 text-red-500" />
            <span className="text-sm text-muted-foreground">Down</span>
          </div>
          <p className="text-3xl font-semibold">{healthData.down}</p>
        </div>

        <div className="p-4 bg-muted rounded-lg border border-border">
          <div className="flex items-center gap-2 mb-2">
            <Activity className="w-5 h-5 text-muted-foreground" />
            <span className="text-sm text-muted-foreground">Avg Response</span>
          </div>
          <p className="text-3xl font-semibold">{healthData.avgResponseTime}ms</p>
        </div>
      </div>
    </Card>
  )
}
