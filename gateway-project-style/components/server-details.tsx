"use client"

import { useEffect, useState } from "react"
import { Card } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { CheckCircle2, Server, Globe, Clock, AlertCircle, HelpCircle, XCircle } from "lucide-react"
import { useSearchParams } from "next/navigation"
import httpClient, { type ActionResult } from "@/lib/http-client"
import { type ServerDetail, type HealthStatus } from "@/types/server"

interface ServerDetailsProps {
  serverId: string
}

const statusConfig = {
  HEALTHY: {
    icon: CheckCircle2,
    color: "text-green-500",
    label: "Healthy",
  },
  DEGRADED: {
    icon: AlertCircle,
    color: "text-yellow-500",
    label: "Degraded",
  },
  UNHEALTHY: {
    icon: XCircle,
    color: "text-red-500",
    label: "Unhealthy",
  },
  UNKNOWN: {
    icon: HelpCircle,
    color: "text-gray-500",
    label: "Unknown",
  },
}

export function ServerDetails({ serverId }: ServerDetailsProps) {
  const [server, setServer] = useState<ServerDetail | null>(null)
  const [loading, setLoading] = useState(true)
  const searchParams = useSearchParams()
  const serverName = searchParams.get("serverName") || ""

  useEffect(() => {
    // 获取服务详情
    if (serverName) {
      setLoading(true)
      httpClient.get<ActionResult<ServerDetail>>(`/monitors/${serverId}/detail?serverName=${serverName}`)
        .then(response => {
          setServer(response.data.data)
          setLoading(false)
        })
        .catch(error => {
          console.error("Failed to fetch server details:", error)
          setLoading(false)
        })
    } else {
      setLoading(false)
    }
  }, [serverId, serverName])

  if (loading) {
    return (
      <Card className="p-6">
        <div className="text-center py-12 text-muted-foreground">
          <p>Loading server details...</p>
        </div>
      </Card>
    )
  }

  if (!serverName) {
    return (
      <Card className="p-6">
        <div className="text-center py-12 text-muted-foreground">
          <p>Missing serverName parameter</p>
        </div>
      </Card>
    )
  }

  if (!server) {
    return (
      <Card className="p-6">
        <div className="text-center py-12 text-muted-foreground">
          <p>Server not found</p>
        </div>
      </Card>
    )
  }

  const status = statusConfig[server.healthStatus]
  const StatusIcon = status.icon

  return (
    <Card className="p-6">
      <div className="flex items-start justify-between mb-6">
        <div className="flex items-center gap-4">
          <div className="p-3 bg-secondary rounded-lg">
            <Server className="w-6 h-6" />
          </div>
          <div>
            <div className="flex items-center gap-2 mb-1">
              <h1 className="text-2xl font-semibold font-mono">{server.serverName}</h1>
              <Badge variant="outline" className="gap-1">
                <StatusIcon className={`w-3 h-3 ${status.color}`} />
                {status.label}
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
          <p className="text-sm font-mono">{server.endpoint}</p>
        </div>
        <div className="p-4 bg-muted rounded-lg">
          <div className="flex items-center gap-2 mb-1">
            <Server className="w-4 h-4 text-muted-foreground" />
            <span className="text-xs text-muted-foreground">Protocol</span>
          </div>
          <p className="text-sm font-semibold">{server.transportType}</p>
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
          <p className="text-sm font-semibold">{server.version || "N/A"}</p>
        </div>
      </div>
    </Card>
  )
}
