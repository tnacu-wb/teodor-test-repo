export const CHART_COLORS = {
  brand: '#5c2d82',
  brand2: '#7b3faf',
  good: '#1f9d6b',
  warn: '#c8811a',
  bad: '#cf3b45',
  info: '#2f6fed',
  ink2: '#5a6675',
  ink3: '#8a94a3',
};

export const TEAM_COLORS: Record<string, string> = {
  ds: '#5c2d82',
  bp: '#2f6fed',
  mm: '#1f9d6b',
  id: '#c8811a',
  asl: '#cf3b45',
  qa: '#7b3faf',
};

export function teamColor(teamId: string): string {
  return TEAM_COLORS[teamId] ?? CHART_COLORS.ink2;
}

export function healthyBandLines(low: number, high: number) {
  return [
    { y: low, stroke: CHART_COLORS.good, strokeDasharray: '4 4', label: `${Math.round(low * 100)}%` },
    { y: high, stroke: CHART_COLORS.good, strokeDasharray: '4 4', label: `${Math.round(high * 100)}%` },
  ];
}

export function getBarColor(sayDo: number): string {
  if (sayDo >= 0.8 && sayDo <= 1.05) return CHART_COLORS.good;
  if (sayDo >= 0.7) return CHART_COLORS.warn;
  return CHART_COLORS.bad;
}

export const COMMON_CHART_PROPS = {
  margin: { top: 8, right: 16, left: 0, bottom: 4 },
};
