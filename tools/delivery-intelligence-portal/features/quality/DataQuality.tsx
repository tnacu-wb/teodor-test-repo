'use client';

import { useMemo } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell } from 'recharts';
import { Sprint, Team, RawData, FilterState } from '@/lib/types';
import { sum } from '@/lib/metrics';
import { pct } from '@/lib/formatters';
import { CHART_COLORS, COMMON_CHART_PROPS } from '@/lib/chartHelpers';
import { KpiCard } from '@/components/KpiCard';
import { ChartCard } from '@/components/ChartCard';
import { CheckCircle2, XCircle, AlertTriangle } from 'lucide-react';

interface Props {
  sprints: Sprint[];
  teams: Team[];
  config: RawData['config'];
  raw: RawData;
  filters: FilterState;
}

interface QualityCheck {
  name: string;
  description: string;
  passed: number;
  failed: number;
  status: 'pass' | 'warn' | 'fail';
}

export function DataQuality({ sprints, teams, config, raw }: Props) {
  const checks: QualityCheck[] = useMemo(() => {
    const totalRecords = sprints.length;

    // Check 1: All sprints have committed > 0
    const hasCommitted = sprints.filter((s) => s.committed > 0).length;
    // Check 2: Delivered <= committed * 1.5 (no outlandish over-delivery)
    const reasonableDelivery = sprints.filter((s) => s.delivered <= s.committed * 1.5).length;
    // Check 3: Scope added can be negative (scope removed), but should be within expected bounds
    const validScope = sprints.filter((s) => s.scopeAdded >= -s.committed).length;
    // Check 4: Carry-forward is non-negative
    const validCarry = sprints.filter((s) => s.carryForward >= 0).length;
    // Check 5: Dates are valid
    const validDates = sprints.filter((s) => new Date(s.endDate) > new Date(s.startDate)).length;
    // Check 6: Team reference exists
    const validTeam = sprints.filter((s) => teams.some((t) => t.id === s.team)).length;

    return [
      { name: 'Commitment Data', description: 'All sprints have committed > 0', passed: hasCommitted, failed: totalRecords - hasCommitted, status: hasCommitted === totalRecords ? 'pass' : 'warn' },
      { name: 'Delivery Bounds', description: 'Delivered ≤ 150% of committed', passed: reasonableDelivery, failed: totalRecords - reasonableDelivery, status: reasonableDelivery === totalRecords ? 'pass' : 'warn' },
      { name: 'Scope Validity', description: 'Scope change is within expected bounds (>= -committed)', passed: validScope, failed: totalRecords - validScope, status: validScope === totalRecords ? 'pass' : 'warn' },
      { name: 'Carry-forward Validity', description: 'Carry-forward is non-negative', passed: validCarry, failed: totalRecords - validCarry, status: validCarry === totalRecords ? 'pass' : 'fail' },
      { name: 'Date Integrity', description: 'End date > Start date', passed: validDates, failed: totalRecords - validDates, status: validDates === totalRecords ? 'pass' : 'fail' },
      { name: 'Team References', description: 'All teams exist in team registry', passed: validTeam, failed: totalRecords - validTeam, status: validTeam === totalRecords ? 'pass' : 'fail' },
    ] as QualityCheck[];
  }, [sprints, teams]);

  const totalChecks = checks.length;
  const passedChecks = checks.filter((c) => c.status === 'pass').length;
  const qualityScore = totalChecks > 0 ? passedChecks / totalChecks : 0;

  // Valid records per sprint
  const sprintNames = [...new Set(sprints.map((s) => s.name))].sort();
  const validRecordData = sprintNames.map((name) => {
    const sprintRecords = sprints.filter((s) => s.name === name);
    const valid = sprintRecords.filter((s) => s.committed > 0 && s.carryForward >= 0 && s.scopeAdded >= 0).length;
    return { sprint: name, valid, total: sprintRecords.length, pct: Math.round((valid / sprintRecords.length) * 100) };
  });

  // Review table: records that need review
  const reviewRecords = sprints.filter((s) => {
    const sayDo = s.committed > 0 ? s.delivered / s.committed : 0;
    return sayDo > 1.2 || sayDo < 0.6 || s.scopeAdded > s.committed * 0.25;
  }).map((s) => ({
    sprint: s.name,
    team: teams.find((t) => t.id === s.team)?.name ?? s.team,
    issue: s.committed > 0 && s.delivered / s.committed > 1.2 ? 'High over-delivery' :
           s.committed > 0 && s.delivered / s.committed < 0.6 ? 'Very low delivery' :
           'High scope addition',
    committed: s.committed,
    delivered: s.delivered,
    scopeAdded: s.scopeAdded,
  }));

  return (
    <div className="space-y-6">
      {/* KPIs */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard label="Data Quality Score" value={pct(qualityScore)} status={qualityScore === 1 ? 'good' : qualityScore > 0.8 ? 'warn' : 'bad'} />
        <KpiCard label="Checks Passed" value={`${passedChecks}/${totalChecks}`} status={passedChecks === totalChecks ? 'good' : 'warn'} />
        <KpiCard label="Total Records" value={String(sprints.length)} status="info" />
        <KpiCard label="Records for Review" value={String(reviewRecords.length)} status={reviewRecords.length === 0 ? 'good' : 'warn'} />
      </div>

      {/* Quality checks panel */}
      <div className="card">
        <h3 className="text-sm font-semibold text-ink mb-3">Quality Checks</h3>
        <div className="space-y-2">
          {checks.map((check) => (
            <div key={check.name} className="flex items-center gap-3 p-2.5 rounded-lg border border-ink-3/10">
              {check.status === 'pass' ? (
                <CheckCircle2 size={16} className="text-good shrink-0" />
              ) : check.status === 'warn' ? (
                <AlertTriangle size={16} className="text-warn shrink-0" />
              ) : (
                <XCircle size={16} className="text-bad shrink-0" />
              )}
              <div className="flex-1">
                <p className="text-xs font-medium text-ink">{check.name}</p>
                <p className="text-[10px] text-ink-3">{check.description}</p>
              </div>
              <span className="text-xs text-ink-2">{check.passed}/{check.passed + check.failed} passed</span>
            </div>
          ))}
        </div>
      </div>

      {/* Valid records chart */}
      <ChartCard title="Valid Records per Sprint" subtitle="% of records passing all checks">
        <ResponsiveContainer width="100%" height={200}>
          <BarChart data={validRecordData} {...COMMON_CHART_PROPS}>
            <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
            <XAxis dataKey="sprint" tick={{ fontSize: 11 }} />
            <YAxis domain={[0, 100]} tick={{ fontSize: 11 }} />
            <Tooltip formatter={(v: number) => `${v}%`} contentStyle={{ fontSize: 12 }} />
            <Bar dataKey="pct" name="Valid %" radius={[3, 3, 0, 0]}>
              {validRecordData.map((entry, i) => (
                <Cell key={i} fill={entry.pct === 100 ? CHART_COLORS.good : entry.pct > 80 ? CHART_COLORS.warn : CHART_COLORS.bad} />
              ))}
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      </ChartCard>

      {/* Records for review */}
      {reviewRecords.length > 0 && (
        <div className="card">
          <h3 className="text-sm font-semibold text-ink mb-3">Records for Review</h3>
          <div className="overflow-x-auto rounded-lg border border-ink-3/10">
            <table className="w-full text-xs">
              <thead>
                <tr className="bg-bg border-b border-ink-3/10">
                  <th className="px-3 py-2 text-left font-semibold text-ink-2">Sprint</th>
                  <th className="px-3 py-2 text-left font-semibold text-ink-2">Team</th>
                  <th className="px-3 py-2 text-left font-semibold text-ink-2">Issue</th>
                  <th className="px-3 py-2 text-left font-semibold text-ink-2">Committed</th>
                  <th className="px-3 py-2 text-left font-semibold text-ink-2">Delivered</th>
                  <th className="px-3 py-2 text-left font-semibold text-ink-2">Scope Added</th>
                </tr>
              </thead>
              <tbody>
                {reviewRecords.map((r, i) => (
                  <tr key={i} className="border-b border-ink-3/5">
                    <td className="px-3 py-2 text-ink">{r.sprint}</td>
                    <td className="px-3 py-2 text-ink">{r.team}</td>
                    <td className="px-3 py-2">
                      <span className="badge bg-warn/10 text-warn">{r.issue}</span>
                    </td>
                    <td className="px-3 py-2 text-ink">{r.committed}</td>
                    <td className="px-3 py-2 text-ink">{r.delivered}</td>
                    <td className="px-3 py-2 text-ink">{r.scopeAdded}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
