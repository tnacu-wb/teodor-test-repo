'use client';

import { AlertTriangle, CheckCircle, Info, XCircle } from 'lucide-react';

interface InsightCardProps {
  type: 'success' | 'warning' | 'risk' | 'info';
  title: string;
  body: string;
}

const ICON_MAP = {
  success: CheckCircle,
  warning: AlertTriangle,
  risk: XCircle,
  info: Info,
};

const STYLE_MAP = {
  success: 'border-good/20 bg-good/5',
  warning: 'border-warn/20 bg-warn/5',
  risk: 'border-bad/20 bg-bad/5',
  info: 'border-info/20 bg-info/5',
};

const ICON_COLOR = {
  success: 'text-good',
  warning: 'text-warn',
  risk: 'text-bad',
  info: 'text-info',
};

export function InsightCard({ type, title, body }: InsightCardProps) {
  const Icon = ICON_MAP[type];
  return (
    <div className={`rounded-lg border p-4 ${STYLE_MAP[type]}`}>
      <div className="flex items-start gap-3">
        <Icon size={18} className={`mt-0.5 shrink-0 ${ICON_COLOR[type]}`} />
        <div>
          <p className="text-sm font-semibold text-ink">{title}</p>
          <p className="text-xs text-ink-2 mt-0.5">{body}</p>
        </div>
      </div>
    </div>
  );
}
