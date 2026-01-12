"use client"

import { Card } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Copy, Check } from "lucide-react"
import { useState } from "react"

interface ResponseViewerProps {
  response: string | null
  isLoading: boolean
}

export function ResponseViewer({ response, isLoading }: ResponseViewerProps) {
  const [copied, setCopied] = useState(false)

  const handleCopy = () => {
    if (response) {
      navigator.clipboard.writeText(response)
      setCopied(true)
      setTimeout(() => setCopied(false), 2000)
    }
  }

  return (
    <Card className="p-6">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <h2 className="text-lg font-semibold">Response</h2>
          {response && (
            <Badge variant="outline" className="bg-green-500/10 text-green-500 border-green-500/20">
              200 OK
            </Badge>
          )}
        </div>
        {response && (
          <Button variant="outline" size="sm" onClick={handleCopy}>
            {copied ? <Check className="w-4 h-4 mr-2" /> : <Copy className="w-4 h-4 mr-2" />}
            {copied ? "Copied" : "Copy"}
          </Button>
        )}
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center h-[300px] bg-muted/30 rounded-md">
          <div className="text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto mb-2" />
            <p className="text-sm text-muted-foreground">Executing request...</p>
          </div>
        </div>
      ) : response ? (
        <Tabs defaultValue="formatted" className="w-full">
          <TabsList>
            <TabsTrigger value="formatted">Formatted</TabsTrigger>
            <TabsTrigger value="raw">Raw</TabsTrigger>
            <TabsTrigger value="headers">Headers</TabsTrigger>
          </TabsList>
          <TabsContent value="formatted" className="mt-4">
            <pre className="font-mono text-sm bg-muted/30 p-4 rounded-md overflow-auto max-h-[500px]">{response}</pre>
          </TabsContent>
          <TabsContent value="raw" className="mt-4">
            <pre className="font-mono text-sm bg-muted/30 p-4 rounded-md overflow-auto max-h-[500px] whitespace-pre-wrap break-all">
              {response}
            </pre>
          </TabsContent>
          <TabsContent value="headers" className="mt-4">
            <pre className="font-mono text-sm bg-muted/30 p-4 rounded-md overflow-auto max-h-[500px]">
              {JSON.stringify(
                {
                  "content-type": "application/json",
                  "x-response-time": "24ms",
                  date: new Date().toUTCString(),
                },
                null,
                2,
              )}
            </pre>
          </TabsContent>
        </Tabs>
      ) : (
        <div className="flex items-center justify-center h-[300px] bg-muted/30 rounded-md border-2 border-dashed border-border">
          <p className="text-sm text-muted-foreground">Execute a request to see the response</p>
        </div>
      )}
    </Card>
  )
}
