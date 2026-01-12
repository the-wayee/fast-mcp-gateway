import { Header } from "@/components/header"
import { InspectorInterface } from "@/components/inspector-interface"
import { Button } from "@/components/ui/button"
import { ArrowLeft } from "lucide-react"
import Link from "next/link"

export default function InspectorPage({ params }: { params: { id: string } }) {
  return (
    <div className="min-h-screen bg-background">
      <Header />
      <main className="mx-auto max-w-[1400px] px-6 py-8">
        <div className="mb-6">
          <Link href={`/server/${params.id}`}>
            <Button variant="ghost" size="sm" className="mb-4">
              <ArrowLeft className="w-4 h-4 mr-2" />
              Back to Server
            </Button>
          </Link>
          <h1 className="text-3xl font-semibold tracking-tight text-balance mb-2">Debug Inspector</h1>
          <p className="text-muted-foreground text-pretty">Test and debug MCP server methods and view responses</p>
        </div>

        <InspectorInterface serverId={params.id} />
      </main>
    </div>
  )
}
