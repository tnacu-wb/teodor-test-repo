'use client';

import { X } from 'lucide-react';
import { FilterState, Team } from '@/lib/types';

interface FilterChipsProps {
  filters: FilterState;
  setFilters: (f: FilterState) => void;
  teams: Team[];
}

export function FilterChips({ filters, setFilters, teams }: FilterChipsProps) {
  const chips: { label: string; clear: () => void }[] = [];

  if (filters.team) {
    const teamName = teams.find((t) => t.id === filters.team)?.name ?? filters.team;
    chips.push({ label: teamName, clear: () => setFilters({ ...filters, team: null }) });
  }
  if (filters.sprint) {
    chips.push({ label: filters.sprint, clear: () => setFilters({ ...filters, sprint: null }) });
  }
  if (filters.vendor) {
    chips.push({ label: filters.vendor, clear: () => setFilters({ ...filters, vendor: null }) });
  }

  if (chips.length === 0) return null;

  return (
    <div className="hidden lg:flex items-center gap-1.5">
      {chips.map((chip) => (
        <span key={chip.label} className="inline-flex items-center gap-1 text-xs bg-brand/10 text-brand px-2 py-0.5 rounded-full">
          {chip.label}
          <button onClick={chip.clear} className="hover:text-brand-2">
            <X size={12} />
          </button>
        </span>
      ))}
    </div>
  );
}
