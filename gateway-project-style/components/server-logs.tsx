"use client"

import { Card } from "@/components/ui/card"
import { ScrollArea } from "@/components/ui/scroll-area"
import { Badge } from "@/components/ui/badge"

interface ServerLogsProps {
  serverId: string
}

const logs = [
  { time: "14:23:45", level: "info", message: "Request processed successfully" },
  { time: "14:23:42", level: "info", message: "Connection established from client" },
  { time: "14:23:38", level: "warn", message: "High latency detected: 234ms" },
  { time: "14:23:35", level: "info", message: "Cache hit for resource /api/data" },
  { time: "14:23:30", level: "error", message: "Failed to connect to upstream service" },
  { time: "14:23:25", level: "info", message: "Request processed successfully" },
  { time: "14:23:20", level: "info", message: "Health check passed" },
]

const levelColors = {
  info: "bg-blue-500/10 text-blue-500 border-blue-500/20",
  warn: "bg-yellow-500/10 text-yellow-500 border-yellow-500/20",
  error: "bg-red-500/10 text-red-500 border-red-500/20",
}

export function ServerLogs({ serverId }: ServerLogsProps) {
  return (
    <Card className="p-6">
      <h2 className="text-lg font-semibold mb-4">Recent Logs</h2>
      <ScrollArea className="h-[300px] w-full rounded-md border border-border p-4 bg-muted/30">
        <div className="space-y-2">
          {logs.map((log, index) => (
            <div key={index} className="flex items-start gap-3 text-sm font-mono">
              <span className="text-muted-foreground shrink-0">{log.time}</span>
              <Badge variant="outline" className={`shrink-0 ${levelColors[log.level as keyof typeof levelColors]}`}>
                {log.level}
              </Badge>
              <span className="text-foreground">{log.message}</span>
            </div>
          ))}
        </div>
      </ScrollArea>
    </Card>
  )
}
