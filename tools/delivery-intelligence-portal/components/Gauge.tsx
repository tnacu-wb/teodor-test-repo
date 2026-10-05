'use client';

interface GaugeProps {
  value: number; // 0-1
  label: string;
  color?: string;
  size?: number;
}

export function Gauge({ value, label, color = '#5c2d82', size = 80 }: GaugeProps) {
  const pct = Math.round(value * 100);
  const radius = (size - 8) / 2;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference * (1 - value);

  return (
    <div className="flex flex-col items-center gap-1">
      <svg width={size} height={size} className="-rotate-90">
        <circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          fill="none"
          stroke="#e5e7eb"
          strokeWidth={6}
        />
        <circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          fill="none"
          stroke={color}
          strokeWidth={6}
          strokeDasharray={circumference}
          strokeDashoffset={offset}
          strokeLinecap="round"
        />
      </svg>
      <span className="text-lg font-bold text-ink">{pct}%</span>
      <span className="text-[10px] text-ink-3 text-center">{label}</span>
    </div>
  );
}
