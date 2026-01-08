"use client";


import { MOCK_SERVERS } from '@/lib/mock-data';
import { Server } from '@/lib/types';
import { useState, useEffect } from 'react';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';

import { ArrowLeft, Clock, Activity, Terminal, FileText, Code2, Copy } from 'lucide-react';
import Link from 'next/link';
import { useParams } from 'next/navigation';
import { cn } from '@/lib/utils';

// Simple Tabs implementation if we don't have the component ready, 
// but let's assume I'll build the component or handle standard tabs here.
// I'll create a simple tabs UI here to avoid complex component dependencies for now.

export default function ServerDetailPage() {
    const params = useParams();
    const id = params.id as string; // in app router [id] maps to name often
    // Mock fetch
    const [server, setServer] = useState<Server | null>(null);
    const [loading, setLoading] = useState(true);
    const [activeTab, setActiveTab] = useState("tools");

    useEffect(() => {
        // Simulate finding server
        const found = MOCK_SERVERS.find(s => s.name === decodeURIComponent(id));
        if (found) {
            setServer(found);
        }
        setLoading(false);
    }, [id]);

    if (loading) return <div className="p-10">Loading...</div>;
    if (!server) return <div className="p-10">Server not found</div>;

    const statusVariant =
        server.server_status === 'active' ? 'success' :
            server.server_status === 'inactive' ? 'secondary' : 'destructive';

    return (
        <div className="flex flex-col gap-6 max-w-5xl mx-auto animate-in fade-in-50 duration-500">
            <Link href="/" className="flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground transition-colors w-fit">
                <ArrowLeft className="h-4 w-4" />
                Back to Dashboard
            </Link>

            <div className="flex flex-col gap-6 md:flex-row md:items-start md:justify-between border-b pb-6">
                <div className="space-y-1">
                    <div className="flex items-center gap-3">
                        <h1 className="text-3xl font-bold tracking-tight">{server.name}</h1>
                        <Badge variant={statusVariant} className="capitalize">
                            {server.server_status}
                        </Badge>
                    </div>
                    <div className="flex items-center gap-4 text-sm text-muted-foreground font-mono mt-2">
                        <span className="bg-muted px-2 py-0.5 rounded">{server.transport_type}</span>
                        {server.endpoint && <span className="opacity-60">{server.endpoint}</span>}
                    </div>
                </div>

                <div className="flex gap-3">
                    <Card className="flex-row items-center gap-4 p-3 shadow-none bg-muted/30 border-none">
                        <div className="p-2 bg-background rounded-md shadow-sm">
                            <Clock className="h-4 w-4 text-muted-foreground" />
                        </div>
                        <div>
                            <p className="text-xs text-muted-foreground font-medium uppercase">Uptime</p>
                            <p className="text-sm font-mono font-bold">{server.uptime || '-'}</p>
                        </div>
                    </Card>
                    <Card className="flex-row items-center gap-4 p-3 shadow-none bg-muted/30 border-none">
                        <div className="p-2 bg-background rounded-md shadow-sm">
                            <Activity className="h-4 w-4 text-muted-foreground" />
                        </div>
                        <div>
                            <p className="text-xs text-muted-foreground font-medium uppercase">Latency</p>
                            <p className="text-sm font-mono font-bold">{server.latency || '-'}</p>
                        </div>
                    </Card>
                </div>
            </div>

            <div className="space-y-6">
                <div className="flex border-b">
                    <button
                        onClick={() => setActiveTab('tools')}
                        className={cn(
                            "flex items-center gap-2 border-b-2 px-4 py-3 text-sm font-medium transition-colors hover:text-foreground",
                            activeTab === 'tools'
                                ? "border-primary text-foreground"
                                : "border-transparent text-muted-foreground"
                        )}
                    >
                        Tools <Badge variant="secondary" className="ml-1 text-[10px] h-5 px-1">{server.tools.length}</Badge>
                    </button>
                    <button
                        onClick={() => setActiveTab('resources')}
                        className={cn(
                            "flex items-center gap-2 border-b-2 px-4 py-3 text-sm font-medium transition-colors hover:text-foreground",
                            activeTab === 'resources'
                                ? "border-primary text-foreground"
                                : "border-transparent text-muted-foreground"
                        )}
                    >
                        Resources <Badge variant="secondary" className="ml-1 text-[10px] h-5 px-1">{server.resources.length}</Badge>
                    </button>
                    <button
                        onClick={() => setActiveTab('prompts')}
                        className={cn(
                            "flex items-center gap-2 border-b-2 px-4 py-3 text-sm font-medium transition-colors hover:text-foreground",
                            activeTab === 'prompts'
                                ? "border-primary text-foreground"
                                : "border-transparent text-muted-foreground"
                        )}
                    >
                        Prompts <Badge variant="secondary" className="ml-1 text-[10px] h-5 px-1">{server.prompts?.length || 0}</Badge>
                    </button>
                </div>

                <div className="min-h-[400px]">
                    {activeTab === 'tools' && (
                        <div className="grid gap-4">
                            {server.tools.length === 0 && <EmptyState label="tools" />}
                            {server.tools.map(tool => (
                                <Card key={tool.name} className="overflow-hidden">
                                    <CardHeader className="bg-muted/30 py-4">
                                        <div className="flex items-center justify-between">
                                            <div className="flex items-center gap-3">
                                                <Code2 className="h-4 w-4 text-primary" />
                                                <span className="font-mono font-semibold text-sm">{tool.name}</span>
                                            </div>
                                            <Button size="sm" variant="ghost" className="h-7 text-xs">
                                                Try It
                                            </Button>
                                        </div>
                                    </CardHeader>
                                    <CardContent className="pt-4 space-y-4">
                                        <p className="text-sm text-muted-foreground">{tool.description}</p>
                                        <div className="bg-muted/50 rounded-lg p-3 border">
                                            <div className="text-xs text-muted-foreground mb-2 font-semibold">Input Schema</div>
                                            <pre className="text-xs font-mono overflow-auto text-foreground/80">
                                                {JSON.stringify(tool.inputSchema, null, 2)}
                                            </pre>
                                        </div>
                                    </CardContent>
                                </Card>
                            ))}
                        </div>
                    )}

                    {activeTab === 'resources' && (
                        <div className="grid gap-4">
                            {server.resources.length === 0 && <EmptyState label="resources" />}
                            {server.resources.map(resource => (
                                <Card key={resource.uri} className="flex items-center justify-between p-4">
                                    <div className="flex items-center gap-4 overflow-hidden">
                                        <div className="p-2 bg-blue-500/10 text-blue-500 rounded-md shrink-0">
                                            <FileText className="h-5 w-5" />
                                        </div>
                                        <div className="min-w-0">
                                            <p className="font-medium text-sm truncate">{resource.name}</p>
                                            <p className="text-xs text-muted-foreground font-mono truncate">{resource.uri}</p>
                                        </div>
                                    </div>
                                    {resource.mimeType && (
                                        <Badge variant="outline" className="shrink-0">{resource.mimeType}</Badge>
                                    )}
                                </Card>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

function EmptyState({ label }: { label: string }) {
    return (
        <div className="flex flex-col items-center justify-center py-16 border rounded-xl border-dashed bg-muted/20">
            <Terminal className="h-10 w-10 text-muted-foreground/30 mb-4" />
            <p className="text-muted-foreground font-medium">No {label} available</p>
        </div>
    );
}
