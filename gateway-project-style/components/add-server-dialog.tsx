"use client"

import type React from "react"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Plus } from "lucide-react"
import httpClient from "@/lib/http-client"

export function AddServerDialog() {
  const [open, setOpen] = useState(false)
  const [loading, setLoading] = useState(false)
  const [serverData, setServerData] = useState({
    name: "",
    description: "",
    transportType: "STREAMABLE_HTTP",
    endpoint: "",
    version: "1.0.0",
  })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)

    try {
      await httpClient.post("/servers", serverData)

      setOpen(false)
      // Reset form
      setServerData({
        name: "",
        description: "",
        transportType: "STREAMABLE_HTTP",
        endpoint: "",
        version: "1.0.0",
      })
      // Refresh the page
      window.location.reload()
    } catch (error) {
      console.error("Error registering server:", error)
      // Toast is handled by interceptor
    } finally {
      setLoading(false)
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button size="sm">
          <Plus className="w-4 h-4 mr-2" />
          Add Server
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[500px]">
        <form onSubmit={handleSubmit}>
          <DialogHeader>
            <DialogTitle>Add MCP Server</DialogTitle>
            <DialogDescription>Connect a new Model Context Protocol server to your gateway.</DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="name">Server Name</Label>
              <Input
                id="name"
                placeholder="filesystem"
                value={serverData.name}
                onChange={(e) => setServerData({ ...serverData, name: e.target.value })}
                required
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="description">Description</Label>
              <Textarea
                id="description"
                placeholder="Brief description of the server's purpose"
                value={serverData.description}
                onChange={(e) => setServerData({ ...serverData, description: e.target.value })}
                rows={3}
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="transportType">Transport Type</Label>
              <Select
                value={serverData.transportType}
                onValueChange={(value) => setServerData({ ...serverData, transportType: value })}
              >
                <SelectTrigger id="transportType">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="STREAMABLE_HTTP">STREAMABLE_HTTP</SelectItem>
                  <SelectItem value="SSE">SSE</SelectItem>
                  <SelectItem value="STDIO">STDIO</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="grid gap-2">
              <Label htmlFor="endpoint">Endpoint</Label>
              <Input
                id="endpoint"
                placeholder="http://localhost:8000"
                value={serverData.endpoint}
                onChange={(e) => setServerData({ ...serverData, endpoint: e.target.value })}
                required
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="version">Version</Label>
              <Input
                id="version"
                placeholder="1.0.0"
                value={serverData.version}
                onChange={(e) => setServerData({ ...serverData, version: e.target.value })}
              />
            </div>
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => setOpen(false)} disabled={loading}>
              Cancel
            </Button>
            <Button type="submit" disabled={loading}>
              {loading ? "Adding..." : "Add Server"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
