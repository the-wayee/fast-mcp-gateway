"use client"

import { useState, useEffect } from "react"
import { ServerCard } from "@/components/server-card"
import { Input } from "@/components/ui/input"
import { Search } from "lucide-react"
import httpClient, { type ActionResult } from "@/lib/http-client"
import { type ServerMonitorSummary } from "@/types/server"

export function ServerGrid() {
  const [servers, setServers] = useState<ServerMonitorSummary[]>([])
  const [loading, setLoading] = useState(true)
  const [searchQuery, setSearchQuery] = useState("")

  useEffect(() => {
    // 获取所有服务监控摘要
    httpClient.get<ActionResult<ServerMonitorSummary[]>>("/monitors/summary")
      .then(response => {
        setServers(response.data.data)
        setLoading(false)
      })
      .catch(error => {
        console.error("Failed to fetch servers:", error)
        setLoading(false)
      })
  }, [])

  const filteredServers = servers.filter(
    (server) =>
      server.serverName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      server.description.toLowerCase().includes(searchQuery.toLowerCase()),
  )

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-semibold">Connected Servers</h2>
        <div className="relative w-64">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
          <Input
            placeholder="Search servers..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-9"
          />
        </div>
      </div>

      {loading ? (
        <div className="text-center py-12 text-muted-foreground">
          <p>Loading servers...</p>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {filteredServers.map((server) => (
              <ServerCard key={server.serverId} server={server} />
            ))}
          </div>

          {filteredServers.length === 0 && (
            <div className="text-center py-12 text-muted-foreground">
              <p>No servers found matching your search.</p>
            </div>
          )}
        </>
      )}
    </div>
  )
}
