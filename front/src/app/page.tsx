"use client";


import { ServerCard } from "@/components/dashboard/server-card";
import { Search, Plus } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useState, useEffect } from "react";
import { Server } from "@/lib/types";
import { MOCK_SERVERS } from "@/lib/mock-data";

export default function Dashboard() {
  const [servers, setServers] = useState<Server[]>([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Simulate fetch
    const timer = setTimeout(() => {
      setServers(MOCK_SERVERS);
      setLoading(false);
    }, 600);
    return () => clearTimeout(timer);
  }, []);

  const filteredServers = servers.filter(s =>
    s.name.toLowerCase().includes(search.toLowerCase()) ||
    s.transport_type.includes(search.toLowerCase())
  );

  const activeCount = servers.filter(s => s.server_status === 'active').length;
  const inactiveCount = servers.filter(s => s.server_status === 'inactive').length;
  const unhealthyCount = servers.filter(s => s.server_status === 'unhealthy').length;

  return (
    <div className="flex flex-col gap-8">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
          <p className="text-muted-foreground mt-1 text-lg">Overview of your MCP infrastructure</p>
        </div>
        <Button>
          <Plus className="mr-2 h-4 w-4" /> Add Server
        </Button>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <StatsCard title="Total Servers" value={servers.length} />
        <StatsCard title="Active" value={activeCount} indicator="emerald" />
        <StatsCard title="Inactive" value={inactiveCount} indicator="sky" />
        <StatsCard title="Unhealthy" value={unhealthyCount} indicator="rose" />
      </div>

      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-xl font-semibold tracking-tight">Registered Servers</h2>
          <div className="bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
            <div className="relative">
              <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                type="search"
                placeholder="Search servers..."
                className="pl-9 w-[250px] lg:w-[350px]"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
            </div>
          </div>
        </div>

        {loading ? (
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
            {[1, 2, 3].map(i => (
              <div key={i} className="h-[240px] rounded-xl border bg-card/40 animate-pulse" />
            ))}
          </div>
        ) : filteredServers.length > 0 ? (
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
            {filteredServers.map((server) => (
              <ServerCard key={server.name} server={server} />
            ))}
          </div>
        ) : (
          <div className="flex h-[400px] flex-col items-center justify-center rounded-xl border border-dashed bg-muted/40">
            <div className="h-12 w-12 rounded-full bg-muted flex items-center justify-center mb-4">
              <Search className="h-6 w-6 text-muted-foreground" />
            </div>
            <h3 className="text-lg font-medium">No servers found</h3>
            <p className="text-sm text-muted-foreground mt-1 max-w-sm text-center">
              We couldn't find any servers matching your search. Try adjusting your filters or add a new server.
            </p>
          </div>
        )}
      </div>
    </div>
  );
}

function StatsCard({ title, value, indicator }: { title: string, value: number, indicator?: string }) {
  return (
    <div className="rounded-xl border bg-card p-6 shadow-sm">
      <div className="flex flex-row items-center justify-between space-y-0 pb-2">
        <h3 className="tracking-tight text-sm font-medium text-muted-foreground">{title}</h3>
        {indicator && (
          <div className={`h-2 w-2 rounded-full ${indicator === 'emerald' ? 'bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.4)]' :
            indicator === 'sky' ? 'bg-zinc-500' :
              indicator === 'rose' ? 'bg-rose-500 shadow-[0_0_8px_rgba(244,63,94,0.4)]' : 'bg-primary'
            }`} />
        )}
      </div>
      <div className="text-3xl font-bold">{value}</div>
    </div>
  );
}
