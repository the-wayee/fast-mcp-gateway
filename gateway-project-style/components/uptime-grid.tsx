import { Card } from "@/components/ui/card"

const servers = [
  { name: "filesystem", uptime: 99.9, data: Array.from({ length: 90 }, () => Math.random() > 0.05) },
  { name: "database", uptime: 99.5, data: Array.from({ length: 90 }, () => Math.random() > 0.1) },
  { name: "github", uptime: 98.2, data: Array.from({ length: 90 }, () => Math.random() > 0.15) },
  { name: "slack", uptime: 99.8, data: Array.from({ length: 90 }, () => Math.random() > 0.03) },
  { name: "analytics", uptime: 95.3, data: Array.from({ length: 90 }, () => Math.random() > 0.3) },
  { name: "memory", uptime: 100, data: Array.from({ length: 90 }, () => true) },
]

export function UptimeGrid() {
  return (
    <Card className="p-6">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-xl font-semibold">90-Day Uptime</h2>
        <span className="text-sm text-muted-foreground">Last 90 days</span>
      </div>

      <div className="space-y-4">
        {servers.map((server) => (
          <div key={server.name}>
            <div className="flex items-center justify-between mb-2">
              <span className="font-mono text-sm font-semibold">{server.name}</span>
              <span className="text-sm text-muted-foreground">{server.uptime}%</span>
            </div>
            <div className="flex gap-[2px]">
              {server.data.map((isUp, i) => (
                <div
                  key={i}
                  className={`flex-1 h-8 rounded-sm transition-colors ${
                    isUp ? "bg-green-500 hover:bg-green-400" : "bg-red-500 hover:bg-red-400"
                  }`}
                  title={`Day ${i + 1}: ${isUp ? "Up" : "Down"}`}
                />
              ))}
            </div>
          </div>
        ))}
      </div>

      <div className="mt-6 flex items-center justify-between text-xs text-muted-foreground">
        <span>90 days ago</span>
        <span>Today</span>
      </div>
    </Card>
  )
}
