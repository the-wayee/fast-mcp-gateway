import Link from 'next/link';
import { Server } from '@/lib/types';
import { ArrowRight, Terminal, Globe, Server as ServerIcon, Clock, Activity } from 'lucide-react';
import { cn } from '@/lib/utils';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';

interface ServerCardProps {
    server: Server;
}

export function ServerCard({ server }: ServerCardProps) {
    const Icon = server.transport_type === 'stdio' ? Terminal :
        server.transport_type === 'sse' ? Globe : ServerIcon;

    const statusVariant =
        server.server_status === 'active' ? 'success' :
            server.server_status === 'inactive' ? 'secondary' : 'destructive';

    return (
        <Card className="group overflow-hidden transition-all hover:shadow-md hover:border-primary/20">
            <CardHeader className="pb-4">
                <div className="flex items-start justify-between">
                    <div className="flex items-center gap-3">
                        <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-muted group-hover:bg-primary/5 group-hover:text-primary transition-colors border">
                            <Icon className="h-5 w-5" />
                        </div>
                        <div>
                            <CardTitle className="text-base">{server.name}</CardTitle>
                            <CardDescription className="font-mono text-xs mt-1">
                                {server.transport_type}
                            </CardDescription>
                        </div>
                    </div>
                    <Badge variant={statusVariant} className="capitalize">
                        {server.server_status}
                    </Badge>
                </div>
            </CardHeader>

            <CardContent className="pb-2">
                <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-1">
                        <p className="text-xs font-medium text-muted-foreground">Tools</p>
                        <p className="text-2xl font-bold tracking-tight">{server.tools.length}</p>
                    </div>
                    <div className="space-y-1">
                        <p className="text-xs font-medium text-muted-foreground">Resources</p>
                        <p className="text-2xl font-bold tracking-tight">{server.resources.length}</p>
                    </div>
                </div>
            </CardContent>

            <CardFooter className="bg-muted/30 p-4 flex items-center justify-between text-xs mt-2 border-t">
                <div className="flex items-center gap-3 text-muted-foreground">
                    {server.latency && (
                        <div className="flex items-center gap-1">
                            <Activity className="h-3 w-3" /> {server.latency}
                        </div>
                    )}
                    {server.uptime && (
                        <div className="flex items-center gap-1">
                            <Clock className="h-3 w-3" /> {server.uptime}
                        </div>
                    )}
                </div>
                <Link
                    href={`/server/${server.name}`}
                    className="flex items-center font-medium text-primary opacity-0 -translate-x-2 transition-all group-hover:opacity-100 group-hover:translate-x-0"
                >
                    Manage <ArrowRight className="ml-1 h-3 w-3" />
                </Link>
            </CardFooter>
        </Card>
    );
}
