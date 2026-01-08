"use client";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Save } from "lucide-react";

export default function SettingsPage() {
    return (
        <div className="max-w-3xl mx-auto space-y-8">
            <div>
                <h1 className="text-3xl font-bold tracking-tight">Settings</h1>
                <p className="text-muted-foreground mt-1">Manage your gateway configuration and preferences</p>
            </div>

            <div className="grid gap-6">
                <Card>
                    <CardHeader>
                        <CardTitle>General Configuration</CardTitle>
                        <CardDescription>Basic settings for your MCP Gateway instance.</CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="grid gap-2">
                            <label className="text-sm font-medium leading-none">Gateway Name</label>
                            <Input defaultValue="Production Gateway" />
                        </div>
                        <div className="grid gap-2">
                            <label className="text-sm font-medium leading-none">Environment</label>
                            <select className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring">
                                <option>Production</option>
                                <option>Staging</option>
                                <option>Development</option>
                            </select>
                        </div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader>
                        <CardTitle>Network</CardTitle>
                        <CardDescription>Configure ports and connection settings.</CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="grid grid-cols-2 gap-4">
                            <div className="grid gap-2">
                                <label className="text-sm font-medium leading-none">HTTP Port</label>
                                <Input type="number" defaultValue="8080" />
                            </div>
                            <div className="grid gap-2">
                                <label className="text-sm font-medium leading-none">SSE Port</label>
                                <Input type="number" defaultValue="8081" />
                            </div>
                        </div>
                        <div className="flex items-center gap-2 mt-2">
                            <input type="checkbox" id="ssl" className="rounded border-gray-300 h-4 w-4" defaultChecked />
                            <label htmlFor="ssl" className="text-sm font-medium leading-none">Enable SSL/TLS</label>
                        </div>
                    </CardContent>
                    <CardFooter className="bg-muted/40 border-t px-6 py-4">
                        <p className="text-xs text-muted-foreground">Requires restart to apply changes</p>
                    </CardFooter>
                </Card>

                <div className="flex justify-end">
                    <Button>
                        <Save className="mr-2 h-4 w-4" /> Save Changes
                    </Button>
                </div>
            </div>
        </div>
    );
}
