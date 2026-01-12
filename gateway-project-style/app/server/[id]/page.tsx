import { ServerDetails } from "@/components/server-details"
import { ServerActions } from "@/components/server-actions"
import { ServerMetrics } from "@/components/server-metrics"
import { ServerLogs } from "@/components/server-logs"
import { Header } from "@/components/header"
import { Button } from "@/components/ui/button"
import { ArrowLeft } from "lucide-react"
import Link from "next/link"

export default function ServerPage({ params }: { params: { id: string } }) {
  return (
    <div className="min-h-screen bg-background">
      <Header />
      <main className="mx-auto max-w-[1400px] px-6 py-8">
        <div className="mb-6">
          <Link href="/">
            <Button variant="ghost" size="sm" className="mb-4">
              <ArrowLeft className="w-4 h-4 mr-2" />
              Back to Dashboard
            </Button>
          </Link>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 space-y-6">
            <ServerDetails serverId={params.id} />
            <ServerMetrics serverId={params.id} />
            <ServerLogs serverId={params.id} />
          </div>
          <div>
            <ServerActions serverId={params.id} />
          </div>
        </div>
      </main>
    </div>
  )
}
