'use client';

import { useMemo } from 'react';
import { BarChart, Bar, AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, ReferenceLine, Cell } from 'recharts';
import { Sprint, Team, RawData, FilterState } from '@/lib/types';
import { aggBySprint, sum } from '@/lib/metrics';
import { pct } from '@/lib/formatters';
import { CHART_COLORS, COMMON_CHART_PROPS, getBarColor } from '@/lib/chartHelpers';
import { ChartCard } from '@/components/ChartCard';
import { KpiCard } from '@/components/KpiCard';

interface Props {
  sprints: Sprint[];
  teams: Team[];
  config: RawData['config'];
  raw: RawData;
  filters: FilterState;
}

export function SprintAnalytics({ sprints, teams, config }: Props) {
  const sprintAgg = useMemo(() => aggBySprint(sprints), [sprints]);

  // Burn-up data (cumulative)
  const burnUp = useMemo(() => {
    let cumCommitted = 0;
    let cumDelivered = 0;
    return sprintAgg.map((s) => {
      cumCommitted += s.committed;
      cumDelivered += s.delivered;
      return { sprint: s.sprint, cumCommitted, cumDelivered };
    });
  }, [sprintAgg]);

  // Goal met per sprint
  const sprintNames = [...new Set(sprints.map((s) => s.name))].sort();
  const goalMetData = sprintNames.map((name) => {
    const sprintTeams = sprints.filter((s) => s.name === name);
    const met = sprintTeams.filter((s) => s.goalMet).length;
    const total = sprintTeams.length;
    return { sprint: name, goalMet: met, notMet: total - met, pct: total > 0 ? met / total : 0 };
  });

  // Carry-forward analysis
  const carryData = sprintAgg.map((s) => ({
    sprint: s.sprint,
    carryForward: s.carryForward,
    carryPct: s.committed > 0 ? Math.round((s.carryForward / s.committed) * 100) : 0,
  }));

  // Say/Do heatmap data - team x sprint
  const heatmapData = useMemo(() => {
    return teams.flatMap((t) =>
      sprintNames.map((name) => {
        const ts = sprints.find((s) => s.team === t.id && s.name === name);
        const sayDo = ts && ts.committed > 0 ? ts.delivered / ts.committed : 0;
        return { team: t.name, sprint: name, sayDo, color: getBarColor(sayDo) };
      })
    );
  }, [sprints, teams, sprintNames]);

  const totalGoalsMet = sprints.filter((s) => s.goalMet).length;
  const totalSprints = sprints.length;
  const goalsRatio = totalSprints > 0 ? totalGoalsMet / totalSprints : 0;
  const totalCommitted = sum(sprints.map((s) => s.committed));
  const totalDelivered = sum(sprints.map((s) => s.delivered));
  const avgSayDo = totalCommitted > 0 ? totalDelivered / totalCommitted : 0;

  return (
    <div className="space-y-6">
      {/* KPIs */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard label="Goals Met" value={`${totalGoalsMet}/${totalSprints}`} status={goalsRatio > 0.75 ? 'good' : 'warn'} subtitle={pct(goalsRatio)} />
        <KpiCard label="Total Carry-forward" value={`${sum(sprints.map((s) => s.carryForward))} pts`} status="warn" />
        <KpiCard label="Avg Say/Do" value={pct(avgSayDo)} status="info" />
        <KpiCard label="Sprints Analyzed" value={String(sprintNames.length)} status="neutral" />
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <ChartCard title="Sprint-by-Sprint Performance" subtitle="Committed vs Delivered">
          <ResponsiveContainer width="100%" height={240}>
            <BarChart data={sprintAgg} {...COMMON_CHART_PROPS}>
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

        <ChartCard title="Burn-up" subtitle="Cumulative delivery progress">
          <ResponsiveContainer width="100%" height={240}>
            <AreaChart data={burnUp} {...COMMON_CHART_PROPS}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="sprint" tick={{ fontSize: 11 }} />
              <YAxis tick={{ fontSize: 11 }} />
              <Tooltip contentStyle={{ fontSize: 12 }} />
              <Legend wrapperStyle={{ fontSize: 11 }} />
              <Area type="monotone" dataKey="cumCommitted" stroke={CHART_COLORS.ink3} fill={CHART_COLORS.ink3} fillOpacity={0.1} name="Committed" />
              <Area type="monotone" dataKey="cumDelivered" stroke={CHART_COLORS.brand} fill={CHART_COLORS.brand} fillOpacity={0.2} name="Delivered" />
            </AreaChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Sprint Goals Met" subtitle="Teams meeting goals per sprint">
          <ResponsiveContainer width="100%" height={240}>
            <BarChart data={goalMetData} {...COMMON_CHART_PROPS}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="sprint" tick={{ fontSize: 11 }} />
              <YAxis tick={{ fontSize: 11 }} />
              <Tooltip contentStyle={{ fontSize: 12 }} />
              <Legend wrapperStyle={{ fontSize: 11 }} />
              <Bar dataKey="goalMet" stackId="a" fill={CHART_COLORS.good} name="Met" radius={[3, 3, 0, 0]} />
              <Bar dataKey="notMet" stackId="a" fill={CHART_COLORS.bad} name="Not Met" radius={[3, 3, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Carry-forward %" subtitle="% of committed work carried over">
          <ResponsiveContainer width="100%" height={240}>
            <BarChart data={carryData} {...COMMON_CHART_PROPS}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="sprint" tick={{ fontSize: 11 }} />
              <YAxis tick={{ fontSize: 11 }} />
              <Tooltip formatter={(v: number) => `${v}%`} contentStyle={{ fontSize: 12 }} />
              <ReferenceLine y={20} stroke={CHART_COLORS.warn} strokeDasharray="4 4" label="20% threshold" />
              <Bar dataKey="carryPct" name="Carry %" radius={[3, 3, 0, 0]}>
                {carryData.map((entry, i) => (
                  <Cell key={i} fill={entry.carryPct > 20 ? CHART_COLORS.bad : CHART_COLORS.good} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </ChartCard>
      </div>

      {/* Say/Do Heatmap */}
      <ChartCard title="Say/Do Heatmap" subtitle="Team × Sprint">
        <div className="overflow-x-auto">
          <table className="w-full text-xs">
            <thead>
              <tr>
                <th className="px-2 py-1 text-left text-ink-2 font-medium">Team</th>
                {sprintNames.map((s) => (
                  <th key={s} className="px-2 py-1 text-center text-ink-2 font-medium">{s}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {teams.map((t) => (
                <tr key={t.id} className="border-t border-ink-3/10">
                  <td className="px-2 py-1.5 font-medium text-ink">{t.name}</td>
                  {sprintNames.map((name) => {
                    const cell = heatmapData.find((d) => d.team === t.name && d.sprint === name);
                    return (
                      <td key={name} className="px-2 py-1.5 text-center">
                        <span
                          className="inline-block px-2 py-0.5 rounded text-white text-[10px] font-medium"
                          style={{ backgroundColor: cell?.color ?? '#ccc' }}
                        >
                          {cell ? pct(cell.sayDo) : '—'}
                        </span>
                      </td>
                    );
                  })}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </ChartCard>
    </div>
  );
}
