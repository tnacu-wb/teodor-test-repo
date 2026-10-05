'use client';

import { ViewId } from '@/lib/types';
import {
  LayoutDashboard, TrendingUp, LineChart, CalendarDays,
  Bug, Users, Briefcase, ShieldAlert, Database, CheckCircle2, X
} from 'lucide-react';

interface SidebarProps {
  activeView: ViewId;
  onNavigate: (view: ViewId) => void;
  onClose: () => void;
}

const NAV_GROUPS = [
  {
    label: 'Leadership',
    items: [
      { id: 'executive' as ViewId, label: 'Executive Overview', icon: LayoutDashboard },
      { id: 'delivery' as ViewId, label: 'Delivery Performance', icon: TrendingUp },
      { id: 'trend' as ViewId, label: 'Trend Analysis', icon: LineChart },
    ],
  },
  {
    label: 'Agile Execution',
    items: [
      { id: 'sprint' as ViewId, label: 'Sprint Analytics', icon: CalendarDays },
      { id: 'jira' as ViewId, label: 'Jira Insights', icon: Bug },
      { id: 'team' as ViewId, label: 'Team Performance', icon: Users },
      { id: 'resource' as ViewId, label: 'Resource & Workload', icon: Briefcase },
    ],
  },
  {
    label: 'Governance',
    items: [
      { id: 'risk' as ViewId, label: 'Risk & Improvement', icon: ShieldAlert },
      { id: 'register' as ViewId, label: 'Data Register', icon: Database },
      { id: 'quality' as ViewId, label: 'Data Quality', icon: CheckCircle2 },
    ],
  },
];

export function Sidebar({ activeView, onNavigate, onClose }: SidebarProps) {
  return (
    <aside className="w-60 bg-panel border-r border-ink-3/20 flex flex-col h-full shrink-0">
      <div className="flex items-center justify-between px-4 py-4 border-b border-ink-3/10">
        <span className="text-sm font-semibold text-brand">Delivery Intelligence</span>
        <button onClick={onClose} className="p-1 rounded hover:bg-bg text-ink-3 lg:hidden">
          <X size={16} />
        </button>
      </div>
      <nav className="flex-1 overflow-y-auto py-3 scrollbar-thin">
        {NAV_GROUPS.map((group) => (
          <div key={group.label} className="mb-4">
            <p className="px-4 mb-1 text-[10px] font-semibold uppercase tracking-wider text-ink-3">
              {group.label}
            </p>
            {group.items.map((item) => {
              const Icon = item.icon;
              const isActive = activeView === item.id;
              return (
                <button
                  key={item.id}
                  onClick={() => onNavigate(item.id)}
                  className={`w-full flex items-center gap-2.5 px-4 py-2 text-sm transition-colors ${
                    isActive
                      ? 'bg-brand/10 text-brand font-medium border-r-2 border-brand'
                      : 'text-ink-2 hover:bg-bg hover:text-ink'
                  }`}
                >
                  <Icon size={16} className={isActive ? 'text-brand' : 'text-ink-3'} />
                  {item.label}
                </button>
              );
            })}
          </div>
        ))}
      </nav>
      <div className="px-4 py-3 border-t border-ink-3/10 text-[10px] text-ink-3">
        PI 25.3 · v8.1
      </div>
    </aside>
  );
}
