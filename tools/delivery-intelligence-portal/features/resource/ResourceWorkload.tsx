'use client';

import { useMemo } from 'react';
import { BarChart, Bar, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { Sprint, Team, RawData, FilterState } from '@/lib/types';
import { sum, mean } from '@/lib/metrics';
import { pct } from '@/lib/formatters';
import { CHART_COLORS, teamColor, COMMON_CHART_PROPS } from '@/lib/chartHelpers';
import { KpiCard } from '@/components/KpiCard';
import { ChartCard } from '@/components/ChartCard';
import { DataTable, Column } from '@/components/DataTable';
import { Info } from 'lucide-react';

interface Props {
  sprints: Sprint[];
  teams: Team[];
  config: RawData['config'];
  raw: RawData;
  filters: FilterState;
}

export function ResourceWorkload({ sprints, teams, config, raw, filters }: Props) {
  const dcData = useMemo(() => {
    let data = raw.dcCapacity;
    if (filters.team || filters.vendor) {
      const teamIds = new Set(teams.map((t) => t.id));
      data = data.filter((d) => teamIds.has(d.team));
    }
    if (filters.sprint) data = data.filter((d) => d.sprint === filters.sprint);
    if (filters.vendor) {
      const vendorTeams = new Set(teams.map((t) => t.id));
      data = data.filter((d) => vendorTeams.has(d.team));
    }
    return data;
  }, [raw.dcCapacity, filters, teams]);

  const totalPlanned = sum(dcData.map((d) => d.planned));
  const totalActual = sum(dcData.map((d) => d.actual));
  const utilization = totalPlanned > 0 ? totalActual / totalPlanned : 0;
  const totalPeople = sum(teams.map((t) => t.members.length));

  // Load by team
  const loadData = teams.map((t) => {
    const teamDc = dcData.filter((d) => d.team === t.id);
    const planned = sum(teamDc.map((d) => d.planned));
    const actual = sum(teamDc.map((d) => d.actual));
    return { name: t.name, planned, actual, utilization: planned > 0 ? Math.round((actual / planned) * 100) : 0 };
  });

  // Vendor distribution donut (aggregate by actual vendor values in the dataset)
  const vendorTotals = teams.reduce<Record<string, number>>((acc, t) => {
    acc[t.vendor] = (acc[t.vendor] ?? 0) + t.capacity;
    return acc;
  }, {});
  const vendorPalette = [CHART_COLORS.brand, CHART_COLORS.info, CHART_COLORS.good, CHART_COLORS.warn, CHART_COLORS.bad];
  const vendorDist = Object.entries(vendorTotals).map(([name, value], i) => ({
    name,
    value,
    color: vendorPalette[i % vendorPalette.length],
  }));

  // Planned capacity over sprints
  const sprintNames = [...new Set(dcData.map((d) => d.sprint))].sort();
  const capacityData = sprintNames.map((s) => {
    const items = dcData.filter((d) => d.sprint === s);
    return { sprint: s, planned: sum(items.map((d) => d.planned)), actual: sum(items.map((d) => d.actual)) };
  });

  // Contribution table
  const contributionData = teams.map((t) => {
    const teamDc = dcData.filter((d) => d.team === t.id);
    const teamSprints = sprints.filter((s) => s.team === t.id);
    return {
      team: t.name,
      members: t.members.length,
      capacity: t.capacity,
      planned: sum(teamDc.map((d) => d.planned)),
      actual: sum(teamDc.map((d) => d.actual)),
      delivered: sum(teamSprints.map((s) => s.delivered)),
      utilization: pct(sum(teamDc.map((d) => d.actual)) / Math.max(sum(teamDc.map((d) => d.planned)), 1)),
    };
  });

  const columns: Column<typeof contributionData[0]>[] = [
    { key: 'team', label: 'Team' },
    { key: 'members', label: 'Members' },
    { key: 'capacity', label: 'Capacity' },
    { key: 'planned', label: 'Planned' },
    { key: 'actual', label: 'Actual' },
    { key: 'delivered', label: 'Delivered' },
    { key: 'utilization', label: 'Utilization' },
  ];

  return (
    <div className="space-y-6">
      {/* Disclaimer */}
      <div className="flex items-start gap-2 p-3 rounded-lg bg-info/5 border border-info/20">
        <Info size={16} className="text-info shrink-0 mt-0.5" />
        <p className="text-xs text-ink-2">
          Story points reflect team-level effort estimation, not individual performance.
          This view is for capacity planning and workload balancing, not evaluating individuals.
        </p>
      </div>

      {/* KPIs */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard label="Capacity Utilization" value={pct(utilization)} status={utilization > 0.85 ? 'good' : 'warn'} />
        <KpiCard label="Total People" value={String(totalPeople)} status="info" />
        <KpiCard label="Planned Capacity" value={`${totalPlanned} pts`} status="neutral" />
        <KpiCard label="Actual Delivered" value={`${totalActual} pts`} status="info" />
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <ChartCard title="Load by Team" subtitle="Planned vs Actual capacity">
          <ResponsiveContainer width="100%" height={240}>
            <BarChart data={loadData} {...COMMON_CHART_PROPS}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="name" tick={{ fontSize: 10 }} />
              <YAxis tick={{ fontSize: 11 }} />
              <Tooltip contentStyle={{ fontSize: 12 }} />
              <Legend wrapperStyle={{ fontSize: 11 }} />
              <Bar dataKey="planned" fill={CHART_COLORS.ink3} name="Planned" radius={[3, 3, 0, 0]} />
              <Bar dataKey="actual" fill={CHART_COLORS.brand} name="Actual" radius={[3, 3, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Vendor Split" subtitle="Capacity by vendor">
          <ResponsiveContainer width="100%" height={240}>
            <PieChart>
              <Pie data={vendorDist} cx="50%" cy="50%" innerRadius={55} outerRadius={85} dataKey="value" label={({ name, percent }) => `${name} ${Math.round(percent * 100)}%`} labelLine={false}>
                {vendorDist.map((entry, i) => (
                  <Cell key={i} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip contentStyle={{ fontSize: 12 }} />
            </PieChart>
          </ResponsiveContainer>
        </ChartCard>
      </div>

      {/* Capacity over sprints */}
      <ChartCard title="Planned vs Actual Capacity" subtitle="Per sprint">
        <ResponsiveContainer width="100%" height={200}>
          <BarChart data={capacityData} {...COMMON_CHART_PROPS}>
            <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
            <XAxis dataKey="sprint" tick={{ fontSize: 11 }} />
            <YAxis tick={{ fontSize: 11 }} />
            <Tooltip contentStyle={{ fontSize: 12 }} />
            <Legend wrapperStyle={{ fontSize: 11 }} />
            <Bar dataKey="planned" fill={CHART_COLORS.ink3} name="Planned" radius={[3, 3, 0, 0]} />
            <Bar dataKey="actual" fill={CHART_COLORS.good} name="Actual" radius={[3, 3, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </ChartCard>

      {/* Contribution table */}
      <div className="card">
        <h3 className="text-sm font-semibold text-ink mb-3">Team Contribution</h3>
        <DataTable data={contributionData} columns={columns} searchKeys={['team']} />
      </div>
    </div>
  );
}
