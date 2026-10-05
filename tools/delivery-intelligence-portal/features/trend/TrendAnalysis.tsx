'use client';

import { useMemo } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend, ReferenceLine } from 'recharts';
import { Sprint, Team, RawData, FilterState } from '@/lib/types';
import { aggBySprint, teamMetrics } from '@/lib/metrics';
import { CHART_COLORS, teamColor, COMMON_CHART_PROPS } from '@/lib/chartHelpers';
import { ChartCard } from '@/components/ChartCard';

interface Props {
  sprints: Sprint[];
  teams: Team[];
  config: RawData['config'];
  raw: RawData;
  filters: FilterState;
}

export function TrendAnalysis({ sprints, teams, config }: Props) {
  const sprintAgg = useMemo(() => aggBySprint(sprints), [sprints]);

  // Per-team per-sprint data
  const sprintNames = [...new Set(sprints.map((s) => s.name))].sort();
  const teamSprintData = useMemo(() => {
    return sprintNames.map((name) => {
      const row: Record<string, number | string> = { sprint: name };
      teams.forEach((t) => {
        const ts = sprints.filter((s) => s.team === t.id && s.name === name);
        const deliveredSum = ts.reduce((a, s) => a + s.delivered, 0);
        const committedSum = ts.reduce((a, s) => a + s.committed, 0);
        row[`${t.id}_vel`] = deliveredSum;
        row[`${t.id}_sayDo`] = committedSum > 0 ? deliveredSum / committedSum : 0;
        row[`${t.id}_scope`] = ts.reduce((a, s) => a + s.scopeAdded, 0);
        row[`${t.id}_carry`] = ts.reduce((a, s) => a + s.carryForward, 0);
      });
      return row;
    });
  }, [sprints, teams, sprintNames]);

  // Aggregate trends
  const velocityTrendData = sprintAgg.map((s) => ({ sprint: s.sprint, velocity: s.delivered }));
  const reliabilityTrendData = sprintAgg.map((s) => ({ sprint: s.sprint, sayDo: Math.round(s.sayDo * 100) }));
  const scopeTrendData = sprintAgg.map((s) => ({ sprint: s.sprint, scopeAdded: s.scopeAdded }));
  const carryTrendData = sprintAgg.map((s) => ({ sprint: s.sprint, carryForward: s.carryForward }));

  return (
    <div className="space-y-6">
      {/* Main trend charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <ChartCard title="Velocity Trend" subtitle="Total delivered per sprint">
          <ResponsiveContainer width="100%" height={220}>
            <LineChart data={velocityTrendData} {...COMMON_CHART_PROPS}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="sprint" tick={{ fontSize: 11 }} />
              <YAxis tick={{ fontSize: 11 }} />
              <Tooltip contentStyle={{ fontSize: 12 }} />
              <Line type="monotone" dataKey="velocity" stroke={CHART_COLORS.brand} strokeWidth={2} dot={{ r: 4 }} />
            </LineChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Reliability Trend" subtitle="Say/Do % over sprints">
          <ResponsiveContainer width="100%" height={220}>
            <LineChart data={reliabilityTrendData} {...COMMON_CHART_PROPS}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="sprint" tick={{ fontSize: 11 }} />
              <YAxis domain={[50, 110]} tick={{ fontSize: 11 }} />
              <Tooltip formatter={(v: number) => `${v}%`} contentStyle={{ fontSize: 12 }} />
              <ReferenceLine y={80} stroke={CHART_COLORS.good} strokeDasharray="4 4" />
              <ReferenceLine y={105} stroke={CHART_COLORS.good} strokeDasharray="4 4" />
              <Line type="monotone" dataKey="sayDo" stroke={CHART_COLORS.info} strokeWidth={2} dot={{ r: 4 }} />
            </LineChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Scope Additions Trend" subtitle="Points added mid-sprint">
          <ResponsiveContainer width="100%" height={220}>
            <LineChart data={scopeTrendData} {...COMMON_CHART_PROPS}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="sprint" tick={{ fontSize: 11 }} />
              <YAxis tick={{ fontSize: 11 }} />
              <Tooltip contentStyle={{ fontSize: 12 }} />
              <Line type="monotone" dataKey="scopeAdded" stroke={CHART_COLORS.warn} strokeWidth={2} dot={{ r: 4 }} />
            </LineChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Carry-forward Trend" subtitle="Unfinished work carried to next sprint">
          <ResponsiveContainer width="100%" height={220}>
            <LineChart data={carryTrendData} {...COMMON_CHART_PROPS}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="sprint" tick={{ fontSize: 11 }} />
              <YAxis tick={{ fontSize: 11 }} />
              <Tooltip contentStyle={{ fontSize: 12 }} />
              <Line type="monotone" dataKey="carryForward" stroke={CHART_COLORS.bad} strokeWidth={2} dot={{ r: 4 }} />
            </LineChart>
          </ResponsiveContainer>
        </ChartCard>
      </div>

      {/* Velocity by team mini charts */}
      <h3 className="text-sm font-semibold text-ink">Velocity by Team</h3>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {teams.map((t) => (
          <ChartCard key={t.id} title={t.name} subtitle={t.vendor}>
            <ResponsiveContainer width="100%" height={140}>
              <LineChart data={teamSprintData} {...COMMON_CHART_PROPS}>
                <XAxis dataKey="sprint" tick={{ fontSize: 10 }} />
                <YAxis tick={{ fontSize: 10 }} />
                <Tooltip contentStyle={{ fontSize: 11 }} />
                <Line type="monotone" dataKey={`${t.id}_vel`} stroke={teamColor(t.id)} strokeWidth={2} dot={{ r: 3 }} name="Delivered" />
              </LineChart>
            </ResponsiveContainer>
          </ChartCard>
        ))}
      </div>
    </div>
  );
}
