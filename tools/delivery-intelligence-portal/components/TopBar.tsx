'use client';

import { ChevronLeft, ChevronRight, Maximize2, Minimize2, Menu, RotateCcw, Upload } from 'lucide-react';
import { FilterState, Team, ViewId } from '@/lib/types';
import { FilterChips } from './FilterChips';

const VIEW_LABELS: Record<ViewId, string> = {
  executive: 'Executive Overview',
  delivery: 'Delivery Performance',
  trend: 'Trend Analysis',
  sprint: 'Sprint Analytics',
  jira: 'Jira Insights',
  team: 'Team Performance',
  resource: 'Resource & Workload',
  risk: 'Risk & Improvement',
  register: 'Data Register',
  quality: 'Data Quality',
};

interface TopBarProps {
  activeView: ViewId;
  currentIndex: number;
  totalViews: number;
  onNext: () => void;
  onPrev: () => void;
  filters: FilterState;
  setFilters: (f: FilterState) => void;
  isPresenting: boolean;
  setIsPresenting: (v: boolean) => void;
  sidebarOpen: boolean;
  setSidebarOpen: (v: boolean) => void;
  teams: Team[];
  onOpenImport?: () => void;
}

export function TopBar({
  activeView, currentIndex, totalViews, onNext, onPrev,
  filters, setFilters, isPresenting, setIsPresenting, sidebarOpen, setSidebarOpen, teams, onOpenImport,
}: TopBarProps) {
  const sprints = Array.from(new Set(teams.length > 0 ? ['26.1.1', '26.1.2', '26.1.3', '26.1.4', '26.1.5', '26.1.6', '26.2.1', '26.2.2'] : []));
  const vendors = Array.from(new Set(teams.map((t) => t.vendor)));

  const resetFilters = () => setFilters({ team: null, sprint: null, vendor: null });

  return (
    <header className="sticky top-0 z-30 bg-panel border-b border-ink-3/20 px-4 py-2.5 flex items-center gap-3 shrink-0">
      {!sidebarOpen && (
        <button onClick={() => setSidebarOpen(true)} className="p-1.5 rounded hover:bg-bg text-ink-3">
          <Menu size={18} />
        </button>
      )}

      {/* Breadcrumb */}
      <div className="flex items-center gap-2 mr-auto">
        <span className="text-sm font-semibold text-ink">{VIEW_LABELS[activeView]}</span>
      </div>

      {/* Filters */}
      <div className="hidden md:flex items-center gap-2">
        <select
          value={filters.team ?? ''}
          onChange={(e) => setFilters({ ...filters, team: e.target.value || null })}
          className="text-xs border border-ink-3/20 rounded-md px-2 py-1.5 bg-panel text-ink"
        >
          <option value="">All Teams</option>
          {teams.map((t) => (
            <option key={t.id} value={t.id}>{t.name}</option>
          ))}
        </select>
        <select
          value={filters.sprint ?? ''}
          onChange={(e) => setFilters({ ...filters, sprint: e.target.value || null })}
          className="text-xs border border-ink-3/20 rounded-md px-2 py-1.5 bg-panel text-ink"
        >
          <option value="">All Sprints</option>
          {sprints.map((s) => (
            <option key={s} value={s}>{s}</option>
          ))}
        </select>
        <select
          value={filters.vendor ?? ''}
          onChange={(e) => setFilters({ ...filters, vendor: e.target.value || null })}
          className="text-xs border border-ink-3/20 rounded-md px-2 py-1.5 bg-panel text-ink"
        >
          <option value="">All Vendors</option>
          {vendors.map((v) => (
            <option key={v} value={v}>{v}</option>
          ))}
        </select>
        {(filters.team || filters.sprint || filters.vendor) && (
          <button onClick={resetFilters} className="p-1.5 rounded hover:bg-bg text-ink-3" title="Reset filters">
            <RotateCcw size={14} />
          </button>
        )}
      </div>

      {/* Active filter chips */}
      <FilterChips filters={filters} setFilters={setFilters} teams={teams} />

      {/* Nav controls */}
      <div className="flex items-center gap-1 border-l border-ink-3/20 pl-3 ml-2">
        <button onClick={onPrev} disabled={currentIndex === 0} className="p-1 rounded hover:bg-bg disabled:opacity-30 text-ink-2">
          <ChevronLeft size={18} />
        </button>
        <span className="text-xs text-ink-3 min-w-[3rem] text-center">{currentIndex + 1}/{totalViews}</span>
        <button onClick={onNext} disabled={currentIndex === totalViews - 1} className="p-1 rounded hover:bg-bg disabled:opacity-30 text-ink-2">
          <ChevronRight size={18} />
        </button>
      </div>

      {/* Import & Present mode */}
      {onOpenImport && (
        <button
          onClick={onOpenImport}
          className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-brand border border-brand/30 rounded-lg hover:bg-brand/5 transition-colors"
          title="Import data"
        >
          <Upload size={14} />
          Import
        </button>
      )}
      <button
        onClick={() => setIsPresenting(!isPresenting)}
        className="p-1.5 rounded hover:bg-bg text-ink-3"
        title="Toggle present mode (F)"
      >
        {isPresenting ? <Minimize2 size={16} /> : <Maximize2 size={16} />}
      </button>
    </header>
  );
}
