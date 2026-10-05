'use client';

import { useMemo } from 'react';
import { BarChart, Bar, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, ReferenceLine, ComposedChart, Area } from 'recharts';
import { Sprint, Team, RawData, FilterState } from '@/lib/types';
import { portfolio, teamMetrics, aggBySprint } from '@/lib/metrics';
import { pct, pts } from '@/lib/formatters';
import { CHART_COLORS, COMMON_CHART_PROPS, getBarColor } from '@/lib/chartHelpers';
import { KpiCard } from '@/components/KpiCard';
import { ChartCard } from '@/components/ChartCard';
import { DataTable, Column } from '@/components/DataTable';

interface Props {
  sprints: Sprint[];
  teams: Team[];
  config: RawData['config'];
  raw: RawData;
  filters: FilterState;
}

export function DeliveryPerformance({ sprints, teams, config }: Props) {
  const p = useMemo(() => portfolio(sprints, teams, config), [sprints, teams, config]);
  const teamMetricsArr = useMemo(() => teams.map((t) => teamMetrics(sprints, t.id)), [sprints, teams]);
  const sprintAgg = useMemo(() => aggBySprint(sprints), [sprints]);

  const healthStatus = p.deliveryHealth === 'Good' ? 'good' : p.deliveryHealth === 'Warning' ? 'warn' : 'bad';

  // Velocity by sprint
  const velocityData = sprintAgg.map((s) => ({
    sprint: s.sprint,
    velocity: s.delivered,
    committed: s.committed,
  }));

  // Reliability data
  const reliabilityData = sprintAgg.map((s) => ({
    sprint: s.sprint,
    sayDo: Math.round(s.sayDo * 100),
  }));

  // Waterfall chart data
  const waterfallData = sprintAgg.map((s) => ({
    sprint: s.sprint,
    committed: s.committed,
    scopeAdded: s.scopeAdded,
    delivered: s.delivered,
    carryForward: s.carryForward,
  }));

  // Team scorecard
  const scorecardData = teamMetricsArr.map((t) => ({
    team: teams.find((tm) => tm.id === t.team)?.name ?? t.team,
    sayDo: pct(t.sayDo),
    velocity: Math.round(t.avgVelocity),
    stability: t.stability,
    health: t.deliveryHealth,
    scopeCreep: pct(t.scopeCreep),
    carryForward: t.carryForward,
  }));

  const columns: Column<typeof scorecardData[0]>[] = [
    { key: 'team', label: 'Team' },
    { key: 'sayDo', label: 'Say/Do' },
    { key: 'velocity', label: 'Avg Velocity' },
    { key: 'stability', label: 'Stability' },
    { key: 'health', label: 'Health', render: (row) => (
      <span className={`badge ${row.health === 'Good' ? 'bg-good/10 text-good' : row.health === 'Warning' ? 'bg-warn/10 text-warn' : 'bg-bad/10 text-bad'}`}>
        {row.health}
      </span>
    )},
    { key: 'scopeCreep', label: 'Scope Creep' },
    { key: 'carryForward', label: 'Carry-fwd' },
  ];

  return (
    <div className="space-y-6">
      {/* KPIs */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard label="Portfolio Say/Do" value={pct(p.sayDo)} status={healthStatus} />
        <KpiCard label="Total Delivered" value={pts(p.totalDelivered)} status="info" />
        <KpiCard label="Scope Creep" value={pct(p.scopeCreep)} status={p.scopeCreep > 0.15 ? 'warn' : 'good'} />
        <KpiCard label="Carry-forward" value={pts(p.totalCarryForward)} status={p.totalCarryForward / p.totalCommitted > 0.15 ? 'warn' : 'good'} />
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <ChartCard title="Velocity by Sprint" subtitle="Story points delivered">
          <ResponsiveContainer width="100%" height={240}>
            <ComposedChart data={velocityData} {...COMMON_CHART_PROPS}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="sprint" tick={{ fontSize: 11 }} />
              <YAxis tick={{ fontSize: 11 }} />
              <Tooltip contentStyle={{ fontSize: 12 }} />
              <Legend wrapperStyle={{ fontSize: 11 }} />
              <Bar dataKey="committed" fill={CHART_COLORS.ink3} name="Committed" radius={[3, 3, 0, 0]} />
              <Line type="monotone" dataKey="velocity" stroke={CHART_COLORS.brand} strokeWidth={2} name="Delivered" dot={{ r: 4 }} />
            </ComposedChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Commitment Reliability" subtitle="Say/Do % per sprint">
          <ResponsiveContainer width="100%" height={240}>
            <BarChart data={reliabilityData} {...COMMON_CHART_PROPS}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="sprint" tick={{ fontSize: 11 }} />
              <YAxis domain={[0, 120]} tick={{ fontSize: 11 }} />
              <Tooltip formatter={(v: number) => `${v}%`} contentStyle={{ fontSize: 12 }} />
              <ReferenceLine y={80} stroke={CHART_COLORS.good} strokeDasharray="4 4" label="80%" />
              <ReferenceLine y={105} stroke={CHART_COLORS.good} strokeDasharray="4 4" label="105%" />
              <Bar dataKey="sayDo" name="Say/Do %" radius={[3, 3, 0, 0]} fill={CHART_COLORS.brand} />
            </BarChart>
          </ResponsiveContainer>
        </ChartCard>
      </div>

      {/* Waterfall */}
      <ChartCard title="Sprint Waterfall" subtitle="Committed → Scope Added → Delivered → Carry-forward">
        <ResponsiveContainer width="100%" height={240}>
          <BarChart data={waterfallData} {...COMMON_CHART_PROPS}>
            <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
            <XAxis dataKey="sprint" tick={{ fontSize: 11 }} />
            <YAxis tick={{ fontSize: 11 }} />
            <Tooltip contentStyle={{ fontSize: 12 }} />
            <Legend wrapperStyle={{ fontSize: 11 }} />
            <Bar dataKey="committed" stackId="a" fill={CHART_COLORS.ink3} name="Committed" />
            <Bar dataKey="scopeAdded" stackId="a" fill={CHART_COLORS.warn} name="Scope Added" />
            <Bar dataKey="delivered" fill={CHART_COLORS.good} name="Delivered" radius={[3, 3, 0, 0]} />
            <Bar dataKey="carryForward" fill={CHART_COLORS.bad} name="Carry-forward" radius={[3, 3, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </ChartCard>

      {/* Team Scorecard */}
      <div className="card">
        <h3 className="text-sm font-semibold text-ink mb-3">Team Scorecard</h3>
        <DataTable data={scorecardData} columns={columns} searchKeys={['team']} />
      </div>
    </div>
  );
}
