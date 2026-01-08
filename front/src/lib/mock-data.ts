import { Server } from './types';

export const MOCK_SERVERS: Server[] = [
    {
        name: 'filesystem-server',
        transport_type: 'stdio',
        server_status: 'active',
        endpoint: 'n/a',
        lastHeartbeat: '2s ago',
        uptime: '3d 2h',
        latency: '12ms',
        tools: [
            {
                name: 'read_file',
                description: 'Read file from allowed directories',
                inputSchema: { type: 'object', properties: { path: { type: 'string' } } }
            },
            {
                name: 'list_directory',
                description: 'List files in directory',
                inputSchema: { type: 'object', properties: { path: { type: 'string' } } }
            }
        ],
        resources: [
            { uri: 'file:///logs/access.log', name: 'Access Logs', mimeType: 'text/plain' },
            { uri: 'file:///config/app.json', name: 'App Config', mimeType: 'application/json' }
        ],
        prompts: []
    },
    {
        name: 'database-inspector',
        transport_type: 'streamable-http',
        endpoint: 'http://localhost:8080/mcp',
        server_status: 'active',
        lastHeartbeat: '45s ago',
        uptime: '12h 30m',
        latency: '85ms',
        tools: [
            {
                name: 'query_db',
                description: 'Execute read-only SQL query',
                inputSchema: { type: 'object', properties: { query: { type: 'string' } } }
            }
        ],
        resources: [],
        prompts: [
            {
                name: 'analyze-schema',
                description: 'Analyze database schema and suggest improvements',
                arguments: [
                    { name: 'table', description: 'Table name to analyze', required: true }
                ]
            }
        ]
    },
    {
        name: 'weather-service',
        transport_type: 'sse',
        endpoint: 'http://api.weather.com/mcp',
        server_status: 'inactive',
        lastHeartbeat: '15m ago',
        uptime: '0m',
        latency: '-',
        tools: [],
        resources: [],
        prompts: []
    },
    {
        name: 'github-integration',
        transport_type: 'stdio',
        server_status: 'unhealthy',
        endpoint: 'n/a',
        lastHeartbeat: '5s ago',
        uptime: '5d',
        latency: '1200ms',
        tools: [
            { name: 'create_issue', description: 'Create a GitHub issue', inputSchema: {} }
        ],
        resources: [],
        prompts: []
    }
];
