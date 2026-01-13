"use client"

import { useEffect, useState } from "react"
import { Card } from "@/components/ui/card"
import { Activity, Clock, TrendingUp, Zap } from "lucide-react"
import httpClient, { type ActionResult } from "@/lib/http-client"
import { useSearchParams } from "next/navigation"
import { type ServerDetail } from "@/types/server"

interface ServerMetricsProps {
  serverId: string
}

export function ServerMetrics({ serverId }: ServerMetricsProps) {
  const [metrics, setMetrics] = useState<ServerDetail | null>(null)
  const [loading, setLoading] = useState(true)
  const searchParams = useSearchParams()
  const serverName = searchParams.get("serverName") || ""

  useEffect(() => {
    if (serverName) {
      setLoading(true)
      httpClient.get<ActionResult<ServerDetail>>(`/monitors/${serverId}/detail?serverName=${serverName}`)
        .then(response => {
          setMetrics(response.data.data)
          setLoading(false)
        })
        .catch(error => {
          console.error("Failed to fetch server metrics:", error)
          setLoading(false)
        })
    }
  }, [serverId, serverName])

  if (loading) {
    return (
      <Card className="p-6">
        <h2 className="text-lg font-semibold mb-4">Metrics</h2>
        <div className="text-center py-12 text-muted-foreground">
          <p>Loading metrics...</p>
        </div>
      </Card>
    )
  }

  if (!metrics) {
    return (
      <Card className="p-6">
        <h2 className="text-lg font-semibold mb-4">Metrics</h2>
        <div className="text-center py-12 text-muted-foreground">
          <p>No metrics available</p>
        </div>
      </Card>
    )
  }

  const metricData = [
    {
      label: "Total Requests",
      value: metrics.totalRequests.toLocaleString(),
      icon: Activity,
    },
    {
      label: "Avg Latency",
      value: `${metrics.avgLatency.toFixed(0)}ms`,
      icon: Clock,
    },
    {
      label: "Success Rate",
      value: `${metrics.successRate.toFixed(1)}%`,
      icon: TrendingUp,
    },
    {
      label: "Active Connections",
      value: metrics.activeConnections?.toString() || "N/A",
      icon: Zap,
    },
  ]

  return (
    <Card className="p-6">
      <h2 className="text-lg font-semibold mb-4">Metrics</h2>
      <div className="grid grid-cols-2 gap-4">
        {metricData.map((metric) => {
          const Icon = metric.icon
          return (
            <div key={metric.label} className="p-4 bg-muted rounded-lg">
              <div className="flex items-center gap-2 mb-2">
                <Icon className="w-4 h-4 text-muted-foreground" />
                <span className="text-xs text-muted-foreground">{metric.label}</span>
              </div>
              <p className="text-2xl font-semibold">{metric.value}</p>
            </div>
          )
        })}
      </div>

      {/* 额外的详细指标 */}
      <div className="mt-6 pt-6 border-t border-border">
        <h3 className="text-sm font-medium mb-4 text-muted-foreground">Detailed Metrics</h3>
        <div className="grid grid-cols-3 gap-4 text-sm">
          <div>
            <p className="text-muted-foreground">Success Requests</p>
            <p className="font-semibold">{metrics.successRequests.toLocaleString()}</p>
          </div>
          <div>
            <p className="text-muted-foreground">Failed Requests</p>
            <p className="font-semibold">{metrics.failedRequests.toLocaleString()}</p>
          </div>
          <div>
            <p className="text-muted-foreground">Min Latency</p>
            <p className="font-semibold">{metrics.minLatency}ms</p>
          </div>
          <div>
            <p className="text-muted-foreground">Max Latency</p>
            <p className="font-semibold">{metrics.maxLatency}ms</p>
          </div>
          <div>
            <p className="text-muted-foreground">Failure Rate</p>
            <p className="font-semibold">{metrics.failureRate.toFixed(1)}%</p>
          </div>
          <div>
            <p className="text-muted-foreground">Uptime</p>
            <p className="font-semibold">{metrics.uptime}</p>
          </div>
        </div>
      </div>
    </Card>
  )
}
