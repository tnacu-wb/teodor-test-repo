import { SortingState } from '@tanstack/react-table';
import { GET_BATCH_SUMMARY } from '@whitbread-eos/api';
import { useQueryRequest } from '@whitbread-eos/utils';
import { format } from 'date-fns';
import { useMemo } from 'react';

import { GetBatchSummaryResponse, UseBatchSummaryOptions } from './types';

export const formatDateTime = (isoString: string): string => {
  const date = new Date(isoString);
  return format(date, 'dd LLL yyyy | HH:mm');
};

export const LIST_REFRESH_TIME = 5000;
export const ACTIVE_STATUS_SET = new Set(['RUNNING', 'PENDING']);

export const mapStatus = (status: string): string => {
  switch (status) {
    case 'COMPLETED':
      return 'Complete';
    case 'FAILED':
      return 'Failed';
    case 'PENDING':
      return 'Pending';
    case 'RUNNING':
      return 'Running';
    case 'EXPIRED':
      return 'Expired';
    default:
      return status;
  }
};

/**
 * Safely detect invalid API response (string, null, wrong shape, etc)
 */
const isValidBatchResponse = (data: any): data is GetBatchSummaryResponse => {
  if (!data || typeof data !== 'object') return false;
  if (!('getBatchSummary' in data)) return false;
  if (!data.getBatchSummary || typeof data.getBatchSummary !== 'object') return false;
  if (!Array.isArray(data.getBatchSummary.items)) return false;
  return true;
};

export const shouldRefetchBatchSummary = (query: any): number | false => {
  const response = query?.state?.data;

  if (!isValidBatchResponse(response)) {
    return false;
  }
  const items = response.getBatchSummary?.items;
  const hasRunning = items.some(
    (item: { status?: string }) => !!item.status && ACTIVE_STATUS_SET.has(item.status)
  );

  return hasRunning ? LIST_REFRESH_TIME : false;
};

export const useBatchSummary = (
  page: number,
  size: number,
  sort: string,
  options: UseBatchSummaryOptions = {}
) => {
  const { enabled = true } = options;

  const {
    data,
    isLoading,
    isError: queryError,
    isFetching,
    refetch,
  } = useQueryRequest(
    ['getBatchSummary', page, size, sort],
    GET_BATCH_SUMMARY,
    { page, size, sort },
    {
      enabled,
      refetchInterval: shouldRefetchBatchSummary,
      refetchIntervalInBackground: true,
    }
  );

  // If API returned string "no healthy upstream", or any incorrect structure
  const safe = isValidBatchResponse(data);

  const summary = safe ? data.getBatchSummary : null;

  const finalIsError = queryError || !safe;

  return {
    summary,
    items: safe && summary ? summary.items : [],
    pagination: safe
      ? {
          page: data.getBatchSummary.page,
          size: data.getBatchSummary.size,
          totalPages: data.getBatchSummary.totalPages,
          totalElements: data.getBatchSummary.totalElements,
          hasNext: data.getBatchSummary.hasNext,
        }
      : null,
    isLoading,
    isError: finalIsError,
    isFetching,
    refetch,
  };
};

const SORT_FIELD_MAP: Record<string, string> = {
  campaign: 'campaignName',
  created: 'createdAt',
  requestedBy: 'requestedBy',
  promoCode: 'operaPromoCode',
  amount: 'batchCount',
  isGeneric: 'isMultiple',
  status: 'status',
  maxRedemptionLimit: 'maxRedemptionLimit',
};

export const usePromoBatches = ({
  pageIndex,
  pageSize,
  sorting,
}: {
  pageIndex: number;
  pageSize: number;
  sorting: SortingState;
}) => {
  const sortField = sorting[0]?.id ?? 'created';
  const sortDirection: 'asc' | 'desc' = sorting[0]?.desc ? 'desc' : 'asc';

  const apiField = SORT_FIELD_MAP[sortField] ?? sortField;
  const sort = `${apiField},${sortDirection}`;

  const { items, pagination, isLoading, isFetching, isError, refetch } = useBatchSummary(
    pageIndex,
    pageSize,
    sort
  );

  const data = useMemo(
    () =>
      items.map((item) => {
        return {
          batchId: item?.batchId,
          campaign: item?.campaignName,
          created: formatDateTime(item.createdAt),
          requestedBy: item?.requestedBy,
          promoCode: item?.operaPromoCode,
          amount: item?.batchCount,
          status: mapStatus(item?.status),
          isGeneric: item?.isMultiple ? 'Yes' : 'No',
          eligibility: item?.batchEligibilities,
          maxRedemptionLimit: item?.maxRedemptionLimit,
          downloaded: item?.downloaded,
        };
      }),
    [items]
  );

  return {
    data,
    total: pagination?.totalElements ?? 0,
    isLoading,
    isFetching,
    isError,
    refetch,
  };
};
