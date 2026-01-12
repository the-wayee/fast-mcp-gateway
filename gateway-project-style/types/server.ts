export interface Server {
    id: string
    name: string
    description?: string
    transportType: "STDIO" | "SSE" | "STREAMABLE_HTTP"
    endpoint: string
    status: "ACTIVE" | "INACTIVE" | "CONNECTING" | "DISCONNECTED"

    // UI Display fields (Mocked for now)
    requests?: number
    latency?: number
    uptime?: number
}
