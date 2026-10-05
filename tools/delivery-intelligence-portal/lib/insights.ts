import { PortfolioMetric, TeamMetric } from './types';
import { pct } from './formatters';

export interface Insight {
  type: 'success' | 'warning' | 'risk' | 'info';
  title: string;
  body: string;
}

export function portfolioInsights(p: PortfolioMetric, teamMetrics: TeamMetric[]): Insight[] {
  const insights: Insight[] = [];

  // Delivery health
  if (p.deliveryHealth === 'Good') {
    insights.push({
      type: 'success',
      title: 'Delivery Health is Good',
      body: `The portfolio is delivering ${pct(p.sayDo)} of commitments — within the healthy band.`,
    });
  } else if (p.deliveryHealth === 'Warning') {
    insights.push({
      type: 'warning',
      title: 'Delivery Health Warning',
      body: `Say/Do ratio is ${pct(p.sayDo)} — slightly below the healthy threshold.`,
    });
  } else {
    insights.push({
      type: 'risk',
      title: 'Delivery Health At Risk',
      body: `Only ${pct(p.sayDo)} of committed work is being delivered. Immediate attention required.`,
    });
  }

  // Scope creep
  if (p.scopeCreep > 0.15) {
    insights.push({
      type: 'warning',
      title: 'Scope Creep Elevated',
      body: `${pct(p.scopeCreep)} of work is being added mid-sprint. Consider tighter scope locks.`,
    });
  } else {
    insights.push({
      type: 'success',
      title: 'Scope Creep Controlled',
      body: `Scope additions at ${pct(p.scopeCreep)} — within acceptable limits.`,
    });
  }

  // Sprint stability
  insights.push({
    type: p.sprintStability === 'Stable' ? 'success' : p.sprintStability === 'Moderate' ? 'info' : 'risk',
    title: `Sprint Stability: ${p.sprintStability}`,
    body: `Velocity coefficient of variation indicates ${p.sprintStability.toLowerCase()} delivery cadence.`,
  });

  // Team performance
  const atRiskTeams = teamMetrics.filter((t) => t.deliveryHealth === 'At Risk');
  if (atRiskTeams.length > 0) {
    insights.push({
      type: 'risk',
      title: `${atRiskTeams.length} Team(s) At Risk`,
      body: `Teams needing attention: ${atRiskTeams.map((t) => t.team).join(', ')}`,
    });
  }

  // Carry-forward
  const carryRatio = p.totalCommitted > 0 ? p.totalCarryForward / p.totalCommitted : 0;
  if (carryRatio > 0.15) {
    insights.push({
      type: 'warning',
      title: 'Carry-forward Elevated',
      body: `${pct(carryRatio)} of committed work is being carried forward between sprints.`,
    });
  }

  return insights;
}

export function teamInsights(t: TeamMetric): Insight[] {
  const insights: Insight[] = [];

  insights.push({
    type: t.deliveryHealth === 'Good' ? 'success' : t.deliveryHealth === 'Warning' ? 'warning' : 'risk',
    title: `Delivery: ${t.deliveryHealth}`,
    body: `Say/Do ${pct(t.sayDo)} | Velocity ${Math.round(t.avgVelocity)} pts/sprint | Stability: ${t.stability}`,
  });

  if (t.scopeCreep > 0.12) {
    insights.push({
      type: 'warning',
      title: 'High Scope Additions',
      body: `${pct(t.scopeCreep)} scope added vs committed — review sprint planning accuracy.`,
    });
  }

  if (t.overDelivery > 0.05) {
    insights.push({
      type: 'info',
      title: 'Over-delivery Detected',
      body: `Delivering ${pct(t.overDelivery)} above commitment — consider raising commitment.`,
    });
  }

  return insights;
}
