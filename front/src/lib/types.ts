export interface Tool {
    name: string;
    description: string;
    inputSchema: Record<string, any>;
}

export interface Resource {
    uri: string;
    name: string;
    mimeType?: string;
    description?: string;
}

export interface Prompt {
    name: string;
    description?: string;
    arguments?: Array<{
        name: string;
        description?: string;
        required?: boolean;
    }>;
}

export interface Server {
    name: string;
    transport_type: 'stdio' | 'sse' | 'streamable-http';
    endpoint?: string;
    server_status: 'active' | 'inactive' | 'unhealthy';
    tools: Tool[];
    resources: Resource[];
    prompts: Prompt[];
    lastHeartbeat?: string;
    uptime?: string;
    latency?: string;
}
