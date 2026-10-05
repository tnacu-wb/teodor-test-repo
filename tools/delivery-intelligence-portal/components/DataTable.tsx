'use client';

import { useState, useMemo } from 'react';
import { Search, ArrowUpDown } from 'lucide-react';

export interface Column<T> {
  key: string;
  label: string;
  render?: (row: T) => React.ReactNode;
  sortable?: boolean;
}

interface DataTableProps<T> {
  data: T[];
  columns: Column<T>[];
  searchKeys?: string[];
  pageSize?: number;
}

export function DataTable<T extends Record<string, unknown>>({
  data, columns, searchKeys = [], pageSize = 20,
}: DataTableProps<T>) {
  const [search, setSearch] = useState('');
  const [sortKey, setSortKey] = useState<string | null>(null);
  const [sortDir, setSortDir] = useState<'asc' | 'desc'>('asc');

  const filtered = useMemo(() => {
    if (!search) return data;
    const lower = search.toLowerCase();
    return data.filter((row) =>
      searchKeys.some((key) => String(row[key] ?? '').toLowerCase().includes(lower))
    );
  }, [data, search, searchKeys]);

  const sorted = useMemo(() => {
    if (!sortKey) return filtered;
    return [...filtered].sort((a, b) => {
      const aVal = a[sortKey];
      const bVal = b[sortKey];
      if (typeof aVal === 'number' && typeof bVal === 'number') {
        return sortDir === 'asc' ? aVal - bVal : bVal - aVal;
      }
      if (typeof aVal === 'string' && typeof bVal === 'string' && aVal.endsWith('%') && bVal.endsWith('%')) {
        const aNum = Number.parseFloat(aVal);
        const bNum = Number.parseFloat(bVal);
        return sortDir === 'asc' ? aNum - bNum : bNum - aNum;
      }
      return sortDir === 'asc'
        ? String(aVal).localeCompare(String(bVal))
        : String(bVal).localeCompare(String(aVal));
    });
  }, [filtered, sortKey, sortDir]);

  const displayed = sorted.slice(0, pageSize);

  const toggleSort = (key: string) => {
    if (sortKey === key) {
      setSortDir((d) => (d === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortKey(key);
      setSortDir('asc');
    }
  };

  return (
    <div className="space-y-3">
      {searchKeys.length > 0 && (
        <div className="relative max-w-xs">
          <Search size={14} className="absolute left-2.5 top-1/2 -translate-y-1/2 text-ink-3" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search..."
            className="w-full text-xs border border-ink-3/20 rounded-md pl-8 pr-3 py-1.5 bg-panel text-ink placeholder:text-ink-3"
          />
        </div>
      )}
      <div className="overflow-x-auto rounded-lg border border-ink-3/10">
        <table className="w-full text-xs">
          <thead>
            <tr className="bg-bg border-b border-ink-3/10">
              {columns.map((col) => (
                <th
                  key={col.key}
                  className="px-3 py-2 text-left font-semibold text-ink-2 whitespace-nowrap"
                >
                  {col.sortable !== false ? (
                    <button onClick={() => toggleSort(col.key)} className="flex items-center gap-1 hover:text-ink">
                      {col.label}
                      <ArrowUpDown size={12} className="text-ink-3" />
                    </button>
                  ) : (
                    col.label
                  )}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {displayed.map((row, i) => (
              <tr key={i} className="border-b border-ink-3/5 hover:bg-bg/50">
                {columns.map((col) => (
                  <td key={col.key} className="px-3 py-2 text-ink whitespace-nowrap">
                    {col.render ? col.render(row) : String(row[col.key] ?? '')}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {sorted.length > pageSize && (
        <p className="text-xs text-ink-3">Showing {pageSize} of {sorted.length} records</p>
      )}
    </div>
  );
}
