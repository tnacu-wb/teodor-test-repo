export interface BatchEligibility {
  region: string;
  channel: string;
  platforms: string[];
}

export type BatchSummaryItem = {
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
  createdAt: string;
  isMultiple: boolean;
  batchEligibilities: BatchEligibility[];
  maxRedemptionLimit: number | null;
};

export type BatchSummary = {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
  startIndex: number;
  endIndex: number;
  items: BatchSummaryItem[];
};

export type GetBatchSummaryResponse = {
  getBatchSummary: BatchSummary;
};

export interface UseBatchSummaryOptions {
  enabled?: boolean;
}

export interface BatchById {
  batchId: string;
  campaignName: string;
  operaPromoCode: string;
  prefix: string;
  batchCount: number;
  status: string;
  downloaded: boolean;
  requestedBy: string;
  createdAt: string;
  codeLength: number;
  s3Key: string;
  password: string;
  expiryDate: string;
  notes?: string;
  updatedAt?: string;
  completedAt?: string;
}

export interface GetBatchByIdResponse {
  getBatchById: BatchSummaryItem;
}

export interface GetBatchAsDownloadedResponse {
  markPromoBatchAsDownloaded?: boolean;
}
export interface UseBatchByIdOptions {
  enabled?: boolean;
}
