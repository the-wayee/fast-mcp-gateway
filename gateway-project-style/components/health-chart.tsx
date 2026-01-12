"use client"

import { Card } from "@/components/ui/card"
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs"

export function HealthChart() {
  return (
    <Card className="p-6">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-semibold">Performance Metrics</h2>
        <Tabs defaultValue="24h">
          <TabsList>
            <TabsTrigger value="1h">1H</TabsTrigger>
            <TabsTrigger value="24h">24H</TabsTrigger>
            <TabsTrigger value="7d">7D</TabsTrigger>
            <TabsTrigger value="30d">30D</TabsTrigger>
          </TabsList>
        </Tabs>
      </div>

      <div className="h-[300px] flex items-end justify-between gap-1 px-4">
        {Array.from({ length: 48 }).map((_, i) => {
          const height = Math.random() * 80 + 20
          const isError = Math.random() > 0.9
          const isWarning = !isError && Math.random() > 0.85

          return (
            <div key={i} className="flex-1 flex flex-col justify-end group cursor-pointer">
              <div
                className={`w-full rounded-sm transition-all ${
                  isError
                    ? "bg-red-500 hover:bg-red-400"
                    : isWarning
                      ? "bg-yellow-500 hover:bg-yellow-400"
                      : "bg-primary hover:bg-primary/80"
                }`}
                style={{ height: `${height}%` }}
              />
            </div>
          )
        })}
      </div>

      <div className="mt-6 flex items-center justify-center gap-6 text-sm">
        <div className="flex items-center gap-2">
          <div className="w-3 h-3 rounded-sm bg-primary" />
          <span className="text-muted-foreground">Healthy</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-3 h-3 rounded-sm bg-yellow-500" />
          <span className="text-muted-foreground">Degraded</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-3 h-3 rounded-sm bg-red-500" />
          <span className="text-muted-foreground">Error</span>
        </div>
      </div>
    </Card>
  )
}
