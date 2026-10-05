'use client';

import { useEffect, useRef } from 'react';
import { X } from 'lucide-react';

interface DrawerProps {
  open: boolean;
  onClose: () => void;
  title: string;
  children: React.ReactNode;
}

export function Drawer({ open, onClose, title, children }: DrawerProps) {
  const drawerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!open) return;
    function handleKey(e: KeyboardEvent) {
      if (e.key === 'Escape') onClose();
    }
    window.addEventListener('keydown', handleKey);
    return () => window.removeEventListener('keydown', handleKey);
  }, [open, onClose]);

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex justify-end">
      <div className="absolute inset-0 bg-ink/30" onClick={onClose} />
      <div
        ref={drawerRef}
        className="relative w-full max-w-md bg-panel shadow-lg flex flex-col animate-in slide-in-from-right"
      >
        <div className="flex items-center justify-between px-5 py-4 border-b border-ink-3/20">
          <h2 className="text-sm font-semibold text-ink">{title}</h2>
          <button onClick={onClose} className="p-1 rounded hover:bg-bg text-ink-3">
            <X size={18} />
          </button>
        </div>
        <div className="flex-1 overflow-y-auto p-5 scrollbar-thin">{children}</div>
      </div>
    </div>
  );
}
