'use client';

export type EnvironmentOption = 'UAT' | 'SIT' | 'PERF';

const ENVIRONMENTS: { id: EnvironmentOption; label: string; color: string }[] = [
  { id: 'UAT', label: 'UAT', color: '#2563eb' },
  { id: 'SIT', label: 'SIT', color: '#7c3aed' },
  { id: 'PERF', label: 'PERF', color: '#dc2626' },
];

export default function EnvironmentSelector({
  selected,
  onChange,
}: {
  selected: EnvironmentOption;
  onChange: (env: EnvironmentOption) => void;
}) {
  return (
    <div className="env-selector">
      <label className="env-label">Environment:</label>
      <div className="env-buttons">
        {ENVIRONMENTS.map((env) => (
          <button
            key={env.id}
            type="button"
            className={`env-btn ${selected === env.id ? 'active' : ''}`}
            style={selected === env.id ? { background: env.color, borderColor: env.color } : {}}
            onClick={() => onChange(env.id)}
          >
            {env.label}
          </button>
        ))}
      </div>
      <span className="env-indicator" style={{ color: ENVIRONMENTS.find((e) => e.id === selected)?.color }}>
        ● Connected to {selected}
      </span>
    </div>
  );
}
