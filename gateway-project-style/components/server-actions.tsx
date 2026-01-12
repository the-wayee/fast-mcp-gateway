"use client"

import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { RefreshCw, PlayCircle, StopCircle, Trash2, Code, FileText } from "lucide-react"
import Link from "next/link"

interface ServerActionsProps {
  serverId: string
}

export function ServerActions({ serverId }: ServerActionsProps) {
  return (
    <Card className="p-6">
      <h2 className="text-lg font-semibold mb-4">Actions</h2>
      <div className="space-y-2">
        <Button variant="outline" className="w-full justify-start bg-transparent" size="sm">
          <RefreshCw className="w-4 h-4 mr-2" />
          Restart Server
        </Button>
        <Button variant="outline" className="w-full justify-start bg-transparent" size="sm">
          <PlayCircle className="w-4 h-4 mr-2" />
          Start Server
        </Button>
        <Button variant="outline" className="w-full justify-start bg-transparent" size="sm">
          <StopCircle className="w-4 h-4 mr-2" />
          Stop Server
        </Button>
        <Link href={`/inspector/${serverId}`}>
          <Button variant="outline" className="w-full justify-start bg-transparent" size="sm">
            <Code className="w-4 h-4 mr-2" />
            Open Inspector
          </Button>
        </Link>
        <Button variant="outline" className="w-full justify-start bg-transparent" size="sm">
          <FileText className="w-4 h-4 mr-2" />
          View Logs
        </Button>
        <Button variant="destructive" className="w-full justify-start mt-4" size="sm">
          <Trash2 className="w-4 h-4 mr-2" />
          Remove Server
        </Button>
      </div>
    </Card>
  )
}
