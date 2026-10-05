'use client';

import { useMemo } from 'react';
import { BarChart, Bar, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { Sprint, Team, RawData, FilterState } from '@/lib/types';
import { teamMetrics, sum } from '@/lib/metrics';
import { pct } from '@/lib/formatters';
import { CHART_COLORS, COMMON_CHART_PROPS } from '@/lib/chartHelpers';
import { KpiCard } from '@/components/KpiCard';
import { ChartCard } from '@/components/ChartCard';
import { InsightCard } from '@/components/InsightCard';

interface Props {
  sprints: Sprint[];
  teams: Team[];
  config: RawData['config'];
  raw: RawData;
  filters: FilterState;
}

export function RiskImprovement({ sprints, teams, config }: Props) {
  const teamMetricsArr = useMemo(() => teams.map((t) => teamMetrics(sprints, t.id)), [sprints, teams]);

  // Risk scoring per team
  const riskData = teamMetricsArr.map((t) => {
    const sayDoRisk = t.sayDo < 0.7 ? 30 : t.sayDo < 0.8 ? 15 : 0;
    const stabilityRisk = t.stability === 'Volatile' ? 25 : t.stability === 'Moderate' ? 10 : 0;
    const scopeRisk = t.scopeCreep > 0.2 ? 20 : t.scopeCreep > 0.12 ? 10 : 0;
    const carryRisk = t.committed > 0 && t.carryForward / t.committed > 0.2 ? 25 : t.carryForward / t.committed > 0.1 ? 10 : 0;
    const total = sayDoRisk + stabilityRisk + scopeRisk + carryRisk;
    return {
      name: teams.find((tm) => tm.id === t.team)?.name ?? t.team,
      riskScore: total,
      sayDoRisk,
      stabilityRisk,
      scopeRisk,
      carryRisk,
      color: total > 50 ? CHART_COLORS.bad : total > 25 ? CHART_COLORS.warn : CHART_COLORS.good,
    };
  });

  // Risk drivers aggregate
  const riskDrivers = [
    { name: 'Say/Do Gap', value: sum(riskData.map((r) => r.sayDoRisk)), color: CHART_COLORS.bad },
    { name: 'Instability', value: sum(riskData.map((r) => r.stabilityRisk)), color: CHART_COLORS.warn },
    { name: 'Scope Creep', value: sum(riskData.map((r) => r.scopeRisk)), color: CHART_COLORS.info },
    { name: 'Carry-forward', value: sum(riskData.map((r) => r.carryRisk)), color: CHART_COLORS.brand },
  ];

  const avgRisk = riskData.length > 0 ? sum(riskData.map((r) => r.riskScore)) / riskData.length : 0;
  const highRiskTeams = riskData.filter((r) => r.riskScore > 50).length;
  const atRiskCount = teamMetricsArr.filter((t) => t.deliveryHealth === 'At Risk').length;

  // Improvement insights
  const improvements = useMemo(() => {
    const insights = [];
    const highScope = teamMetricsArr.filter((t) => t.scopeCreep > 0.15);
    if (highScope.length > 0) {
      insights.push({
        type: 'warning' as const,
        title: 'Reduce Mid-Sprint Scope Additions',
        body: `${highScope.length} team(s) have scope creep >15%. Recommend tighter sprint commitment discipline.`,
      });
    }
    const volatile = teamMetricsArr.filter((t) => t.stability === 'Volatile');
    if (volatile.length > 0) {
      insights.push({
        type: 'risk' as const,
        title: 'Address Volatile Teams',
        body: `${volatile.length} team(s) showing volatile delivery. Review capacity, skill mix, and dependencies.`,
      });
    }
    insights.push({
      type: 'info' as const,
      title: 'Capacity Planning',
      body: 'Ensure committed capacity aligns with available capacity, accounting for leave and oncall.',
    });
    insights.push({
      type: 'success' as const,
      title: 'Celebrate Consistency',
      body: `${teamMetricsArr.filter((t) => t.stability === 'Stable').length} team(s) delivering with predictable cadence — share practices across teams.`,
    });
    return insights;
  }, [teamMetricsArr]);

  return (
    <div className="space-y-6">
      {/* KPIs */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard label="Avg Risk Score" value={String(Math.round(avgRisk))} status={avgRisk > 50 ? 'bad' : avgRisk > 25 ? 'warn' : 'good'} subtitle="Out of 100" />
        <KpiCard label="High Risk Teams" value={String(highRiskTeams)} status={highRiskTeams > 0 ? 'bad' : 'good'} />
        <KpiCard label="Teams At Risk" value={String(atRiskCount)} status={atRiskCount > 0 ? 'bad' : 'good'} />
        <KpiCard label="Active Teams" value={String(teams.length)} status="info" />
      </div>

      {/* Risk charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <ChartCard title="Risk Score by Team" subtitle="Composite risk (0-100)">
          <ResponsiveContainer width="100%" height={240}>
            <BarChart data={riskData} layout="vertical" {...COMMON_CHART_PROPS}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis type="number" domain={[0, 100]} tick={{ fontSize: 11 }} />
              <YAxis type="category" dataKey="name" tick={{ fontSize: 11 }} width={120} />
              <Tooltip contentStyle={{ fontSize: 12 }} />
              <Bar dataKey="riskScore" name="Risk Score" radius={[0, 3, 3, 0]}>
                {riskData.map((entry, i) => (
                  <Cell key={i} fill={entry.color} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Risk Drivers" subtitle="Aggregate contribution">
          <ResponsiveContainer width="100%" height={240}>
            <PieChart>
              <Pie data={riskDrivers} cx="50%" cy="50%" innerRadius={55} outerRadius={85} dataKey="value" label={({ name, percent }) => `${name} ${Math.round(percent * 100)}%`} labelLine={false}>
                {riskDrivers.map((entry, i) => (
                  <Cell key={i} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip contentStyle={{ fontSize: 12 }} />
            </PieChart>
          </ResponsiveContainer>
        </ChartCard>
      </div>

      {/* Risk register table */}
      <ChartCard title="Risk Register">
        <div className="overflow-x-auto">
          <table className="w-full text-xs">
            <thead>
              <tr className="border-b border-ink-3/10 bg-bg">
                <th className="px-3 py-2 text-left font-semibold text-ink-2">Team</th>
                <th className="px-3 py-2 text-left font-semibold text-ink-2">Score</th>
                <th className="px-3 py-2 text-left font-semibold text-ink-2">Say/Do Risk</th>
                <th className="px-3 py-2 text-left font-semibold text-ink-2">Stability Risk</th>
                <th className="px-3 py-2 text-left font-semibold text-ink-2">Scope Risk</th>
                <th className="px-3 py-2 text-left font-semibold text-ink-2">Carry Risk</th>
              </tr>
            </thead>
            <tbody>
              {riskData.sort((a, b) => b.riskScore - a.riskScore).map((r) => (
                <tr key={r.name} className="border-b border-ink-3/5">
                  <td className="px-3 py-2 font-medium text-ink">{r.name}</td>
                  <td className="px-3 py-2"><span className="badge" style={{ backgroundColor: `${r.color}20`, color: r.color }}>{r.riskScore}</span></td>
                  <td className="px-3 py-2">{r.sayDoRisk}</td>
                  <td className="px-3 py-2">{r.stabilityRisk}</td>
                  <td className="px-3 py-2">{r.scopeRisk}</td>
                  <td className="px-3 py-2">{r.carryRisk}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </ChartCard>

      {/* Improvement insights */}
      <div className="space-y-3">
        <h3 className="text-sm font-semibold text-ink">Improvement Opportunities</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          {improvements.map((ins, i) => (
            <InsightCard key={i} type={ins.type} title={ins.title} body={ins.body} />
          ))}
        </div>
      </div>
    </div>
  );
}
