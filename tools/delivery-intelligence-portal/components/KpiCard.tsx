'use client';

interface KpiCardProps {
  label: string;
  value: string;
  trend?: '↑' | '↓' | '→';
  status?: 'good' | 'warn' | 'bad' | 'info' | 'neutral';
  subtitle?: string;
}

const STATUS_CLASSES: Record<string, string> = {
  good: 'text-good',
  warn: 'text-warn',
  bad: 'text-bad',
  info: 'text-info',
  neutral: 'text-ink-2',
};

export function KpiCard({ label, value, trend, status = 'neutral', subtitle }: KpiCardProps) {
  return (
    <div className="kpi-card">
      <p className="text-xs text-ink-3 font-medium uppercase tracking-wide">{label}</p>
      <div className="flex items-baseline gap-2">
        <span className={`text-2xl font-bold ${STATUS_CLASSES[status]}`}>{value}</span>
        {trend && (
          <span className={`text-sm ${trend === '↑' ? 'text-good' : trend === '↓' ? 'text-bad' : 'text-ink-3'}`}>
            {trend}
          </span>
        )}
      </div>
      {subtitle && <p className="text-xs text-ink-3">{subtitle}</p>}
    </div>
  );
}
