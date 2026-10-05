export interface Sprint {
  id: string;
  name: string;
  team: string;
  vendor: string;
  startDate: string;
  endDate: string;
  committed: number;
  delivered: number;
  scopeAdded: number;
  carryForward: number;
  goalMet: boolean;
  tpm?: string;
  sm?: string;
  notes?: string;
}

export interface Team {
  id: string;
  name: string;
  vendor: string;
  members: string[];
  capacity: number;
}

export interface DcCapacity {
  sprint: string;
  team: string;
  planned: number;
  actual: number;
  people: number;
}

export interface JiraSnapshot {
  sprint: string;
  team: string;
  totalIssues: number;
  completed: number;
  inProgress: number;
  open: number;
  blocked: number;
  scopeAdded: number;
  avgLeadTimeDays: number;
  avgCycleTimeDays: number;
  priorities: { critical: number; high: number; medium: number; low: number };
}

export interface Meta {
  org: string;
  program: string;
  source: string;
  refreshed: string;
  piName: string;
  reportVersion: string;
}

export interface RawData {
  meta: Meta;
  config: { healthyBandLow: number; healthyBandHigh: number; carryThreshold: number };
  sprints: Sprint[];
  teams: Team[];
  dcCapacity: DcCapacity[];
  jiraSnapshots: JiraSnapshot[];
}

export interface TeamMetric {
  team: string;
  vendor: string;
  committed: number;
  delivered: number;
  scopeAdded: number;
  carryForward: number;
  sayDo: number;
  overDelivery: number;
  avgVelocity: number;
  rawCv: number;
  trendAdjustedCv: number;
  trendPct: number;
  stability: 'Stable' | 'Moderate' | 'Volatile';
  predictability: 'Predictable' | 'Moderate' | 'Unpredictable';
  scopeCreep: number;
  commitmentReliability: number;
  deliveryHealth: 'Good' | 'Warning' | 'At Risk';
  sprintCount: number;
}

export interface PortfolioMetric {
  totalCommitted: number;
  totalDelivered: number;
  totalScopeAdded: number;
  totalCarryForward: number;
  sayDo: number;
  deliveryHealth: 'Good' | 'Warning' | 'At Risk';
  sprintStability: 'Stable' | 'Moderate' | 'Volatile';
  teamPerformanceIndex: number;
  scopeCreep: number;
  velocityTrend: number;
  activeTeams: number;
  activeSprints: number;
  sprintSeries: SprintAggregate[];
}

export interface SprintAggregate {
  sprint: string;
  committed: number;
  delivered: number;
  scopeAdded: number;
  carryForward: number;
  sayDo: number;
}

export interface RegisterRecord {
  id: string;
  sprint: string;
  team: string;
  vendor: string;
  committed: number;
  delivered: number;
  scopeAdded: number;
  carryForward: number;
  sayDo: number;
  type: 'Under-delivered' | 'Over-delivered' | 'Scope added' | 'Normal';
}

export interface FilterState {
  team: string | null;
  sprint: string | null;
  vendor: string | null;
}

export type ViewId =
  | 'executive'
  | 'delivery'
  | 'trend'
  | 'sprint'
  | 'jira'
  | 'team'
  | 'resource'
  | 'risk'
  | 'register'
  | 'quality';
