import { ColumnDef, SortingState, Table as ReactTable } from '@tanstack/react-table';
import { Dispatch, SetStateAction } from 'react';

export type PromoBatch = {
  batchId: string;
  campaign: string;
  created: string;
  requestedBy: string | null;
  promoCode: string;
  amount: number;
  status: string;
  downloaded: boolean;
  eligibility: any;
};

export type HeaderSectionProps = {
  loadingTransition: boolean;
  handleToggle: () => void;
  sorting: SortingState;
  setSorting: React.Dispatch<React.SetStateAction<SortingState>>;
};

export type FetchPromoBatchesParams = {
  pageIndex: number;
  pageSize: number;
  sorting: SortingState;
};

export type PromoBatchesResult = {
  data: PromoBatch[];
  total: number;
};

export type PromoBatchesTableProps = {
  fetchPromoBatches: (params: FetchPromoBatchesParams) => Promise<PromoBatchesResult>;
};

export type DesktopTablePropsType = {
  columns: ColumnDef<PromoBatch>[];
  table: ReactTable<PromoBatch>;
  sorting: SortingState;
  setSorting: React.Dispatch<React.SetStateAction<SortingState>>;
  loading: boolean;
};
export type PaginationUpdater = {
  pageIndex: number;
  pageSize: number;
};

export type UpdaterFn = (old: PaginationUpdater) => PaginationUpdater;

export type OtpModalPropTypes = {
  showModal: boolean;
  setShowModal: Dispatch<SetStateAction<OtpModalPropTypes>>;
};

export interface BatchByIdData {
  operaPromoCode: string;
  password: string;
}

export interface BatchSummaryItem {
  batchId: string;
  campaignName: string;
  operaPromoCode: string;
  prefix: string;
  batchCount: number;
  status: 'FAILED' | 'SUCCESS' | 'PENDING';
  s3Key: string | null;
  notes: string | null;
  downloaded: boolean;
  password: string;
  requestedBy: string | null;
  createdAt: string; // ISO date string
}

export type PromoBatchesTableContextType<T> = {
  showModal: T;
  setShowModal: Dispatch<SetStateAction<boolean>>;
  setBatchId: Dispatch<SetStateAction<string | null>>;
  batchIdData: BatchSummaryItem | null;
};

export type ResendTextProps = {
  onResend: () => void;
};
export type TimerStatusProps = {
  timeLeft: number;
};
