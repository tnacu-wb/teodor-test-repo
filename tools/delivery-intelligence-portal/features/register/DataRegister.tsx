'use client';

import { useMemo, useState } from 'react';
import { Sprint, Team, RawData, FilterState, RegisterRecord } from '@/lib/types';
import { DataTable, Column } from '@/components/DataTable';
import { Download } from 'lucide-react';

interface Props {
  sprints: Sprint[];
  teams: Team[];
  config: RawData['config'];
  raw: RawData;
  filters: FilterState;
}

function classifyRecord(s: Sprint): RegisterRecord['type'] {
  const sayDo = s.committed > 0 ? s.delivered / s.committed : 0;
  if (sayDo > 1.05) return 'Over-delivered';
  if (sayDo < 0.8) return 'Under-delivered';
  if (s.scopeAdded > s.committed * 0.15) return 'Scope added';
  return 'Normal';
}

export function DataRegister({ sprints, teams, config, raw }: Props) {
  const [vendorFilter, setVendorFilter] = useState<string | null>(null);
  const [typeFilter, setTypeFilter] = useState<string | null>(null);

  const records: RegisterRecord[] = useMemo(() => {
    return sprints.map((s) => ({
      id: s.id,
      sprint: s.name,
      team: teams.find((t) => t.id === s.team)?.name ?? s.team,
      vendor: s.vendor,
      committed: s.committed,
      delivered: s.delivered,
      scopeAdded: s.scopeAdded,
      carryForward: s.carryForward,
      sayDo: s.committed > 0 ? s.delivered / s.committed : 0,
      type: classifyRecord(s),
    }));
  }, [sprints, teams]);

  const filteredRecords = useMemo(() => {
    let data = records;
    if (vendorFilter) data = data.filter((r) => r.vendor === vendorFilter);
    if (typeFilter) data = data.filter((r) => r.type === typeFilter);
    return data;
  }, [records, vendorFilter, typeFilter]);

  const vendors = [...new Set(records.map((r) => r.vendor))];
  const types: RegisterRecord['type'][] = ['Normal', 'Under-delivered', 'Over-delivered', 'Scope added'];

  const exportCsv = () => {
    const headers = ['ID', 'Sprint', 'Team', 'Vendor', 'Committed', 'Delivered', 'Scope Added', 'Carry Forward', 'Say/Do', 'Type'];
    const rows = filteredRecords.map((r) =>
      [r.id, r.sprint, r.team, r.vendor, r.committed, r.delivered, r.scopeAdded, r.carryForward, Math.round(r.sayDo * 100) + '%', r.type].join(',')
    );
    const csv = [headers.join(','), ...rows].join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'delivery-register.csv';
    a.click();
    URL.revokeObjectURL(url);
  };

  const columns: Column<RegisterRecord>[] = [
    { key: 'sprint', label: 'Sprint' },
    { key: 'team', label: 'Team' },
    { key: 'vendor', label: 'Vendor', render: (row) => (
      <span className={`badge ${row.vendor === 'Whitbread' ? 'bg-brand/10 text-brand' : 'bg-info/10 text-info'}`}>
        {row.vendor}
      </span>
    )},
    { key: 'committed', label: 'Committed' },
    { key: 'delivered', label: 'Delivered' },
    { key: 'scopeAdded', label: 'Scope Added' },
    { key: 'carryForward', label: 'Carry Fwd' },
    { key: 'sayDo', label: 'Say/Do', render: (row) => `${Math.round(row.sayDo * 100)}%` },
    { key: 'type', label: 'Type', render: (row) => (
      <span className={`badge ${
        row.type === 'Normal' ? 'bg-good/10 text-good' :
        row.type === 'Over-delivered' ? 'bg-info/10 text-info' :
        row.type === 'Under-delivered' ? 'bg-bad/10 text-bad' :
        'bg-warn/10 text-warn'
      }`}>
        {row.type}
      </span>
    )},
  ];

  return (
    <div className="space-y-4">
      {/* Filter toolbar */}
      <div className="flex flex-wrap items-center gap-3">
        {/* Vendor pills */}
        <div className="flex items-center gap-1.5">
          <span className="text-xs text-ink-3 font-medium">Vendor:</span>
          <button
            onClick={() => setVendorFilter(null)}
            className={`text-xs px-2.5 py-1 rounded-full border transition-colors ${!vendorFilter ? 'bg-brand text-white border-brand' : 'bg-panel text-ink-2 border-ink-3/20 hover:border-brand'}`}
          >
            All
          </button>
          {vendors.map((v) => (
            <button
              key={v}
              onClick={() => setVendorFilter(v)}
              className={`text-xs px-2.5 py-1 rounded-full border transition-colors ${vendorFilter === v ? 'bg-brand text-white border-brand' : 'bg-panel text-ink-2 border-ink-3/20 hover:border-brand'}`}
            >
              {v}
            </button>
          ))}
        </div>

        {/* Type filter */}
        <div className="flex items-center gap-1.5">
          <span className="text-xs text-ink-3 font-medium">Type:</span>
          <button
            onClick={() => setTypeFilter(null)}
            className={`text-xs px-2.5 py-1 rounded-full border transition-colors ${!typeFilter ? 'bg-brand text-white border-brand' : 'bg-panel text-ink-2 border-ink-3/20 hover:border-brand'}`}
          >
            All
          </button>
          {types.map((t) => (
            <button
              key={t}
              onClick={() => setTypeFilter(t)}
              className={`text-xs px-2.5 py-1 rounded-full border transition-colors ${typeFilter === t ? 'bg-brand text-white border-brand' : 'bg-panel text-ink-2 border-ink-3/20 hover:border-brand'}`}
            >
              {t}
            </button>
          ))}
        </div>

        {/* CSV export */}
        <button
          onClick={exportCsv}
          className="ml-auto flex items-center gap-1.5 text-xs font-medium text-brand hover:text-brand-2 transition-colors"
        >
          <Download size={14} />
          Export CSV
        </button>
      </div>

      {/* Table */}
      <div className="card">
        <DataTable
          data={filteredRecords as unknown as Record<string, unknown>[]}
          columns={columns as unknown as Column<Record<string, unknown>>[]}
          searchKeys={['team', 'sprint', 'vendor', 'type']}
          pageSize={30}
        />
      </div>

      <p className="text-xs text-ink-3">{filteredRecords.length} records</p>
    </div>
  );
}
