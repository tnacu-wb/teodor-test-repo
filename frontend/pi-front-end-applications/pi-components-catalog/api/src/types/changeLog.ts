export interface ChangeLogResults {
  activityLog: any[];
  hasMore: boolean;
  limit: number;
  offset: number;
  totalPages: number;
  totalResults: number;
  isLoadingChangeLog: boolean;
  isErrorChangeLog: boolean;
}
