"use client"

import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { MoreVertical, Activity, Clock, TrendingUp, AlertCircle, CheckCircle2, XCircle } from "lucide-react"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import Link from "next/link"

interface Server {
  id: string
  name: string
  description: string
  status: "healthy" | "warning" | "error"
  url: string
  requests: number
  latency: number
  uptime: number
}

interface ServerCardProps {
  server: Server
}

const statusConfig = {
  healthy: {
    icon: CheckCircle2,
    color: "text-green-500",
    bg: "bg-green-500/10",
    label: "Healthy",
  },
  warning: {
    icon: AlertCircle,
    color: "text-yellow-500",
    bg: "bg-yellow-500/10",
    label: "Warning",
  },
  error: {
    icon: XCircle,
    color: "text-red-500",
    bg: "bg-red-500/10",
    label: "Error",
  },
}

export function ServerCard({ server }: ServerCardProps) {
  const status = statusConfig[server.status]
  const StatusIcon = status.icon

  return (
    <Link href={`/server/${server.id}`}>
      <Card className="p-5 hover:bg-accent/50 transition-colors group cursor-pointer">
        <div className="flex items-start justify-between mb-4">
          <div className="flex items-center gap-3">
            <div className={`p-2 rounded-md ${status.bg}`}>
              <StatusIcon className={`w-4 h-4 ${status.color}`} />
            </div>
            <div>
              <h3 className="font-semibold font-mono text-sm">{server.name}</h3>
              <Badge variant="outline" className="mt-1 text-xs">
                {status.label}
              </Badge>
            </div>
          </div>

          <DropdownMenu>
            <DropdownMenuTrigger asChild onClick={(e) => e.preventDefault()}>
              <Button
                variant="ghost"
                size="sm"
                className="h-8 w-8 p-0 opacity-0 group-hover:opacity-100 transition-opacity"
              >
                <MoreVertical className="w-4 h-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem>View Details</DropdownMenuItem>
              <DropdownMenuItem>Debug</DropdownMenuItem>
              <DropdownMenuItem>View Logs</DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem>Restart</DropdownMenuItem>
              <DropdownMenuItem className="text-destructive">Remove</DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>

        <p className="text-sm text-muted-foreground mb-4 line-clamp-2">{server.description}</p>

        <div className="text-xs font-mono text-muted-foreground mb-4 p-2 bg-muted rounded border border-border">
          {server.url}
        </div>

        <div className="grid grid-cols-3 gap-3 pt-4 border-t border-border">
          <div>
            <div className="flex items-center gap-1 text-muted-foreground mb-1">
              <Activity className="w-3 h-3" />
              <span className="text-xs">Requests</span>
            </div>
            <p className="text-sm font-semibold">{server.requests.toLocaleString()}</p>
          </div>
          <div>
            <div className="flex items-center gap-1 text-muted-foreground mb-1">
              <Clock className="w-3 h-3" />
              <span className="text-xs">Latency</span>
            </div>
            <p className="text-sm font-semibold">{server.latency}ms</p>
          </div>
          <div>
            <div className="flex items-center gap-1 text-muted-foreground mb-1">
              <TrendingUp className="w-3 h-3" />
              <span className="text-xs">Uptime</span>
            </div>
            <p className="text-sm font-semibold">{server.uptime}%</p>
          </div>
        </div>
      </Card>
    </Link>
  )
}
