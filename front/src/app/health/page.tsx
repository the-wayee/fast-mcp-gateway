"use client";

import { Activity, CheckCircle, AlertTriangle, RefreshCw, Server, Zap, Cpu } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

export default function HealthPage() {
    return (
        <div className="flex flex-col gap-8 max-w-5xl mx-auto">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">System Health</h1>
                    <p className="text-muted-foreground mt-1">Real-time monitoring and diagnostics</p>
                </div>
                <Button variant="outline">
                    <RefreshCw className="mr-2 h-4 w-4" /> Refresh
                </Button>
            </div>

            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
                <HealthCard title="System Status" value="Healthy" icon={Activity} status="good" />
                <HealthCard title="Uptime" value="14d 2h 15m" icon={ClockIcon} status="neutral" />
                <HealthCard title="Active Nodes" value="3/3" icon={Server} status="good" />
                <HealthCard title="Response Time" value="45ms" icon={Zap} status="good" />
            </div>

            <div className="grid gap-6 md:grid-cols-2">
                <Card>
                    <CardHeader>
                        <CardTitle>Recent Alerts</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-4">
                            <AlertItem
                                type="warning"
                                title="High Latency on 'github-integration'"
                                time="2 hours ago"
                            />
                            <AlertItem
                                type="success"
                                title="System Backup Completed"
                                time="5 hours ago"
                            />
                            <AlertItem
                                type="warning"
                                title="Connection failover triggered"
                                time="1 day ago"
                            />
                        </div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader>
                        <CardTitle>Resource Usage</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-6">
                            <ResourceBar label="CPU Load" value={12} />
                            <ResourceBar label="Memory Usage" value={45} />
                            <ResourceBar label="Disk Space" value={28} />
                        </div>
                    </CardContent>
                </Card>
            </div>
        </div>
    );
}

function ClockIcon(props: any) {
    return <svg {...props} xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10" /><polyline points="12 6 12 12 16 14" /></svg>
}

function HealthCard({ title, value, icon: Icon, status }: any) {
    return (
        <Card>
            <CardContent className="p-6">
                <div className="flex items-center justify-between space-y-0 pb-2 mb-2">
                    <span className="text-sm font-medium text-muted-foreground group-hover:text-primary transition-colors">{title}</span>
                    <Icon className={`h-4 w-4 ${status === 'good' ? 'text-emerald-500' : 'text-blue-500'}`} />
                </div>
                <div className="text-2xl font-bold">{value}</div>
            </CardContent>
        </Card>
    )
}

function AlertItem({ type, title, time }: any) {
    return (
        <div className="flex gap-4 items-start p-3 rounded-lg hover:bg-muted/50 transition-colors">
            <div className={`mt-0.5 p-1.5 rounded-full ${type === 'warning' ? 'bg-amber-500/10 text-amber-500' : 'bg-emerald-500/10 text-emerald-500'}`}>
                {type === 'warning' ? <AlertTriangle className="h-4 w-4" /> : <CheckCircle className="h-4 w-4" />}
            </div>
            <div>
                <p className="text-sm font-medium leading-none">{title}</p>
                <p className="text-xs text-muted-foreground mt-1">{time}</p>
            </div>
        </div>
    )
}

function ResourceBar({ label, value }: any) {
    return (
        <div className="space-y-2">
            <div className="flex items-center justify-between text-sm">
                <span>{label}</span>
                <span className="font-mono text-muted-foreground">{value}%</span>
            </div>
            <div className="h-2 w-full bg-secondary rounded-full overflow-hidden">
                <div className="h-full bg-primary transition-all duration-500" style={{ width: `${value}%` }} />
            </div>
        </div>
    )
}
