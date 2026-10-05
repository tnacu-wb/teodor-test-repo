'use client';

import { useMemo } from 'react';
import { BarChart, Bar, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, ReferenceLine } from 'recharts';
import { Sprint, Team, RawData, FilterState } from '@/lib/types';
import { portfolio, teamMetrics } from '@/lib/metrics';
import { pct, pts } from '@/lib/formatters';
import { portfolioInsights } from '@/lib/insights';
import { CHART_COLORS, getBarColor, COMMON_CHART_PROPS } from '@/lib/chartHelpers';
import { KpiCard } from '@/components/KpiCard';
import { ChartCard } from '@/components/ChartCard';
import { InsightCard } from '@/components/InsightCard';
import { Gauge } from '@/components/Gauge';
import { Target, TrendingUp, AlertTriangle } from 'lucide-react';

interface Props {
  sprints: Sprint[];
  teams: Team[];
  config: RawData['config'];
  raw: RawData;
  filters: FilterState;
}

export function ExecutiveOverview({ sprints, teams, config }: Props) {
  const p = useMemo(() => portfolio(sprints, teams, config), [sprints, teams, config]);
  const teamMetricsArr = useMemo(() => teams.map((t) => teamMetrics(sprints, t.id)), [sprints, teams]);
  const insights = useMemo(() => portfolioInsights(p, teamMetricsArr), [p, teamMetricsArr]);

  const healthStatus = p.deliveryHealth === 'Good' ? 'good' : p.deliveryHealth === 'Warning' ? 'warn' : 'bad';

  const teamHealthData = teamMetricsArr.map((t) => ({
    name: teams.find((tm) => tm.id === t.team)?.name ?? t.team,
    sayDo: Math.round(t.sayDo * 100),
    fill: getBarColor(t.sayDo),
  }));

  return (
    <div className="space-y-6">
      {/* KPIs */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard label="Delivery Health" value={p.deliveryHealth} status={healthStatus} subtitle={`Say/Do: ${pct(p.sayDo)}`} />
        <KpiCard label="Say/Do Ratio" value={pct(p.sayDo)} status={healthStatus} />
        <KpiCard label="Avg Velocity" value={pts(p.activeSprints > 0 ? p.totalDelivered / p.activeSprints : 0)} status="info" subtitle={`${p.activeSprints} sprints`} />
        <KpiCard label="Scope Added" value={pct(p.scopeCreep)} status={p.scopeCreep > 0.15 ? 'warn' : 'good'} />
      </div>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard label="Team Performance Index" value={pct(p.teamPerformanceIndex)} status={p.teamPerformanceIndex > 0.7 ? 'good' : 'warn'} />
        <KpiCard label="Sprint Stability" value={p.sprintStability} status={p.sprintStability === 'Stable' ? 'good' : p.sprintStability === 'Moderate' ? 'warn' : 'bad'} />
        <KpiCard label="Carry-forward" value={pts(p.totalCarryForward)} status={p.totalCarryForward / p.totalCommitted > 0.15 ? 'warn' : 'good'} />
        <KpiCard label="Committed vs Delivered" value={`${p.totalDelivered}/${p.totalCommitted}`} status="info" subtitle="Story Points" />
      </div>

      {/* Charts row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <ChartCard title="Planned vs Delivered" subtitle="Story points per sprint">
          <ResponsiveContainer width="100%" height={240}>
            <BarChart data={p.sprintSeries} {...COMMON_CHART_PROPS}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="sprint" tick={{ fontSize: 11 }} />
              <YAxis tick={{ fontSize: 11 }} />
              <Tooltip contentStyle={{ fontSize: 12 }} />
              <Legend wrapperStyle={{ fontSize: 11 }} />
              <Bar dataKey="committed" fill={CHART_COLORS.ink3} name="Committed" radius={[3, 3, 0, 0]} />
              <Bar dataKey="delivered" fill={CHART_COLORS.brand} name="Delivered" radius={[3, 3, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Say/Do Trend" subtitle="Across sprints">
          <ResponsiveContainer width="100%" height={240}>
            <LineChart data={p.sprintSeries} {...COMMON_CHART_PROPS}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="sprint" tick={{ fontSize: 11 }} />
              <YAxis domain={[0.5, 1.2]} tickFormatter={(v: number) => `${Math.round(v * 100)}%`} tick={{ fontSize: 11 }} />
              <Tooltip formatter={(v: number) => `${Math.round(v * 100)}%`} contentStyle={{ fontSize: 12 }} />
              <ReferenceLine y={config.healthyBandLow} stroke={CHART_COLORS.good} strokeDasharray="4 4" />
              <ReferenceLine y={config.healthyBandHigh} stroke={CHART_COLORS.good} strokeDasharray="4 4" />
              <Line type="monotone" dataKey="sayDo" stroke={CHART_COLORS.brand} strokeWidth={2} dot={{ r: 4 }} name="Say/Do" />
            </LineChart>
          </ResponsiveContainer>
        </ChartCard>
      </div>

      {/* Team Health */}
      <ChartCard title="Team Health" subtitle="Say/Do ratio by team">
        <ResponsiveContainer width="100%" height={200}>
          <BarChart data={teamHealthData} layout="vertical" {...COMMON_CHART_PROPS}>
            <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
            <XAxis type="number" domain={[0, 110]} tick={{ fontSize: 11 }} />
            <YAxis type="category" dataKey="name" tick={{ fontSize: 11 }} width={120} />
            <Tooltip formatter={(v: number) => `${v}%`} contentStyle={{ fontSize: 12 }} />
            <ReferenceLine x={80} stroke={CHART_COLORS.good} strokeDasharray="4 4" />
            <Bar dataKey="sayDo" radius={[0, 3, 3, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </ChartCard>

      {/* Insights */}
      <div className="space-y-3">
        <h3 className="text-sm font-semibold text-ink">Key Insights</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          {insights.map((ins, i) => (
            <InsightCard key={i} type={ins.type} title={ins.title} body={ins.body} />
          ))}
        </div>
      </div>

      {/* Leadership Questions */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="card flex items-start gap-3">
          <Target size={20} className="text-brand shrink-0 mt-0.5" />
          <div>
            <p className="text-sm font-semibold text-ink">Are we delivering what we commit?</p>
            <p className="text-xs text-ink-2 mt-1">
              {p.deliveryHealth === 'Good'
                ? `Yes — ${pct(p.sayDo)} of commitments met across ${p.activeTeams} teams.`
                : `Attention needed — only ${pct(p.sayDo)} of commitments delivered.`}
            </p>
          </div>
        </div>
        <div className="card flex items-start gap-3">
          <TrendingUp size={20} className="text-info shrink-0 mt-0.5" />
          <div>
            <p className="text-sm font-semibold text-ink">Is velocity trending up?</p>
            <p className="text-xs text-ink-2 mt-1">
              {p.velocityTrend > 0 ? 'Velocity shows an upward trend across the PI.' : 'Velocity is flat or declining — review capacity planning.'}
            </p>
          </div>
        </div>
        <div className="card flex items-start gap-3">
          <AlertTriangle size={20} className="text-warn shrink-0 mt-0.5" />
          <div>
            <p className="text-sm font-semibold text-ink">Are there teams at risk?</p>
            <p className="text-xs text-ink-2 mt-1">
              {teamMetricsArr.filter((t) => t.deliveryHealth === 'At Risk').length === 0
                ? 'All teams operating within healthy parameters.'
                : `${teamMetricsArr.filter((t) => t.deliveryHealth === 'At Risk').length} team(s) require attention.`}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
