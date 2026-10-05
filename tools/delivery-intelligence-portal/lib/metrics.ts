import { Sprint, Team, TeamMetric, PortfolioMetric, SprintAggregate, RawData } from './types';

// ─── Utility functions ───────────────────────────────────────────────────────

export function sum(arr: number[]): number {
  return arr.reduce((a, b) => a + b, 0);
}

export function mean(arr: number[]): number {
  if (arr.length === 0) return 0;
  return sum(arr) / arr.length;
}

export function stddev(arr: number[]): number {
  if (arr.length === 0) return 0;
  const m = mean(arr);
  const squaredDiffs = arr.map((v) => (v - m) ** 2);
  return Math.sqrt(mean(squaredDiffs));
}

export function linearRegression(arr: number[]): { slope: number; intercept: number } {
  const n = arr.length;
  if (n < 2) return { slope: 0, intercept: arr[0] ?? 0 };
  const xs = arr.map((_, i) => i);
  const xMean = mean(xs);
  const yMean = mean(arr);
  let num = 0;
  let den = 0;
  for (let i = 0; i < n; i++) {
    num += (xs[i] - xMean) * (arr[i] - yMean);
    den += (xs[i] - xMean) ** 2;
  }
  const slope = den === 0 ? 0 : num / den;
  const intercept = yMean - slope * xMean;
  return { slope, intercept };
}

export function halfTrend(arr: number[]): number {
  if (arr.length === 0) return 0;
  const half = Math.ceil(arr.length / 2);
  return mean(arr.slice(half));
}

export function clamp(val: number, min: number, max: number): number {
  return Math.max(min, Math.min(max, val));
}

// ─── Team Metrics ────────────────────────────────────────────────────────────

export function teamMetrics(sprints: Sprint[], teamId: string): TeamMetric {
  const teamSprints = sprints.filter((s) => s.team === teamId);
  const committed = sum(teamSprints.map((s) => s.committed));
  const delivered = sum(teamSprints.map((s) => s.delivered));
  const scopeAdded = sum(teamSprints.map((s) => s.scopeAdded));
  const carryForward = sum(teamSprints.map((s) => s.carryForward));
  const sayDo = committed > 0 ? delivered / committed : 0;
  const overDelivery = committed > 0 && delivered > committed ? (delivered - committed) / committed : 0;
  const deliveredArr = teamSprints.map((s) => s.delivered);
  const avgVelocity = mean(deliveredArr);
  const rawCv = avgVelocity > 0 ? stddev(deliveredArr) / avgVelocity : 0;

  // Trend-adjusted CV
  const { slope } = linearRegression(deliveredArr);
  const trendPct = avgVelocity > 0 ? (slope * deliveredArr.length) / avgVelocity : 0;
  const detrended = deliveredArr.map((v, i) => v - slope * i);
  const detrendedMean = mean(detrended);
  const trendAdjustedCv = detrendedMean > 0 ? stddev(detrended) / detrendedMean : rawCv;

  // Stability
  const stability: TeamMetric['stability'] =
    trendAdjustedCv < 0.15 ? 'Stable' : trendAdjustedCv < 0.25 ? 'Moderate' : 'Volatile';

  // Predictability
  const predictability: TeamMetric['predictability'] =
    stability === 'Stable' ? 'Predictable' : stability === 'Moderate' ? 'Moderate' : 'Unpredictable';

  // Scope creep
  const scopeCreep = committed > 0 ? scopeAdded / committed : 0;

  // Commitment reliability
  const commitmentReliability = clamp(sayDo, 0, 1);

  // Delivery health based on sayDo
  const deliveryHealth = sayDoBandTeam(sayDo);

  return {
    team: teamId,
    vendor: teamSprints[0]?.vendor ?? 'Unknown',
    committed,
    delivered,
    scopeAdded,
    carryForward,
    sayDo,
    overDelivery,
    avgVelocity,
    rawCv,
    trendAdjustedCv,
    trendPct,
    stability,
    predictability,
    scopeCreep,
    commitmentReliability,
    deliveryHealth,
    sprintCount: teamSprints.length,
  };
}

// ─── Portfolio Metrics ───────────────────────────────────────────────────────

export function portfolio(
  sprints: Sprint[],
  teams: Team[],
  config: RawData['config']
): PortfolioMetric {
  const totalCommitted = sum(sprints.map((s) => s.committed));
  const totalDelivered = sum(sprints.map((s) => s.delivered));
  const totalScopeAdded = sum(sprints.map((s) => s.scopeAdded));
  const totalCarryForward = sum(sprints.map((s) => s.carryForward));
  const sayDo = totalCommitted > 0 ? totalDelivered / totalCommitted : 0;
  const deliveryHealth = healthBand(sayDo, config);

  const teamMetricsArr = teams.map((t) => teamMetrics(sprints, t.id));
  const cvs = teamMetricsArr.map((t) => t.trendAdjustedCv);
  const avgCv = mean(cvs);
  const sprintStability: PortfolioMetric['sprintStability'] =
    avgCv < 0.15 ? 'Stable' : avgCv < 0.25 ? 'Moderate' : 'Volatile';

  const goodCount = teamMetricsArr.filter((t) => t.deliveryHealth === 'Good').length;
  const teamPerformanceIndex = teams.length > 0 ? goodCount / teams.length : 0;

  const scopeCreep = totalCommitted > 0 ? totalScopeAdded / totalCommitted : 0;

const sprintSeries = aggBySprint(sprints);
  const deliveredArr = sprintSeries.map((s) => s.delivered);
  const { slope } = linearRegression(deliveredArr);
  const velocityTrend = mean(deliveredArr) > 0 ? slope / mean(deliveredArr) : 0;

  return {
    totalCommitted,
    totalDelivered,
    totalScopeAdded,
    totalCarryForward,
    sayDo,
    deliveryHealth,
    sprintStability,
    teamPerformanceIndex,
    scopeCreep,
    velocityTrend,
    activeTeams: teams.length,
    activeSprints: new Set(sprints.map((s) => s.name)).size,
    sprintSeries,
  };
}

// ─── Sprint Aggregation ──────────────────────────────────────────────────────

export function aggBySprint(sprints: Sprint[]): SprintAggregate[] {
  const grouped = new Map<string, Sprint[]>();
  for (const s of sprints) {
    const existing = grouped.get(s.name) ?? [];
    existing.push(s);
    grouped.set(s.name, existing);
  }
  const result: SprintAggregate[] = [];
  for (const [name, group] of grouped) {
    const committed = sum(group.map((s) => s.committed));
    const delivered = sum(group.map((s) => s.delivered));
    const scopeAddedVal = sum(group.map((s) => s.scopeAdded));
    const carryForwardVal = sum(group.map((s) => s.carryForward));
    result.push({
      sprint: name,
      committed,
      delivered,
      scopeAdded: scopeAddedVal,
      carryForward: carryForwardVal,
      sayDo: committed > 0 ? delivered / committed : 0,
    });
  }
  return result;
}

// ─── Health Bands ────────────────────────────────────────────────────────────

export function healthBand(
  sayDo: number,
  config: RawData['config']
): 'Good' | 'Warning' | 'At Risk' {
  if (sayDo >= config.healthyBandLow && sayDo <= config.healthyBandHigh) return 'Good';
  if (sayDo >= config.healthyBandLow * 0.9) return 'Warning';
  return 'At Risk';
}

export function sayDoBandTeam(sayDo: number): 'Good' | 'Warning' | 'At Risk' {
  if (sayDo >= 0.8 && sayDo <= 1.05) return 'Good';
  if (sayDo >= 0.7) return 'Warning';
  return 'At Risk';
}
