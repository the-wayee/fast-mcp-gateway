"use client"

import { useState } from "react"
import { ServerCard } from "@/components/server-card"
import { Input } from "@/components/ui/input"
import { Search } from "lucide-react"

const servers = [
  {
    id: "1",
    name: "filesystem",
    description: "Local filesystem access and operations",
    status: "healthy" as const,
    url: "stdio://localhost:3001",
    requests: 1247,
    latency: 12,
    uptime: 99.9,
  },
  {
    id: "2",
    name: "database",
    description: "PostgreSQL database interface",
    status: "healthy" as const,
    url: "http://localhost:3002",
    requests: 3421,
    latency: 45,
    uptime: 99.5,
  },
  {
    id: "3",
    name: "github",
    description: "GitHub API integration",
    status: "warning" as const,
    url: "http://localhost:3003",
    requests: 892,
    latency: 234,
    uptime: 98.2,
  },
  {
    id: "4",
    name: "slack",
    description: "Slack workspace integration",
    status: "healthy" as const,
    url: "http://localhost:3004",
    requests: 567,
    latency: 89,
    uptime: 99.8,
  },
  {
    id: "5",
    name: "analytics",
    description: "Data analytics and reporting",
    status: "error" as const,
    url: "http://localhost:3005",
    requests: 124,
    latency: 0,
    uptime: 0,
  },
  {
    id: "6",
    name: "memory",
    description: "In-memory key-value store",
    status: "healthy" as const,
    url: "stdio://localhost:3006",
    requests: 8234,
    latency: 3,
    uptime: 100,
  },
]

export function ServerGrid() {
  const [searchQuery, setSearchQuery] = useState("")

  const filteredServers = servers.filter(
    (server) =>
      server.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
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

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {filteredServers.map((server) => (
          <ServerCard key={server.id} server={server} />
        ))}
      </div>

      {filteredServers.length === 0 && (
        <div className="text-center py-12 text-muted-foreground">
          <p>No servers found matching your search.</p>
        </div>
      )}
    </div>
  )
}
