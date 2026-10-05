'use client';

import { useState, useRef, useCallback } from 'react';
import { Upload, FileJson, FileSpreadsheet, X, CheckCircle2, AlertTriangle, Download } from 'lucide-react';
import { RawData, Sprint, Team, DcCapacity, JiraSnapshot } from '@/lib/types';
import { RAW } from '@/lib/rawData';

interface DataImportProps {
  onImport: (data: RawData) => void;
  onClose: () => void;
}

type ImportTab = 'json' | 'csv' | 'template';

interface ValidationResult {
  valid: boolean;
  errors: string[];
  warnings: string[];
  summary: { teams: number; sprints: number; capacity: number; jira: number };
}

function parseCSVRow(row: string): string[] {
  const result: string[] = [];
  let current = '';
  let inQuotes = false;
  for (let i = 0; i < row.length; i++) {
    const ch = row[i];
    if (ch === '"') {
      inQuotes = !inQuotes;
    } else if (ch === ',' && !inQuotes) {
      result.push(current.trim());
      current = '';
    } else {
      current += ch;
    }
  }
  result.push(current.trim());
  return result;
}

function parseCSV(text: string): Record<string, string>[] {
  const lines = text.split(/\r?\n/).filter((l) => l.trim());
  if (lines.length < 2) return [];
  const headers = parseCSVRow(lines[0]).map((h) => h.toLowerCase().replace(/\s+/g, '_'));
  return lines.slice(1).map((line) => {
    const values = parseCSVRow(line);
    const record: Record<string, string> = {};
    headers.forEach((h, i) => {
      record[h] = values[i] ?? '';
    });
    return record;
  });
}

function csvToSprints(records: Record<string, string>[]): Sprint[] {
  return records.map((r, i) => ({
    id: r.id || `imported-${i}`,
    name: r.name || r.sprint_name || r.sprint || '',
    team: r.team || r.team_id || '',
    vendor: r.vendor || 'Unknown',
    startDate: r.start_date || r.startdate || r.start || '',
    endDate: r.end_date || r.enddate || r.end || '',
    committed: Number(r.committed) || 0,
    delivered: Number(r.delivered) || 0,
    scopeAdded: Number(r.scope_added || r.scopeadded || r.added) || 0,
    carryForward: Number(r.carry_forward || r.carryforward || r.carry) || 0,
    goalMet: ['true', '1', 'yes', 'y'].includes((r.goal_met || r.goalmet || '').toLowerCase()),
  }));
}

function csvToTeams(records: Record<string, string>[]): Team[] {
  return records.map((r) => ({
    id: r.id || r.team_id || '',
    name: r.name || r.team_name || '',
    vendor: r.vendor || 'Unknown',
    members: (r.members || '').split(';').map((m) => m.trim()).filter(Boolean),
    capacity: Number(r.capacity) || 0,
  }));
}

function validateData(data: Partial<RawData>): ValidationResult {
  const errors: string[] = [];
  const warnings: string[] = [];
  const summary = { teams: 0, sprints: 0, capacity: 0, jira: 0 };

  if (!data.sprints || data.sprints.length === 0) {
    errors.push('No sprint data found. At least one sprint is required.');
  } else {
    summary.sprints = data.sprints.length;
    const missingTeam = data.sprints.filter((s) => !s.team);
    if (missingTeam.length > 0) errors.push(`${missingTeam.length} sprint(s) missing team field.`);
    const missingName = data.sprints.filter((s) => !s.name);
    if (missingName.length > 0) errors.push(`${missingName.length} sprint(s) missing name field.`);
    const negatives = data.sprints.filter((s) => s.committed < 0 || s.delivered < 0);
    if (negatives.length > 0) warnings.push(`${negatives.length} sprint(s) have negative values.`);
  }

  if (!data.teams || data.teams.length === 0) {
    warnings.push('No teams defined. Will use teams derived from sprint data.');
  } else {
    summary.teams = data.teams.length;
  }

  if (data.dcCapacity) summary.capacity = data.dcCapacity.length;
  if (data.jiraSnapshots) summary.jira = data.jiraSnapshots.length;

  return { valid: errors.length === 0, errors, warnings, summary };
}

function deriveTeamsFromSprints(sprints: Sprint[]): Team[] {
  const teamMap = new Map<string, Team>();
  for (const s of sprints) {
    if (!teamMap.has(s.team)) {
      teamMap.set(s.team, {
        id: s.team,
        name: s.team,
        vendor: s.vendor,
        members: [],
        capacity: 0,
      });
    }
  }
  return Array.from(teamMap.values());
}

function generateTemplate(): string {
  const headers = 'id,name,team,vendor,start_date,end_date,committed,delivered,scope_added,carry_forward,goal_met';
  const example = 'ds-s1,Sprint 1,ds,Whitbread,2026-06-02,2026-06-13,34,31,4,3,true';
  return `${headers}\n${example}\n`;
}

function generateTeamsTemplate(): string {
  const headers = 'id,name,vendor,members,capacity';
  const example = 'ds,Discover & Search,Whitbread,Alice;Bob;Carol,40';
  return `${headers}\n${example}\n`;
}

export function DataImport({ onImport, onClose }: DataImportProps) {
  const [tab, setTab] = useState<ImportTab>('json');
  const [dragOver, setDragOver] = useState(false);
  const [preview, setPreview] = useState<Partial<RawData> | null>(null);
  const [validation, setValidation] = useState<ValidationResult | null>(null);
  const [error, setError] = useState<string | null>(null);
  const fileRef = useRef<HTMLInputElement>(null);

  const processFile = useCallback((file: File) => {
    setError(null);
    setPreview(null);
    setValidation(null);

    const reader = new FileReader();
    reader.onload = (e) => {
      const text = e.target?.result as string;
      try {
        if (file.name.endsWith('.json')) {
          const parsed = JSON.parse(text);
          // Support both full RawData and partial (just sprints array)
          let data: Partial<RawData>;
          if (Array.isArray(parsed)) {
            const sprints = parsed as Sprint[];
            data = { ...RAW, sprints, teams: deriveTeamsFromSprints(sprints) };
          } else if (parsed && Array.isArray(parsed.sprints)) {
            const sprints = parsed.sprints as Sprint[];
            const teams = Array.isArray(parsed.teams) && parsed.teams.length > 0
              ? parsed.teams
              : deriveTeamsFromSprints(sprints);
            data = { ...RAW, ...parsed, sprints, teams };
          } else {
            throw new Error('JSON must contain a "sprints" array or be an array of sprint objects.');
          }
          const result = validateData(data);
          setPreview(data);
          setValidation(result);
        } else if (file.name.endsWith('.csv')) {
          const records = parseCSV(text);
          if (records.length === 0) {
            setError('CSV file is empty or has no data rows.');
            return;
          }
          // Detect if this is a teams CSV or sprints CSV
          const firstKeys = Object.keys(records[0]);
          let data: Partial<RawData>;
          if (firstKeys.includes('committed') || firstKeys.includes('delivered')) {
            const sprints = csvToSprints(records);
            const teams = deriveTeamsFromSprints(sprints);
            data = { ...RAW, sprints, teams };
          } else if (firstKeys.includes('capacity') || firstKeys.includes('members')) {
            const teams = csvToTeams(records);
            data = { ...RAW, teams };
          } else {
            // Try as sprints anyway
            const sprints = csvToSprints(records);
            const teams = deriveTeamsFromSprints(sprints);
            data = { ...RAW, sprints, teams };
          }
          const result = validateData(data);
          setPreview(data);
          setValidation(result);
        } else {
          setError('Unsupported file type. Please upload .json or .csv files.');
        }
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to parse file.');
      }
    };
    reader.readAsText(file);
  }, []);

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setDragOver(false);
    const file = e.dataTransfer.files[0];
    if (file) processFile(file);
  }, [processFile]);

  const handleFileSelect = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) processFile(file);
  }, [processFile]);

  const handleConfirmImport = () => {
    if (preview && validation?.valid) {
      const fullData: RawData = {
        meta: preview.meta ?? RAW.meta,
        config: preview.config ?? RAW.config,
        teams: preview.teams ?? RAW.teams,
        sprints: preview.sprints ?? RAW.sprints,
        dcCapacity: preview.dcCapacity ?? RAW.dcCapacity,
        jiraSnapshots: preview.jiraSnapshots ?? RAW.jiraSnapshots,
      };
      onImport(fullData);
    }
  };

  const downloadTemplate = (type: 'sprints' | 'teams') => {
    const content = type === 'sprints' ? generateTemplate() : generateTeamsTemplate();
    const blob = new Blob([content], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${type}-template.csv`;
    a.click();
    URL.revokeObjectURL(url);
  };

  const downloadJsonTemplate = () => {
    const template = {
      meta: RAW.meta,
      config: RAW.config,
      teams: [RAW.teams[0]],
      sprints: [RAW.sprints[0]],
      dcCapacity: RAW.dcCapacity,
      jiraSnapshots: RAW.jiraSnapshots,
    };
    const blob = new Blob([JSON.stringify(template, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'data-template.json';
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-ink/40 backdrop-blur-sm">
      <div className="bg-panel rounded-xl shadow-xl w-full max-w-2xl max-h-[85vh] flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-ink-3/10">
          <div>
            <h2 className="text-lg font-semibold text-ink">Import Data</h2>
            <p className="text-xs text-ink-2 mt-0.5">Upload JSON or CSV to update the dashboard</p>
          </div>
          <button onClick={onClose} className="p-2 rounded-lg hover:bg-bg text-ink-3">
            <X size={18} />
          </button>
        </div>

        {/* Tabs */}
        <div className="flex border-b border-ink-3/10">
          {[
            { id: 'json' as ImportTab, label: 'JSON Upload', icon: FileJson },
            { id: 'csv' as ImportTab, label: 'CSV Upload', icon: FileSpreadsheet },
            { id: 'template' as ImportTab, label: 'Templates', icon: Download },
          ].map(({ id, label, icon: Icon }) => (
            <button
              key={id}
              onClick={() => setTab(id)}
              className={`flex items-center gap-2 px-5 py-3 text-sm border-b-2 transition-colors ${
                tab === id ? 'border-brand text-brand font-medium' : 'border-transparent text-ink-2 hover:text-ink'
              }`}
            >
              <Icon size={15} />
              {label}
            </button>
          ))}
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto p-6">
          {tab === 'template' ? (
            <div className="space-y-4">
              <p className="text-sm text-ink-2">Download templates to see the expected format, then fill in your data and upload.</p>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                <button onClick={() => downloadTemplate('sprints')} className="card flex flex-col items-center gap-2 p-4 hover:border-brand/30 border border-transparent transition-colors">
                  <FileSpreadsheet size={24} className="text-brand" />
                  <span className="text-sm font-medium text-ink">Sprints CSV</span>
                  <span className="text-xs text-ink-3">Sprint data template</span>
                </button>
                <button onClick={() => downloadTemplate('teams')} className="card flex flex-col items-center gap-2 p-4 hover:border-brand/30 border border-transparent transition-colors">
                  <FileSpreadsheet size={24} className="text-info" />
                  <span className="text-sm font-medium text-ink">Teams CSV</span>
                  <span className="text-xs text-ink-3">Team definitions template</span>
                </button>
                <button onClick={downloadJsonTemplate} className="card flex flex-col items-center gap-2 p-4 hover:border-brand/30 border border-transparent transition-colors">
                  <FileJson size={24} className="text-good" />
                  <span className="text-sm font-medium text-ink">Full JSON</span>
                  <span className="text-xs text-ink-3">Complete data template</span>
                </button>
              </div>
              <div className="mt-4 p-4 bg-bg rounded-lg">
                <p className="text-xs font-semibold text-ink mb-2">CSV Column Reference (Sprints)</p>
                <code className="text-[11px] text-ink-2 block">
                  id, name, team, vendor, start_date, end_date, committed, delivered, scope_added, carry_forward, goal_met
                </code>
                <p className="text-xs font-semibold text-ink mt-3 mb-2">CSV Column Reference (Teams)</p>
                <code className="text-[11px] text-ink-2 block">
                  id, name, vendor, members (semicolon-separated), capacity
                </code>
              </div>
            </div>
          ) : (
            <div className="space-y-4">
              {/* Drop zone */}
              <div
                onDragOver={(e) => { e.preventDefault(); setDragOver(true); }}
                onDragLeave={() => setDragOver(false)}
                onDrop={handleDrop}
                onClick={() => fileRef.current?.click()}
                className={`border-2 border-dashed rounded-xl p-8 text-center cursor-pointer transition-colors ${
                  dragOver ? 'border-brand bg-brand/5' : 'border-ink-3/30 hover:border-brand/50 hover:bg-bg'
                }`}
              >
                <Upload size={32} className="mx-auto text-ink-3 mb-3" />
                <p className="text-sm text-ink-2">
                  Drop your <span className="font-medium text-ink">.{tab}</span> file here, or click to browse
                </p>
                <p className="text-xs text-ink-3 mt-1">
                  {tab === 'json' ? 'Accepts full RawData object or array of sprints' : 'Accepts sprint data or team data CSV'}
                </p>
                <input
                  ref={fileRef}
                  type="file"
                  accept={tab === 'json' ? '.json' : '.csv'}
                  onChange={handleFileSelect}
                  className="hidden"
                />
              </div>

              {/* Error */}
              {error && (
                <div className="flex items-start gap-2 p-3 bg-bad/10 rounded-lg">
                  <AlertTriangle size={16} className="text-bad shrink-0 mt-0.5" />
                  <p className="text-sm text-bad">{error}</p>
                </div>
              )}

              {/* Validation Result */}
              {validation && (
                <div className="space-y-3">
                  <div className={`flex items-start gap-2 p-3 rounded-lg ${validation.valid ? 'bg-good/10' : 'bg-bad/10'}`}>
                    {validation.valid ? (
                      <CheckCircle2 size={16} className="text-good shrink-0 mt-0.5" />
                    ) : (
                      <AlertTriangle size={16} className="text-bad shrink-0 mt-0.5" />
                    )}
                    <div>
                      <p className={`text-sm font-medium ${validation.valid ? 'text-good' : 'text-bad'}`}>
                        {validation.valid ? 'Data is valid and ready to import' : 'Validation failed'}
                      </p>
                      {validation.errors.map((e, i) => (
                        <p key={i} className="text-xs text-bad mt-1">• {e}</p>
                      ))}
                      {validation.warnings.map((w, i) => (
                        <p key={i} className="text-xs text-warn mt-1">⚠ {w}</p>
                      ))}
                    </div>
                  </div>

                  {/* Summary */}
                  <div className="grid grid-cols-4 gap-2">
                    <div className="bg-bg rounded-lg p-3 text-center">
                      <p className="text-lg font-semibold text-ink">{validation.summary.sprints}</p>
                      <p className="text-[10px] text-ink-3">Sprints</p>
                    </div>
                    <div className="bg-bg rounded-lg p-3 text-center">
                      <p className="text-lg font-semibold text-ink">{validation.summary.teams}</p>
                      <p className="text-[10px] text-ink-3">Teams</p>
                    </div>
                    <div className="bg-bg rounded-lg p-3 text-center">
                      <p className="text-lg font-semibold text-ink">{validation.summary.capacity}</p>
                      <p className="text-[10px] text-ink-3">Capacity</p>
                    </div>
                    <div className="bg-bg rounded-lg p-3 text-center">
                      <p className="text-lg font-semibold text-ink">{validation.summary.jira}</p>
                      <p className="text-[10px] text-ink-3">Jira Snapshots</p>
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="flex items-center justify-end gap-3 px-6 py-4 border-t border-ink-3/10">
          <button onClick={onClose} className="px-4 py-2 text-sm text-ink-2 hover:text-ink rounded-lg hover:bg-bg">
            Cancel
          </button>
          {preview && validation?.valid && (
            <button
              onClick={handleConfirmImport}
              className="px-4 py-2 text-sm font-medium text-white bg-brand rounded-lg hover:bg-brand-2 transition-colors"
            >
              Import Data ({validation.summary.sprints} sprints)
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
