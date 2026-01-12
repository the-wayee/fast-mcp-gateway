"use client"

import { Card } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { CheckCircle2, Server, Globe, Clock } from "lucide-react"

interface ServerDetailsProps {
  serverId: string
}

export function ServerDetails({ serverId }: ServerDetailsProps) {
  // Mock data - in real app, fetch based on serverId
  const server = {
    name: "filesystem",
    description: "Local filesystem access and operations",
    status: "healthy",
    url: "stdio://localhost:3001",
    protocol: "STDIO",
    version: "1.0.0",
    uptime: "15d 7h 23m",
    connectedAt: "2025-12-23 09:14:32",
  }

  return (
    <Card className="p-6">
      <div className="flex items-start justify-between mb-6">
        <div className="flex items-center gap-4">
          <div className="p-3 bg-secondary rounded-lg">
            <Server className="w-6 h-6" />
          </div>
          <div>
            <div className="flex items-center gap-2 mb-1">
              <h1 className="text-2xl font-semibold font-mono">{server.name}</h1>
              <Badge variant="outline" className="gap-1">
                <CheckCircle2 className="w-3 h-3 text-green-500" />
                {server.status}
              </Badge>
            </div>
            <p className="text-sm text-muted-foreground">{server.description}</p>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div className="p-4 bg-muted rounded-lg">
          <div className="flex items-center gap-2 mb-1">
            <Globe className="w-4 h-4 text-muted-foreground" />
            <span className="text-xs text-muted-foreground">Endpoint</span>
          </div>
          <p className="text-sm font-mono">{server.url}</p>
        </div>
        <div className="p-4 bg-muted rounded-lg">
          <div className="flex items-center gap-2 mb-1">
            <Server className="w-4 h-4 text-muted-foreground" />
            <span className="text-xs text-muted-foreground">Protocol</span>
          </div>
          <p className="text-sm font-semibold">{server.protocol}</p>
        </div>
        <div className="p-4 bg-muted rounded-lg">
          <div className="flex items-center gap-2 mb-1">
            <Clock className="w-4 h-4 text-muted-foreground" />
            <span className="text-xs text-muted-foreground">Uptime</span>
          </div>
          <p className="text-sm font-semibold">{server.uptime}</p>
        </div>
        <div className="p-4 bg-muted rounded-lg">
          <div className="flex items-center gap-2 mb-1">
            <CheckCircle2 className="w-4 h-4 text-muted-foreground" />
            <span className="text-xs text-muted-foreground">Version</span>
          </div>
          <p className="text-sm font-semibold">{server.version}</p>
        </div>
      </div>
    </Card>
  )
}
