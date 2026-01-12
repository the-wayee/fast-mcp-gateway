"use client"

import { useState } from "react"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { MethodSelector } from "@/components/method-selector"
import { RequestEditor } from "@/components/request-editor"
import { ResponseViewer } from "@/components/response-viewer"
import { Play, Save, Trash2 } from "lucide-react"

interface InspectorInterfaceProps {
  serverId: string
}

export function InspectorInterface({ serverId }: InspectorInterfaceProps) {
  const [selectedMethod, setSelectedMethod] = useState("tools/list")
  const [requestBody, setRequestBody] = useState("{}")
  const [response, setResponse] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(false)

  const handleExecute = async () => {
    setIsLoading(true)
    // Simulate API call
    setTimeout(() => {
      setResponse(
        JSON.stringify(
          {
            jsonrpc: "2.0",
            id: 1,
            result: {
              tools: [
                {
                  name: "read_file",
                  description: "Read contents of a file",
                  inputSchema: {
                    type: "object",
                    properties: {
                      path: { type: "string" },
                    },
                  },
                },
                {
                  name: "write_file",
                  description: "Write contents to a file",
                  inputSchema: {
                    type: "object",
                    properties: {
                      path: { type: "string" },
                      content: { type: "string" },
                    },
                  },
                },
              ],
            },
          },
          null,
          2,
        ),
      )
      setIsLoading(false)
    }, 800)
  }

  return (
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div className="lg:col-span-1">
        <MethodSelector selectedMethod={selectedMethod} onMethodChange={setSelectedMethod} />
      </div>

      <div className="lg:col-span-2 space-y-4">
        <Card className="p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold">Request</h2>
            <div className="flex items-center gap-2">
              <Button variant="outline" size="sm">
                <Save className="w-4 h-4 mr-2" />
                Save
              </Button>
              <Button variant="outline" size="sm">
                <Trash2 className="w-4 h-4 mr-2" />
                Clear
              </Button>
              <Button size="sm" onClick={handleExecute} disabled={isLoading}>
                <Play className="w-4 h-4 mr-2" />
                {isLoading ? "Executing..." : "Execute"}
              </Button>
            </div>
          </div>

          <Tabs defaultValue="body" className="w-full">
            <TabsList className="grid w-full grid-cols-3">
              <TabsTrigger value="body">Body</TabsTrigger>
              <TabsTrigger value="headers">Headers</TabsTrigger>
              <TabsTrigger value="params">Params</TabsTrigger>
            </TabsList>
            <TabsContent value="body" className="mt-4">
              <RequestEditor value={requestBody} onChange={setRequestBody} />
            </TabsContent>
            <TabsContent value="headers" className="mt-4">
              <RequestEditor value='{\n  "Content-Type": "application/json"\n}' onChange={() => {}} />
            </TabsContent>
            <TabsContent value="params" className="mt-4">
              <RequestEditor value="{}" onChange={() => {}} />
            </TabsContent>
          </Tabs>
        </Card>

        <ResponseViewer response={response} isLoading={isLoading} />
      </div>
    </div>
  )
}
