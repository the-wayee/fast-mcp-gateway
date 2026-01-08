"use client";

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { cn } from '@/lib/utils';
import { LayoutDashboard, Server, Activity, Settings, HardDrive, Zap } from 'lucide-react';

const items = [
    {
        title: "Dashboard",
        url: "/",
        icon: LayoutDashboard,
    },
    {
        title: "Servers",
        url: "/servers",
        icon: Server,
    },
    {
        title: "Health",
        url: "/health",
        icon: Activity,
    },
    {
        title: "Settings",
        url: "/settings",
        icon: Settings,
    },
];

export function Sidebar() {
    const pathname = usePathname();

    return (
        <aside className="hidden border-r bg-muted/20 md:block w-[240px] lg:w-[260px] h-screen sticky top-0 backdrop-blur-xl">
            <div className="flex h-full max-h-screen flex-col gap-2">
                <div className="flex h-16 items-center border-b px-6">
                    <Link href="/" className="flex items-center gap-2 font-bold tracking-tight text-lg">
                        <div className="flex items-center justify-center p-1.5 rounded-lg bg-primary text-primary-foreground">
                            <Zap className="h-4 w-4" />
                        </div>
                        <span>MCP Gateway</span>
                    </Link>
                </div>
                <div className="flex-1 overflow-auto py-4">
                    <nav className="grid items-start px-4 text-sm font-medium space-y-1">
                        {items.map((item) => {
                            const isActive = item.url === '/'
                                ? pathname === '/'
                                : pathname.startsWith(item.url);

                            return (
                                <Link
                                    key={item.url}
                                    href={item.url}
                                    className={cn(
                                        "flex items-center gap-3 rounded-lg px-3 py-2.5 transition-all",
                                        isActive
                                            ? "bg-primary text-primary-foreground shadow-sm"
                                            : "text-muted-foreground hover:bg-muted hover:text-foreground"
                                    )}
                                >
                                    <item.icon className="h-4 w-4" />
                                    {item.title}
                                </Link>
                            );
                        })}
                    </nav>
                </div>
                <div className="mt-auto p-4 border-t">
                    <div className="flex items-center gap-3 px-2 py-2">
                        <div className="h-8 w-8 rounded-full bg-slate-200 dark:bg-slate-800 flex items-center justify-center text-xs font-bold">
                            AG
                        </div>
                        <div className="flex flex-col">
                            <span className="text-sm font-medium">Admin User</span>
                            <span className="text-xs text-muted-foreground">Gateway Admin</span>
                        </div>
                    </div>
                </div>
            </div>
        </aside>
    );
}
