'use client';

import { useMemo } from 'react';
import { ScatterChart, Scatter, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell, ZAxis } from 'recharts';
import { Sprint, Team, RawData, FilterState } from '@/lib/types';
import { teamMetrics } from '@/lib/metrics';
import { pct } from '@/lib/formatters';
import { CHART_COLORS, teamColor, COMMON_CHART_PROPS } from '@/lib/chartHelpers';
import { ChartCard } from '@/components/ChartCard';
import { KpiCard } from '@/components/KpiCard';
import { DataTable, Column } from '@/components/DataTable';

interface Props {
  sprints: Sprint[];
  teams: Team[];
  config: RawData['config'];
  raw: RawData;
  filters: FilterState;
}

export function TeamPerformance({ sprints, teams, config }: Props) {
  const teamMetricsArr = useMemo(() => teams.map((t) => teamMetrics(sprints, t.id)), [sprints, teams]);

  // Scatter: velocity vs stability
  const scatterData = teamMetricsArr.map((t) => ({
    name: teams.find((tm) => tm.id === t.team)?.name ?? t.team,
    x: t.avgVelocity,
    y: Math.round(t.sayDo * 100),
    z: t.sprintCount,
    color: teamColor(t.team),
  }));

  // Consistency bars (CV)
  const consistencyData = teamMetricsArr.map((t) => ({
    name: teams.find((tm) => tm.id === t.team)?.name ?? t.team,
    cv: Math.round(t.trendAdjustedCv * 100),
    color: t.stability === 'Stable' ? CHART_COLORS.good : t.stability === 'Moderate' ? CHART_COLORS.warn : CHART_COLORS.bad,
  }));

  // Top performers
  const sorted = [...teamMetricsArr].sort((a, b) => b.sayDo - a.sayDo);
  const top3 = sorted.slice(0, 3);

  // Leaderboard
  const leaderboardData = teamMetricsArr.map((t) => ({
    team: teams.find((tm) => tm.id === t.team)?.name ?? t.team,
    sayDo: pct(t.sayDo),
    velocity: Math.round(t.avgVelocity),
    stability: t.stability,
    predictability: t.predictability,
    health: t.deliveryHealth,
    scopeCreep: pct(t.scopeCreep),
  }));

  const columns: Column<typeof leaderboardData[0]>[] = [
    { key: 'team', label: 'Team' },
    { key: 'sayDo', label: 'Say/Do' },
    { key: 'velocity', label: 'Avg Velocity' },
    { key: 'stability', label: 'Stability' },
    { key: 'predictability', label: 'Predictability' },
    { key: 'health', label: 'Health', render: (row) => (
      <span className={`badge ${row.health === 'Good' ? 'bg-good/10 text-good' : row.health === 'Warning' ? 'bg-warn/10 text-warn' : 'bg-bad/10 text-bad'}`}>
        {row.health}
      </span>
    )},
    { key: 'scopeCreep', label: 'Scope Creep' },
  ];

  return (
    <div className="space-y-6">
      {/* Scatter */}
      <ChartCard title="Velocity vs Reliability" subtitle="Bubble size = sprint count">
        <ResponsiveContainer width="100%" height={280}>
          <ScatterChart {...COMMON_CHART_PROPS}>
            <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
            <XAxis type="number" dataKey="x" name="Velocity" tick={{ fontSize: 11 }} label={{ value: 'Avg Velocity (pts)', position: 'bottom', fontSize: 11 }} />
            <YAxis type="number" dataKey="y" name="Say/Do %" tick={{ fontSize: 11 }} domain={[60, 110]} label={{ value: 'Say/Do %', angle: -90, position: 'insideLeft', fontSize: 11 }} />
            <ZAxis type="number" dataKey="z" range={[60, 300]} />
            <Tooltip cursor={{ strokeDasharray: '3 3' }} contentStyle={{ fontSize: 12 }} formatter={(value: number, name: string) => [name === 'x' ? `${value} pts` : `${value}%`, name === 'x' ? 'Velocity' : 'Say/Do']} />
            <Scatter data={scatterData} name="Teams">
              {scatterData.map((entry, i) => (
                <Cell key={i} fill={entry.color} />
              ))}
            </Scatter>
          </ScatterChart>
        </ResponsiveContainer>
      </ChartCard>

      {/* Consistency */}
      <ChartCard title="Delivery Consistency" subtitle="Coefficient of Variation (lower = more consistent)">
        <ResponsiveContainer width="100%" height={200}>
          <BarChart data={consistencyData} layout="vertical" {...COMMON_CHART_PROPS}>
            <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
            <XAxis type="number" domain={[0, 40]} tick={{ fontSize: 11 }} />
            <YAxis type="category" dataKey="name" tick={{ fontSize: 11 }} width={120} />
            <Tooltip formatter={(v: number) => `${v}%`} contentStyle={{ fontSize: 12 }} />
            <Bar dataKey="cv" name="CV %" radius={[0, 3, 3, 0]}>
              {consistencyData.map((entry, i) => (
                <Cell key={i} fill={entry.color} />
              ))}
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      </ChartCard>

      {/* Spotlight cards */}
      <h3 className="text-sm font-semibold text-ink">Top Performers</h3>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {top3.map((t, i) => (
          <div key={t.team} className="card">
            <div className="flex items-center gap-2 mb-2">
              <span className="text-lg font-bold text-brand">#{i + 1}</span>
              <span className="text-sm font-semibold text-ink">{teams.find((tm) => tm.id === t.team)?.name}</span>
            </div>
            <div className="grid grid-cols-2 gap-2 text-xs">
              <div><span className="text-ink-3">Say/Do:</span> <span className="font-medium text-ink">{pct(t.sayDo)}</span></div>
              <div><span className="text-ink-3">Velocity:</span> <span className="font-medium text-ink">{Math.round(t.avgVelocity)} pts</span></div>
              <div><span className="text-ink-3">Stability:</span> <span className="font-medium text-ink">{t.stability}</span></div>
              <div><span className="text-ink-3">Health:</span> <span className={`font-medium ${t.deliveryHealth === 'Good' ? 'text-good' : t.deliveryHealth === 'Warning' ? 'text-warn' : 'text-bad'}`}>{t.deliveryHealth}</span></div>
            </div>
          </div>
        ))}
      </div>

      {/* Leaderboard */}
      <div className="card">
        <h3 className="text-sm font-semibold text-ink mb-3">Team Leaderboard</h3>
        <DataTable data={leaderboardData} columns={columns} searchKeys={['team']} />
      </div>
    </div>
  );
}
