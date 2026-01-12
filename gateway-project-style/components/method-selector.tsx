"use client"

import { Card } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Search } from "lucide-react"
import { useState } from "react"

interface MethodSelectorProps {
  selectedMethod: string
  onMethodChange: (method: string) => void
}

const methods = [
  { name: "initialize", category: "lifecycle", description: "Initialize the connection" },
  { name: "ping", category: "lifecycle", description: "Health check" },
  { name: "shutdown", category: "lifecycle", description: "Gracefully shutdown" },
  { name: "tools/list", category: "tools", description: "List available tools" },
  { name: "tools/call", category: "tools", description: "Execute a tool" },
  { name: "prompts/list", category: "prompts", description: "List available prompts" },
  { name: "prompts/get", category: "prompts", description: "Get prompt template" },
  { name: "resources/list", category: "resources", description: "List available resources" },
  { name: "resources/read", category: "resources", description: "Read resource contents" },
  { name: "logging/setLevel", category: "logging", description: "Set logging level" },
]

export function MethodSelector({ selectedMethod, onMethodChange }: MethodSelectorProps) {
  const [search, setSearch] = useState("")

  const filteredMethods = methods.filter(
    (method) =>
      method.name.toLowerCase().includes(search.toLowerCase()) ||
      method.category.toLowerCase().includes(search.toLowerCase()),
  )

  const categories = Array.from(new Set(filteredMethods.map((m) => m.category)))

  return (
    <Card className="p-4">
      <div className="mb-4">
        <h2 className="text-lg font-semibold mb-3">Methods</h2>
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
          <Input
            placeholder="Search methods..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-9"
          />
        </div>
      </div>

      <div className="space-y-4 max-h-[600px] overflow-y-auto">
        {categories.map((category) => (
          <div key={category}>
            <h3 className="text-xs font-semibold uppercase text-muted-foreground mb-2">{category}</h3>
            <div className="space-y-1">
              {filteredMethods
                .filter((m) => m.category === category)
                .map((method) => (
                  <button
                    key={method.name}
                    onClick={() => onMethodChange(method.name)}
                    className={`w-full text-left p-3 rounded-md transition-colors ${
                      selectedMethod === method.name ? "bg-accent" : "hover:bg-accent/50"
                    }`}
                  >
                    <div className="flex items-center justify-between mb-1">
                      <span className="font-mono text-sm font-semibold">{method.name}</span>
                      <Badge variant="outline" className="text-xs">
                        {category}
                      </Badge>
                    </div>
                    <p className="text-xs text-muted-foreground">{method.description}</p>
                  </button>
                ))}
            </div>
          </div>
        ))}
      </div>
    </Card>
  )
}
