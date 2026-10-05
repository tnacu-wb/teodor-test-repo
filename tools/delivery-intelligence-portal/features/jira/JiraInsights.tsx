'use client';

import { useMemo } from 'react';
import { BarChart, Bar, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, LineChart, Line } from 'recharts';
import { Sprint, Team, RawData, FilterState } from '@/lib/types';
import { sum, mean } from '@/lib/metrics';
import { CHART_COLORS, COMMON_CHART_PROPS } from '@/lib/chartHelpers';
import { KpiCard } from '@/components/KpiCard';
import { ChartCard } from '@/components/ChartCard';

interface Props {
  sprints: Sprint[];
  teams: Team[];
  config: RawData['config'];
  raw: RawData;
  filters: FilterState;
}

export function JiraInsights({ sprints, teams, config, raw, filters }: Props) {
  const jiraData = useMemo(() => {
    let data = raw.jiraSnapshots;
    if (filters.team || filters.vendor) {
      const teamIds = new Set(teams.map((t) => t.id));
      data = data.filter((j) => teamIds.has(j.team));
    }
    if (filters.sprint) data = data.filter((j) => j.sprint === filters.sprint);
    return data;
  }, [raw.jiraSnapshots, filters, teams]);

  const totalIssues = sum(jiraData.map((j) => j.totalIssues));
  const totalCompleted = sum(jiraData.map((j) => j.completed));
  const totalBlocked = sum(jiraData.map((j) => j.blocked));
  const avgLeadTime = mean(jiraData.map((j) => j.avgLeadTimeDays));
  const avgCycleTime = mean(jiraData.map((j) => j.avgCycleTimeDays));
  const throughputRate = totalIssues > 0 ? totalCompleted / totalIssues : 0;

  // Status distribution
  const statusData = [
    { name: 'Completed', value: totalCompleted, color: CHART_COLORS.good },
    { name: 'In Progress', value: sum(jiraData.map((j) => j.inProgress)), color: CHART_COLORS.info },
    { name: 'Open', value: sum(jiraData.map((j) => j.open)), color: CHART_COLORS.ink3 },
    { name: 'Blocked', value: totalBlocked, color: CHART_COLORS.bad },
  ];

  // Priority distribution
  const priorityData = [
    { name: 'Critical', value: sum(jiraData.map((j) => j.priorities.critical)), color: CHART_COLORS.bad },
    { name: 'High', value: sum(jiraData.map((j) => j.priorities.high)), color: CHART_COLORS.warn },
    { name: 'Medium', value: sum(jiraData.map((j) => j.priorities.medium)), color: CHART_COLORS.info },
    { name: 'Low', value: sum(jiraData.map((j) => j.priorities.low)), color: CHART_COLORS.ink3 },
  ];

  // Blocked per sprint
  const sprintNames = [...new Set(jiraData.map((j) => j.sprint))].sort();
  const blockedData = sprintNames.map((name) => {
    const items = jiraData.filter((j) => j.sprint === name);
    return { sprint: name, blocked: sum(items.map((j) => j.blocked)) };
  });

  // Throughput trend
  const throughputData = sprintNames.map((name) => {
    const items = jiraData.filter((j) => j.sprint === name);
    return { sprint: name, completed: sum(items.map((j) => j.completed)) };
  });

  // Lead/Cycle time trend
  const timeData = sprintNames.map((name) => {
    const items = jiraData.filter((j) => j.sprint === name);
    return {
      sprint: name,
      leadTime: mean(items.map((j) => j.avgLeadTimeDays)),
      cycleTime: mean(items.map((j) => j.avgCycleTimeDays)),
    };
  });

  return (
    <div className="space-y-6">
      {/* KPIs */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard label="Total Issues" value={String(totalIssues)} status="info" />
        <KpiCard label="Throughput Rate" value={`${Math.round(throughputRate * 100)}%`} status={throughputRate > 0.75 ? 'good' : 'warn'} />
        <KpiCard label="Avg Lead Time" value={`${avgLeadTime.toFixed(1)} days`} status={avgLeadTime < 9 ? 'good' : 'warn'} />
        <KpiCard label="Avg Cycle Time" value={`${avgCycleTime.toFixed(1)} days`} status={avgCycleTime < 4 ? 'good' : 'warn'} />
      </div>

      {/* Donut charts */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <ChartCard title="Status Distribution">
          <ResponsiveContainer width="100%" height={240}>
            <PieChart>
              <Pie data={statusData} cx="50%" cy="50%" innerRadius={55} outerRadius={85} dataKey="value" label={({ name, percent }) => `${name} ${Math.round(percent * 100)}%`} labelLine={false}>
                {statusData.map((entry, i) => (
                  <Cell key={i} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip contentStyle={{ fontSize: 12 }} />
            </PieChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Priority Distribution">
          <ResponsiveContainer width="100%" height={240}>
            <PieChart>
              <Pie data={priorityData} cx="50%" cy="50%" innerRadius={55} outerRadius={85} dataKey="value" label={({ name, percent }) => `${name} ${Math.round(percent * 100)}%`} labelLine={false}>
                {priorityData.map((entry, i) => (
                  <Cell key={i} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip contentStyle={{ fontSize: 12 }} />
            </PieChart>
          </ResponsiveContainer>
        </ChartCard>
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <ChartCard title="Blocked Items" subtitle="Per sprint">
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={blockedData} {...COMMON_CHART_PROPS}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="sprint" tick={{ fontSize: 11 }} />
              <YAxis tick={{ fontSize: 11 }} />
              <Tooltip contentStyle={{ fontSize: 12 }} />
              <Bar dataKey="blocked" fill={CHART_COLORS.bad} name="Blocked" radius={[3, 3, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Throughput" subtitle="Completed issues per sprint">
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={throughputData} {...COMMON_CHART_PROPS}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="sprint" tick={{ fontSize: 11 }} />
              <YAxis tick={{ fontSize: 11 }} />
              <Tooltip contentStyle={{ fontSize: 12 }} />
              <Bar dataKey="completed" fill={CHART_COLORS.good} name="Completed" radius={[3, 3, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </ChartCard>
      </div>

      {/* Lead/Cycle Time */}
      <ChartCard title="Lead & Cycle Time Trend" subtitle="Days">
        <ResponsiveContainer width="100%" height={220}>
          <LineChart data={timeData} {...COMMON_CHART_PROPS}>
            <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
            <XAxis dataKey="sprint" tick={{ fontSize: 11 }} />
            <YAxis tick={{ fontSize: 11 }} />
            <Tooltip contentStyle={{ fontSize: 12 }} />
            <Legend wrapperStyle={{ fontSize: 11 }} />
            <Line type="monotone" dataKey="leadTime" stroke={CHART_COLORS.brand} strokeWidth={2} dot={{ r: 4 }} name="Lead Time" />
            <Line type="monotone" dataKey="cycleTime" stroke={CHART_COLORS.info} strokeWidth={2} dot={{ r: 4 }} name="Cycle Time" />
          </LineChart>
        </ResponsiveContainer>
      </ChartCard>
    </div>
  );
}
