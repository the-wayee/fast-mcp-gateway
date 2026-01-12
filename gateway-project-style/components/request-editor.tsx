"use client"

import { Textarea } from "@/components/ui/textarea"

interface RequestEditorProps {
  value: string
  onChange: (value: string) => void
}

export function RequestEditor({ value, onChange }: RequestEditorProps) {
  return (
    <div className="relative">
      <Textarea
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="font-mono text-sm min-h-[300px] resize-none bg-muted/30"
        placeholder="Enter request body..."
      />
      <div className="absolute bottom-2 right-2 text-xs text-muted-foreground">{value.split("\n").length} lines</div>
    </div>
  )
}
