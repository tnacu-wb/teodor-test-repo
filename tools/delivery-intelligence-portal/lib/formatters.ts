export function pct(val: number): string {
  return `${Math.round(val * 100)}%`;
}

export function pts(val: number): string {
  return `${Math.round(val)} pts`;
}

export function band(val: 'Good' | 'Warning' | 'At Risk'): string {
  switch (val) {
    case 'Good':
      return 'bg-good/10 text-good border border-good/20';
    case 'Warning':
      return 'bg-warn/10 text-warn border border-warn/20';
    case 'At Risk':
      return 'bg-bad/10 text-bad border border-bad/20';
  }
}

export function trendIcon(val: number): '↑' | '↓' | '→' {
  if (val > 0.02) return '↑';
  if (val < -0.02) return '↓';
  return '→';
}

export function decimal(val: number, places = 1): string {
  return val.toFixed(places);
}
